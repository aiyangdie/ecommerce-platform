# ecommerce-platform

[![CI](https://github.com/aiyangdie/ecommerce-platform/actions/workflows/ci.yml/badge.svg)](https://github.com/aiyangdie/ecommerce-platform/actions/workflows/ci.yml)
[![Pages](https://github.com/aiyangdie/ecommerce-platform/actions/workflows/deploy-admin.yml/badge.svg)](https://github.com/aiyangdie/ecommerce-platform/actions/workflows/deploy-admin.yml)

> 前后端分离 · 数据库独立 · 开源成熟框架 · 支持 H5 / 微信小程序 / 微信公众号 / App

高品质电商平台技术底座，用于展示完整的电商系统架构与工程化能力（Spring Boot 3、Vue 3、uni-app、MySQL、Redis）。

## 架构一览

```
┌─────────────┐  ┌─────────────┐  ┌──────────────────────────────┐
│  mall-uni   │  │ mall-admin  │  │         mall-api             │
│  uni-app    │  │ Vue3+Vite   │  │ Spring Boot 3 + MyBatis-Plus │
│  C端四端    │  │ 管理后台     │  │         REST API             │
└──────┬──────┘  └──────┬──────┘  └──────────────┬───────────────┘
       │                │                        │
       └────────────────┴────────────────────────┘
                                │
                    ┌───────────┴───────────┐
                    │  MySQL 8   Redis 7    │
                    │  (Docker 独立容器)     │
                    └───────────────────────┘
```

| 目录 | 技术栈 | 说明 |
|------|--------|------|
| [`mall-api/`](mall-api/) | Spring Boot 3、MyBatis-Plus、Knife4j | 后端 API，端口 `8080` |
| [`mall-admin/`](mall-admin/) | Vue 3、Element Plus、Vite | 运营管理后台 |
| [`mall-uni/`](mall-uni/) | uni-app、Vue 3 | H5 / 小程序 / 公众号 / App |
| [`docker/`](docker/) | Docker Compose | MySQL + Redis，与前后端分离部署 |
| [`docs/`](docs/) | 设计文档、搭建与部署指南 | |

## 快速开始

### 1. 启动数据库（Docker）

```bash
docker compose -f docker/docker-compose.yml up -d
```

### 2. 启动后端

```bash
cd mall-api
mvn spring-boot:run
```

接口文档：http://127.0.0.1:8080/doc.html

### 3. 启动管理端

```bash
cd mall-admin
npm install
npm run dev
```

访问：http://127.0.0.1:5173

### 4. C 端（uni-app）

使用 HBuilderX 打开 `mall-uni/`，详见 [mall-uni/README.md](mall-uni/README.md)。

更完整步骤见 **[docs/SETUP.md](docs/SETUP.md)**。

## 在线演示（GitHub Pages）

| 页面 | 地址 |
|------|------|
| **门户首页** | https://aiyangdie.github.io/ecommerce-platform/ |
| **管理后台演示** | https://aiyangdie.github.io/ecommerce-platform/admin/ |

首次启用请在仓库 **Settings → Pages → Build and deployment → Source** 选择 **GitHub Actions**，然后到 **Actions** 运行 **Deploy Online (GitHub Pages)**（推送 `main` 会自动触发）。

| 组件 | 部署方式 |
|------|----------|
| **门户 + 管理端** | GitHub Actions → Pages（含在线演示数据） |
| **后端 API** | 云主机 / Railway / Render（见 [docs/DEPLOY.md](docs/DEPLOY.md)） |
| **MySQL / Redis** | Docker 或云数据库 |

> 私有仓库需 GitHub Pro 才能用 Pages；**建议改为 Public** 以便他人访问在线演示。

## 文档

- [电商平台开发文档](docs/电商平台开发文档.md) — 业务域、高并发、技术选型
- [本地搭建指南](docs/SETUP.md)
- [部署与 GitHub CI/CD](docs/DEPLOY.md)

## 许可证

本项目为演示与学习用途的自有实现；商用请自行完善支付、合规与风控模块。

---

**作者**：[@aiyangdie](https://github.com/aiyangdie)
