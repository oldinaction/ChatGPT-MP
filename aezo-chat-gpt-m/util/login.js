import config from '@/common/config.js'
import {
	isWechat,
	getUrQuery
} from '@/util/squ.js'
import {
	getCurQuery, toast
} from '@/util/squni.js'
import {
	post
} from '@/util/request.js'
import store from '@/store'

export const LoginAlready = 'LoginAlready'
export const LoginSuccess = 'LoginSuccess'
export const LoginUnBind = 'LoginUnBind'
export const LoginError = 'LoginError'

// 开始登录
export const login = () => {
	return new Promise((resolve) => {
		if (store.token) {
			console.debug('本地已存在token, 则认为已经登录')
			resolve(LoginAlready)
		}
		
		// #ifdef H5
		const accessToken = getCurQuery('accessToken') || getUrQuery('accessToken')
		if (accessToken) {
			console.debug('请求参数中包含accessToken, 则认为登录成功')
			store.commit('setToken', accessToken)
			resolve(LoginSuccess)
		} else if (isWechat()) {
			h5WxLogin().then((res) => {
				resolve(res)
			})
		} else {
			h5Login().then((res) => {
				resolve(res)
			})
		}
		// #endif

		// #ifdef MP-WEIXIN
		// 获取进入不同平台的登录方法
		uni.getProvider({
			service: 'oauth',
			success: function(res) {
				// 当前只做微信小程序
				if (~res.provider.indexOf('weixin')) {
					uni.login({
						provider: 'weixin',
						success: function(loginRes) {
							// 前端获取到微信登录临时码，传至后台换openId和sessionId
							let code = loginRes.code
							wxLogin(code).then((wxRes) => {
								resolve(wxRes)
							})
						},
					})
				}
			},
		})
		// #endif
	})
}

// 请求后台微信小程序登录
export const wxLogin = (code) => {
	return new Promise((resolve) => {
		let inviterUserId = getCurQuery('inviterUserId')
		console.debug(`调用微信小程序登录. inviterUserId=${inviterUserId || ''}`)
		post(`/core/user/login?loginType=WXMP`, {
			appid: config.appId,
			code: code,
			inviterUserId: inviterUserId,
			autoRegister: 'true'
		}).then((res) => {
			console.log(res);
			if ('success' == res.status) {
				// 调用成功，根据是否有token判断跳转的页面
				let data = res.data
				store.commit('setOpenid', data.openid)
				if (data.tokenInfo) {
					// tokenInfo不为空，可直接进入首页
					console.debug('微信小程序登录成功')
					store.commit('setToken', data.tokenInfo.tokenValue)
					store.commit('setRefreshToken', data.refreshToken)
					resolve(LoginSuccess)
				} else {
					// accessToken为空，需要先进行绑定，可跳转至绑定列表页面
					console.debug('获取到openid，尚未绑定账号')
					resolve(LoginUnBind)
				}
			} else {
				resolve(LoginError)
			}
		})
	})
}

// H5微信登录(公众号)
export const h5WxLogin = () => {
	const code = getCurQuery('code') || getUrQuery('code')
	if (!code) {
		console.debug('不包含code，需进行用户授权')
		// 授权获取openid
		const redirectUri = encodeURIComponent(window.location.href)
		const scope = 'snsapi_base' // snsapi_base snsapi_userinfo // 静默授权 用户无感知
		const state = 'sqbiz' // 自定义参数，回调时会带上
		const url =
			`https://open.weixin.qq.com/connect/oauth2/authorize?appid=${config.appId}&redirect_uri=${redirectUri}&response_type=code&scope=${scope}&state=${state}#wechat_redirect`
		window.location.href = url
	} else {
		console.debug('包含code，进行微信登录')
		wxLogin(code)
	}
}

// H5登录
export const h5Login = () => {
	console.warn('h5Login todo')
	return new Promise((resolve) => {
		resolve(LoginError)
	})
}
