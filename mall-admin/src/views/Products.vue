<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '../api/http'
import { ElMessage } from 'element-plus'

interface Product {
  id: number
  title: string
  subTitle?: string
  coverUrl?: string
  status: number
  skus?: { skuName: string; price: number; stock: number }[]
}

const list = ref<Product[]>([])
const loading = ref(false)
const dialog = ref(false)
const form = ref({
  id: null as number | null,
  title: '',
  subTitle: '',
  coverUrl: '',
  status: 1,
  skus: [{ skuName: '默认', price: 99, stock: 100 }],
})

async function load() {
  loading.value = true
  try {
    const res = await http.get('/api/v1/admin/products')
    list.value = res.data.data
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.value = { id: null, title: '', subTitle: '', coverUrl: '', status: 1, skus: [{ skuName: '默认', price: 99, stock: 100 }] }
  dialog.value = true
}

async function save() {
  await http.post('/api/v1/admin/products', form.value)
  ElMessage.success('保存成功')
  dialog.value = false
  load()
}

async function toggleStatus(row: Product) {
  const status = row.status === 1 ? 0 : 1
  await http.put(`/api/v1/admin/products/${row.id}/status`, { status })
  ElMessage.success('已更新')
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="toolbar">
      <h2>商品管理</h2>
      <el-button type="primary" @click="openCreate">新增商品</el-button>
    </div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="subTitle" label="副标题" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link @click="toggleStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" title="新增商品" width="520px">
      <el-form label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="副标题"><el-input v-model="form.subTitle" /></el-form-item>
        <el-form-item label="封面URL"><el-input v-model="form.coverUrl" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="form.skus[0].skuName" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.skus[0].price" :min="0" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.skus[0].stock" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar h2 { margin: 0; }
</style>
