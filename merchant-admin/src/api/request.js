import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router";

// 创建 axios 实例
const service = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 8000
});

// Token 工具方法
const getAccessToken = () => localStorage.getItem("accessToken");
const getRefreshToken = () => localStorage.getItem("refreshToken");

const setTokens = (accessToken, refreshToken) => {
  if (accessToken) localStorage.setItem("accessToken", accessToken);
  if (refreshToken) localStorage.setItem("refreshToken", refreshToken);
};

// 请求拦截器
service.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) config.headers.Authorization = "Bearer " + token;
  return config;
});

// 是否正在刷新 token
let isRefreshing = false;
let queue = [];

// 响应拦截器
service.interceptors.response.use(
  (res) => res.data,
  async (error) => {
    const { response, config } = error;

    if (!response) {
      ElMessage.error("网络连接失败！请检查网络");
      return Promise.reject(error);
    }

    if (response.status === 401) {
      const refreshToken = getRefreshToken();

      if (!refreshToken) {
        ElMessage.error("登录已过期，请重新登录");
        localStorage.clear();
        router.push("/login");
        return Promise.reject(error);
      }

      // 已在刷新 → 加入队列等待
      if (isRefreshing) {
        return new Promise((resolve) => {
          queue.push((token) => {
            config.headers.Authorization = "Bearer " + token;
            resolve(service(config));
          });
        });
      }

      // 开始刷新 Token
      isRefreshing = true;

      try {
        const result = await service.post("/api/merchant/refresh", {
          refreshToken,
        });

        if (result.code !== 200) throw new Error(result.msg);

        const { accessToken, refreshToken: newRefresh } = result.data;

        setTokens(accessToken, newRefresh);

        // 处理等待队列
        queue.forEach((cb) => cb(accessToken));
        queue = [];

        config.headers.Authorization = "Bearer " + accessToken;
        return service(config);
      } catch (e) {
        ElMessage.error("登录信息已失效，请重新登录");
        localStorage.clear();
        router.push("/login");
      } finally {
        isRefreshing = false;
      }
    }

    ElMessage.error(response.data?.msg || `请求错误：${response.status}`);
    return Promise.reject(error);
  }
);

export default service;
