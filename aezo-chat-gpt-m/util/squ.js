/**
 * 是否为微信浏览器
 * @returns true | false
 */
export const isWechat = () => {
	const ua = window.navigator.userAgent.toLowerCase()
	return ua.match(/micromessenger/i) == 'micromessenger'
}

/**
 * 是否为IOS浏览器
 * @returns true | false
 */
export const isIOS = () => {
	let isIphone = navigator.userAgent.includes('iPhone')
	let isIpad = navigator.userAgent.includes('iPad')
	return isIphone || isIpad
}

/**
 * 日期格式化
 * 
 * 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
 * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 * 例子：
 * (new Date()).Format("yyyy-MM-dd h:m:s.S") ==> 2006-07-02 8:9:4.423
 * (new Date()).Format("yyyy-M-d hh:mm:ss") ==> 2006-7-2 08:09:04.18
 * (new Date()).Format("yyyy年MM月dd日 hh:mm") ==> 2006年7月2日 08:09
 * (new Date()).Format("hh:mm") ==> 08:09
 */
export const dateFormat = (date, fmt) => {
	let o = {
		"M+": date.getMonth() + 1, // 月份
		"d+": date.getDate(), // 日
		"h+": date.getHours(), // 小时
		"m+": date.getMinutes(), // 分
		"s+": date.getSeconds(), // 秒
		"q+": Math.floor((date.getMonth() + 3) / 3), // 季度
		"S": date.getMilliseconds() // 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
	for (let k in o)
		if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : ((
			"00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

/**
 * 获取url参数. uni-app可参考squni.js中的 getCurQuery
 */
export const getUrQuery = (name) => {
	return (
		decodeURIComponent(
			(new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(
				/\+/g, '%20')
		) || null
	)
}

/**
 * Map转成url参数: k1=v1&k2=v2
 */
export const jsonToQuery = (json, ignoreFields) => {
    return Object.keys(json)
        .filter(key => json[key] != null && ignoreFields.indexOf(key) === -1)
        .map(key => key + '=' + encodeURIComponent(json[key])).join('&');
}

/**
 * 定时执行，需要再回调中清除定时
 */
export const interval = (callback, timer = null, interval = 1000) => {
	//清除原定时器
	clearInterval(timer)
	//开启定时器定时
	timer = setInterval(() => {
		callback && callback()
	}, interval)
	return timer
}

/**
 * 生成uuid. eg: bb8263ae-ce73-4e3b-82de-67c105bc1a4b
 */
export const uuid = (removeMidline) => {
	let s = [];
	let hexDigits = "0123456789abcdef";
	for (let i = 0; i < 36; i++) {
		s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
	}
	s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
	s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
	s[8] = s[13] = s[18] = s[23] = "-";

	let uuid = s.join("");
	return removeMidline ? uuid.replace(/\-/g, '') : uuid;
}
