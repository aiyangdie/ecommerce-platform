<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http, { isDemoMode } from '../api/http'

const router = useRouter()
const form = ref({ username: 'admin', password: 'admin123' })
const loading = ref(false)
const error = ref('')

async function submit() {
  loading.value = true
  error.value = ''
  try {
    if (isDemoMode) {
      if (form.value.username !== 'admin' || form.value.password !== 'admin123') {
        throw new Error('演示账号：admin / admin123')
      }
      localStorage.setItem('admin_token', 'demo')
      router.push('/')
      return
    }
    const res = await http.post('/api/v1/admin/auth/login', form.value)
    localStorage.setItem('admin_token', res.data.data.token)
    router.push('/')
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="box">
      <h2>商城管理后台</h2>
      <p v-if="isDemoMode" class="hint warn">当前为在线演示（静态页），登录后浏览演示数据</p>
      <p v-else class="hint">默认账号 admin / admin123 · 需 mall-api 已启动</p>
      <el-form @submit.prevent="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-bottom:12px" />
        <el-button type="primary" native-type="submit" :loading="loading" style="width:100%">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
}
.box { width: 400px; padding: 8px; }
h2 { margin: 0 0 8px; text-align: center; }
.hint { text-align: center; color: #888; font-size: 13px; margin-bottom: 20px; }
.hint.warn { color: #e6a23c; }
</style>
