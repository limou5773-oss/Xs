技术栈
1. 微信小程序端
微信小程序原生开发
WXML / WXSS / JavaScript
2. PC 端后台
Vue 3
Vite
Element Plus
Axios
ECharts
Vue Router
3. 后端
Spring Boot 3
MyBatis / MyBatis-Plus
PageHelper
MySQL
Redis
JWT
Knife4j
功能模块
用户端（微信小程序）
微信授权登录
首页商品推荐
商品分类与搜索
商品详情查看
购物车管理
订单提交
模拟支付
我的订单
收货地址管理
个人信息维护
售后申请
订单评价
消息通知查看
商家端（PC 后台）
后台登录
控制台统计
商品管理
商品分类管理
订单处理
售后审核
公告管理
商品评价管理
运行环境
推荐环境
JDK 21
Maven 3.9+
MySQL 8.0
Redis
Node.js 18+
微信开发者工具
启动说明
一、启动后端 xsps
使用 IDEA 打开 xsps 项目
创建数据库，例如：
CREATE DATABASE fresh_delivery DEFAULT CHARACTER SET utf8mb4;
修改配置文件：

路径：

xsps/src/main/resources/application.yml

重点修改以下内容：

数据库地址
数据库用户名密码
Redis 配置（如果项目里有单独配置）
微信小程序 appid 和 secret
启动 Spring Boot 主程序

默认端口：

8080
二、启动商家后台 merchant-admin
进入项目目录：
cd merchant-admin
安装依赖：
npm install
启动开发环境：
npm run dev
打包项目：
npm run build
三、启动微信小程序 community-fresh
打开微信开发者工具
导入项目目录：
community-fresh
检查接口地址配置
编译运行
主要页面
微信小程序端
首页
分类页
商品列表页
商品详情页
购物车页
订单页
订单详情页
地址管理页
个人中心页
售后申请页
评价页
商家后台
控制台
商品管理页
订单管理页
售后审核页
评价管理页
接口说明

小程序端当前主要通过以下接口与后端交互：

/api/auth/wxLogin
/api/user/*
/api/address/*
/api/cart/*
/api/product/*
/api/category/*
/api/order/*
/api/notice/*
/api/afterSale/*
/api/message/*
/api/review/*
数据库说明

系统主要涉及以下数据表：

user 用户表
category 商品分类表
product 商品表
orders 订单主表
order_item 订单明细表
address 收货地址表
after_sale 售后表
message 消息通知表
review 评价表
项目特点
采用前后端分离架构
基于微信小程序，使用门槛低
支持社区生鲜配送业务场景
同时包含用户端与商家端
适合作为毕业设计项目展示
注意事项
本项目中的部分配置文件可能包含本地开发信息，上传 GitHub 前建议先处理敏感信息。
建议不要将以下内容直接提交到公开仓库：
数据库真实密码
微信小程序 appid / secret
Redis 密码
本地缓存文件
merchant-admin/node_modules 不建议上传到 GitHub，应通过 npm install 重新安装依赖。
Git 提交建议

如果你准备上传到 GitHub，建议先补充 .gitignore，至少忽略以下内容：

node_modules/
dist/
target/
.idea/
.vscode/
*.log
.DS_Store
项目用途

本项目主要用于：

本科毕业设计
Java Web 综合实训
微信小程序开发实践
Vue + Spring Boot 前后端分离项目练习
