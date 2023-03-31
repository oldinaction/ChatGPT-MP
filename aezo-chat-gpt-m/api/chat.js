import { get, post } from '@/util/request.js'

// AI聊天(弃用)
export const sendMsgApi = (params, config = {}) => post('/tools/chat/sendMsg', params, config)

// 查询用户余额
export const getUserChatAssetApi = (params, config = {}) => post('/tools/chat/getUserChatAsset', params, config)

