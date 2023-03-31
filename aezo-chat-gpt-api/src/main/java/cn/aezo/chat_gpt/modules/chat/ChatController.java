package cn.aezo.chat_gpt.modules.chat;

import cn.aezo.chat_gpt.limit.LimitRequestNum;
import cn.aezo.chat_gpt.util.MiscU;
import cn.aezo.chat_gpt.util.Result;
import cn.aezo.chat_gpt.util.ValidU;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * HTTP方式进行聊天<br/>
 * 模型对应为text-davinci-003<br/>
 * 通过WSS等流式响应时，可选择模型gpt-3.5-turbo<br/>
 * 更多模型参考<br/>
 * @see com.unfbx.chatgpt.entity.chat.ChatCompletion.Model
 */
@Slf4j
@RestController
@RequestMapping("/tools/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 阻塞式对话，模型对应为text-davinci-003，非流式响应无法使用gpt-3.5-turbo
     * 流式会话请参考 WebSocketServer，支持gpt-3.5-turbo
     */
    @Deprecated
    @LimitRequestNum
    @RequestMapping("/sendMsg")
    public Result sendMsg(HttpServletRequest request, @RequestBody Map<String, Object>parmas) {
        String userId = (String) parmas.get("userId");
        String question = (String) parmas.get("question");
        if(ValidU.isEmpty(question)) {
            return Result.failure("请先输入您的问题哦");
        }
        log.info(userId + "发送消息: " + question);
        String ack = chatService.chatDirectly(question, userId);
        log.info(userId + "接收消息: " + ack);
        return Result.success(MiscU.Instance.toMap("ack", ack));
    }

    @RequestMapping("/getUserChatAsset")
    public Result getUserChatAsset() {
        return chatService.getUserChatAsset();
    }
}
