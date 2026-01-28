export function formatTimeToNow(timeStr) {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = (now - time) / 1000

  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
  if (diff < 172800) return '昨天'

  // 默认返回日期
  return timeStr.substring(0, 16)
}
