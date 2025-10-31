package top.lrshuai.ai.vector.pg.controller;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.lrshuai.ai.common.resp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/vectorPg")
public class DemoController {

    @Resource
    private PgVectorStore vectorStore;

    @SneakyThrows
    @GetMapping("/add")
    public R add() {
        log.info("开始添加数据");
        List<Document> documents = new ArrayList<>(List.of(
                new Document("白羊座：本月事业运旺盛，适合开拓新项目，财运在月中旬会有意外惊喜", Map.of("星座", "白羊座")),
                new Document("金牛座：感情运势上升，单身者有机会遇到心仪对象，投资需谨慎", Map.of("星座", "金牛座")),
                new Document("双子座：学习能力增强，适合考取证书，健康方面注意休息", Map.of("星座", "双子座")),
                new Document("巨蟹座：家庭关系和谐，可能有亲友来访，工作上有贵人相助", Map.of("星座", "巨蟹座")),
                new Document("狮子座：财运亨通，偏财运佳，但要注意控制消费欲望", Map.of("星座", "狮子座")),
                new Document("处女座：工作压力较大，但成果显著，感情需要更多沟通", Map.of("星座", "处女座")),
                new Document("天秤座：社交活跃，认识新朋友的机会多，旅行运佳", Map.of("星座", "天秤座")),
                new Document("天蝎座：直觉敏锐，适合做重要决策，健康方面注意饮食", Map.of("星座", "天蝎座")),
                new Document("射手座：冒险精神旺盛，适合尝试新事物，财运平稳", Map.of("星座", "射手座")),
                new Document("摩羯座：事业稳步上升，领导认可度提高，感情需要主动", Map.of("星座", "摩羯座")),
                new Document("水瓶座：创意灵感爆发，适合艺术创作，注意电子产品维护", Map.of("星座", "水瓶座")),
                new Document("双鱼座：人际关系和谐，团队合作顺利，财运下旬转好", Map.of("星座", "双鱼座"))
        ));
        // 检查并去重
        List<Document> filteredDocuments = filterDuplicateDocuments(documents);

        if (!filteredDocuments.isEmpty()) {
            vectorStore.add(filteredDocuments);
            log.info("知识库添加成功，新增文档数量={}", filteredDocuments.size());
        } else {
            log.info("没有需要添加的新文档");
        }
        return R.ok(filteredDocuments.size());
    }

    private List<Document> filterDuplicateDocuments(List<Document> newDocuments) {
        List<Document> filtered = new ArrayList<>();
        for (Document newDoc : newDocuments) {
            // 搜索相似文档
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(newDoc.getText())
                    .topK(1)
                    .similarityThreshold(0.95) // 设置相似度阈值
                    .build();
            List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
            // 如果没有高度相似的文档，则添加
            if (similarDocs.isEmpty()) {
                filtered.add(newDoc);
            } else {
                log.info("跳过重复文档: {}", newDoc.getText().substring(0, Math.min(20, newDoc.getText().length())));
            }
        }
        return filtered;
    }

    /**
     * 相似性搜索
     *
     * @param query 检索词
     * @param topK  返回的相似文档数量
     */
    @GetMapping("/search")
    public List<Document> search(@RequestParam(defaultValue = "电影台词") String query, @RequestParam(defaultValue = "3") Integer topK) {
        log.info("执行相似性搜索，query={},topK={}", query, topK);
        SearchRequest searchRequest = SearchRequest.builder().query(query).topK(topK).build();
        return vectorStore.similaritySearch(searchRequest);
    }

    /**
     * 删除文档
     * @param id
     * @return
     */
    @GetMapping("/del")
    public R del(String id) {
        vectorStore.delete(List.of(id));
        return R.ok();
    }

}
