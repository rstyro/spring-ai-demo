package top.lrshuai.ai.vector.redis.controller;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.lrshuai.ai.common.exception.ServiceException;
import top.lrshuai.ai.common.resp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/vectorRedis")
public class DemoController {

    @Resource
    private RedisVectorStore vectorStore;

    @Value("classpath:data/movie.txt")
    private org.springframework.core.io.Resource file;

    @SneakyThrows
    @GetMapping("/add")
    public R add() {
        log.info("开始添加数据");
        // 验证文件是否存在
        if (!file.exists()) {
            throw new ServiceException("文件不存在！路径：" + file.getURI());
        }
        // 读取文档
        TextReader textReader = new TextReader(file);
        List<Document> textDocumentList = textReader.get();

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
        documents.addAll(textDocumentList);
        vectorStore.add(documents);
        log.info("知识库添加成功，size={}", documents.size());
        return R.ok();
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

}
