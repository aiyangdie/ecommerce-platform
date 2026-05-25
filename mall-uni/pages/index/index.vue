<template>
  <view class="container">
    <view class="hero">
      <text class="title">高品质电商平台</text>
      <text class="sub">uni-app · H5 / 小程序 / 公众号 / App</text>
    </view>
    <view class="card" v-if="loading">
      <text>加载中…</text>
    </view>
    <view class="card" v-else-if="error">
      <text class="err">{{ error }}</text>
      <text class="hint">请启动 docker 与 mall-api</text>
    </view>
    <view v-else class="list">
      <view class="card item" v-for="p in products" :key="p.id">
        <text class="name">{{ p.title }}</text>
        <text class="desc">{{ p.subTitle || '—' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onMounted, ref } from 'vue'

const API_BASE = 'http://127.0.0.1:8080'
const loading = ref(true)
const error = ref('')
const products = ref([])

onMounted(() => {
  uni.request({
    url: API_BASE + '/api/v1/products',
    success: (res) => {
      const body = res.data
      if (body && body.code === 0) {
        products.value = body.data || []
      } else {
        error.value = body?.message || '请求失败'
      }
      loading.value = false
    },
    fail: () => {
      error.value = '无法连接 API'
      loading.value = false
    },
  })
})
</script>

<style scoped>
.container { padding: 24rpx; }
.hero {
  padding: 48rpx 24rpx;
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  border-radius: 16rpx;
  margin-bottom: 24rpx;
}
.title { display: block; color: #fff; font-size: 40rpx; font-weight: 700; }
.sub { display: block; color: rgba(255,255,255,0.9); font-size: 26rpx; margin-top: 12rpx; }
.card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 16rpx; }
.item .name { font-size: 32rpx; font-weight: 600; display: block; }
.item .desc { font-size: 26rpx; color: #888; margin-top: 8rpx; display: block; }
.err { color: #e74c3c; }
.hint { display: block; font-size: 24rpx; color: #999; margin-top: 8rpx; }
</style>
