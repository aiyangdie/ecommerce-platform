<template>
  <view class="page">
    <view class="box">
      <text class="title">手机号登录</text>
      <input v-model="mobile" placeholder="手机号" class="input" />
      <input v-model="code" placeholder="验证码 123456" class="input" />
      <button type="primary" @click="login">登录</button>
      <text class="hint">演示验证码固定 123456</text>
      <view v-if="user" class="user">
        <text>已登录：{{ user.nickname }} ({{ user.mobile }})</text>
        <button size="mini" @click="logout">退出</button>
      </view>
    </view>
  </view>
</template>

<script>
import { request } from '../../utils/api.js'
export default {
  data() { return { mobile: '13800138000', code: '123456', user: null } },
  onShow() { this.user = uni.getStorageSync('mall_user') || null },
  methods: {
    async login() {
      const data = await request('/auth/login', { method: 'POST', data: { mobile: this.mobile, code: this.code } })
      uni.setStorageSync('mall_token', data.token)
      uni.setStorageSync('mall_user', data.user)
      this.user = data.user
      uni.showToast({ title: '登录成功' })
    },
    logout() {
      uni.removeStorageSync('mall_token')
      uni.removeStorageSync('mall_user')
      this.user = null
    }
  }
}
</script>

<style scoped>
.page { padding: 32rpx; }
.box { background: #fff; padding: 32rpx; border-radius: 16rpx; }
.title { font-size: 32rpx; font-weight: 600; display: block; margin-bottom: 24rpx; }
.input { border: 1px solid #ddd; padding: 20rpx; margin-bottom: 16rpx; border-radius: 8rpx; }
.hint { font-size: 24rpx; color: #999; display: block; margin-top: 16rpx; }
.user { margin-top: 24rpx; padding-top: 24rpx; border-top: 1px solid #eee; }
</style>
