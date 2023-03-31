import gloablConfig from '@/common/config.js'
const globalKeyPrefix = gloablConfig.key ? gloablConfig.key + '$' : 'squni$'

// ====== 存储
// uni.getStorageInfo uni.getStorageInfoSync uni.clearStorageSync 略
export const setStorage = config =>
	uni.setStorage({
		key: globalKeyPrefix + config.key,
		data: config.data,
		success: config.success,
		fail: config.fail,
		complete: config.complete
	})

export const setStorageSync = (key, value) => uni.setStorageSync(globalKeyPrefix + key, value)

export const getStorage = config =>
	uni.getStorage({
		key: globalKeyPrefix + config.key,
		success: config.success,
		fail: config.fail,
		complete: config.complete
	})

export const getStorageSync = key => uni.getStorageSync(globalKeyPrefix + key)

export const removeStorage = config =>
	uni.removeStorage({
		key: globalKeyPrefix + config.key,
		success: config.success,
		fail: config.fail,
		complete: config.complete
	})

export const removeStorageSync = key => uni.removeStorageSync(globalKeyPrefix + key)

// ====== 路由
export const navigateTo = (url) => {
	uni.navigateTo({
		url: url
	})
}

export const navigateBack = (vm) => {
	// 从分享打开的，返回首页. 可引入wx-share.js
	if (vm.share == true) {
		redirectHome(vm)
	} else {
		// 不是从分享打开的，返回上一页
		uni.navigateBack({
			delta: 1,
		})
	}
}

export const redirectTo = (url) => {
	uni.redirectTo({
		url: url
	})
}

export const redirectHome = (vm) => {
	if (vm.$config.indexType === 'Tab') {
		uni.switchTab({
			url: vm.$config.indexPath
		})
	} else {
		uni.redirectTo({
			url: vm.$config.indexPath
		})
	}
}

/**
 * 获取当前页面请求路径
 */
export const getCurPage = () => {
	// uni-app内置函数
	const pages = getCurrentPages()
	return (pages && pages.length > 0) ? pages[pages.length - 1] : {}
}
/**
 * 获取当前页面请求路径所有参数
 */
export const getCurQueryAll = () => {
	const curPage = getCurPage()
	// 在微信小程序或是app中，通过curPage.options；如果是H5，则需要curPage.$route.query
	return curPage.options || (curPage.$route && curPage.$route.query)
}
/**
 * 获取当前页面请求路径参数. 可参考 squ.js 中的 getUrQuery
 */
export const getCurQuery = (name) => {
	const query = getCurQueryAll()
	return query ? query[name] : null
}

// ====== 其他
export const copy = (value) => {
	uni.setClipboardData({
		data: value,
		success: () => {
			uni.showToast({
				title: '复制成功',
				duration: 500
			})
		}
	});
}

/**
 * 信息提示更简洁
 */
export const toast = (message, icon) => {
	let config = {
		title: message
	}
	config.icon = icon || 'none'
	uni.showToast(config)
}

/**
 * 滚动页面到底部。如聊天时
 */
export const scrollToBottom = () => {
	// 要加点延迟, 不然有可能不生效
	setTimeout(() => {
		uni.pageScrollTo({
			scrollTop: 999999,
			duration: 0
		});
	}, 50);
}

export default {
	globalKeyPrefix,
	setStorageSync,
	getStorageSync,
	removeStorageSync,
	navigateTo,
	navigateBack,
	redirectTo,
	redirectHome,
	getCurQuery,
	copy,
	toast
}
