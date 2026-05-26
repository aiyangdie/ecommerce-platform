# 安装指南

## 方式一：一键安装（推荐）

### Windows

1. 安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)
2. 克隆仓库后，在项目根目录执行：

```powershell
.\install.ps1
```

### Linux / macOS

```bash
chmod +x install.sh
./install.sh
```

安装完成后：

| 服务 | 地址 | 说明 |
|------|------|------|
| **H5 商城** | http://127.0.0.1:8082 | 用户购物、下单 |
| **管理后台** | http://127.0.0.1:8081 | 商品/订单管理 |
| **后端 API** | http://127.0.0.1:8080 | REST 接口 |
| **Swagger** | http://127.0.0.1:8080/doc.html | 接口文档 |

**默认账号**

- 管理员：`admin` / `admin123`
- C 端用户：任意 11 位手机号 + 验证码 `123456`

---

## 方式二：Docker Compose 手动

```bash
cp .env.example .env
docker compose -f docker/docker-compose.yml up -d --build
```

---

## 方式三：本地开发（前后端分离调试）

### 1. 仅启动数据库

```bash
docker compose -f docker/docker-compose.yml up -d mysql redis
```

### 2. 后端

```bash
cd mall-api
mvn spring-boot:run
```

### 3. 管理端

```bash
cd mall-admin
npm install
npm run dev
```

### 4. C 端 uni-app

HBuilderX 打开 `mall-uni/`，详见 [mall-uni/README.md](../mall-uni/README.md)。

---

## 架构说明

```
┌──────────┐  ┌──────────┐  ┌──────────┐
│ mall-h5  │  │mall-admin│  │ mall-uni │
│  :8082   │  │  :8081   │  │ 本地开发  │
└────┬─────┘  └────┬─────┘  └────┬─────┘
     │    Nginx 反代 /api       │
     └────────────┬─────────────┘
                  ▼
           ┌─────────────┐
           │  mall-api   │ :8080
           │ Spring Boot │
           └──────┬──────┘
                  │
        ┌─────────┴─────────┐
        ▼                   ▼
   MySQL :3306         Redis :6379
```

各服务独立容器，符合生产级前后端分离设计。

---

## 常用命令

```bash
# 查看日志
docker compose -f docker/docker-compose.yml logs -f api

# 停止
docker compose -f docker/docker-compose.yml down

# 停止并删除数据卷（重置数据库）
docker compose -f docker/docker-compose.yml down -v
```

---

## 环境变量

复制 `.env.example` 为 `.env`，可修改端口与数据库密码：

| 变量 | 默认 |
|------|------|
| API_PORT | 8080 |
| ADMIN_PORT | 8081 |
| H5_PORT | 8082 |
| MYSQL_PASSWORD | mall_pass_2026 |

---

## 故障排查

| 问题 | 解决 |
|------|------|
| 8080 无响应 | `docker logs mall-api`，等待 MySQL 初始化完成（首次约 1 分钟） |
| 管理端登录失败 | 确认 API 正常；默认 admin/admin123 |
| 端口冲突 | 修改 `.env` 中端口后重新 `docker compose up -d` |

更多部署见 [DEPLOY.md](./DEPLOY.md)。
