import request from "@/api/request";

// ⭐ 改成 POST + PageParam ⭐
export function noticePage(data) {
  return request({
    url: "/api/admin/notice/page",
    method: "post",
    data,
  });
}

export function noticeDetail(id) {
  return request({
    url: `/api/admin/notice/${id}`,
    method: "get",
  });
}

export function noticeCreate(data) {
  return request({
    url: "/api/admin/notice/create",
    method: "post",
    data,
  });
}

export function noticeUpdate(data) {
  return request({
    url: "/api/admin/notice/update",
    method: "post",
    data,
  });
}

export function noticeDelete(id) {
  return request({
    url: `/api/admin/notice/${id}`,
    method: "delete",
  });
}
