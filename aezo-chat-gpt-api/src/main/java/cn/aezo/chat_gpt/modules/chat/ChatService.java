package cn.aezo.chat_gpt.modules.chat;

import cn.aezo.chat_gpt.modules.chat.mapper.ChatMapper;
import cn.aezo.chat_gpt.util.MiscU;
import cn.aezo.chat_gpt.util.Result;
import cn.aezo.chat_gpt.util.SpringU;
import cn.aezo.chat_gpt.util.ValidU;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.common.Choice;
import com.unfbx.chatgpt.entity.completions.CompletionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Value("${sq-mini-tools.chat.regist-num:10}")
    private String chatRegistNum;

    @Value("${sq-mini-tools.chat.daily-free-num:5}")
    private String chatDailyFreeNum;

    @Value("${sq-mini-tools.openai.api-host:}")
    private String apiHost;

    @Value("${sq-mini-tools.openai.api-key:}")
    private String apiKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ChatMapper chatMapper;

    public String chatDirectly(String question, String userId) {
        // 日志输出可以不添加
        // HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        // httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(apiKey)
                .connectTimeout(50)
                .writeTimeout(50)
                .readTimeout(50)
                //.interceptor(Arrays.asList(httpLoggingInterceptor))
                .apiHost(apiHost)
                .build();
        // 默认为 text-davinci-003
        CompletionResponse completions = openAiClient.completions(question);
        removeIrrelevantChar(completions, userId);
        List<String> collect = Arrays.stream(completions.getChoices()).map(Choice::getText).collect(Collectors.toList());
        String ack = StrUtil.join("", collect);
        return ack;
    }

    public Result getUserChatAsset() {
        String userId = StpUtil.getLoginIdAsString();
        Map<String, Object> assetMap = getUserChatAsset(userId);
        if(assetMap.get("N") == null) {
            SpringU.getBean(ChatService.class).createChatAsset(userId);
            assetMap = getUserChatAsset(userId);
        }
        return Result.success(assetMap);
    }

    public Map<String, Object> getUserChatAsset(String userId) {
        Map<String, Object> assetMap = jdbcTemplate.queryForMap(
                "select sum(case t.asset_type when 'N' then t.asset else 0 end) as n " +
                        ",max(case t.asset_type when 'N' then t.version else 0 end) as version_n " +
                        ",sum(case t.asset_type when 'DFN' then t.asset else 0 end) as dfn " +
                        ",max(case t.asset_type when 'DFN' then t.version else 0 end) as version_dfn " +
                        ",max(case when t.asset_type = 'DFN' and t.create_time < CURDATE() then 1 else 0 end) as old_dfn " +
                        "from mt_user_asset t where t.valid_status = 1 and t.biz_type = 'Chat' and t.user_id = ?", userId);
        if("1".equals(assetMap.get("old_dfn") + "")) {
            int count = jdbcTemplate.update(
                    "update mt_user_asset set asset = ?, version=version+1 ,create_time = now(), update_time = now() " +
                            "where valid_status = 1 and biz_type = 'Chat' and asset_type = 'DFN' and version = ? and user_id = ?",
                    chatDailyFreeNum, assetMap.get("version_dfn"), userId);
            if(count > 0) {
                assetMap.put("dfn", new BigDecimal(chatDailyFreeNum));
                assetMap.put("version_dfn", Integer.parseInt(assetMap.get("version_dfn").toString()) + 1);
                assetMap.put("old_dfn", new BigDecimal(0));
            }
        }
        return assetMap;
    }

    /**
     * 创建聊天资产
     * @author smalle
     * @since 2023/3/26
     */
    @Transactional(rollbackFor = Exception.class)
    public Result createChatAsset(String userId) {
        jdbcTemplate.update("insert into mt_user_asset(id, user_id, biz_type, asset_type, asset, create_time, update_time) " +
                "values(?, ?, 'Chat', 'N', ?, now(), now())", IdUtil.getSnowflakeNextIdStr(), userId, chatRegistNum);

        jdbcTemplate.update("insert into mt_user_asset(id, user_id, biz_type, asset_type, asset, create_time, update_time) " +
                "values(?, ?, 'Chat', 'DFN', ?, now(), now())", IdUtil.getSnowflakeNextIdStr(), userId, chatDailyFreeNum);
        return Result.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public Result checkAndUpdateAsset(String userId) {
        Map<String, Object> userChatAsset = getUserChatAsset(userId);
        BigDecimal n = (BigDecimal) userChatAsset.get("n");
        Integer versionN = Integer.valueOf(userChatAsset.get("version_n").toString());
        BigDecimal dfn = (BigDecimal) userChatAsset.get("dfn");
        Integer versionDfn = Integer.valueOf(userChatAsset.get("version_dfn").toString());
        String assetType = "N";
        int assetRate = -1;
        if(dfn.compareTo(BigDecimal.ZERO) > 0) {
            dfn = NumberUtil.add(dfn, assetRate);
            assetType = "DFN";
        } else if(n.compareTo(BigDecimal.ZERO) > 0) {
            n = NumberUtil.add(n, assetRate);
        } else {
            return Result.failure("剩余次数不足", "chat.asset_short");
        }
        int upCount = chatMapper.updateUserAsset(userId, assetType, "N".equals(assetType) ? n : dfn, "N".equals(assetType) ? versionN : versionDfn);
        if(upCount == 0) {
            return Result.failure("接收消息失败, 请重新发送试试~", "chat.asset_calc");
        }
        upCount = chatMapper.insertUserAssetHis(MiscU.Instance.toMap(
                "id", IdUtil.getSnowflakeNextIdStr(), "userId", userId, "bizType", "Chat",
                "assetType", assetType, "asset", assetRate, "remark", "聊天消耗"));
        if(upCount == 0) {
            return Result.failure("接收消息失败, 请重新发送试试~", "chat.asset_his");
        }
        return Result.success();
    }

    private static void removeIrrelevantChar(CompletionResponse completions, String userId) {
        if(ValidU.isEmpty(completions.getChoices())) {
            return;
        }
        Choice choice = completions.getChoices()[0];
        String msg = choice.getText();
        if(msg == null) {
            msg = "";
        }
        msg = msg.replaceAll("openai:", "")
                .replaceAll("openai：", "")
                .replaceAll("OpenAi:", "")
                .replaceAll("OpenAi：", "")
                .replaceAll("OpenAI：", "")
                .replaceAll("OpenAI:", "")
                .replaceAll(userId + ":", "")
                .replaceAll(userId + "：", "")
                .replaceAll("^\\n|\\n$", "");
        choice.setText(msg);
    }
}
