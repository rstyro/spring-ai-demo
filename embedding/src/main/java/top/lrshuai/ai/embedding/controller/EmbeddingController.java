package top.lrshuai.ai.embedding.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/embedding")
@RestController
public class EmbeddingController {

    @Resource
    private EmbeddingModel embeddingModel;

    /**
     * 文本转向量
     * @param message 文本
     * @return 向量相关结果集
     */
    @GetMapping("/textToVector")
    public Map<String,Object> textToVector(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
        return Map.of("embedding", embeddingResponse);
    }

    /**
     * 文本转向量,可以自定义构建请求配置
     * @param message 文本
     * @return 向量相关结果集
     */
    @GetMapping("/textToVector2")
    public Map<String,Object> textToVector2(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        EmbeddingOptions embeddingOptions = OllamaEmbeddingOptions.builder()
                .model("embeddinggemma")
                .truncate(false)
                .build();
        EmbeddingRequest embeddingRequest = new EmbeddingRequest(List.of("Hello World", message),embeddingOptions);
        EmbeddingResponse embeddingResponse = embeddingModel.call(embeddingRequest);
        return Map.of("embedding", embeddingResponse);
    }
}
