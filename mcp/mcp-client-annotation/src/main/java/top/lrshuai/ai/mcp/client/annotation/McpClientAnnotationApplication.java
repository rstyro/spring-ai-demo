package top.lrshuai.ai.mcp.client.annotation;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class McpClientAnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpClientAnnotationApplication.class, args);
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
