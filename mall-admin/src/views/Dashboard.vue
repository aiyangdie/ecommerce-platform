<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http, { demoGet, isDemoModeActive } from '../api/http'

const stats = ref({ products: 0, orders: 0, api: '检测中' })

onMounted(async () => {
  if (isDemoModeActive()) {
    stats.value.api = '演示模式'
    const products = await demoGet<unknown[]>('products.json')
    const orders = await demoGet<unknown[]>('orders.json')
    stats.value.products = products.length
    stats.value.orders = orders.length
    return
  }
  try {
    await http.get('/api/health')
    stats.value.api = '正常'
    const p = await http.get('/api/v1/admin/products')
    stats.value.products = p.data.data?.length ?? 0
    const o = await http.get('/api/v1/admin/orders')
    stats.value.orders = o.data.data?.length ?? 0
  } catch {
    stats.value.api = '未连接（请启动 Docker / mall-api）'
  }
})
</script>

<template>
  <div>
    <h2>运营概览</h2>
    <el-row :gutter="16">
      <el-col :span="8"><el-statistic title="API 状态" :value="stats.api" /></el-col>
      <el-col :span="8"><el-statistic title="商品数" :value="stats.products" /></el-col>
      <el-col :span="8"><el-statistic title="订单数" :value="stats.orders" /></el-col>
    </el-row>
    <el-alert
      style="margin-top:24px"
      :title="isDemoModeActive() ? 'GitHub Pages 演示：完整功能请本地运行 install.ps1' : '前后端分离：mall-api + MySQL + Redis + 本管理端 + H5'"
      type="info"
      show-icon
      :closable="false"
    />
  </div>
</template>
