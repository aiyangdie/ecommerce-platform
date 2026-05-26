<template>
  <view class="page">
    <view v-if="!items.length" class="empty">购物车为空</view>
    <view v-for="i in items" :key="i.id" class="card">
      <text class="name">{{ i.skuName }}</text>
      <text class="price">¥{{ i.price }} x {{ i.quantity }}</text>
      <button size="mini" @click="remove(i.id)">删除</button>
    </view>
    <button v-if="items.length" type="primary" class="checkout" @click="checkout">结算</button>
  </view>
</template>

<script>
import { request } from '../../utils/api.js'
export default {
  data() { return { items: [] } },
  onShow() { this.load() },
  methods: {
    async load() {
      if (!uni.getStorageSync('mall_token')) return this.items = []
      try { this.items = await request('/cart/items') } catch { this.items = [] }
    },
    async remove(id) {
      await request('/cart/items/' + id, { method: 'DELETE' })
      this.load()
    },
    async checkout() {
      const user = uni.getStorageSync('mall_user') || {}
      const order = await request('/orders/from-cart', {
        method: 'POST',
        data: { receiverName: '用户', receiverPhone: user.mobile, receiverAddr: '演示地址' }
      })
      await request('/orders/' + order.orderNo + '/pay', { method: 'POST' })
      uni.showToast({ title: '结算成功' })
      uni.switchTab({ url: '/pages/orders/orders' })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; }
.card { background: #fff; padding: 24rpx; margin-bottom: 16rpx; border-radius: 12rpx; }
.name { font-size: 28rpx; display: block; }
.price { color: #ff6b35; display: block; margin: 8rpx 0; }
.checkout { margin-top: 24rpx; }
.empty { text-align: center; color: #999; padding: 80rpx; }
</style>
