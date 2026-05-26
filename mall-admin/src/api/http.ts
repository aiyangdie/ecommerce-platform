import axios from 'axios'

/** GitHub Pages 等静态托管无法转发 POST /api，启用演示模式 */
export const isDemoMode = import.meta.env.VITE_DEMO_MODE === 'true'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
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
    const status = err.response?.status
    if (status === 405) {
      return Promise.reject(
        new Error(
          '接口 405：当前页面无法访问后端 API。请运行 install.ps1 启动 Docker 后访问 http://127.0.0.1:8081 ，或确认 mall-api 已启动（:8080）'
        )
      )
    }
    if (status === 502 || status === 503) {
      return Promise.reject(new Error('后端服务未启动，请先运行 install.ps1 或启动 mall-api'))
    }
    const msg = err.response?.data?.message || err.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)

/** 演示模式：从静态 JSON 读取 */
export async function demoGet<T>(path: string): Promise<T> {
  const base = import.meta.env.BASE_URL
  const res = await fetch(`${base}demo/${path}`)
  if (!res.ok) throw new Error('演示数据加载失败')
  return res.json() as Promise<T>
}

export default http
