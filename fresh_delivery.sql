/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : localhost:3306
 Source Schema         : fresh_delivery

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 24/01/2026 20:19:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '省市区',
  `detail` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '详细地址',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (4, 1, '张三', '18526222222', '广西壮族自治区 南宁市 青秀区', '广西大学', 1, '2025-12-01 18:36:38', '2025-12-01 18:36:38');
INSERT INTO `address` VALUES (5, 1, '李四', '15232333333', '广西壮族自治区 南宁市 青秀区', '广西民族大学', 0, '2025-12-01 18:37:05', '2025-12-01 18:37:05');

-- ----------------------------
-- Table structure for after_sale
-- ----------------------------
DROP TABLE IF EXISTS `after_sale`;
CREATE TABLE `after_sale`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `merchant_id` bigint NULL DEFAULT NULL,
  `type` int NULL DEFAULT NULL COMMENT '1仅退款 2退货退款',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL COMMENT '0待处理 1已同意 2已拒绝',
  `create_time` datetime NULL DEFAULT NULL,
  `handle_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of after_sale
-- ----------------------------
INSERT INTO `after_sale` VALUES (21, 2069, 1, 1, 1, '不好吃', 1, '2025-12-01 18:39:48', '2025-12-01 18:39:57');
INSERT INTO `after_sale` VALUES (22, 2071, 1, 1, 2, '不好吃', 2, '2025-12-01 18:40:44', '2025-12-01 18:40:51');

-- ----------------------------
-- Table structure for after_sale_log
-- ----------------------------
DROP TABLE IF EXISTS `after_sale_log`;
CREATE TABLE `after_sale_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `after_sale_id` bigint NOT NULL,
  `action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作内容',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作者：user/merchant/system',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of after_sale_log
-- ----------------------------
INSERT INTO `after_sale_log` VALUES (69, 21, '用户申请售后', 'user', '2025-12-01 18:39:48');
INSERT INTO `after_sale_log` VALUES (70, 21, '商家同意售后申请', 'merchant', '2025-12-01 18:39:57');
INSERT INTO `after_sale_log` VALUES (71, 22, '用户申请售后', 'user', '2025-12-01 18:40:44');
INSERT INTO `after_sale_log` VALUES (72, 22, '商家拒绝售后申请', 'merchant', '2025-12-01 18:40:51');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL,
  `product_id` bigint NULL DEFAULT NULL,
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `quantity` int NULL DEFAULT NULL,
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `checked` tinyint NULL DEFAULT 1 COMMENT '1选中 0未选中',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 129 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (9, '水果', '2025-12-01 18:37:17', '2025-12-01 18:53:47');
INSERT INTO `category` VALUES (10, '生鲜', '2025-12-01 18:37:23', '2025-12-01 18:37:23');
INSERT INTO `category` VALUES (11, '蔬菜', '2025-12-01 18:53:52', '2025-12-01 18:53:52');
INSERT INTO `category` VALUES (12, '海鲜', '2025-12-01 18:54:00', '2025-12-01 18:54:00');
INSERT INTO `category` VALUES (13, '干货', '2025-12-01 18:54:08', '2025-12-01 18:54:08');

-- ----------------------------
-- Table structure for merchant_user
-- ----------------------------
DROP TABLE IF EXISTS `merchant_user`;
CREATE TABLE `merchant_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of merchant_user
-- ----------------------------
INSERT INTO `merchant_user` VALUES (1, 1, 'admin', '$2a$10$bAy7iq9Lgq7CNRWXgy37Eu/5Hruzr5I23D34AQFqyDYXswpriX8.i', '2025-11-16 00:49:35', '2025-11-16 01:38:57');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `receiver_id` bigint NOT NULL COMMENT '商家后台用户ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `status` tinyint NOT NULL COMMENT '订单状态：0待支付 1已支付 2配送中 3已完成 4已取消',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容，例如：订单已配送/订单已完成',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `read_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_receiver`(`receiver_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单状态消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (67, 1, 2069, 1, '您有新的订单，订单号：O1764585523190，请及时处理。', 0, '2025-12-01 18:38:43', NULL);
INSERT INTO `message` VALUES (68, 1, 2070, 4, '订单已取消，订单号：O1764585532728', 0, '2025-12-01 18:41:53', NULL);
INSERT INTO `message` VALUES (69, 1, 2071, 1, '您有新的订单，订单号：O1764585539476，请及时处理。', 0, '2025-12-01 18:38:59', NULL);
INSERT INTO `message` VALUES (70, 1, 2072, 1, '您有新的订单，订单号：O1764585671695，请及时处理。', 0, '2025-12-01 18:41:11', NULL);

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_published` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (3, '全程八折', '<p>? 今日特价：全场满30减5元！新鲜水果刚刚到货~</p>', 1, '2025-11-30 15:14:06', '2025-11-30 15:14:06');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL DEFAULT NULL,
  `product_id` bigint NULL DEFAULT NULL,
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `quantity` int NULL DEFAULT NULL,
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3113 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (3110, 2069, 31, '苹果', 6.00, 2, 'http://localhost:8080/imgs/1764585465013_1120.jpg');
INSERT INTO `order_item` VALUES (3111, 2070, 31, '苹果', 6.00, 2, 'http://localhost:8080/imgs/1764585465013_1120.jpg');
INSERT INTO `order_item` VALUES (3112, 2071, 32, '南宁苹果', 15.00, 2, 'http://localhost:8080/imgs/1764585490293_1120.jpg');
INSERT INTO `order_item` VALUES (3113, 2072, 31, '苹果', 6.00, 2, 'http://localhost:8080/imgs/1764585465013_1120.jpg');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `merchant_id` bigint NULL DEFAULT NULL,
  `total_price` decimal(10, 2) NULL DEFAULT NULL,
  `pay_price` decimal(10, 2) NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT NULL COMMENT '0待支付 1已支付 2配送中 3已完成 4已取消',
  `address_snapshot` json NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2072 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (2069, 'O1764585523190', 1, 1, 12.00, 12.00, 5, '{\"name\": \"张三\", \"phone\": \"18526222222\", \"detail\": \"广西大学\", \"region\": \"广西壮族自治区 南宁市 青秀区\"}', '', '2025-12-01 18:38:43', '2025-12-01 18:39:47');
INSERT INTO `orders` VALUES (2070, 'O1764585532728', 1, 1, 12.00, 12.00, 4, '{\"name\": \"张三\", \"phone\": \"18526222222\", \"detail\": \"广西大学\", \"region\": \"广西壮族自治区 南宁市 青秀区\"}', '', '2025-12-01 18:38:53', '2025-12-01 18:41:54');
INSERT INTO `orders` VALUES (2071, 'O1764585539476', 1, 1, 30.00, 30.00, 5, '{\"name\": \"张三\", \"phone\": \"18526222222\", \"detail\": \"广西大学\", \"region\": \"广西壮族自治区 南宁市 青秀区\"}', '', '2025-12-01 18:38:59', '2025-12-01 18:40:43');
INSERT INTO `orders` VALUES (2072, 'O1764585671695', 1, 1, 12.00, 12.00, 1, '{\"name\": \"张三\", \"phone\": \"18526222222\", \"detail\": \"广西大学\", \"region\": \"广西壮族自治区 南宁市 青秀区\"}', '', '2025-12-01 18:41:12', '2025-12-01 18:41:12');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint NULL DEFAULT NULL,
  `category_id` bigint NULL DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `stock` int NULL DEFAULT NULL,
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 1 COMMENT '1上架 0下架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品介绍',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (33, 1, 9, '广西苹果', 3.00, 30, '斤', 'http://localhost:8080/imgs/1764586679715_1120.jpg', 1, '2025-12-01 18:58:05', '2025-12-01 18:58:05', '非常有特色的广西苹果，很甜');
INSERT INTO `product` VALUES (34, 1, 10, '鸡翅', 4.50, 36, '个', 'http://localhost:8080/imgs/1764586978060_生成生鲜图片 (1).png', 1, '2025-12-01 19:03:09', '2025-12-01 19:03:09', '新鲜美味的鸡翅');
INSERT INTO `product` VALUES (35, 1, 10, '牛肉', 50.00, 30, '斤', 'http://localhost:8080/imgs/1764586995907_生成生鲜图片.png', 1, '2025-12-01 19:03:44', '2025-12-01 19:03:44', '非常新鲜的牛肉');
INSERT INTO `product` VALUES (36, 1, 10, '猪排', 30.00, 60, '斤', 'http://localhost:8080/imgs/1764587032736_生成生鲜图片 (2).png', 1, '2025-12-01 19:04:20', '2025-12-01 19:04:20', '非常新鲜的猪排');
INSERT INTO `product` VALUES (37, 1, 9, '蓝莓', 6.00, 60, '盒', 'http://localhost:8080/imgs/1764587099899_生成生鲜图片 (1).png', 1, '2025-12-01 19:05:34', '2025-12-01 19:05:34', '南非采购的新鲜蓝莓');
INSERT INTO `product` VALUES (38, 1, 9, '草莓', 15.00, 55, '盒', 'http://localhost:8080/imgs/1764587142644_生成生鲜图片.png', 1, '2025-12-01 19:06:06', '2025-12-01 19:06:06', '美国进口草莓');
INSERT INTO `product` VALUES (39, 1, 12, '澳洲龙虾', 150.00, 10, '个', 'http://localhost:8080/imgs/1764587184370_生成生鲜图片 (2).png', 1, '2025-12-01 19:06:38', '2025-12-01 19:06:38', '澳洲进口大龙虾');
INSERT INTO `product` VALUES (40, 1, 12, '三文鱼肉', 30.00, 90, '盒', 'http://localhost:8080/imgs/1764587229644_生成生鲜图片 (3).png', 1, '2025-12-01 19:07:10', '2025-12-01 19:07:10', '进口三文鱼');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` enum('USER','MERCHANT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `openid`(`openid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'oWCK16r46shGqrPoo2quVWHN0TeU', NULL, '嘻嘻', 'http://localhost:8080/imgs/1764587285398_BZXgJMRieJpGadd5d12c42b296eb6566898d2bcbf74e.jpeg', NULL, NULL, '2025-12-01 19:08:05');

SET FOREIGN_KEY_CHECKS = 1;
