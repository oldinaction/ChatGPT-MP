package cn.aezo.chat_gpt.service.impl;

import cn.aezo.chat_gpt.config.SqWxConfig;
import cn.aezo.chat_gpt.modules.chat.mapper.ChatMapper;
import cn.aezo.chat_gpt.service.UserService;
import cn.aezo.chat_gpt.config.BizException;
import cn.aezo.chat_gpt.util.MiscU;
import cn.aezo.chat_gpt.util.Result;
import cn.aezo.chat_gpt.util.SpringU;
import cn.aezo.chat_gpt.util.ValidU;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Value("${sq-app-common.enc-pass-key:}")
    private String encPassKey;

    @Value("${sq-mini-tools.chat.invite-num:10}")
    private String chatinviteNum;

    @Value("${sq-mini-tools.chat.invite-max:5}")
    private String chatinviteMax;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String checkAndGetUserId(String username, String password) {
        if(ValidU.isEmpty(username) || ValidU.isEmpty(password)) {
            throw new BizException("用户名和密码不能为空");
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id, password from mt_user_info " +
                " where valid_status = 1 and username = ?", username);
        if(ValidU.isEmpty(list) || list.size() != 1) {
            throw new BizException("无效的用户");
        }
        Map<String, Object> user = list.get(0);
        boolean passwordPass = checkPassword(password, (String) user.get("password"));
        if(!passwordPass) {
            throw new BizException("密码不正确");
        }
        return ValidU.isNotEmpty(user.get("ID")) ? user.get("ID").toString() : username;
    }

    @Override
    public List<String> getPermissionList(Object userId) {
        return MiscU.Instance.toList((String) getUserInfo(userId).get("USER_LEVEL"));
    }

    @Override
    public List<String> getRoleList(Object userId) {
        return MiscU.Instance.toList((String) getUserInfo(userId).get("USER_LEVEL"));
    }

    @Override
    public Map<String, Object> getUserInfo(Object userId) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "select id, username, nick_name, user_level, mobile, email, avatar, balance " +
                "from mt_user_info ui " +
                "where ui.valid_status = 1 and ui.id = ?", userId);
        if(ValidU.isNotEmpty(list) && list.size() == 1) {
            return list.get(0);
        } else {
            throw new BizException("无效的用户");
        }
    }

    @Override
    public Result loginByWXMP(Map<String, Object> params) {
        String appid = (String) params.get("appid");
        String code = (String) params.get("code");
        String inviterUserId = (String) params.get("inviterUserId");
        if(ValidU.isEmpty(appid) || ValidU.isEmpty(code)) {
            return Result.failure("缺少必须参数appid/code");
        }

        final WxMaService wxService = SqWxConfig.getMaService(appid);
        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
            String openid = session.getOpenid();
            return this.getUserIdByOpenid(openid, inviterUserId);
        } catch (WxErrorException e) {
            log.error("登录出错", e);
            return Result.failure("登录出错");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> registerByOpenid(String openid, String inviterUserId) {
        String username = this.getRandomUsername();
        String userId = IdUtil.getSnowflakeNextIdStr();
        // 默认注册为一级普通会员
        jdbcTemplate.update("insert into mt_user_info(id, username, nick_name, user_level, inviter, version, create_time, update_time) " +
                "values(?, ?, ?, ?, ?, 0, now(), now())", userId, username, username, "M1", inviterUserId);

        String id = IdUtil.getSnowflakeNextIdStr();
        jdbcTemplate.update("insert into mt_user_oauth(id, user_id, login_type, openid, create_time, update_time) " +
                "values(?, ?, 'WXMP', ?, now(), now())", id, userId, openid);

        this.inviterReward(inviterUserId, userId);
        return MiscU.Instance.toMap("id", id, "userId", userId, "openid", openid);
    }

    @Override
    public String encPassword(String password) {
        String pass = SaSecureUtil.aesEncrypt(encPassKey, password);
        return SaSecureUtil.sha1(pass);
    }

    @Override
    public boolean checkPassword(String passwordPure, String password) {
        if(ValidU.isEmpty(passwordPure)) {
            return false;
        }
        String pass = encPassword(passwordPure);
        return pass.equals(password);
    }

    private Result getUserIdByOpenid(String openid, String inviterUserId) {
        // 查询是否存在相应类型的 openid
        List<Map<String, Object>> userOauthList = jdbcTemplate.queryForList(
                "select id \"id\", user_id \"userId\", openid \"openid\" from mt_user_oauth ua where " +
                        " ua.valid_status = 1 and ua.login_type = 'WXMP' and ua.openid = ?", openid);
        if(ValidU.isEmpty(userOauthList)) {
            UserService userService = SpringU.getBean(UserService.class);
            Map<String, Object> userOauth = userService.registerByOpenid(openid, inviterUserId);
            if(userOauth != null) {
                userOauthList.add(userOauth);
            } else {
                return Result.failure("无第三方认证授权信息");
            }
        }
        if(userOauthList.size() > 1) {
            return Result.failure("存在多个认证授权信息");
        }

        Map<String, Object> userOauth = userOauthList.get(0);
        if(ValidU.isNotEmpty(userOauth.get("userId"))) {
            return Result.success(userOauth);
        }
        throw new BizException("错误状态");
    }

    private boolean inviterReward(String inviterUserId, String userId) {
        if(ValidU.isEmpty(inviterUserId)) {
            return true;
        }
        try {
            // 防止同时多个用户点击分享链接
            for (int i = 0; i < 3; i++) {
                Integer inviteToday = jdbcTemplate.queryForObject("select count(1) from mt_user_info " +
                        "where create_time > CURDATE() and inviter = ?", Integer.class, inviterUserId);
                if (inviteToday == null || inviteToday.compareTo(Integer.valueOf(chatinviteMax)) >= 0) {
                    return true;
                }

                Map<String, Object> inviterMap = jdbcTemplate.queryForMap("select asset, version from mt_user_asset " +
                        "where valid_status = 1 and biz_type = 'Chat' and asset_type = 'N' and user_id = ?", inviterUserId);
                ChatMapper chatMapper = SpringU.getBean(ChatMapper.class);
                BigDecimal asset = NumberUtil.add(new BigDecimal(inviterMap.get("asset").toString()), new BigDecimal(chatinviteNum));
                int upCount = chatMapper.updateUserAsset(inviterUserId, "N", asset, Integer.valueOf(inviterMap.get("version").toString()));
                if(upCount > 0) {
                    upCount = chatMapper.insertUserAssetHis(MiscU.Instance.toMap(
                            "id", IdUtil.getSnowflakeNextIdStr(), "userId", inviterUserId, "bizType", "Chat",
                            "assetType", "N", "asset", chatinviteNum, "remark", "邀请新用户获得"));
                    if(upCount > 0) {
                        return true;
                    }
                }
                Thread.sleep(200);
            }
        } catch (Exception e) {
            log.error("{} 邀请 {} 获取奖励出错", inviterUserId, userId, e);
        }
        log.warn("{} 邀请 {} 获取奖励失败", inviterUserId, userId);
        return false;
    }

    private String getRandomUsername() {
        String username;
        int count = 0;
        do {
            username = "mt_" + RandomUtil.randomString(10);
            count = jdbcTemplate.queryForObject("select count(1) from mt_user_info where username = ?",
                    new Object[]{username}, Integer.class);
        } while (count > 0);
        return username;
    }

    public static void main(String[] args) {
        String key = "aezo-chat-gpt";
        String pass = SaSecureUtil.aesEncrypt(key, "123456");
        pass = SaSecureUtil.sha1(pass);
        System.out.println("pass = " + pass);
    }
}
