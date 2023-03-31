import {
	jsonToQuery
} from '@/util/squ.js'
export default {
	data() {
		return {
			shareTitle: 'One能聊天',
			// 分享的连接
			sharePath: '',
			// 分享连接扩展参数，如果定义了sharePath则此参数无效
			shareQuery: {},
			shareUrl: 'https://cdn7.aezo.cn/one/mt/img/icon/logo-100.png',
			// 进入当前页面是否是通过点击分享连接进入
			share: false
		}
	},
	onLoad(options) {
		// 页面初次加载判断有没有携带分享参数，从而解决分享页面无法返回问题
		if (options && (options.share || options.share == 'true')) {
			this.share = true
		}
		try {
			wx.showShareMenu({
				withShareTicket: true,
				menus: ["shareAppMessage", "shareTimeline"]
			})
		} catch(error) {
			console.error(error)
		}
	},
	// 分享朋友圈和微信好友函数和 onLoad 等生命周期函数同级
	onShareAppMessage(res) {
		let path = this.getSharePath()
		console.log(path);
		if (res.from === 'button') {
			// 为自定义按钮分享. 这块需要传参，不然链接地址进去获取不到数据
			return {
				title: this.shareTitle,
				path: path,
				imageUrl: this.shareUrl
			};
		}
		if (res.from === 'menu') {
			return {
				title: this.shareTitle,
				path: path,
				imageUrl: this.shareUrl
			};
		}
	},
	// 分享到朋友圈
	onShareTimeline(res) {
		let path = this.getSharePath()
		return {
			title: this.shareTitle,
			path: path,
			imageUrl: this.shareUrl
		};
	},
	methods: {
		getSharePath() {
			if (this.sharePath) {
				return this.sharePath
			}
			let sharePath = `/${this.$scope.route}?share=true&inviterUserId=${this.$store.getters.userId}&`
			if (this.shareQuery) {
				sharePath += jsonToQuery(this.shareQuery)
			}
			return sharePath
		},
		navigateBack () {
			this.$squni.navigateBack(this)
		},
	}
}
