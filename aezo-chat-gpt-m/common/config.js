const env = process.env
const dev = process.env.NODE_ENV === 'development'
// 改成自己的IP
const baseUrl = dev ? 'http://localhost:6501/api' : 'https://www.example.com/api'
const wssUrl = dev ? 'ws://localhost:6501/api' : 'wss://www.example.com/api'

console.log(`\n %c API URL %c ${baseUrl} \n\n`, 'color: #ffffff; background: #f37b1d; padding:5px 0;', 'color: #f37b1d;background: #ffffff; padding:5px 0;');

const config = {
	env,
	baseUrl,
	wssUrl,
	// 当前微信小程序ID
	appId: 'wx5f9e2e22f03c0d5d',
	// 唯一项目代码，如用于本地Storage前缀
	key: 'aezo-chat-gpt',
	// Token的名字
	Authorization: 'SQ-ACCESS-TOKEN',
	// 主页路径. 必须以/开头
	indexPath: '/pages/main/index',
	// 主页类型: Tab(通过switchTab跳转)/Page(通过redirectTo跳转)
	indexType: 'Page'
}

export default config
