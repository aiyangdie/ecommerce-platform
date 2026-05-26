<template>
  <view class="page">
    <view class="hero">
      <text class="title">高品质商城</text>
      <text class="sub">开源 Spring Boot + uni-app</text>
    </view>
    <view v-if="loading" class="tip">加载中…</view>
    <view v-for="p in products" :key="p.id" class="card">
      <image class="cover" :src="p.coverUrl || defaultImg" mode="aspectFill" />
      <view class="info">
        <text class="name">{{ p.title }}</text>
        <text class="desc">{{ p.subTitle }}</text>
        <text class="price">¥{{ p.minPrice }}</text>
        <view class="actions">
          <button size="mini" @click="addCart(p.defaultSkuId)">加购</button>
          <button size="mini" type="warn" @click="buy(p.defaultSkuId)">购买</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { request } from '../../utils/api.js'
export default {
  data() {
    return { products: [], loading: true, defaultImg: 'https://picsum.photos/200' }
  },
  onShow() { this.load() },
  methods: {
    async load() {
      this.loading = true
      try { this.products = await request('/products') }
      catch (e) { uni.showToast({ title: e.message || '加载失败', icon: 'none' }) }
      finally { this.loading = false }
    },
    needLogin() {
      if (!uni.getStorageSync('mall_token')) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        uni.switchTab({ url: '/pages/login/login' })
        return true
      }
      return false
    },
    async addCart(skuId) {
      if (this.needLogin()) return
      await request('/cart/items', { method: 'POST', data: { skuId, quantity: 1 } })
      uni.showToast({ title: '已加入购物车' })
    },
    async buy(skuId) {
      if (this.needLogin()) return
      const user = uni.getStorageSync('mall_user') || {}
      const order = await request('/orders', {
        method: 'POST',
        data: { skuId, quantity: 1, receiverName: '用户', receiverPhone: user.mobile || '13800000000', receiverAddr: '演示地址' }
      })
      await request('/orders/' + order.orderNo + '/pay', { method: 'POST' })
      uni.showToast({ title: '下单成功' })
      uni.switchTab({ url: '/pages/orders/orders' })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; }
.hero { background: linear-gradient(135deg,#ff6b35,#f7931e); padding: 32rpx; border-radius: 16rpx; margin-bottom: 24rpx; }
.title { color: #fff; font-size: 36rpx; font-weight: 700; display: block; }
.sub { color: rgba(255,255,255,.9); font-size: 24rpx; }
.card { display: flex; background: #fff; border-radius: 12rpx; padding: 20rpx; margin-bottom: 16rpx; }
.cover { width: 160rpx; height: 160rpx; border-radius: 8rpx; }
.info { flex: 1; margin-left: 16rpx; }
.name { font-size: 30rpx; font-weight: 600; display: block; }
.desc { font-size: 24rpx; color: #888; display: block; margin: 8rpx 0; }
.price { color: #ff6b35; font-weight: 700; font-size: 32rpx; }
.actions { margin-top: 12rpx; display: flex; gap: 12rpx; }
.tip { text-align: center; color: #999; padding: 40rpx; }
</style>
