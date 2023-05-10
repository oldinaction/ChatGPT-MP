## ChatGPT-MP(基于ChatGPT实现的微信小程序)

包含前后台(后端请参考下文github地址)，技术栈：JDK8 + SpringBoot + Vue2 + Uniapp + Mysql。**禁止商用及倒卖，仅供学习交流。** 感谢Star！

**Github地址**：[https://github.com/oldinaction/ChatGPT-MP](https://github.com/oldinaction/ChatGPT-MP)

Gitee地址(国内访问更快)：[https://gitee.com/smalle/ChatGPT-MP](https://gitee.com/smalle/ChatGPT-MP)

小程序演示地址

![One能抽屉](https://cdn7.aezo.cn/common/qrcode/one_qrcode.jpg)

## 包含功能

- [x] ChatGPT聊天
- [x] 用户聊天次数限制
- [x] 分享得聊天次数
- [x] 每日领取免费次数
- [x] 查看聊天历史
- [x] 显示连接情况
- [x] 清除聊天历史
- [x] 开通会员
- [x] 购买次数包
- [x] 联系客服领取次数
- [x] 看广告得次数
- [x] 后台管理系统，暂时为收费版功能，之后会择机开源
- [ ] AI生成图片、语音转换等功能开发中......

## 部署

### 后端

- 创建Mysql数据库aezo-chat-gpt, 执行脚本文件 aezo-chat-gpt-api/doc/aezo-chat-gpt.sql
- 使用IDEA打开aezo-chat-gpt-api项目
- 修改application.yml中的小程序id和秘钥、OpenAI地址和KEY
- 启动项目

### 前端小程序

- 使用HBuilder打开aezo-chat-gpt-m项目
- 修改common/config.js中的API地址
- 运行项目到微信小程序

## 交流学习

- 参考 [https://github.com/oldinaction/ChatGPT-MP](https://github.com/oldinaction/ChatGPT-MP)
