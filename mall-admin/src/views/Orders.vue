<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http, { demoGet, isDemoMode } from '../api/http'
import { ElMessage } from 'element-plus'

interface Order {
  orderNo: string
  status: number
  payAmount: number
  receiverName: string
  items?: { skuName: string; quantity: number }[]
}

const list = ref<Order[]>([])
const loading = ref(false)
const statusMap: Record<number, string> = { 10: '待支付', 20: '待发货', 30: '待收货', 40: '已完成', 50: '已取消' }
const readOnly = isDemoMode || (() => localStorage.getItem('admin_token') === 'demo')()

async function load() {
  loading.value = true
  try {
    if (readOnly) {
      list.value = await demoGet<Order[]>('orders.json')
      return
    }
    const res = await http.get('/api/v1/admin/orders')
    list.value = res.data.data
  } finally {
    loading.value = false
  }
}

async function ship(orderNo: string) {
  if (readOnly) {
    ElMessage.warning('演示模式不可发货')
    return
  }
  await http.post(`/api/v1/admin/orders/${orderNo}/ship`)
  ElMessage.success('已发货')
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <h2>订单管理</h2>
    <el-alert v-if="readOnly" title="演示数据（只读）" type="warning" show-icon :closable="false" style="margin-bottom:12px" />
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column label="商品">
        <template #default="{ row }">{{ (row.items || []).map(i => i.skuName + 'x' + i.quantity).join(', ') }}</template>
      </el-table-column>
      <el-table-column prop="payAmount" label="金额" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">{{ statusMap[row.status] }}</template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收货人" width="100" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button v-if="row.status === 20" link type="primary" @click="ship(row.orderNo)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
