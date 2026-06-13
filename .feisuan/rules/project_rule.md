
# Sky Take-Out 项目开发规范指南

为保证代码质量、可维护性、安全性与可扩展性，请在开发过程中严格遵循以下规范。

## 一、项目基础信息

- **项目名称**：Sky Take-Out (sky-take-out)
- **工作区路径**：`/Users/wangqi/workSpace/code/sky-take-out`
- **代码作者**：wangqi
- **操作系统**：Mac OS X
- **构建工具**：Maven
- **JDK 版本**：17.0.14
- **Spring Boot 版本**：2.7.3 (Parent)

## 二、多模块目录结构

项目采用多模块结构，请严格遵循以下目录树进行代码组织：

```text
sky-take-out
├── sky-common          # 通用工具与常量模块
│   └── src/main/java/com/sky
│       ├── constant    # 常量定义
│       ├── context     # 上下文（如用户上下文）
│       ├── enumeration # 枚举类
│       ├── exception   # 自定义异常
│       ├── json        # JSON 处理工具
│       ├── properties  # 配置属性类
│       ├── result      # 统一返回结果封装
│       └── utils       # 通用工具类
├── sky-pojo            # 数据传输对象模块
│   └── src/main/java/com/sky
│       ├── dto         # 数据传输对象 (Request/Command)
│       ├── entity      # 数据库实体对象 (MyBatis Entity)
│       └── vo          # 视图展示对象 (Response)
└── sky-server          # 业务服务模块
    └── src/main/java/com/sky
        ├── config      # 配置类 (Web, Redis, MyBatis 等)
        ├── controller  # 控制器
        │   └── admin   # 后台管理接口
        ├── handler     # 全局异常处理器
        ├── interceptor # 拦截器
        ├── mapper      # 数据访问层接口
        └── service     # 业务逻辑层
            └── impl    # 业务逻辑实现类
```

## 三、技术栈与依赖规范

### 1. 核心框架
- **主框架**：Spring Boot 2.7.3
- **持久层**：MyBatis (mybatis-spring-boot-starter 2.2.0) + MyBatis-Plus (若使用)
- **数据库连接池**：Druid (1.2.1)
- **ORM 映射**：MyBatis (启用驼峰命名 `map-underscore-to-camel-case: true`)

### 2. 数据存储
- **关系型数据库**：MySQL
- **缓存**：Redis (spring-boot-starter-data-redis)

### 3. 第三方集成
- **API 文档**：Knife4j (3.0.2)
- **JSON 处理**：Fastjson (1.2.76) 及 Jackson
- **对象转换**：Lombok (1.18.20)
- **分页插件**：PageHelper (1.3.0)
- **文件存储**：阿里云 OSS (aliyun-sdk-oss 3.10.2)
- **Excel 处理**：Apache POI (3.16)
- **即时通讯**：WebSocket
- **支付集成**：微信支付 SDK (wechatpay-apiv3)
- **JWT 认证**：JJWT (0.9.1)

### 4. 通用依赖规则
- **Lombok**：全局使用 `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Slf4j`。
- **包扫描**：MyBatis Mapper 扫描包为 `com.sky.mapper`，Entity 别名包为 `com.sky.entity`。
- **Mapper XML 位置**：`classpath:mapper/*.xml`

## 四、分层架构规范

| 层级        | 职责说明                         | 开发约束与注意事项                                               |
|-------------|----------------------------------|----------------------------------------------------------------|
| **Controller** | 处理 HTTP 请求，调用 Service     | 仅负责参数接收、校验和响应封装；严禁包含业务逻辑               |
| **Service**    | 实现核心业务逻辑                 | 接口定义在 `service` 包，实现类在 `service.impl` 包            |
| **Mapper**     | 数据库 CRUD 操作                 | 继承 `BaseMapper` 或自定义 XML；禁止在 Java 代码中拼接 SQL     |
| **POJO**       | 数据传输载体                     | 严格区分 DTO (入参), VO (出参), Entity (存储)                  |

### 接口与实现分离
- 所有 Service 接口需定义在 `com.sky.service` 包下。
- 所有 Service 实现类需放在 `com.sky.service.impl` 包下，类名以 `Impl` 结尾。

## 五、安全与性能规范

### 1. 认证与授权
- 使用 **JWT** 进行无状态认证。
- 通过 **Interceptor** 拦截请求，解析 Token 并获取用户 ID，存入 `ThreadLocal` (Context)。
- 敏感接口需校验 Token 有效性及权限。

### 2. 输入校验
- 使用 JSR-303 校验注解（`javax.validation.constraints.*`）。
- 禁止手动拼接 SQL 字符串，防止 SQL 注入。
- 所有 Controller 入参建议使用 `@Validated` 或 `@Valid` 进行校验。

### 3. 缓存规范
- 热点数据（如分类、菜品信息）优先使用 **Redis** 缓存。
- 注意缓存穿透、击穿、雪崩的防护措施。
- 更新数据库时，务必同步更新或删除对应缓存。

### 4. 事务管理
- `@Transactional` 注解仅用于 **Service 层**方法。
- 避免在事务中调用远程服务或进行耗时 IO 操作。

## 六、代码风格规范

### 1. 命名规范

| 类型       | 命名方式             | 示例                  | 说明                     |
|------------|----------------------|-----------------------|--------------------------|
| 类名       | UpperCamelCase       | `DishServiceImpl`     |                          |
| 方法/变量  | lowerCamelCase       | `saveDish()`          |                          |
| 常量       | UPPER_SNAKE_CASE     | `USER_LOGIN_KEY`      |                          |
| 包名       | 全小写               | `com.sky.controller`  |                          |

### 2. POJO 命名后缀规范

| 后缀 | 包路径示例             | 用途说明                     | 示例         |
|------|------------------------|------------------------------|--------------|
| **DTO** | `com.sky.dto`          | 数据传输对象 (Controller -> Service) | `DishDTO`    |
| **VO**  | `com.sky.vo`           | 视图展示对象 (Service -> Controller) | `DishVO`     |
| **Entity** | `com.sky.entity` (在 sky-pojo) | 数据库实体对象 (Mapper <-> DB) | `Dish`       |

### 3. 注释规范
- **第一语言**：中文。
- **类注释**：必须包含类的作用描述、作者 (`@author wangqi`)。
- **方法注释**：公共 API 方法必须包含 Javadoc，说明参数、返回值及异常。
- **关键逻辑**：复杂业务逻辑需添加行内注释解释“为什么”这样做。

### 4. 实体类简化工具
- 强制使用 Lombok 注解：
  - `@Data`：生成 getter/setter/toString/equals/hashCode
  - `@Slf4j`：生成日志对象
  - `@NoArgsConstructor` / `@AllArgsConstructor`：按需使用

## 七、扩展性与日志规范

### 1. 配置管理
- 敏感配置（数据库密码、OSS Key、JWT Secret）必须通过环境变量或外部配置文件注入，禁止硬编码。
- 使用 `@ConfigurationProperties` 绑定配置属性类（位于 `sky-common/properties`）。

### 2. 日志记录
- 使用 `@Slf4j` 注解注入日志对象。
- 日志级别规范：
  - `debug`：Mapper 层调试信息
  - `info`：Service 层关键流程、Controller 层请求入口
  - `error`：异常堆栈、关键错误
- 禁止使用 `System.out.println`。

### 3. 统一返回结果
- 所有接口必须返回统一的结构体（如 `R<T>`），包含 `code`, `msg`, `data`。
- 全局异常处理器 (`GlobalExceptionHandler`) 需捕获所有异常并转换为统一返回格式。

## 八、编码原则总结

| 原则       | 说明                                       |
|------------|--------------------------------------------|
| **SOLID**  | 高内聚、低耦合，增强可维护性与可扩展性     |
| **DRY**    | 避免重复代码，提取公共工具类到 `sky-common` |
| **KISS**   | 保持代码简洁易懂，避免过度设计             |
| **YAGNI**  | 不实现当前不需要的功能                     |
| **OWASP**  | 防范常见安全漏洞，如 SQL 注入、XSS、敏感信息泄露 |
