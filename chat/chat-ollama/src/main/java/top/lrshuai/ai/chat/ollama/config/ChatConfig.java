package top.lrshuai.ai.chat.ollama.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder clientBuilder) {
        return clientBuilder
                .defaultSystem("回答以下问题结果都用中文回答,如果你不知道答案，请回复“我不知道” ")
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(MessageWindowChatMemory.builder().maxMessages(3).build())
                        .build())
                // 打印日志
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


}
