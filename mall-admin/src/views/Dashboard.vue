<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http, { demoGet, isDemoModeActive } from '../api/http'

interface Stats {
  productCount: number
  onSaleCount: number
  orderCount: number
  pendingPayCount: number
  pendingShipCount: number
  totalGmv: number
}

const stats = ref<Stats | null>(null)
const apiStatus = ref('检测中')
const demo = isDemoModeActive()

onMounted(async () => {
  if (demo) {
    apiStatus.value = '在线演示'
    stats.value = await demoGet<Stats>('stats.json')
    return
  }
  try {
    await http.get('/api/health')
    apiStatus.value = '正常'
    const res = await http.get('/api/v1/admin/stats')
    stats.value = res.data.data
  } catch {
    apiStatus.value = '未连接'
  }
})
</script>

<template>
  <div>
    <h2>运营概览</h2>
    <el-alert
      v-if="demo"
      title="GitHub Pages 演示数据 · 完整功能请运行 install.ps1"
      type="warning"
      show-icon
      :closable="false"
      style="margin-bottom: 16px"
    />
    <el-row v-if="stats" :gutter="16">
      <el-col :span="6">
        <el-card shadow="hover"><el-statistic title="商品总数" :value="stats.productCount" /></el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><el-statistic title="在售商品" :value="stats.onSaleCount" /></el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><el-statistic title="订单总数" :value="stats.orderCount" /></el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><el-statistic title="成交 GMV" :value="stats.totalGmv" prefix="¥" /></el-card>
      </el-col>
    </el-row>
    <el-row v-if="stats" :gutter="16" style="margin-top: 16px">
      <el-col :span="8">
        <el-card shadow="hover"><el-statistic title="待支付" :value="stats.pendingPayCount" /></el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover"><el-statistic title="待发货" :value="stats.pendingShipCount" /></el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover"><el-statistic title="API" :value="apiStatus" /></el-card>
      </el-col>
    </el-row>
  </div>
</template>
