import request from '@/api/request'

// 获取当前商家后台用户 ID
function getReceiverId() {
  return localStorage.getItem('merchantUserId')
}

// 分页查询消息
export function getMessages(data) {
  return request({
    url: `/api/message/page/${getReceiverId()}`,
    method: 'post',
    data
  })
}

// 查询未读数量
export function getUnreadCount() {
  return request({
    url: `/api/message/unread/${getReceiverId()}`,
    method: 'get'
  })
}

// 标记消息已读
export function markRead(id) {
  return request({
    url: `/api/message/read/${id}`,
    method: 'post'
  })
}
