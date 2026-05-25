<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import http from '../api/http'

interface Product {
  id: number
  title: string
  subTitle?: string
  status: number
}

const isOnlineDemo = import.meta.env.VITE_DEMO_MODE === 'true'
const loading = ref(false)
const products = ref<Product[]>([])
const apiStatus = ref('检测中…')
const dataSource = ref('')

const statusType = computed(() => {
  if (apiStatus.value === '正常') return 'success'
  if (isOnlineDemo) return 'info'
  return 'warning'
})

async function loadDemoProducts() {
  const base = import.meta.env.BASE_URL
  const res = await fetch(`${base}demo/products.json`)
  if (!res.ok) throw new Error('demo json')
  products.value = await res.json()
  apiStatus.value = '在线演示'
  dataSource.value = '静态演示数据（GitHub Pages）'
}

onMounted(async () => {
  loading.value = true
  try {
    const health = await http.get('/api/health')
    apiStatus.value = health.data?.data?.status === 'UP' ? '正常' : '异常'
    const res = await http.get('/api/v1/products')
    products.value = res.data?.data ?? []
    dataSource.value = 'mall-api 实时接口'
  } catch {
    if (isOnlineDemo) {
      try {
        await loadDemoProducts()
      } catch {
        apiStatus.value = '演示加载失败'
      }
    } else {
      apiStatus.value = '未连接（请启动 mall-api 与 MySQL）'
      dataSource.value = ''
    }
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page">
    <header>
      <h1>电商管理后台</h1>
      <p>Vue 3 + Element Plus · 前后端分离 · 开源框架</p>
      <el-alert
        v-if="isOnlineDemo"
        title="当前为 GitHub Pages 在线演示环境"
        type="success"
        :closable="false"
        show-icon
        style="margin-top: 12px"
      />
    </header>
    <el-card shadow="hover">
      <template #header>系统状态</template>
      <p>后端 API：<el-tag :type="statusType">{{ apiStatus }}</el-tag></p>
      <p v-if="dataSource" class="meta">数据来源：{{ dataSource }}</p>
    </el-card>
    <el-card shadow="hover" style="margin-top: 16px" v-loading="loading">
      <template #header>商品列表</template>
      <el-table :data="products" stripe empty-text="暂无数据">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="subTitle" label="副标题" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">上架</el-tag>
            <el-tag v-else type="info">下架</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.page {
  max-width: 960px;
  margin: 0 auto;
  padding: 32px 24px;
}
header h1 {
  margin: 0 0 8px;
  font-size: 28px;
}
header p {
  color: #666;
  margin: 0 0 8px;
}
.meta {
  margin-top: 8px;
  font-size: 13px;
  color: #888;
}
</style>
