// ============= 微信小程序工具类 =============
/**
 * 检查小程序是否有更新，并进行更新
 */
export const updateMini = () => {
	const updateManager = uni.getUpdateManager();
	updateManager.onCheckForUpdate(function(res) {
		// 请求完新版本信息的回调
		if (res.hasUpdate) {
			// 有新版本，静默下载
			updateManager.onUpdateReady(function(res) {
				uni.showModal({
					title: '更新提示',
					content: '新版本已经准备好，是否重启应用？',
					success(res) {
						if (res.confirm) {
							// 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
							updateManager.applyUpdate();
						} else if (res.cancel) {
							// 强制用户更新
							uni.showModal({
								title: '温馨提示',
								content: '本次版本更新涉及到新的功能添加，旧版本部分功能可能无法正常使用~',
								success(result) {
									updateMini();
								}
							});
						}
					}
				});

			});
		}
	});

	updateManager.onUpdateFailed(function(res) {
		// 新的版本下载失败
		uni.showModal({
			title: '已经有新版本啦',
			content: '新版本已经上线啦~，请您删除当前小程序，重新搜索打开哟~',
		})
	});
}

/**
 * 申请授权
 */
export const guideAuth = (guideMsg) => {
	//引导用户开启权限
	uni.showModal({
		content: guideMsg || '我们需要您的授权，才能继续工作',
		success: (res) => {
			if (res.confirm) {
				uni.openSetting({
					success: (result) => {
						console.log(result.authSetting);
					}
				});
			}
		}
	});
}

/**
 * 申请保存相册授权
 */
export const writePhotosAlbumAuth = (guideMsg, authCallback) => {
	uni.authorize({
		scope: 'scope.writePhotosAlbum',
		success: () => {
			// 已授权
			authCallback && authCallback();
		},
		fail: () => {
			// 拒绝授权，获取当前设置
			uni.getSetting({
				success: (result) => {
					if (!result.authSetting['scope.writePhotosAlbum']) {
						guideAuth(guideMsg)
					}
				}
			});
		}
	})
}
