package top.lrshuai.ai.mcp.server.annotation.tools;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpProgressToken;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 趣味工具
 */
@Slf4j
@Service
public class FortuneTellingTools {

    private final Random random = new Random();

    @McpTool(name = "daily_fortune", description = "测算今日运势")
    public String getDailyFortune(
            McpSyncServerExchange exchange,
            @McpProgressToken String progressToken) {

        log.info("token={}", progressToken);

        // 发送日志通知
        exchange.loggingNotification(McpSchema.LoggingMessageNotification.builder()
                .level(McpSchema.LoggingLevel.INFO)
                .data("工具调用token为: " + progressToken)
                .build());

        progressToken = StrUtil.nullToDefault(progressToken, IdUtil.fastSimpleUUID());

        String[] luckLevels = {"大吉", "中吉", "小吉", "平", "凶", "大凶"};
        String[][] luckDescriptions = {
                {"诸事顺利，心想事成"}, {"平稳发展，小有收获"}, {"稍有波折，但无大碍"},
                {"平平淡淡才是真"}, {"今日不宜做重大决定"}, {"诸事不宜，低调行事"}
        };

        int luckIndex = random.nextInt(luckLevels.length);

        // 发送进度通知
        exchange.progressNotification(new McpSchema.ProgressNotification(
                progressToken, 0.5, 1.0, "Processing..."));

        int overallScore = 60 + luckIndex * 8 + random.nextInt(20);
        String[] emojis = {"🎉", "😊", "🙂", "😐", "😟", "💀"};

        exchange.progressNotification(new McpSchema.ProgressNotification(
                progressToken, 0.9, 1.0, "准备返回数据"));

        return """
                🔮 您今日运势报告
                
                📊 综合评分：%d/100
                ✨ 运势等级：%s
                💫 运势解读：%s
                
                %s 保持好心情，明天会更好！
                """.formatted(overallScore, luckLevels[luckIndex],
                luckDescriptions[luckIndex][0], emojis[luckIndex]);
    }

}
