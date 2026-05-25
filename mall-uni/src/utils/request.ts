import { API_BASE } from './config'

interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export function request<T>(url: string): Promise<T> {
  return new Promise((resolve, reject) => {
    uni.request({
      url: API_BASE + url,
      success: (res) => {
        const body = res.data as ApiResult<T>
        if (body && body.code === 0) {
          resolve(body.data)
        } else {
          reject(new Error(body?.message || '请求失败'))
        }
      },
      fail: (err) => reject(err),
    })
  })
}
