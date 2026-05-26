<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http, { demoGet, isDemoModeActive } from '../api/http'
import { ElMessage } from 'element-plus'

interface Order {
  orderNo: string
  status: number
  payAmount: number
  receiverName: string
  receiverPhone?: string
  receiverAddr?: string
  createdAt?: string
  items?: { skuName: string; quantity: number }[]
}

const list = ref<Order[]>([])
const loading = ref(false)
const statusFilter = ref<number | ''>('')
const statusMap: Record<number, string> = { 10: '待支付', 20: '待发货', 30: '待收货', 40: '已完成', 50: '已取消' }
const readOnly = isDemoModeActive()

async function load() {
  loading.value = true
  try {
    if (readOnly) {
      let data = await demoGet<Order[]>('orders.json')
      if (statusFilter.value !== '') {
        data = data.filter((o) => o.status === statusFilter.value)
      }
      list.value = data
      return
    }
    const params = statusFilter.value !== '' ? { status: statusFilter.value } : {}
    const res = await http.get('/api/v1/admin/orders', { params })
    list.value = res.data.data
  } finally {
    loading.value = false
  }
}

async function ship(orderNo: string) {
  if (readOnly) return ElMessage.warning('演示模式不可操作')
  await http.post(`/api/v1/admin/orders/${orderNo}/ship`)
  ElMessage.success('已发货')
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="toolbar">
      <h2>订单管理</h2>
      <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 140px" @change="load">
        <el-option label="待支付" :value="10" />
        <el-option label="待发货" :value="20" />
        <el-option label="待收货" :value="30" />
        <el-option label="已完成" :value="40" />
        <el-option label="已取消" :value="50" />
      </el-select>
    </div>
    <el-alert v-if="readOnly" title="演示数据（只读）" type="warning" show-icon :closable="false" style="margin-bottom: 12px" />
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="createdAt" label="下单时间" width="170" />
      <el-table-column label="商品" min-width="160">
        <template #default="{ row }">{{ (row.items || []).map((i) => i.skuName + 'x' + i.quantity).join(', ') }}</template>
      </el-table-column>
      <el-table-column prop="payAmount" label="金额" width="90">
        <template #default="{ row }">¥{{ row.payAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 40 ? 'success' : row.status === 50 ? 'info' : 'warning'">
            {{ statusMap[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收货人" width="90" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 20" link type="primary" @click="ship(row.orderNo)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar h2 { margin: 0; }
</style>
