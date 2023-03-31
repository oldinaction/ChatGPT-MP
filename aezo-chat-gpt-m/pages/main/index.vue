<template>
	<view>
		<cu-custom bgColor="bg-cyan" :isBack="true">
			<block slot="backText">返回</block>
			<block slot="content">One能聊天</block>
		</cu-custom>
		<view class="cu-chat">
			<block v-for="(x,i) in msgList" :key="i">
				<!-- 用户消息 -->
				<view v-if="x.my && x.type === 'msg'" class="cu-item self" :class="[i === 0 ? 'first' : '', i === 1 ? 'sec' : '']">
					<view class="main">
						<view class="content bg-cyan shadow" @click="x.msg && $squni.copy(x.msg)">
							<text>{{ x.msg }}</text>
						</view>
					</view>
					<view class="cu-avatar round"
						style="background-image:url('https://cdn7.aezo.cn/one/mt/img/icon/logo-100.png');">
					</view>
					<view v-if="i === 0" class="date">{{ x.date }}</view>
				</view>
				<!-- AI消息 -->
				<view v-if="!x.my && x.type === 'msg'" class="cu-item" :class="[i === 0 ? 'first' : '', i === 1 ? 'sec' : '']">
					<view class="flex flex-direction align-center">
						<view class="cu-avatar round chat-avatar"
							style="background-image:url('https://cdn7.aezo.cn/one/mt/img/icon/robot.png');">
						</view>
						<text v-if="i === 0" class="cuIcon-title" :class="[statusColor]"></text>
					</view>
					<view class="main">
						<view class="content shadow" @click="x.msg && $squni.copy(x.msg)">
							<text>{{ x.msg }}</text>
						</view>
					</view>
					<text v-if="i === 0" class="text-blue" style="line-height: 100upx;"
						@click="$squni.navigateTo('/pages/main/history')">历史</text>
					<view v-if="i === 0" class="date">{{ x.date }}</view>
				</view>
				<view v-if="x.type === 'error'" class="cu-info">
					<text class="cuIcon-roundclosefill text-red "></text> {{ x.msg }}
				</view>
			</block>
			
			<view v-if="msgLoading" class="cu-item">
				<view class="flex flex-direction align-center">
					<view class="cu-avatar round chat-avatar"
						style="background-image:url('https://cdn7.aezo.cn/one/mt/img/icon/robot.png');">
					</view>
				</view>
				<view class="main">
					<text class="cuIcon-loading2 cuIconfont-spin text-cyan"></text>
				</view>
			</view>
		</view>

		<view class="cu-bar foot input" :style="[{bottom:inputBottom+'px'}]">
			<view class="action func" @click="openBottomFunc">
				<text class="cuIcon-list text-cyan" style="font-size: 60upx;"></text>
			</view>
			<input v-model="msg" class="solid padding-lr" :adjust-position="false" :focus="false" maxlength="1000"
				cursor-spacing="10" :placeholder="loading ? 'AI正在思考中，请稍后~' : '用一句简短的话描述您的问题'"
				@focus="inputFocus" @blur="inputBlur" @confirm="sendMsg"></input>
			<!-- <view class="action">
				<text class="cuIcon-emojifill text-grey"></text>
			</view> -->
			<button class="cu-btn bg-cyan shadow" :disabled="loading" @click="sendMsg">
				<text class="cuIcon-loading2 cuIconfont-spin" v-if="loading || !ready"></text>{{ !ready ? '连接中' : '发送' }}
			</button>
		</view>
		
		<bottom-func v-if="bottomFuncShow" ref="bottomFunc" :chatAsset="chatAsset"></bottom-func>
	</view>
</template>

<script>
	import {
		dateFormat, interval
	} from '@/util/squ.js'
	import {
		scrollToBottom
	} from '@/util/squni.js'
	import websocket from '@/util/websocket'
	import {
		sendMsgApi, getUserChatAssetApi
	} from '@/api/chat.js'
	import BottomFunc from './bottom-func'
	const HELLO_MSG = {
		type: 'msg',
		my: false,
		msg: '连接中，请稍后~',
		date: dateFormat(new Date(), 'yyyy年MM月dd日 hh:mm')
	}
	export default {
		components: { BottomFunc },
		data() {
			return {
				loading: false,
				userId: this.$store.getters.userId,
				msgList: [HELLO_MSG],
				msgContent: "",
				msg: "",
				inputBottom: 0,
				bottomFuncShow: false,
				chatAsset: {},
				assetType: 'n',
				statusColor: 'text-red',
				statusTimer: null,
				msgLoading: false
			}
		},
		computed: {
			ready () {
				return this.statusColor === 'text-green'
			}
		},
		watch: {
			loading(n, o) {
				if (n !== o && !n) {
					let last = this.msgList[this.msgList.length - 1]
					if (!last.my) {
						this.addHistory(last)
					}
				}
			},
			statusColor(n, o) {
				if (n === 'text-green') {
					HELLO_MSG.msg = '你好，我是One机器人(M1)，请问有什么问题可以帮助您?'
				} else {
					HELLO_MSG.msg = '连接中，请稍后~'
				}
			}
		},
		async onShow() {
			this.heartStatus()
			await this.$ready
			
			if(!this.userId) {
				this.$squni.toast('请先进行登录哦')
				return
			}
			getUserChatAssetApi().then(res => {
				this.chatAsset = res.data
			})
			
			try {
				//建立socket连接
				websocket.connectSocket(this.$config.wssUrl + '/tools/chat/user/' + this.userId, msg => {
					this.recvMsg(msg)
				}, () => {
					//如果连接成功则发送心跳检测
					//this.heartBeatTest()
				})
			} catch (error) {
				console.log('websocket connectSocket error:' + error)
			}
		},
		onHide() {
			websocket.closeSocket()
			clearInterval(this.statusTimer)
		},
		methods: {
			sendMsg() {
				if (this.msg == "") {
					this.$squni.toast('请先输入您的问题哦')
					return
				}
				let msg = this.msg
				this.putMsg(this.msg, true)
				this.msgLoading = true
				this.loading = true
				
				// ======== 开发环境模拟回复 ========
				//return this.mockReply()
				// ======== 开发环境模拟回复 ========
				
				if(this.calcAsset() === false) {
					this.loading = false
					return
				}
				
				// 发送消息
				websocket.sendMessage(msg, null, () => {
					this.putMsgError('机器人被拔网线了，请稍后再试~')
				})
			},
			recvMsg(msg) {
				this.msgLoading = false
				if(!msg) {
					this.putMsgError('机器人开小差了，请稍后再试~')
					return
				}
				// 发送消息
				// 1+1
				// 收到消息
				// {"role":"assistant","content":null}
				// {"role":null,"content":"2"}
				// {"role":null,"content":null}
				// [DONE]
				if (msg === '[DONE]') {
					this.loading = false
				} else {
					try {
						let msgJson = JSON.parse(msg)
						if (msgJson.role === 'sqchat') {
							let content = msgJson.content
							if (msgJson.codeKey) {
								content += `[${msgJson.codeKey}]`
								if(msgJson.codeKey === 'chat.asset_short') {
									this.openBottomFunc()
								} else if(msgJson.codeKey.indexOf('chat.asset_') >= 0) {
									this.chatAsset[this.assetType]++
								}
							}
							this.putMsgError(content)
						} if (msgJson.role === 'assistant') {
							this.putMsg('', false)
						} else if (msgJson.role == null && msgJson.content) {
							this.msgList[this.msgList.length - 1].msg += msgJson.content
							scrollToBottom()
						}
					} catch(error) {
						this.putMsgError(msg)
					}
				}
			},
			sendMsgBak() {
				let that = this
				if (this.msg == "") {
					return
				}
				this.msgContent += (this.userId + ":" + this.msg + "\n")
				this.putMsg(this.msg, true)
				this.loading = true

				// ======== 开发环境模拟回复 ========
				return this.mockReply()
				// ======== 开发环境模拟回复 ========

				sendMsgApi({
					userId: this.userId + '',
					question: this.msgContent
				}).then(({
					status, data
				}) => {
					if (status === 'success') {
						this.putMsg(data.ack, false)
						this.msgContent += ("openai:" + this.msg + "\n")
					} else {
						this.putMsg(res.message || '机器人开小差了，请稍后再试~', false, 'error')
					}
					that.loading = false
				})
			},
			calcAsset() {
				if (this.chatAsset.dfn > 0) {
					this.chatAsset.dfn--
					this.assetType = 'dfn'
				} else if (this.chatAsset.n > 0) {
					this.chatAsset.n--
					this.assetType = 'n'
				} else {
					this.$squni.toast('剩余次数不足')
					this.openBottomFunc()
					return false
				}
			},
			putMsg (msg, my = false, type = 'msg') {
				let item = {
					type: type,
					msg: msg,
					my: my,
					date: dateFormat(new Date(), 'yyyy年MM月dd日 hh:mm')
				}
				this.msgList.push(item)
				scrollToBottom()
				if (my) {
					this.addHistory(item)
					// 清除消息
					this.msg = ''
					this.msgReply = ''
				}
			},
			putMsgError(msg) {
				this.putMsg(msg, false, 'error')
				this.msgLoading = false
				this.loading = false
			},
			addHistory(item) {
				if (item.type === 'msg') {
					let chatHistory = this.$squni.getStorageSync('chatHistory')
					if(!chatHistory) {
						chatHistory = []
					}
					if(chatHistory.length >= 50) {
						chatHistory.splice(0, 1)
					}
					chatHistory.push(item)
					this.$squni.setStorageSync('chatHistory', chatHistory)
				}
			},
			//心跳检测
			heartBeatTest() {
				let globalTimer = null
				//清除定时器
				clearInterval(globalTimer)
				//开启定时器定时检测心跳
				globalTimer = setInterval(() => {
					//发送消息给服务端
					websocket.sendMessage('PING', null, () => {
						//如果失败则清除定时器
						clearInterval(globalTimer)
					})
				}, 10000)
			},
			heartStatus() {
				this.statusTimer = interval(() => {
					if (websocket.isOpen) {
						this.statusColor = 'text-green'
					} else if(this.statusColor === 'text-red') {
						this.statusColor = 'text-yellow'
					} else {
						this.statusColor = 'text-red'
					}
				}, this.statusTimer, 200)
			},
			inputFocus(e) {
				this.inputBottom = e.detail.height
			},
			inputBlur(e) {
				this.inputBottom = 0
			},
			openBottomFunc() {
				this.bottomFuncShow = true
				this.$nextTick(() => {
					this.$refs.bottomFunc.open()
				})
			},
			mockReply() {
				// 开发环境模拟回复
				if (process.env.NODE_ENV === 'development') {
					setTimeout(() => {
						this.putMsg('这是模拟返回消息: ' + new Date(), false)
						this.loading = false
					}, 1000)
					return
				}
			}
		}
	}
</script>

<style>
	page {
		padding-bottom: 200upx;
	}
	.cu-chat .chat-avatar.cu-avatar {
	    width: 82upx;
	    height: 82upx;
	}
	.cu-item:not(.first) {
		padding-bottom: 0upx;
	}
	.cu-item.sec {
		padding-top: 0upx;
	}
	.main .content {
		word-wrap: break-word;
	}
	.foot {
		padding-top: 20upx;
		padding-bottom: 60upx;
	}
	.foot .cu-btn {
		margin-right: 20upx;
	}
	.foot .action.func {
		margin-left: 30upx;
	}
</style>
