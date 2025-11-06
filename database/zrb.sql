/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80042
 Source Host           : localhost:3306
 Source Schema         : zrb

 Target Server Type    : MySQL
 Target Server Version : 80042
 File Encoding         : 65001

 Date: 04/11/2025 23:22:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `door_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '门牌号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `user_id` int NULL DEFAULT NULL COMMENT '关联用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '联系人信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, '集源酒店', '集源路110号', '李四', '13899887788', 2);
INSERT INTO `address` VALUES (2, '新华书店', '银江路10号', '武五', '13988997755', 2);
INSERT INTO `address` VALUES (3, '瑞幸咖啡', '石鼓路10号', '张三', '13988999977', 4);
INSERT INTO `address` VALUES (4, '众人帮快递中心', '29', '张三三', '13244445555', 2);
INSERT INTO `address` VALUES (5, 'C22', '144', '李思思', '11144445555', 2);
INSERT INTO `address` VALUES (6, '第三食堂', 'C18', '张昊', '11111111134', 4);
INSERT INTO `address` VALUES (7, 'C22宿舍', '145', '张同学', '11111111111', 6);
INSERT INTO `address` VALUES (8, 'A5教学楼', '203', '杨同学', '11122233334', 6);

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色标识',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', 'admin', '管理员', 'http://localhost:9090/files/1697438073596-avatar.png', 'ADMIN', '13677889922', 'admin@xm.com');

-- ----------------------------
-- Table structure for certification
-- ----------------------------
DROP TABLE IF EXISTS `certification`;
CREATE TABLE `certification`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL COMMENT '账号',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '本人照片',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `card_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号码',
  `card1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证正面',
  `card2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证反面',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '常住地址',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核' COMMENT '审核状态',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核理由',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '认证信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of certification
-- ----------------------------
INSERT INTO `certification` VALUES (5, 4, '李同学', 'http://localhost:9090/files/1699194313119-t8UFVshz4lsF06baad89133eedfdc4a7cd08001f6699.jpg', '11', '111', 'http://localhost:9090/files/1699194317034-dVhg1fUJVIVy5dc26ce3186e10e259e4f235a3d15cbc.png', 'http://localhost:9090/files/1699194319129-28Aw6j9eo3kP35977e7dc2159a734f3c81de460e4b8d.jpg', '111', '通过', '可以的');
INSERT INTO `certification` VALUES (6, 6, '张同学', 'http://localhost:9090/files/1711772138611-NwH2XrnfJ1zj94ff6ef47d9878606adc6cbca5cf35f4.jpg', '11122234567', '411303200202190017', 'http://localhost:9090/files/1711772171264-n0DNpXOEF2cq658a2c42a4e5ba2fd53e0adc4d24e78e.jpg', 'http://localhost:9090/files/1711772186818-1hvQ3SzbDpuAb130c87b75a17febd7f7ff8934238178.png', '广东省惠州市大亚湾区', '待审核', NULL);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评论',
  `star` double(10, 1) NULL DEFAULT 0.0 COMMENT '评分',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID',
  `accept_id` int NULL DEFAULT NULL COMMENT '骑手ID',
  `order_id` int NULL DEFAULT NULL COMMENT '订单ID',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC, `order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (4, '很好', 5.0, 4, 4, 8, '2023-11-07 17:55:41');
INSERT INTO `comment` VALUES (5, '很好', 4.0, 2, 4, 16, '2023-11-07 22:01:48');
INSERT INTO `comment` VALUES (6, '骑手速度快，服务好', 5.0, 2, 4, 19, '2024-03-08 17:08:51');
INSERT INTO `comment` VALUES (7, '棒，够快', 5.0, 6, 4, 21, '2024-03-31 16:38:35');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建时间',
  `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (4, '国庆活动', '国庆节期间，下单两次，第二次享八折优惠！！！', '2025-10-01', 'admin');
INSERT INTO `notice` VALUES (5, '今日收入', '今日收入情况进账380元', '2025-10-12', 'admin');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单编号',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品名称',
  `descr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品图片',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品类型',
  `weight` double(10, 2) NULL DEFAULT NULL COMMENT '物品重量',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '小费',
  `user_id` int NULL DEFAULT NULL COMMENT '发起人ID',
  `accept_id` int NULL DEFAULT NULL COMMENT '接单人ID',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建时间',
  `accept_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '接单时间',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单状态',
  `address_id` int NULL DEFAULT NULL COMMENT '取货地址ID',
  `target_id` int NULL DEFAULT NULL COMMENT '收货地址ID',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (5, '1719669205431197696', '代取餐品', '22', 'http://localhost:9090/files/1698836060819-mci8bHdCFD0a2dcd5d60c696c47fdfe0b482c18de0ea.jpg', '代取餐品', 22.00, 1.00, 4, 4, '2025-11-06 18:30:00', '2023-11-06 18:30:33', '待送达', 2, 3, '22');
INSERT INTO `orders` VALUES (6, '1719669473724047360', '代买零食', '333', 'http://localhost:9090/files/1698836124879-bSLefCiz9Wow698f208b46b5b780a231593517b6b26e.jpg', '代买零食', 333.00, 1.00, 4, 4, '2025-11-06 18:30:00', '2023-11-06 18:28:55', '待收货', 2, 3, '333');
INSERT INTO `orders` VALUES (7, '1719671274686861312', '京东快递', '京东', 'http://localhost:9090/files/1698836542845-bcfQ4IO34nxV75ea040ecc58b96bc2f5290daf215105.jpg', '代送鲜花', 3.00, 1.00, 4, 4, '2025-11-06 18:30:00', '2023-11-06 18:28:16', '待收货', 2, 3, '33');
INSERT INTO `orders` VALUES (8, '1721814675729326080', '咖啡', '咖啡', 'http://localhost:9090/files/1699347580329-KzrOgoVQP4fF7cd21a9de1f499f2a532d7b9cc928f02.png', '代取餐品', 0.10, 5.00, 4, 4, '2025-11-07 16:59:43', '2023-11-07 16:59:47', '已完成', 3, 2, '11');
INSERT INTO `orders` VALUES (12, '1721829686623334400', '咖啡', '拿铁咖啡', 'http://localhost:9090/files/1699351156259-ro1VULycqcky7cd21a9de1f499f2a532d7b9cc928f02.png', '代取餐品', 0.10, 5.00, 4, NULL, '2025-11-07 17:59:21', NULL, '已取消', 3, 2, '热的');
INSERT INTO `orders` VALUES (13, '1721830169651965952', '箱子', '大箱子', 'http://localhost:9090/files/1699351274465-4NbP2V57vfbi36c2653fbd444b2ace224379a884f734.png', '代拿快递', 10.00, 10.00, 4, NULL, '2025-11-07 18:01:17', NULL, '已取消', 2, 1, NULL);
INSERT INTO `orders` VALUES (14, '1721882847046336512', '111', '11', 'http://localhost:9090/files/1699363834164-QQ截图20231022184324.png', '代拿快递', 1.00, 1.00, 2, 4, '2025-11-07 21:30:36', '2023-11-07 21:30:45', '待评价', 2, 1, '1');
INSERT INTO `orders` VALUES (15, '1721885679384371200', '555', '55', 'http://localhost:9090/files/1699364509996-QQ截图20231022184302.png', '代取餐品', 5.00, 7.00, 2, 4, '2025-11-07 21:41:51', '2023-11-07 21:42:12', '待评价', 2, 1, NULL);
INSERT INTO `orders` VALUES (16, '1721886994533523456', '666', '666', 'http://localhost:9090/files/1699364823795-QQ截图20231022185805.png', '代取餐品', 6.00, 8.00, 2, 4, '2025-11-07 21:47:05', '2023-11-07 21:47:13', '已完成', 2, 1, NULL);
INSERT INTO `orders` VALUES (17, '1721887440199294976', '11112323', '11', 'http://localhost:9090/files/1699364930694-QQ截图20231022190130.png', '代取餐品', 1.00, 1.00, 2, NULL, '2025-11-07 21:48:51', NULL, '已取消', 2, 1, NULL);
INSERT INTO `orders` VALUES (18, '1763780186005839872', '快递包裹', NULL, 'http://localhost:9090/files/1709352928113-jOdw2FAVbp9d22964f821d7bcd8d718a954ceb7e0ed5.png', '代拿快递', 1.00, 5.00, 2, NULL, '2025-03-02 12:15:40', NULL, '已取消', 4, 5, '送到门口');
INSERT INTO `orders` VALUES (19, '1766027739791368192', '三只松鼠', NULL, 'http://localhost:9090/files/1709888792398-4ui9UTCHzdrc658a2c42a4e5ba2fd53e0adc4d24e78e.jpg', '代拿快递', 1.00, 5.00, 2, 4, '2025-03-08 17:06:39', '2024-03-08 17:07:08', '已完成', 4, 5, '送到门口');
INSERT INTO `orders` VALUES (20, '1770015521857716224', '盒饭', '1', 'http://localhost:9090/files/1710839558658-HjNWqyS6P47M22964f821d7bcd8d718a954ceb7e0ed5.png', '代取餐品', 1.00, 5.00, 4, NULL, '2025-03-19 17:12:40', NULL, '已取消', 3, 6, NULL);
INSERT INTO `orders` VALUES (21, '1774354929297764352', '可乐', NULL, 'http://localhost:9090/files/1711874146079-hYDAsHB6kuUC2ada7599dba08de1c52f3e24dc13e220.png', '代拿快递', 0.50, 5.00, 6, 4, '2025-03-31 16:35:55', '2024-03-31 16:36:16', '已完成', 7, 8, '快速');
INSERT INTO `orders` VALUES (22, '1780174996413829120', '快递', NULL, 'http://localhost:9090/files/1713261766269-JCL8Voxubmi722964f821d7bcd8d718a954ceb7e0ed5.png', '代拿快递', 2.00, 5.00, 6, 4, '2025-04-16 18:02:47', '2024-04-16 18:03:10', '待评价', 8, 7, NULL);

-- ----------------------------
-- Table structure for records
-- ----------------------------
DROP TABLE IF EXISTS `records`;
CREATE TABLE `records`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '事件',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
  `user_id` int NULL DEFAULT NULL COMMENT '用户',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时间',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收支明细' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of records
-- ----------------------------
INSERT INTO `records` VALUES (1, '咖啡订单', 5.00, 4, '2025-11-07 16:59:43', '支出');
INSERT INTO `records` VALUES (2, '钱包充值', 18.00, 4, '2025-11-07 17:00:00', '充值');
INSERT INTO `records` VALUES (3, '外卖订单', 5.00, 4, '2025-11-07 17:27:49', '支出');
INSERT INTO `records` VALUES (4, '11订单', 1.00, 4, '2025-11-07 17:30:47', '支出');
INSERT INTO `records` VALUES (5, '333订单', 5.00, 4, '2025-11-07 17:34:15', '支出');
INSERT INTO `records` VALUES (6, '咖啡订单', 5.00, 4, '2025-11-07 17:59:22', '支出');
INSERT INTO `records` VALUES (7, '箱子订单', 10.00, 4, '2025-11-07 18:01:17', '支出');
INSERT INTO `records` VALUES (8, '111订单', 1.00, 2, '2025-11-07 21:30:36', '骑手');
INSERT INTO `records` VALUES (9, '1721882847046336512订单', 1.00, 4, '2025-11-07 21:30:45', '支出');
INSERT INTO `records` VALUES (10, '1721882847046336512订单', 1.00, 4, '2025-11-07 21:30:51', '支出');
INSERT INTO `records` VALUES (11, '1721882847046336512订单', 1.00, 2, '2025-11-07 21:31:40', '支出');
INSERT INTO `records` VALUES (12, '555订单', 7.00, 2, '2025-11-07 21:41:51', '支出');
INSERT INTO `records` VALUES (13, '1721885679384371200订单', 7.00, 4, '2025-11-07 21:45:47', '骑手');
INSERT INTO `records` VALUES (14, '666订单', 8.00, 2, '2025-11-07 21:47:05', '支出');
INSERT INTO `records` VALUES (15, '666订单', 8.00, 4, '2025-11-07 21:47:38', '骑手');
INSERT INTO `records` VALUES (16, '11112323订单', 1.00, 2, '2025-11-07 21:48:51', '支出');
INSERT INTO `records` VALUES (17, '钱包充值', 6.00, 5, '2025-02-10 22:11:00', '充值');
INSERT INTO `records` VALUES (18, '快递包裹订单', 5.00, 2, '2025-03-02 12:15:40', '支出');
INSERT INTO `records` VALUES (19, '三只松鼠订单', 5.00, 2, '2025-03-08 17:06:39', '支出');
INSERT INTO `records` VALUES (20, '三只松鼠订单', 5.00, 4, '2025-03-08 17:08:35', '骑手');
INSERT INTO `records` VALUES (21, '钱包充值', 18.00, 6, '2025-03-18 15:19:14', '充值');
INSERT INTO `records` VALUES (22, '盒饭订单', 5.00, 4, '2025-03-19 17:12:40', '支出');
INSERT INTO `records` VALUES (23, '钱包充值', 6.00, 4, '2025-03-20 09:28:43', '充值');
INSERT INTO `records` VALUES (24, '可乐订单', 5.00, 6, '2025-03-31 16:35:55', '支出');
INSERT INTO `records` VALUES (25, '可乐订单', 5.00, 4, '2025-03-31 16:38:25', '骑手');
INSERT INTO `records` VALUES (26, '钱包充值', 6.00, 4, '2025-03-31 17:29:31', '充值');
INSERT INTO `records` VALUES (27, '快递订单', 5.00, 6, '2025-04-16 18:02:47', '支出');
INSERT INTO `records` VALUES (28, '快递订单', 5.00, 4, '2025-04-16 18:05:56', '骑手');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png' COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `account` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '余额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, 'acc', '123', '张同学', 'http://localhost:9090/files/1698735381175-微信截图_20231018172208.png', 'USER', '男', '13899887766', 83.00);
INSERT INTO `user` VALUES (4, 'aaa', '123', '胡同学', 'http://localhost:9090/files/1698735377207-boy.png', 'USER', '男', '13877889678', 231.00);
INSERT INTO `user` VALUES (6, 'ccc', '1234', '吴同学', 'http://localhost:9090/files/1707633571424-WBKnFz9adLbC318c0ce5904bd032a1291cba9d0b9ca8.jpg', 'USER', '男', '11133335555', 8.00);

SET FOREIGN_KEY_CHECKS = 1;
