# spring-ai-demo
Spring AI 例子


## 环境：

- JDK17
- SpringBoot3.4.10
- SpringAI 1.1.0-M3


## 项目目录

- **commons** 公共包，通用接口返回和全局异常捕获，可不要
- **chat** 聊天模块
    - **chat-ollama** 基于Ollama的聊天demo
- **embedding** 嵌入模型-文本转向量
- **vector** 向量存储库相关
    - **vector-pg** 向量存贮到pgsql
    - **vector-redis** 向量存贮到redis-stack的demo
- **rag**  RAG相关demo
    - **rag-simple**  这个RAG简单示例与实现多流程实例
- **mcp** MCP相关demo
    - **mcp-client-annotation** mcp客户端，新注解版本
    - **mcp-server-annotation** mcp服务端，新注解版本
