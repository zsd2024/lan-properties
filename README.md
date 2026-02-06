<h1 align="center">
  <img src=".github/logo.png" alt="lan properties 缩略图" width="300">
  LAN Properties 汉化版
</h1>

<div align="center">
**注意以下链接为原始版本连接**
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/lan-properties)
[![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://curseforge.com/minecraft/mc-mods/lan-properties)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/xhyrom/lan-properties)

![fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg)
![forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg)
![neoforge](https://raw.githubusercontent.com/Hyperbole-Devs/vectors/8494ec1ac495cfb481dc7e458356325510933eb0/assets/cozy/supported/neoforge_vector.svg)
![quilt](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/quilt_vector.svg)

[![ko-fi](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/kofi-singular-alt_vector.svg)](https://ko-fi.com/xhyrom)

</div>

**LAN Properties** 扩展了原版的 “Open to LAN” 界面，使其能够像专用服务器上的 server.properties 一样，完全控制所有服务器设置。

## 什么是 LAN Properties

LAN Properties 是一个模组，允许你在将世界开放为局域网时访问所有 server.properties 设置，就像在专用服务器上一样。

## 如何使用

只需安装该模组，然后像平常一样点击 “Open to LAN”。界面中会出现一个配置按钮，允许你自定义所有服务器设置。

### 混合模式

LAN Properties 引入了一个额外的属性，称为 **`hybrid-mode`**。

当 `online-mode` 被禁用但 `hybrid-mode` 被启用时，服务器会以一种“兼顾两者”的方式尝试对玩家进行认证：

- **如果存在与玩家用户名对应的账号** → 服务器会从 Mojang 的会话服务器获取官方 UUID。
- **如果会话服务器无法访问或该用户名不属于有效账号** → 服务器会回退使用离线 UUID。

这样可以确保正版（在线）玩家始终保留其正确的 UUID，同时在需要时仍允许离线玩家加入。
