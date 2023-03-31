package cn.aezo.chat_gpt.controller;

import cn.aezo.chat_gpt.service.UserService;
import cn.aezo.chat_gpt.util.MiscU;
import cn.aezo.chat_gpt.util.Result;
import cn.aezo.chat_gpt.util.ValidU;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/core/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestParam("loginType") String loginType, @RequestBody Map<String, Object> params) {
        String userId = null;
        String openid = null;
        if("WXMP".equals(loginType)) {
            Result<Map> result = userService.loginByWXMP(params);
            if(Result.isFailure(result)) {
                return result;
            }
            userId = (String) result.getData().get("userId");
            openid = (String) result.getData().get("openid");
        } else {
            String username = (String) params.get("username");
            String password = (String) params.get("password");
            if(ValidU.haveEmptyOne(MiscU.Instance.toList(username, password))) {
                return Result.failure("请输入用户名和密码");
            }
            userId = userService.checkAndGetUserId(username, password);
        }
        if(ValidU.isEmpty(userId)) {
            return Result.failure("登录失败");
        }
        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Result.success(MiscU.Instance.toMap("tokenInfo", tokenInfo, "openid", openid));
    }

    @RequestMapping("/info")
    public Result getUserInfo() {
        Object loginId = StpUtil.getLoginId();
        Map<String, Object> userInfo = userService.getUserInfo(loginId);
        List<String> roles = MiscU.Instance.toList((String) userInfo.get("user_level"));
        return Result.success(MiscU.Instance.toMap("userInfo", userInfo, "roles", roles));
    }
}
