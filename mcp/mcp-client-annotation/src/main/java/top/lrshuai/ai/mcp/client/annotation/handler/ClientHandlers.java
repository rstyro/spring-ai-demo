package top.lrshuai.ai.mcp.client.annotation.handler;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springaicommunity.mcp.annotation.McpProgress;
import org.springaicommunity.mcp.annotation.McpResourceListChanged;
import org.springaicommunity.mcp.annotation.McpToolListChanged;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientHandlers {

    private final Logger logger = LoggerFactory.getLogger(ClientHandlers.class);

    @McpLogging(clients = "fortuneServer")
    public void handleLogging(McpSchema.LoggingMessageNotification notification) {
        System.out.println("notification="+notification);
        switch (notification.level()) {
            case ERROR:
                logger.error("[MCP] {} - {}", notification.logger(), notification.data());
                break;
            case WARNING:
                logger.warn("[MCP] {} - {}", notification.logger(), notification.data());
                break;
            case INFO:
                logger.info("[MCP] {} - {}", notification.logger(), notification.data());
                break;
            default:
                logger.debug("[MCP] {} - {}", notification.logger(), notification.data());
        }
    }

    @McpProgress(clients = "fortuneServer")
    public void handleProgress(McpSchema.ProgressNotification notification) {
        logger.info("Progress: {}", notification);
    }

    @McpToolListChanged(clients = "fortuneServer")
    public void handleServer1ToolsChanged(List<McpSchema.Tool> tools) {
        logger.info("Server1 tools updated: {} tools available", tools.size());
    }

    @McpResourceListChanged(clients = "fortuneServer")
    public void handleServer1ResourcesChanged(List<McpSchema.Resource> resources) {
        logger.info("Server1 resources updated: {} resources available", resources.size());

    }
}
