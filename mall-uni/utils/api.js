// uni-app 请求基址：Docker H5 用相对路径；本地开发改 localhost
export const API_BASE = (typeof window !== 'undefined' && window.location.port === '8082')
  ? '/api/v1'
  : 'http://127.0.0.1:8080/api/v1'

export function getToken() {
  return uni.getStorageSync('mall_token') || ''
}

export function request(path, options = {}) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: API_BASE + path,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        Authorization: getToken() ? 'Bearer ' + getToken() : '',
        ...(options.header || {}),
      },
      success: (res) => {
        const body = res.data
        if (body && body.code === 0) resolve(body.data)
        else reject(new Error(body?.message || '请求失败'))
      },
      fail: reject,
    })
  })
}
