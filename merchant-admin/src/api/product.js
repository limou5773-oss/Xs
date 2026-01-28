import request from "@/api/request"

// ⭐ 分页查询
export function productPage(data) {
  return request({
    url: "/api/product/page",
    method: "post",
    data
  })
}

// ⭐ 获取单个商品详情
export function getProductById(id) {
  return request({
    url: `/api/product/detail/${id}`,  // ★ 跟后端保持一致
    method: "get"
  })
}

// 添加商品
export function addProduct(data) {
  return request({
    url: "/api/product/add",
    method: "post",
    data
  })
}

// 更新商品
export function updateProduct(data) {
  return request({
    url: "/api/product/update",
    method: "post",
    data
  })
}

// 删除商品
export function deleteProduct(id) {
  return request({
    url: `/api/product/delete/${id}`,
    method: "delete"
  })
}
