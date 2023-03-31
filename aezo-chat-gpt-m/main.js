import Vue from 'vue'
import App from './App'
import store from './store'
import config from './common/config.js'
import squni from './util/squni.js'
import mixin from './common/mixin'
// #ifdef MP-WEIXIN
import wxShare from './util/wx-share.js'
// #endif

// ColorUI返回组件
import cuCustom from '@/uni_modules/colorui/components/cu-custom.vue'

Vue.config.productionTip = false
Vue.prototype.$store = store
Vue.prototype.$config = config
Vue.prototype.$squni = squni

// 为了保证onLaunch执行完后，再执行页面级别的onShow/onLoad
Vue.prototype.$ready = new Promise(resolve => {
  Vue.prototype.$emitReady = resolve
})

Vue.component('cu-custom', cuCustom)
Vue.mixin(mixin)
// #ifdef MP-WEIXIN
Vue.mixin(wxShare)
// #endif

App.mpType = 'app'

const app = new Vue({
    store,
    ...App
})

app.$mount()
