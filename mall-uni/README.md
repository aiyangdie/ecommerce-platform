# mall-uni — C 端（uni-app）

一套代码编译为：

- **H5**（含微信公众号内网页）
- **微信小程序**
- **Android / iOS App**

## 推荐开发方式

使用 [HBuilderX](https://www.dcloud.io/hbuilderx.html) 打开本目录：

1. 运行 → 运行到浏览器（H5）
2. 运行 → 运行到小程序模拟器（微信）
3. 发行 → 原生 App 云打包

## 配置 API 地址

修改 `pages/index/index.vue` 中 `API_BASE`，或使用 `utils/config.ts`（Vite 模式）。

微信小程序需在后台配置 **request 合法域名**（生产环境 HTTPS）。

## npm 方式（可选）

```bash
npm install
npm run dev:h5
```

需本机已安装 uni-app 相关 CLI 依赖。
