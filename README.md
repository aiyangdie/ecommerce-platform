# ecommerce-platform

[![CI](https://github.com/aiyangdie/ecommerce-platform/actions/workflows/ci.yml/badge.svg)](https://github.com/aiyangdie/ecommerce-platform/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> **开源可部署 · 前后端分离 · 一键安装 · 完整购物流程**

Spring Boot 3 + Vue 3 + MySQL + Redis + Docker，支持 H5 商城、管理后台、uni-app 四端扩展。

## 在线演示

| 页面 | 地址 |
|------|------|
| 项目门户 | https://aiyangdie.github.io/ecommerce-platform/ |
| 管理端静态演示 | https://aiyangdie.github.io/ecommerce-platform/admin/ |

**完整功能**请本地/Docker 一键安装（见下方）。

## 一键安装

```powershell
# Windows（需 Docker Desktop）
.\install.ps1
```

```bash
# Linux / macOS
chmod +x install.sh && ./install.sh
```

| 服务 | 地址 |
|------|------|
| H5 商城 | http://127.0.0.1:8082 |
| 管理后台 | http://127.0.0.1:8081 |
| API | http://127.0.0.1:8080 |
| 文档 | http://127.0.0.1:8080/doc.html |

- 管理员：`admin` / `admin123`
- 用户：手机号 + 验证码 `123456`

详细说明：[docs/INSTALL.md](docs/INSTALL.md)

## 功能清单

- [x] 用户手机号登录（演示验证码）
- [x] 商品浏览、SKU、库存
- [x] 购物车、下单、模拟支付
- [x] 订单状态流转（待支付→待发货→待收货→完成）
- [x] 管理端：商品 CRUD、上下架、发货
- [x] Docker 分离部署：MySQL / Redis / API / Admin / H5
- [x] 一键安装脚本 `install.ps1` / `install.sh`
- [ ] 微信/支付宝真实支付（可扩展）
- [ ] uni-app 小程序/App 打包（见 mall-uni）

## 项目结构

```
├── mall-api/       # 后端 Spring Boot 3 + MyBatis-Plus
├── mall-admin/     # 管理端 Vue 3 + Element Plus
├── mall-uni/       # C端 uni-app（H5/小程序/App）
├── docker/         # Docker Compose + Dockerfile
├── install.ps1     # Windows 一键安装
├── install.sh      # Linux/macOS 一键安装
└── docs/           # 设计文档与安装指南
```

## 架构

```
H5(:8082) ──┐
Admin(:8081)├──► API(:8080) ──► MySQL + Redis
uni-app     ──┘
```

## 文档

- [安装指南](docs/INSTALL.md)
- [本地开发](docs/SETUP.md)
- [部署与 CI/CD](docs/DEPLOY.md)
- [电商设计文档](docs/电商平台开发文档.md)
- [搭建规划](docs/搭建规划.md)

## 开源协议

[MIT](LICENSE) — 可自由使用、修改与商用（支付与合规模块请自行完善）。

**作者**：[@aiyangdie](https://github.com/aiyangdie)
