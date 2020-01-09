# 互联网路由路径推断

### 整体逻辑
- 路径正确标准:  
     节点差距（数量、前缀精确度
- 图的层次:  
    部分图/完全图  
    含目标/不含目标  
    有标签/无标签
- 比对思路:  
    子图最短路径(数量多少) -> 
    全图最短路径 -> 
    带标签全图路径 -> 
    真正路径
### 工程框架
- 整体结构和Web端：  
JAVA的Spring Boot框架  
部分参考文档  
[Official Apache Maven documentation](https://maven.apache.org/guides/index.html)  
[Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/maven-plugin/)

- 数据存储  
Neo4j数据库

### 数据来源  
- CAIDA开源数据
