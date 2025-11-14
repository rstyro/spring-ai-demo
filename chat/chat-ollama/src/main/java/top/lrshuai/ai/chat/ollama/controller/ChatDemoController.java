package top.lrshuai.ai.chat.ollama.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import top.lrshuai.ai.common.resp.R;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatDemoController {

    private static final String DEFAULT_SYSTEM_PROMPT = "你是一位博学多识，专业严谨，幽默风趣，热爱生活的智能助手";
    private static final String DEFAULT_PROMPT = "你夸夸我";

    @Resource
    private ChatClient chatClient;


    @Resource
    private OllamaChatModel ollamaChatModel;

    /**
     * model对象请求
     */
    @GetMapping("/model/chat")
    public R modelChat(@RequestParam(defaultValue = DEFAULT_SYSTEM_PROMPT) String system,
                       @RequestParam(defaultValue = DEFAULT_PROMPT) String prompt) {
        List<Message> messages = new ArrayList<>();
        // 系统人设
        messages.add(new SystemMessage(system));
        messages.add(new UserMessage(prompt));
        AssistantMessage assistantMessage = ollamaChatModel.call(new Prompt(messages)).getResult().getOutput();
        return R.ok(assistantMessage);
    }

    /**
     * model对象stream流式请求
     */
    @GetMapping("/model/stream")
    public Flux<String> modelStream(HttpServletResponse response,
                                    @RequestParam(defaultValue = DEFAULT_PROMPT) String prompt) {
        response.setCharacterEncoding("UTF-8");
        Flux<ChatResponse> stream = ollamaChatModel.stream(new Prompt(prompt));
        return stream.map(resp -> resp.getResult().getOutput().getText());
    }


    /**
     * 使用ChatClient
     */
    @GetMapping("/client/chat")
    public String clientChat(HttpServletResponse response,
                                  @RequestParam(defaultValue = DEFAULT_PROMPT) String prompt) {
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(prompt)
                .system(DEFAULT_SYSTEM_PROMPT)
                .call()
                .content();
    }


    /**
     * 使用ChatClient
     */
    @GetMapping("/client/stream")
    public Flux<String> clientStream(HttpServletResponse response,
                                  @RequestParam(defaultValue = DEFAULT_PROMPT) String prompt) {
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(prompt)
                .system(DEFAULT_SYSTEM_PROMPT)
                .stream()
                .content();
    }

}
