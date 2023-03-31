<template>
	<view>
		<cu-custom bgColor="bg-cyan" :isBack="true">
			<block slot="backText">返回</block>
			<block slot="content">聊天历史</block>
		</cu-custom>
		<view class="cu-chat">
			<block v-for="(x,i) in msgList" :key="i">
				<!-- 用户消息 -->
				<view v-if="x.my && x.type === 'msg'" class="cu-item self" :class="[i === 0 ? 'first' : '', i === 1 ? 'sec' : '']">
					<view class="main">
						<view class="content bg-cyan shadow" @click="x.msg && $squni.copy(x.msg)">
							<text>{{ x.msg }}</text>
						</view>
					</view>
					<view class="cu-avatar round"
						style="background-image:url('https://cdn7.aezo.cn/one/mt/img/icon/logo-100.png');">
					</view>
					<view v-if="i === 0" class="date">{{ x.date }}</view>
				</view>
				<!-- AI消息 -->
				<view v-if="!x.my && x.type === 'msg'" class="cu-item" :class="[i === 0 ? 'first' : '', i === 1 ? 'sec' : '']">
					<view class="cu-avatar round"
						style="background-image:url('https://cdn7.aezo.cn/one/mt/img/icon/robot.png');">
					</view>
					<view class="main">
						<view class="content shadow" @click="x.msg && $squni.copy(x.msg)">
							<text>{{ x.msg }}</text>
						</view>
					</view>
					<view v-if="i === 0" class="date">{{ x.date }}</view>
				</view>
				<view v-if="x.type === 'error'" class="cu-info">
					<text class="cuIcon-roundclosefill text-red "></text> {{ x.msg }}
				</view>
			</block>
			<view v-if="msgList.length === 0" class="text-center gray margin-top">无历史聊天记录</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				msgList: []
			}
		},
		onShow() {
			this.msgList = this.$squni.getStorageSync('chatHistory') || []
		},
		methods: {
			
		}
	}
</script>

<style>
	page {
		padding-bottom: 200upx;
	}
	.cu-item:not(.first) {
		padding-bottom: 0upx;
	}
	.cu-item.sec {
		padding-top: 0upx;
	}
	.main .content {
		word-wrap: break-word;
	}
</style>
