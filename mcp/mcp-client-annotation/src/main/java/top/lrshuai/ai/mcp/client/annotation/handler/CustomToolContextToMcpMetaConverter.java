package top.lrshuai.ai.mcp.client.annotation.handler;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.ToolContextToMcpMetaConverter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义转换器
 * 将给定的ToolContext转换为映射作为MCP工具调用元数据。
 * 默认：ToolContextToMcpMetaConverter.defaultConverter()
 */
@Component
public class CustomToolContextToMcpMetaConverter implements ToolContextToMcpMetaConverter {

    @Override
    public Map<String, Object> convert(ToolContext toolContext) {
        if (toolContext == null || toolContext.getContext() == null) {
            return Map.of();
        }

        // Custom logic to convert tool context to MCP metadata
        Map<String, Object> metadata = new HashMap<>();

        // Example: Add custom prefix to all keys
        for (Map.Entry<String, Object> entry : toolContext.getContext().entrySet()) {
            if (entry.getValue() != null) {
                metadata.put(entry.getKey(), entry.getValue());
            }
        }

        // Example: Add additional metadata
        metadata.put("timestamp", System.currentTimeMillis());
        metadata.put("source", "spring-ai");

        return metadata;
    }
}
