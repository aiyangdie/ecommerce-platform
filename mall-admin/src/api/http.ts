import axios from 'axios'

/** 构建时或 GitHub Pages 运行时均走演示模式，避免 POST /api 导致 405 */
export function isDemoModeActive(): boolean {
  if (import.meta.env.VITE_DEMO_MODE === 'true') return true
  if (typeof window !== 'undefined' && window.location.hostname.includes('github.io')) {
    return true
  }
  return localStorage.getItem('admin_token') === 'demo'
}

/** @deprecated 使用 isDemoModeActive */
export const isDemoMode = isDemoModeActive()

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  if (isDemoModeActive()) {
    return Promise.reject(new Error('DEMO_MODE_SKIP_API'))
  }
  const token = localStorage.getItem('admin_token')
  if (token && token !== 'demo') {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (res) => {
    const body = res.data
    if (body && typeof body.code === 'number' && body.code !== 0) {
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return res
  },
  (err) => {
    if (err.message === 'DEMO_MODE_SKIP_API') {
      return Promise.reject(err)
    }
    const status = err.response?.status
    if (status === 405) {
      return Promise.reject(
        new Error('当前为在线演示页，请使用 admin/admin123 登录（无需后端）')
      )
    }
    if (status === 502 || status === 503) {
      return Promise.reject(new Error('后端未启动，请运行 install.ps1'))
    }
    const msg = err.response?.data?.message || err.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)

export async function demoGet<T>(path: string): Promise<T> {
  const base = import.meta.env.BASE_URL
  const res = await fetch(`${base}demo/${path}`)
  if (!res.ok) throw new Error('演示数据加载失败')
  return res.json() as Promise<T>
}

export default http
