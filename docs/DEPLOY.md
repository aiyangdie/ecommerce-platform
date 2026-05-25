# 部署与 GitHub CI/CD

## 部署策略说明

本项目采用 **前后端 + 数据库分离**，与 GitHub 能力的对应关系如下：

| 组件 | 能否直接用 GitHub Pages | 推荐部署 |
|------|-------------------------|----------|
| mall-admin（静态） | ✅ 可以 | GitHub Actions → Pages |
| mall-uni H5 构建产物 | ✅ 可以 | Pages 子路径或独立域名 |
| mall-api（Java） | ❌ 不行 | 云服务器 / Railway / Render / 阿里云 |
| MySQL / Redis | ❌ 不行 | 云数据库 / 服务器 Docker |

**结论**：GitHub 负责 **代码托管 + CI 构建 + 管理端静态站演示**；数据库与 API 需在云平台或自有服务器运行（这是正规电商架构，而非缺陷）。

---

## 一、GitHub 仓库与 CI

推送 `main` 分支后自动触发：

1. **ci.yml** — 编译 `mall-api`、构建 `mall-admin`
2. **deploy-admin.yml** — 将管理端部署到 **GitHub Pages**

### 启用 GitHub Pages

1. 仓库 **Settings → Pages**
2. Source 选择 **GitHub Actions**
3. 首次 workflow 成功后访问：  
   `https://aiyangdie.github.io/ecommerce-platform/`

> 演示环境 API 需单独部署后，在 workflow 或 Pages 环境变量中配置 `VITE_API_BASE_URL` 指向公网 API。

---

## 二、后端 API 部署示例

### 方案 A：云服务器 + Docker（推荐生产）

```bash
# 服务器上
git clone https://github.com/aiyangdie/ecommerce-platform.git
cd ecommerce-platform
docker compose -f docker/docker-compose.yml up -d
cd mall-api && mvn -DskipTests package
java -jar target/mall-api-1.0.0-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://127.0.0.1:3306/mall \
  --spring.datasource.username=mall \
  --spring.datasource.password=YOUR_PASSWORD
```

前置 Nginx 反向代理 HTTPS，暴露 `https://api.yourdomain.com`。

### 方案 B：Railway / Render（快速演示）

1. 新建 Web Service，连接本仓库，Root Directory 设为 `mall-api`
2. 添加 MySQL、Redis 插件或外部连接串
3. 环境变量：`MYSQL_*`、`REDIS_*`
4. Build：`mvn -DskipTests package`
5. Start：`java -jar target/*.jar`

---

## 三、管理端对接公网 API

构建时注入 API 地址：

```bash
cd mall-admin
VITE_API_BASE_URL=https://api.yourdomain.com npm run build
```

GitHub Actions 中可在 `deploy-admin.yml` 的 `env` 段配置 `VITE_API_BASE_URL`（使用 Repository Variables，勿写 Token）。

---

## 四、uni-app 发布

| 端 | 发布位置 |
|----|----------|
| H5 | Nginx / OSS / GitHub Pages |
| 微信小程序 | 微信公众平台上传审核 |
| 公众号 | 服务号菜单指向 H5 域名 |
| App | 应用商店 + 软著 |

---

## 五、Secrets 管理（重要）

- **切勿**将 GitHub Token、数据库密码、支付密钥提交到仓库
- 使用 GitHub **Settings → Secrets and variables → Actions**
- 本地使用 `.env`（已在 `.gitignore` 排除）

若 Token 曾在聊天或代码中泄露，请立即在 GitHub **撤销并重新生成**。

---

## 六、对外展示建议（作品集）

README 中保留：

- 架构图与目录说明
- GitHub Actions 徽章
- Pages 演示链接（管理端）
- API 文档截图 / Swagger 地址
- 文档链接证明完整电商设计能力

可选：录制 30 秒录屏（本地 Docker + 三端联调）放在 README。
