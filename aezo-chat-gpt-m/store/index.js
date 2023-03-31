import Vue from 'vue'
import Vuex from 'vuex'
import squni from '@/util/squni.js'
import { post } from '@/util/request.js'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    appInfo: squni.getStorageSync('appInfo') || {},
    token: squni.getStorageSync('token') || '',
    refreshToken: squni.getStorageSync('refreshToken') || '',
    userId: squni.getStorageSync('userId') || '',
    openid: squni.getStorageSync('openid') || '',
    userInfo: squni.getStorageSync('userInfo') || {},
	userInfoOth: squni.getStorageSync('userInfoOth') || {},
    permission: squni.getStorageSync('permission') || {},
    roles: squni.getStorageSync('roles') || [],
    // 进入(微信小程序)场景值
    scene: null
  },

  getters: {
    appInfo: state => state.appInfo,
    token: state => state.token,
    refreshToken: state => state.refreshToken,
	userId: state => state.userId,
    openid: state => state.openid,
    userInfo: state => state.userInfo,
	userInfoOth: state => state.userInfoOth,
    permission: state => state.permission,
    roles: state => state.roles,
    scene: state => state.scene,
	usernameSimple: state => {
		// 移除sq_
		let username = state.userInfo.username || ''
		if (username.indexOf('_') > 0) {
			username = username.substring(username.indexOf('_') + 1)
		}
		return username.toUpperCase()
	}
  },

  // this.$store.commit('setAppInfo', appInfo)
  mutations: {
    setAppInfo(state, appInfo) {
      state.appInfo = appInfo
      squni.setStorageSync('appInfo', appInfo)
    },
    setToken(state, token) {
      state.token = token
      squni.setStorageSync('token', token)
    },
    setRefreshToken(state, refreshToken) {
      state.refreshToken = refreshToken
      squni.setStorageSync('refreshToken', refreshToken)
    },
	setUserId(state, userId) {
	  state.userId = userId
	  squni.setStorageSync('userId', userId)
	},
    setOpenid(state, openid) {
      state.openid = openid
      squni.setStorageSync('openid', openid)
    },
	setUserInfo(state, userInfo) {
	  state.userInfo = userInfo
	  squni.setStorageSync('userInfo', userInfo)
	},
    setUserInfoOth(state, userInfoOth) {
      state.userInfoOth = userInfoOth
      squni.setStorageSync('userInfoOth', userInfoOth)
    },
    setPermission(state, permission) {
      state.permission = permission
      squni.setStorageSync('permission', permission)
    },
    setRoles(state, roles) {
      state.roles = roles
      squni.setStorageSync('roles', roles)
    },
    setScene(state, scene) {
      state.scene = scene
      squni.setStorageSync('scene', scene)
    }
  },

  // this.$store.dispatch('LoginByUsername', userInfo)
  actions: {
    GetUserInfo({ commit }) {
      return new Promise((resolve, reject) => {
        post('/core/user/info')
          .then(res => {
            if (res.code === 20000) {
              const data = res.data
			  commit('setUserId', (data.userInfo || {}).id)
			  commit('setUserInfo', data.userInfo || {})
			  commit('setRoles', data.roles || [])
			  commit('setPermission', data.permissions || [])
			  delete data.userInfo
			  delete data.roles
			  delete data.permissions
              commit('setUserInfoOth', data || {})
              resolve(data)
            } else {
              reject(res.message)
            }
          })
          .catch(err => {
            reject(err)
          })
      })
    }
  }
})

export default store
