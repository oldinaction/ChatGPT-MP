<script>
	import Vue from 'vue'
	import {
		updateMini
	} from '@/util/sqmp.js'
	import { login, LoginAlready, LoginSuccess, LoginUnBind } from '@/util/login.js'
	import squni from '@/util/squni.js'
	import config from '@/common/config.js'
	export default {
		async onLaunch(e) {
			console.log('App onLaunch...')
			this.vueStylePrototype()

			// #ifdef MP-WEIXIN
			// 微信小程序打开场景
			this.$store.commit('setScene', e.scene)
			// 微信小程序更新检查
			updateMini();
			// #endif
		},
		async onShow() {
			console.log('App onShow...')
			
			// 如果写在onLaunch里面，用户重新进入小程序Token可能过期，需要做Token刷新逻辑
			// #ifdef MP-WEIXIN
			// 自动进行微信小程序登录及后续跳转
			await this.onLaunchWx()
			// #endif
			
			// 保证onLaunch执行完后，再执行页面级别的onShow/onLoad(两个需要分别进行await this.$ready)
			this.$emitReady()
		},
		onHide() {
			console.log('App onHide...')
		},
		methods: {
			async onLaunchWx(e) {
				if(e) {
					// 微信小程序打开场景
					this.$store.commit('setScene', e.scene)
				}
				// 登录
				await login().then(res => {
					if (res === LoginAlready || res === LoginSuccess) {
						this.$store.dispatch('GetUserInfo')
						// 注意：跳转Tab则需要使用uni.switchTab，跳转普通页面才使用uni.navigateTo
						// uni.navigateTo({
						// 	url: config.indexPath
						// })
					} else if (res === LoginUnBind) {
						this.$store.dispatch('GetUserInfo')
						// uni.navigateTo({
						// 	url: config.indexPath + '?openid=' + squni.getStorageSync('openid')
						// })
					} else {
						squni.toast('登录失败')
					}
				})
			},
			vueStylePrototype() {
				uni.getSystemInfo({
					success: function(e) {
						// ====== 顶部导航高度 ======//
						// #ifndef MP
						Vue.prototype.StatusBar = e.statusBarHeight;
						if (e.platform == 'android') {
							Vue.prototype.CustomBar = e.statusBarHeight + 50;
						} else {
							Vue.prototype.CustomBar = e.statusBarHeight + 45;
						};
						// #endif

						// #ifdef MP-WEIXIN
						Vue.prototype.StatusBar = e.statusBarHeight;
						let custom = wx.getMenuButtonBoundingClientRect();
						Vue.prototype.Custom = custom;
						Vue.prototype.CustomBar = custom.bottom + custom.top - e.statusBarHeight;
						// #endif		

						// #ifdef MP-ALIPAY
						Vue.prototype.StatusBar = e.statusBarHeight;
						Vue.prototype.CustomBar = e.statusBarHeight + e.titleBarHeight;
						// #endif

						// ====== 可用窗口高度 =====//
						// #ifdef MP-WEIXIN
						Vue.prototype.AvailableHeight = e.windowHeight + e.statusBarHeight;
						// #endif
						// #ifdef H5
						Vue.prototype.AvailableHeight = e.windowHeight + e.statusBarHeight - Vue.prototype
							.CustomBar;
						// #endif
					}
				})
			}
		}
	}
</script>

<style lang="scss">
	/*每个页面公共css */
	@import "@/uni_modules/colorui/main.css";
	@import "@/uni_modules/colorui/icon.css";
	// @import "@/uni_modules/uview-ui/index.scss";
	@import "common/common.scss";
</style>
