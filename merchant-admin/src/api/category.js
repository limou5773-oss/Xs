import request from '@/api/request'

// 分页查询
export const getCategoryPage = (data) => request.post('/api/category/page', data)

// 非分页查询（列表）
export const getCategories = () => request.get('/api/category/list')

// 新增分类
export const addCategory = (data) => request.post('/api/category/add', data)

// 更新分类
export const updateCategory = (data) => request.post('/api/category/update', data)

// 删除分类
export const deleteCategory = (id) => request.delete(`/api/category/delete/${id}`)
