package top.lrshuai.ai.mcp.client.annotation.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RequestMapping("/client")
@RestController
public class DemoController {

    private final ChatClient chatClient;
    private static final String DEFAULT_QUESTION="今日运势如何";

    @Resource
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    public DemoController (OllamaChatModel chatModel) {

        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem("回答以下问题结果都用中文回答,不要包含任何一个英文，如果你不知道答案，请只回复‘我不知道’")
                // 打印日志
                .defaultAdvisors(new SimpleLoggerAdvisor())
              .build();
    }

    @GetMapping("/ask")
    public String ask(@RequestParam(defaultValue = DEFAULT_QUESTION) String question) {
        return chatClient
                .prompt(question)
                .call()
                .content();
    }

    @GetMapping("/askByTool")
    public String askByTool(@RequestParam(defaultValue = DEFAULT_QUESTION) String question) {
        return chatClient
                .prompt(question)
                .toolCallbacks(toolCallbackProvider)
                .toolContext(Map.of("progressToken", "my-progress-token"))
                .call()
                .content();
    }

}
