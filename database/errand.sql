/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : errand

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 28/11/2025 22:17:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for evaluation
-- ----------------------------
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation`  (
  `evaluation_id` int NOT NULL AUTO_INCREMENT COMMENT '评价编号',
  `order_id` int NOT NULL COMMENT '订单编号',
  `review` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价（0 差评，1 好评）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`evaluation_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation
-- ----------------------------
INSERT INTO `evaluation` VALUES (1, 1, '1', '2025-11-28 21:53:55');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `order_id` int NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `client_id` int NOT NULL COMMENT '委托人编号',
  `helper_id` int NULL DEFAULT NULL COMMENT '跑腿员编号',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单主题',
  `address` varchar(75) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单指定地点',
  `description` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单描述',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `help_time` datetime NULL DEFAULT NULL COMMENT '订单指定时间',
  `reward` decimal(4, 2) NULL DEFAULT 0.00 COMMENT '悬赏金额',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单状态（0 待帮助，1 已接单，2 已完成，3 已取消）',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `accept_time` datetime NULL DEFAULT NULL COMMENT '接单时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, 101, 103, '帮买瑞幸', '瑞幸咖啡', '帮我买一杯咖啡，带到图书馆门口给我', '13804969358', '2025-11-28 21:55:00', 21.00, '2', '2025-11-28 21:50:34', '2025-11-28 21:52:45', '2025-11-28 21:53:53');
INSERT INTO `order` VALUES (2, 101, NULL, '帮我取快递', '菜鸟驿站', '帮我取件，取件码XXX', '13804969358', '2025-11-29 12:30:00', 3.00, '0', '2025-11-28 21:54:21', NULL, NULL);
INSERT INTO `order` VALUES (3, 102, NULL, '取报告单', '传达室', '帮我拿XXX报告单到教室来', '13812345678', '2025-11-29 10:50:00', 1.50, '0', '2025-11-28 21:57:54', NULL, NULL);
INSERT INTO `order` VALUES (4, 102, 103, '帮我占座', '图书馆', '二楼XXX靠窗的座位', '13812345678', '2025-11-28 22:15:00', 5.00, '1', '2025-11-28 22:00:40', '2025-11-28 22:03:48', NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `username` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户密码',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像（路径）',
  `student_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `certificate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证件（路径）',
  `role` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '角色（0 委托人，1 跑腿员）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uniq_phone`(`phone` ASC) USING BTREE COMMENT '手机号不可重复'
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (101, '13804969358', '用户101', '123456', 'avatar/A-Default.png', NULL, NULL, '0', '2025-11-28 21:36:25', '2025-11-28 21:46:12');
INSERT INTO `user` VALUES (102, '13812345678', '用户102', '123456', 'avatar/A-Default.png', NULL, NULL, '0', '2025-11-28 21:37:25', '2025-11-28 21:46:07');
INSERT INTO `user` VALUES (103, '13012345678', '用户103', '123456', 'avatar/A-Default.png', '202427337001', 'certificate/C103.png', '1', '2025-11-28 21:38:43', '2025-11-28 21:46:02');

-- ----------------------------
-- Table structure for wallet
-- ----------------------------
DROP TABLE IF EXISTS `wallet`;
CREATE TABLE `wallet`  (
  `wallet_id` int NOT NULL AUTO_INCREMENT COMMENT '钱包编号',
  `user_id` int NOT NULL COMMENT '用户编号',
  `total_income` decimal(6, 2) NULL DEFAULT 0.00 COMMENT '总收益',
  `balance` decimal(6, 2) NULL DEFAULT 0.00 COMMENT '可用余额',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`wallet_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '钱包表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wallet
-- ----------------------------
INSERT INTO `wallet` VALUES (1, 103, 21.00, 21.00, '2025-11-28 21:53:53', '2025-11-28 21:53:53');

-- ----------------------------
-- Table structure for withdrawal
-- ----------------------------
DROP TABLE IF EXISTS `withdrawal`;
CREATE TABLE `withdrawal`  (
  `withdrawal_id` int NOT NULL AUTO_INCREMENT COMMENT '提现编号',
  `user_id` int NOT NULL COMMENT '用户编号',
  `amount` decimal(6, 2) NULL DEFAULT NULL COMMENT '提现金额',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提现状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`withdrawal_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '提现表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of withdrawal
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
