# 智能协同云图库

## 项目介绍

基于 Vue 3 + Spring Boot + COS + WebSocket 的 **企业级智能协同云图库平台**。

本项目是一个以"温暖分享，协同制作"为核心理念的图片分享平台。
它不仅能够满足用户们上传、浏览和欣赏优质图片的需求，更能成为他们个人展示独特风格的《图片集》空间；更重要的是，可以多个人协同完成图片的制作。

## 项目架构图

![](https://pic.yupi.icu/1/1732691889100-e562c709-cffa-477d-9329-1dc5ac1d35c8-20241204144304741-20241204145344935-20241204145354234.png)


## 技术选型

### 后端

- MySQL 数据库 + MyBatis-Plus 框架 + MyBatis X 
- Redis 分布式缓存 + Caffeine 本地缓存
- Jsoup 数据抓取
- ⭐️ COS 对象存储
- ⭐️ ShardingSphere 分库分表
- ⭐️ Sa-Token 权限控制
- ⭐️ WebSocket 双向通信
- ⭐️ Disruptor 高性能无锁队列
- ⭐️ JUC 并发和异步编程
- ⭐️ AI 绘图大模型接入
- ⭐️ 多种设计模式的运用
- ⭐️ DDD 领域驱动设计

### 前端

- Vue 3 框架
- Vite 打包工具
- Ant Design Vue 组件库
- Axios 请求库
- Pinia 全局状态管理
- 其他组件：数据可视化、图片编辑等
- ⭐️ 前端工程化：ESLint + Prettier + TypeScript
- ⭐️ OpenAPI 前端代码生成


## 项目预览

该项目主要分为 3 个阶段循序渐进完成~

1）第一阶段，开发公共的图库平台，所有用户都可以在平台公开上传和检索图片素材。实战 Vue 3 + Spring Boot 图片素材网站的快速开发，学习文件存管业务的开发和优化技巧。

> 成果：可用作表情包网站、设计素材网站、壁纸网站等

![](https://cloud-1311088844.cos.ap-beijing.myqcloud.com/public_share/project/%E5%85%AC%E5%85%B1%E5%9B%BE%E5%BA%93.jpg)

管理员可以上传、审核和管理图片，并对系统内的图片进行分析：

![](https://cloud-1311088844.cos.ap-beijing.myqcloud.com/public_share/project/%E5%9B%BE%E7%89%87%E7%AE%A1%E7%90%86.jpg)

2）第二阶段，对项目 C 端功能进行大量扩展。用户可开通私有空间，并对空间图片进行多维检索、扫码分享、批量管理、快速编辑、用量分析。

> 成果：可用作个人网盘、个人相册、作品集等

![](https://cloud-1311088844.cos.ap-beijing.myqcloud.com/public_share/project/%E7%A7%81%E6%9C%89%E7%A9%BA%E9%97%B4.jpg)


3）第三阶段，对项目 B 端功能进行大量扩展。企业可开通团队空间，邀请和管理空间成员，团队内共享图片并**实时协同编辑图片**。

> 成果：可用于提供商业服务，如企业活动相册、企业内部素材库等

![](https://cloud-1311088844.cos.ap-beijing.myqcloud.com/public_share/project/%E6%9D%83%E9%99%90%E6%8E%A7%E5%88%B6.jpg)

使用 websocket 实现实时通信，团队协同编辑

![](https://cloud-1311088844.cos.ap-beijing.myqcloud.com/public_share/project/%E5%8D%8F%E5%90%8C%E7%BC%96%E8%BE%91.jpg)


## 知识点
- 拆解复杂业务，从 0 开始设计实现企业级系统
- 巧用 RBAC 权限模型和框架实现复杂权限控制
- 结合 Redis + Caffeine 构建高性能多级缓存
- 实现文件的高效存储，并通过十几种策略进行优化
- 使用高级数据结构 Disruptor 无锁队列提升并发性能
- 使用 ShardingSphere 实现动态扩容的分库分表
- 使用 WebSocket 多端通信，实现企业级实时协作功能
- 接入 AI 绘图大模型，实现更多高级图片处理能力
- 使用 DDD 架构实现大型企业级项目
