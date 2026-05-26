<template>
  <view class="page">
    <view v-if="product" class="content">
      <image class="cover" :src="product.coverUrl" mode="aspectFill" />
      <text class="title">{{ product.title }}</text>
      <text class="sub">{{ product.subTitle }}</text>
      <view class="skus">
        <view
          v-for="s in product.skus"
          :key="s.id"
          class="sku"
          :class="{ on: skuId === s.id }"
          @click="skuId = s.id"
        >{{ s.skuName }} ¥{{ s.price }}</view>
      </view>
      <view class="actions">
        <button size="mini" @click="addCart">加购</button>
        <button size="mini" type="warn" @click="buy">购买</button>
      </view>
    </view>
  </view>
</template>

<script>
import { request } from '../../utils/api.js'
export default {
  data() {
    return { id: null, product: null, skuId: null }
  },
  onLoad(q) {
    this.id = q.id
    this.load()
  },
  methods: {
    async load() {
      this.product = await request('/products/' + this.id)
      this.skuId = this.product.defaultSkuId || this.product.skus[0].id
    },
    needLogin() {
      if (!uni.getStorageSync('mall_token')) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        uni.switchTab({ url: '/pages/login/login' })
        return true
      }
      return false
    },
    async addCart() {
      if (this.needLogin()) return
      await request('/cart/items', { method: 'POST', data: { skuId: this.skuId, quantity: 1 } })
      uni.showToast({ title: '已加购' })
    },
    async buy() {
      if (this.needLogin()) return
      const user = uni.getStorageSync('mall_user') || {}
      const order = await request('/orders', {
        method: 'POST',
        data: {
          skuId: this.skuId,
          quantity: 1,
          receiverName: '用户',
          receiverPhone: user.mobile || '13800000000',
          receiverAddr: '演示地址',
        },
      })
      await request('/orders/' + order.orderNo + '/pay', { method: 'POST' })
      uni.showToast({ title: '购买成功' })
      uni.switchTab({ url: '/pages/orders/orders' })
    },
  },
}
</script>

<style scoped>
.page { padding: 24rpx; }
.cover { width: 100%; height: 400rpx; border-radius: 12rpx; }
.title { font-size: 36rpx; font-weight: 700; display: block; margin-top: 16rpx; }
.sub { font-size: 26rpx; color: #888; display: block; margin: 8rpx 0 16rpx; }
.skus { display: flex; flex-wrap: wrap; gap: 12rpx; }
.sku { padding: 12rpx 20rpx; border: 1px solid #ddd; border-radius: 8rpx; font-size: 26rpx; }
.sku.on { border-color: #ff6b35; color: #ff6b35; background: #fff5f0; }
.actions { margin-top: 32rpx; display: flex; gap: 16rpx; }
</style>
