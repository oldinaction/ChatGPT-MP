## ChatGPT-MP(基于ChatGPT实现的微信小程序，适配H5/WEB端)

包含前后台，技术栈：JDK8 + SpringBoot + Vue2 + Uniapp + Mysql

小程序演示地址

![One能抽屉](https://cdn7.aezo.cn/common/qrcode/one_qrcode.jpg)

## 包含功能

- [x] ChatGPT聊天
- [x] 提示词功能(角色扮演)
- [x] 用户聊天次数限制
- [x] 分享得聊天次数
- [x] 每日领取免费次数
- [x] 看广告得次数
- [x] 开通会员，购买次数包
- [x] 在线支付
- [x] 敏感词校验
- [x] 小程序/微信公众号/手机号/邮箱注册登录
- [x] 聊天历史查看
- [x] 后台管理系统
- [x] 详细部署及使用文档，交流学习群
- [ ] AI生成图片、语音转换等功能开发中......

## 部署

- 详细文档参考源码中文档

### 后端

- 创建Mysql数据库aezo-chat-gpt, 执行脚本文件 aezo-chat-gpt-api/doc/aezo-chat-gpt.sql
- 使用IDEA打开aezo-chat-gpt-api项目
- 修改application.yml中的小程序id和秘钥、OpenAI地址和KEY
- 启动项目

### 前端小程序

- 使用HBuilder打开aezo-chat-gpt-m项目
- 修改common/config.js中的API地址
- 运行项目到微信小程序

