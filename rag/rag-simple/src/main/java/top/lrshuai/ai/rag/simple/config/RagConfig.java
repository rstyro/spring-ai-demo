package top.lrshuai.ai.rag.simple.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class RagConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder clientBuilder) {
        return clientBuilder
                .defaultSystem("回答以下问题结果都用中文回答,如果你不知道答案，请回复“我不知道” ")
//                .defaultAdvisors(MessageChatMemoryAdvisor
//                        .builder(MessageWindowChatMemory.builder().maxMessages(3).build())
//                        .build())
                // 打印日志
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    /**
     * SimpleVectorStore 基于内存的向量数据库，用于存储文档的向量表示（支持相似性搜索）
     * @param embeddingModel 将文本转换为向量的模型
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        // metadata(元数据)用途 ：可用于后续过滤或排序（例如按年份筛选文档）
        List<Document> documents = List.of(
                new Document("公司内部黑话如：'喝奶茶吗' 的真实意思是 '要摸鱼吗'，回复 '加珍珠' 表示 '可以，半小时后开始'",
                        Map.of("category", "职场", "热度", "高")),
                new Document("相亲市场黑话如：'有上进心'=现在没钱，'会过日子'=抠门，'颜值中上'=靠美颜，'微胖'=真胖",
                        Map.of("category", "婚恋市场", "场景", "相亲")),
                new Document("面试黑话如：'工资4-8k'往往意味着就是4k；'能接受加班吗'的潜台词可能是需要无偿加班；'我们是一家创业公司'暗示可能需要一人分担多职；'有消息我们会通知你'通常表示应聘者可能是备选。",
                        Map.of("category", "职场", "scene", "面试", "热度", "高")),
                new Document("股市黑话如：'价值投资'=被套牢了，'技术性调整'=跌得妈都不认，'震荡行情'=上蹿下跳心脏受不了",
                        Map.of("category", "金融", "市场", "股市")),
                new Document("医生黑话如：'观察观察'=问题不大，'进一步检查'=可能有问题，'加强营养'=多吃点好的",
                        Map.of("category", "医疗", "角色", "医生")),
                new Document("直播黑话如：'家人'=韭菜，'宠粉价'=还是赚你一半，'最后一批'=仓库堆满了",
                        Map.of("category", "直播", "平台", "带货")),
                new Document("""
                        朋友黑话如下：
                        改善伙食=今天不吃食堂了
                        小酌一杯=不醉不归
                        857=夜店蹦迪到凌晨
                        紧急集合=有人请客吃饭
                        撤退信号=摸耳朵表示想走了
                        """,
                        Map.of("category", "朋友黑话", "主题", "吃喝玩乐")),
                new Document("""
                        游戏黑话如下：
                        非洲人=抽卡永远抽不到SSR的非酋玩家
                        欧洲人=随便一抽就是SSR的欧皇玩家
                        肝帝=可以连续熬夜打游戏的狠人
                        氪金大佬=在游戏里充钱不眨眼的神豪
                        搬砖=重复刷副本赚游戏币的枯燥操作
                        坐牢=连续打不过同一个副本的悲惨经历
                        刮痧=伤害太低像在给BOSS按摩
                        下水道=当前版本最弱的职业或角色
                        版本答案=当前版本最强的职业或角色
                        白给=毫无价值地送死
                        666=玩得真厉害，大佬""",
                        Map.of("category", "游戏术语", "游戏类型", "通用"))
        );
        // 加载文档知识库
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }

}
