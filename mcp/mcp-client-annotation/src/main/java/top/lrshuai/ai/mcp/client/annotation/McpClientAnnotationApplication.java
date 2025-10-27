package top.lrshuai.ai.mcp.client.annotation;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class McpClientAnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpClientAnnotationApplication.class, args);
    }

    @Value("${spring.ai.ollama.base-url}")
    private String baseUrl;

    @Bean
    @ConditionalOnMissingBean
    public OllamaApi ollamaApi(ObjectProvider<RestClient.Builder> restClientBuilderProvider,
                               ObjectProvider<WebClient.Builder> webClientBuilderProvider, ResponseErrorHandler responseErrorHandler) {
        return OllamaApi.builder()
                .baseUrl(baseUrl)
                .restClientBuilder(restClientBuilderProvider.getIfAvailable(RestClient::builder))
                .webClientBuilder(webClientBuilderProvider.getIfAvailable(WebClient::builder))
                .responseErrorHandler(responseErrorHandler)
                .build();
    }

    @Bean
    public CommandLineRunner predefinedQuestions(
            List<McpSyncClient> mcpClients) {

        return args -> {

            for (McpSyncClient mcpClient : mcpClients) {
                System.out.println(">>> MCP Client: " + mcpClient.getClientInfo());

                // Call a tool that sends progress notifications
                McpSchema.CallToolRequest toolRequest = McpSchema.CallToolRequest.builder()
                        .name("daily_fortune")
                        .arguments(Map.of("name", "rstyro"))
                        .progressToken("rstyro")
                        .build();
                McpSchema.CallToolResult response = mcpClient.callTool(toolRequest);
                System.out.println("Tool response: " + response);

            }
        };
    }

}
