package top.lrshuai.ai.rag.simple.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;


@RestController
@RequestMapping("/rag")
public class RagDemoController {

    private Logger log = LoggerFactory.getLogger(RagDemoController.class);

    private static final String DEFAULT_PROMPT = "游戏都有哪些黑话";

    String CUSTOM_PROMPT_TEMPLATE = """
            [提供的上下文信息如下]
            ---------------------
            {context}
            ---------------------
            [回答要求]
            请根据以上提供的上下文信息来回答问题。请严格依据上下文，如果上下文中的信息不足以回答问题，请直接说明“根据现有信息无法完整回答此问题”。
            
            用户问题：{query}
            回答：
            """;

    @Resource
    private VectorStore simpleVectorStore;

    @Resource
    private ChatClient chatClient;

    @Resource
    private ChatClient.Builder chatClientBuilder;

    /**
     * 获取相关知识库数据进行问答。
     */
    @GetMapping("/simple")
    public Flux<String> simpleRag(HttpServletResponse response, @RequestParam(value = "prompt", defaultValue = DEFAULT_PROMPT) String prompt) {
        response.setCharacterEncoding("UTF-8");
        // 只需要配置默认的向量存储库，其他流程走默认的
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                // 从向量存储中检索文档
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(simpleVectorStore).build())
                .build();
        return chatClient.prompt(prompt)
                .advisors(retrievalAugmentationAdvisor)
                .stream()
                .content();
    }


    /**
     * 获取相关知识库数据进行问答。
     */
    @GetMapping("/custom")
    public Flux<String> chatRagAdvisor(HttpServletResponse response, @RequestParam(value = "prompt", defaultValue = DEFAULT_PROMPT) String prompt) {
        response.setCharacterEncoding("UTF-8");
        log.info("开始RAG增加模式");

        // 1、TranslationQueryTransformer多语言检索 将中文查询翻译为向量库支持的语言。也可以使用RewriteQueryTransformer让大模型重写查询
        TranslationQueryTransformer translationQueryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .targetLanguage("English")
                .build();

        // 2、查询变体：生成多个与原始查询语义相关的新查询 ，以提高检索的覆盖率和准确性。默认3个
        MultiQueryExpander multiQueryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(5)
                .build();

        // 3、向量检索
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(simpleVectorStore)
                .topK(3)
                .build();

        // 4、文档合并，组合基于多个查询和多个数据检索到的文档
        ConcatenationDocumentJoiner concatenationDocumentJoiner = new ConcatenationDocumentJoiner();

        // 5、向量检索后，处理上面合并得到的文档数据
        DocumentPostProcessor documentPostProcessor = new DocumentPostProcessor() {
            @Override
            public List<Document> process(Query query, List<Document> documents) {
                log.info("DocumentPostProcessor document size: {}", documents.size());
                // 保留相识度检索分数最高的 3条
                List<Document> topK = documents.stream()
                        .sorted((d1, d2) -> Double.compare(d2.getScore(), d1.getScore()))
                        .limit(3)
                        .toList();
                return topK;
            }
        };

        // 6、上下文增强生成，这里会修改prompt 上下文 {context}插入进去，可以看默认的模板：DEFAULT_PROMPT_TEMPLATE
        ContextualQueryAugmenter contextualQueryAugmenter = ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .promptTemplate(new PromptTemplate(CUSTOM_PROMPT_TEMPLATE))
                .build();

        // RetrievalAugmentationAdvisor 提供完整的 RAG 流程框架，支持高度定制化和模块化组合
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                // 转为英文
                .queryTransformers(translationQueryTransformer)
                // 查询变体扩展
                .queryExpander(multiQueryExpander)
                // 从向量存储中检索文档
                .documentRetriever(documentRetriever)
                // 将多个查询检索得到的多个文档进行合并
                .documentJoiner(concatenationDocumentJoiner)
                // 对检索到的文档进行处理
                .documentPostProcessors(documentPostProcessor)
                // 对生成的查询进行上下文增强
                .queryAugmenter(contextualQueryAugmenter)
                .build();
        return chatClient.prompt(prompt)
                .advisors(retrievalAugmentationAdvisor)
                .stream()
                .content();
    }

    /**
     * 相似性搜索
     *
     * @param query 检索词
     * @param topK  返回的相似文档数量
     */
    @GetMapping("/search")
    public List<Document> search(@RequestParam(defaultValue = "黑话") String query, @RequestParam(defaultValue = "3") Integer topK) {
        log.info("执行相似性搜索，query={},topK={}", query, topK);
        SearchRequest searchRequest = SearchRequest.builder().query(query).topK(topK).build();
        return simpleVectorStore.similaritySearch(searchRequest);
    }

}
