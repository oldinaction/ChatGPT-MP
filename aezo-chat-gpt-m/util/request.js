import {
	login
} from '@/util/login.js'
import squni from '@/util/squni.js'
import gloablConfig from '@/common/config.js'

export const get = (url, params, config) =>
	ajax(url, params, Object.assign({
		method: 'GET'
	}, config))

export const post = (url, params, config) =>
	ajax(url, params, Object.assign({
		method: 'POST'
	}, config))

const ajax = (url, params, config) => {
	const accessToken = squni.getStorageSync('token')
	return new Promise((resolve, reject) => {
		uni.request({
			method: config.method,
			url: (config.baseUrl || gloablConfig.baseUrl) + url,
			header: Object.assign({
				'Content-Type': 'application/json',
				[gloablConfig.Authorization]: accessToken || ''
			}, config.header),
			data: params,
			success(resp) {
				resolve(resp.data)
			},
			fail(resp) {
				// resp.status !== 200
				if (config.toast !== false) {
					squni.toast('请求出错')
				}
				reject(resp)
			},
			complete(resp) {
				// console.log(resp);
				if (config.check !== false) {
					check(resp, config)
				}
			}
		})
	})
}

const check = (resp, config) => {
	let code = resp.data && Number(resp.data.code)
	if (!code) {
		console.error(resp)
		code = 50000
	}
	const message = (resp.data && resp.data.message) || '未知错误'
	// 如果是401则跳转到登录页面
	if (code >= 40100 && code <= 40199) {
		// #ifdef MP-WEIXIN
		if (resp.data != null && resp.data.message == 'Auth Token Invalid') {
			login().then(res => {
				if (res === 'loginSuccess') {
					uni.switchTab({
						url: '../pages/homepage/homepage'
					})
				}
			})
		}
		// #endif
		// #ifdef H5
		// TODO refresh token 逻辑
		uni.navigateTo({
			url: './pages/bindAccount/h5Account/h5Account'
		})
		// #endif
	}
	// 如果请求为非200否者默认统一处理
	if (code !== 20000) {
		if (config.toast !== false) {
			squni.toast(message)
		}
	}
}
