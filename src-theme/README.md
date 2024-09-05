# LiquidBounce 默认主题

此目录包含使用 [Svelte](https://svelte.dev/) 制作的 LiquidBounce 默认主题的源代码。

## 开发

**此开发设置是临时的，未来将得到改进！**

1. 在您的计算机上安装 [Node.js](https://nodejs.org/en)（最新版本或稳定版本均可）。
2. 使用 `npm install` 安装所需依赖。
3. 将 [host.ts](https://github.com/CCBlueX/LiquidBounce/blob/nextgen/src-theme/src/integration/host.ts) 中的 `IN_DEV` 设置为 `true`。
4. 启动客户端。
5. 运行 `npm run dev` 以启动开发服务器。

请确保不要推送对 `host.ts` 和 `NettyServer.kt` 所做的更改！
