<template>
  <view class="page">
    <view v-if="!orders.length" class="empty">暂无订单</view>
    <view v-for="o in orders" :key="o.orderNo" class="card">
      <text class="no">{{ o.orderNo }}</text>
      <text class="status">{{ statusMap[o.status] }}</text>
      <text class="items">{{ formatItems(o.items) }}</text>
      <text class="price">¥{{ o.payAmount }}</text>
      <button v-if="o.status===10" size="mini" type="primary" @click="pay(o.orderNo)">支付</button>
      <button v-if="o.status===30" size="mini" @click="confirm(o.orderNo)">确认收货</button>
    </view>
  </view>
</template>

<script>
import { request } from '../../utils/api.js'
export default {
  data() {
    return {
      orders: [],
      statusMap: { 10: '待支付', 20: '待发货', 30: '待收货', 40: '已完成', 50: '已取消' }
    }
  },
  onShow() { this.load() },
  methods: {
    formatItems(items) {
      return (items || []).map(i => i.skuName + 'x' + i.quantity).join(', ')
    },
    async load() {
      if (!uni.getStorageSync('mall_token')) return this.orders = []
      try { this.orders = await request('/orders') } catch { this.orders = [] }
    },
    async pay(no) {
      await request('/orders/' + no + '/pay', { method: 'POST' })
      this.load()
    },
    async confirm(no) {
      await request('/orders/' + no + '/confirm', { method: 'POST' })
      this.load()
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; }
.card { background: #fff; padding: 24rpx; margin-bottom: 16rpx; border-radius: 12rpx; }
.no { font-weight: 600; display: block; }
.status { color: #ff6b35; font-size: 24rpx; }
.items { color: #666; font-size: 24rpx; display: block; margin: 8rpx 0; }
.price { font-weight: 700; display: block; margin-bottom: 8rpx; }
.empty { text-align: center; color: #999; padding: 80rpx; }
</style>
