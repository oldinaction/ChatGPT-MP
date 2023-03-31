<template>
	<view class="cu-modal bottom-modal" :class="show ? 'show' : ''" @click="close">
		<view class="cu-dialog" @click.stop="() => {}">
			<view class="cu-bar bg-white justify-end">
				<view class="content">{{ title }}</view>
				<view class="action" @click="close">
					<text class="cuIcon-close"></text>
				</view>
			</view>
			<view class="padding-sm content">
				<view class="padding-lr-xs">
					<slot></slot>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		props: {
			value: {
				type: Boolean,
				default: false
			},
			title: {
				type: String,
				default: '菜单'
			}
		},
		data() {
			return {
				show: this.value
			};
		},
		watch: {
			value (n, o) {
				this.show = this.value
			}
		},
		methods: {
			open() {
				this.show = true;
				this.$emit('input', this.show)
			},
			close() {
				this.show = false;
				this.$emit('input', this.show)
			}
		}
	};
</script>

<style>
	.cu-dialog {
		border-radius: 20upx 20upx 0 0;
		padding-bottom: 40upx;
		background: #fff;
	}
	.content {
		background-color: #fff;
		padding-top: 16upx;
	}
	.cu-bar .content {
		color: #333333;
		font-weight: 700;
	}
</style>
