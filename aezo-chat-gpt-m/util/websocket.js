const _WEBSOCKET = {
	//是否打开连接
	isOpen: false,
	//连接socket
	connectSocket(url, recvMsgFunc = null, successFunc = null, errorFunc = null) {
		try {
			let that = this
			//连接socket
			uni.connectSocket({
				url,
				success() {
					if (that.isOpen) {
						console.log('Websocket连接已打开，刷新状态完成！', url)
					} else {
						console.log('Websocket初始化成功！正在监听连接打开状态...', url)
					}
				}
			})
			//监听socket连接
			uni.onSocketOpen((res) => {
				this.isOpen = true
				console.log('WebSocket连接已打开！', url)
				if (successFunc) {
					successFunc(res)
				}
			})
			//监听socket连接失败
			uni.onSocketError((res) => {
				this.isOpen = false
				console.log('WebSocket连接打开失败，请检查！', url, res)
				if (errorFunc) {
					errorFunc(res)
				}
			})
			//监听收到消息
			uni.onSocketMessage((res) => {
				console.log('收到服务器内容：' + res.data, url)
				recvMsgFunc && recvMsgFunc(res.data)
			})
			//监听socket关闭
			uni.onSocketClose((res) => {
				console.log('WebSocket 已关闭！', url)
				this.isOpen = false
			})
		} catch (error) {
			console.log('err:' + error)
		}
	},
	//发送消息
	sendMessage(msg = '', successFunc = null, errorFunc = null) {
		if (!msg) {
			console.log('未传消息！')
			if (errorFunc) {
				errorFunc('未传消息！')
			}
			return
		}
		if (!this.isOpen) {
			console.log('连接未打开！')
			if (errorFunc) {
				errorFunc('连接未打开！')
			}
			return
		}
		uni.sendSocketMessage({
			data: msg,
			success(res) {
				console.log('消息发送成功！', msg)
				if (successFunc) {
					successFunc(res)
				}
			},
			fail(err) {
				console.log('消息发送失败！', msg)
				if (errorFunc) {
					errorFunc(err)
				}
			}
		})
	},
	//关闭连接
	closeSocket() {
		if (!this.isOpen) {
			return
		}
		//关闭socket连接
		uni.closeSocket()
	}
}

export default _WEBSOCKET
