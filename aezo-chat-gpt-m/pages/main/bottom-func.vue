<template>
	<view>
		<view class="cu-modal bottom-modal" :class="show ? 'show' : ''" @click="close">
			<view class="cu-dialog" @click.stop="() => {}">
				<view class="cu-bar bg-white justify-end">
					<view class="content">我的菜单栏</view>
					<view class="action" @click="close">
						<text class="cuIcon-close"></text>
					</view>
				</view>
				<view class="padding-sm content">
					<view class="padding-lr-xs">
						<view class="flex align-start">
							<view class="cu-avatar lg">VIP</view>
							<view class="flex flex-direction align-start margin-xs margin-left">
								<view>普通用户</view>
								<view @click="usernameSimple && $squni.copy(usernameSimple)">ID：
									<text class="text-bold">{{ usernameSimple || '尚未登录' }}</text>
									<text class="cuIcon-copy text-cyan margin-left-xs"></text>
								</view>
							</view>
						</view>
						<view class="flex align-start margin-top">
							<view class="cu-capsule">
								<view class='cu-tag radius bg-cyan'>拥有额度</view>
								<view class="cu-tag line-cyan">{{ chatAsset.n || 0 }}次</view>
							</view>
							<view class="cu-capsule">
								<view class='cu-tag radius bg-cyan'>每日免费剩余额度</view>
								<view class="cu-tag line-cyan">{{ chatAsset.dfn || 0 }}次</view>
							</view>
						</view>
						<view class="flex justify-between align-center margin-top">
							<view class="flex flex-direction align-start">
								<view class="text-bold">邀请新的小伙伴来体验</view>
								<view class="padding-tb-xs">奖励 10 次/人，每天最多 5 人</view>
								<view class="text-xs text-gray">提示：点击右上角···可分享给朋友进行邀请</view>
							</view>
							<view>
								<button class="cu-btn lines-cyan round" open-type="share">邀请朋友</button>
							</view>
						</view>
						<view class="flex justify-between align-center margin-top">
							<view class="flex flex-direction align-start">
								<view class="text-bold padding-bottom-sm">看视频广告攒次数</view>
								<view>奖励 3 次/个，每天最多看 10 次</view>
							</view>
							<button class="cu-btn lines-cyan round" @click="$squni.toast('功能完善中，敬请期待~')">观看视频</button>
						</view>
					</view>
					<view>
						<view class="cu-list no-border grid col-4">
							<view class="cu-item" v-for="(item,index) in cuIconList" :key="index" @click="navigateTo(item)">
								<view class="func-list">
									<view :class="['cuIcon-' + item.cuIcon,'text-' + item.color, 'func-icon']"></view>
									<text>{{item.name}}</text>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<bottom-drawer v-model="showVip" title="开通会员">
			<view>
				<view class="margin-tb">关注【阿壹族】公众号，回复关键字【One能聊天】</view>
				<image src="https://cdn7.aezo.cn/common/qrcode/ayz_qrcode.jpg"
					mode="aspectFit" width="100" height="100"></image>
			</view>
		</bottom-drawer>
		<bottom-drawer v-model="showNum" title="次数包">
			<view>
				<view class="margin-tb">关注【阿壹族】公众号，回复关键字【One能聊天】</view>
				<image src="https://cdn7.aezo.cn/common/qrcode/ayz_qrcode.jpg"
					mode="aspectFit" width="100" height="100"></image>
			</view>
		</bottom-drawer>
		<bottom-drawer v-model="showCustomer" title="客服领次数">
			<view>
				<view class="margin-tb">关注【阿壹族】公众号，回复关键字【One能聊天】</view>
				<image src="https://cdn7.aezo.cn/common/qrcode/ayz_qrcode.jpg"
					mode="aspectFit" width="100" height="100"></image>
			</view>
		</bottom-drawer>
	</view>
</template>

<script>
	import BottomDrawer from '@/components/bottom-drawer/bottom-drawer.vue'
	import { mapGetters } from 'vuex'
	export default {
		components: { BottomDrawer },
		props: {
			chatAsset: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				show: true,
				showVip: false,
				showNum: false,
				showCustomer: false,
				cuIconList: [{
					cuIcon: 'vip',
					color: 'orange',
					name: '开通会员',
					callback: () => {
						this.showVip = true
					}
				}, {
					cuIcon: 'baby',
					color: 'blue',
					name: '次数包',
					callback: () => {
						this.showNum = true
					}
				}, {
					cuIcon: 'service',
					color: 'cyan',
					name: '客服领次数',
					callback: () => {
						this.showCustomer = true
					}
				}, {
					cuIcon: 'flashlightclose',
					color: 'red',
					name: '清除记忆',
					callback: () => {
						this.$squni.setStorageSync('chatHistory', [])
						this.$squni.toast('清除记忆成功', 'success')
					}
				}]
			};
		},
		computed: {
			...mapGetters([
				'usernameSimple'
			]),
			showFuncList () {
				return this.showVip || this.showNum || this.showCustomer
			}
		},
		watch: {
			showFuncList (n, o) {
				if (n !== o) {
					this.show = !n
				}
			}
		},
		methods: {
			open() {
				this.show = true;
			},
			close() {
				this.show = false;
			},
			navigateTo(item) {
				if (item.callback) {
					item.callback(item)
				} else {
					uni.navigateTo({
						url: item.page
					})
				}
			}
		}
	};
</script>

<style>
	.cu-dialog {
		border-radius: 20upx 20upx 0 0;
	}
	.content {
		background-color: #fff;
		padding-top: 16upx;
	}
	.cu-bar .content {
		color: #333333;
		font-weight: 700;
	}
	.cu-list {
		margin-bottom: 20upx;
	}
	.cu-list .func-list {
		margin: 0upx 20upx -20upx 0;
	}
	.cu-list .func-list .func-icon {
		border: 1px solid #eee;
		border-radius: 8upx;
		padding: 30upx;
		font-size: 80upx;
	}
</style>
