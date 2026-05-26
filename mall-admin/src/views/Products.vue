<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import http, { demoGet, isDemoModeActive } from '../api/http'
import { ElMessage } from 'element-plus'

interface Sku { id?: number; skuName: string; price: number; stock: number }
interface Product {
  id: number
  title: string
  subTitle?: string
  coverUrl?: string
  status: number
  skus?: Sku[]
  minPrice?: number
}

const list = ref<Product[]>([])
const loading = ref(false)
const dialog = ref(false)
const keyword = ref('')
const readOnly = isDemoModeActive()

const form = ref({
  id: null as number | null,
  title: '',
  subTitle: '',
  coverUrl: '',
  status: 1,
  skus: [{ skuName: '默认', price: 99, stock: 100 }] as Sku[],
})

const filtered = computed(() => {
  if (!keyword.value.trim()) return list.value
  const k = keyword.value.trim().toLowerCase()
  return list.value.filter(
    (p) => p.title.toLowerCase().includes(k) || (p.subTitle || '').toLowerCase().includes(k)
  )
})

async function load() {
  loading.value = true
  try {
    if (readOnly) {
      list.value = await demoGet<Product[]>('products.json')
      return
    }
    const res = await http.get('/api/v1/admin/products')
    list.value = res.data.data
  } finally {
    loading.value = false
  }
}

function openCreate() {
  if (readOnly) return ElMessage.warning('演示模式不可编辑')
  form.value = { id: null, title: '', subTitle: '', coverUrl: '', status: 1, skus: [{ skuName: '默认', price: 99, stock: 100 }] }
  dialog.value = true
}

function openEdit(row: Product) {
  if (readOnly) return ElMessage.warning('演示模式不可编辑')
  const sku = row.skus?.[0] || { skuName: '默认', price: 99, stock: 100 }
  form.value = {
    id: row.id,
    title: row.title,
    subTitle: row.subTitle || '',
    coverUrl: row.coverUrl || '',
    status: row.status,
    skus: [{ skuName: sku.skuName, price: Number(sku.price), stock: sku.stock }],
  }
  dialog.value = true
}

async function save() {
  await http.post('/api/v1/admin/products', form.value)
  ElMessage.success('保存成功')
  dialog.value = false
  load()
}

async function toggleStatus(row: Product) {
  if (readOnly) return ElMessage.warning('演示模式不可修改')
  const status = row.status === 1 ? 0 : 1
  await http.put(`/api/v1/admin/products/${row.id}/status`, { status })
  ElMessage.success('已更新')
  load()
}

function stockSum(row: Product) {
  return (row.skus || []).reduce((s, k) => s + (k.stock || 0), 0)
}

onMounted(load)
</script>

<template>
  <div>
    <div class="toolbar">
      <h2>商品管理</h2>
      <div class="actions">
        <el-input v-model="keyword" placeholder="搜索商品" clearable style="width: 200px; margin-right: 8px" />
        <el-button type="primary" @click="openCreate">新增商品</el-button>
      </div>
    </div>
    <el-alert v-if="readOnly" title="演示数据（只读）" type="warning" show-icon :closable="false" style="margin-bottom: 12px" />
    <el-table :data="filtered" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <el-image v-if="row.coverUrl" :src="row.coverUrl" style="width: 48px; height: 48px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="140" />
      <el-table-column label="价格" width="90">
        <template #default="{ row }">¥{{ row.minPrice ?? row.skus?.[0]?.price }}</template>
      </el-table-column>
      <el-table-column label="库存" width="80">
        <template #default="{ row }">{{ stockSum(row) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link @click="toggleStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="form.id ? '编辑商品' : '新增商品'" width="520px">
      <el-form label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="副标题"><el-input v-model="form.subTitle" /></el-form-item>
        <el-form-item label="封面URL"><el-input v-model="form.coverUrl" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="form.skus[0].skuName" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.skus[0].price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.skus[0].stock" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; flex-wrap: wrap; gap: 12px; }
.toolbar h2 { margin: 0; }
.actions { display: flex; align-items: center; }
</style>
