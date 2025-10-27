package top.lrshuai.ai.mcp.client.annotation.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.mcp.client.common.autoconfigure.annotations.McpClientAnnotationScannerAutoConfiguration;
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

    @Resource
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    @Resource
    private McpClientAnnotationScannerAutoConfiguration.ClientMcpAnnotatedBeans clientMcpAnnotatedBeans;

    public DemoController (OllamaChatModel chatModel) {

        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                // 实现 Logger 的 Advisor
                .defaultAdvisors(new SimpleLoggerAdvisor())
              .build();
    }

    @GetMapping("/ask")
    public String ask(@RequestParam(value = "question", defaultValue = "今日运势如何") String question) {
        return chatClient
                .prompt(question)
                .toolCallbacks(toolCallbackProvider)
                .toolContext(Map.of("progressToken", "my-progress-token"))
                .call()
                .content();
    }

}
