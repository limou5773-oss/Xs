import request from "@/api/request";

export function loginApi(data) {
  return request({
    url: "/api/merchant/login",
    method: "post",
    data
  });
}
