# 本地搭建指南

## 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| Docker Desktop | 最新（用于 MySQL / Redis） |
| HBuilderX | 可选（uni-app 推荐） |

## 一、克隆仓库

```bash
git clone https://github.com/aiyangdie/ecommerce-platform.git
cd ecommerce-platform
```

## 二、配置环境变量（可选）

```bash
cp .env.example .env
```

默认即可对接 `docker-compose` 中的 MySQL / Redis 账号。

## 三、启动基础设施

```bash
docker compose -f docker/docker-compose.yml up -d
```

验证：

- MySQL：`localhost:3306`，库名 `mall`，用户 `mall`
- Redis：`localhost:6379`

初始化 SQL 会在首次启动时自动执行（`docker/init/01-schema.sql`）。

## 四、启动后端 mall-api

```bash
cd mall-api
mvn -q spring-boot:run
```

验证：

```bash
curl http://127.0.0.1:8080/api/health
curl http://127.0.0.1:8080/api/v1/products
```

## 五、启动管理端 mall-admin

```bash
cd mall-admin
npm install
npm run dev
```

浏览器打开 http://127.0.0.1:5173 ，应能看到商品列表示例数据。

## 六、启动 C 端 mall-uni

1. 安装 [HBuilderX](https://www.dcloud.io/hbuilderx.html)
2. 文件 → 导入 → 选择 `mall-uni` 目录
3. 修改 `pages/index/index.vue` 中 `API_BASE` 为你的 API 地址
4. 运行到浏览器 / 微信开发者工具

### 微信公众号

- 使用 **H5 构建产物** 部署到已备案 HTTPS 域名
- 在微信公众平台配置 JS 安全域名、OAuth 域名

### App

- HBuilderX → 发行 → 原生 App 云打包

## 七、常见问题

| 现象 | 处理 |
|------|------|
| 管理端无数据 | 确认 `mall-api` 与 MySQL 已启动 |
| 小程序 request 失败 | 开发阶段可关域名校验；上线需配置合法域名 |
| Maven 下载慢 | 配置国内 mirror |

## 八、下一步开发

按 [电商平台开发文档](./电商平台开发文档.md) 迭代：订单、库存预占、支付回调、用户登录、营销模块等。
