import request from './request'

export function loginApi(data) {
  return request({
    url: '/api/merchant/login',
    method: 'post',
    data
  })
}

export function refreshTokenApi(refreshToken) {
  return request({
    url: '/api/merchant/refresh',
    method: 'post',
    data: { refreshToken }
  })
}
