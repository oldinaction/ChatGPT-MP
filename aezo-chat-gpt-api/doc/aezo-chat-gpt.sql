/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : aezo-chat-gpt

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 31/03/2023 16:27:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mt_user_asset
-- ----------------------------
DROP TABLE IF EXISTS `mt_user_asset`;
CREATE TABLE `mt_user_asset` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `biz_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务类型. 如Chat',
  `asset_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '财产类型. N:次数, DFN:每日免费次数',
  `asset` decimal(10,2) DEFAULT NULL COMMENT '财产值',
  `version` int DEFAULT '0',
  `valid_status` int DEFAULT '1',
  `creator` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `updater` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of mt_user_asset
-- ----------------------------
BEGIN;
INSERT INTO `mt_user_asset` VALUES ('1641718083358904320', '1641718053394796544', 'Chat', 'N', 10.00, 0, 1, NULL, '2023-03-31 16:24:27', NULL, '2023-03-31 16:24:27');
INSERT INTO `mt_user_asset` VALUES ('1641718083367292928', '1641718053394796544', 'Chat', 'DFN', 3.00, 2, 1, NULL, '2023-03-31 16:24:27', NULL, '2023-03-31 16:24:27');
COMMIT;

-- ----------------------------
-- Table structure for mt_user_asset_his
-- ----------------------------
DROP TABLE IF EXISTS `mt_user_asset_his`;
CREATE TABLE `mt_user_asset_his` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `biz_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务类型. 如Chat',
  `asset_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '财产类型',
  `asset` decimal(10,2) DEFAULT NULL COMMENT '财产变化值',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '说明',
  `valid_status` int DEFAULT '1',
  `creator` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of mt_user_asset_his
-- ----------------------------
BEGIN;
INSERT INTO `mt_user_asset_his` VALUES ('1641718167819603968', '1641718053394796544', 'Chat', 'DFN', -1.00, '聊天消耗', 1, '1641718053394796544', '2023-03-31 16:24:47');
INSERT INTO `mt_user_asset_his` VALUES ('1641718422644547584', '1641718053394796544', 'Chat', 'DFN', -1.00, '聊天消耗', 1, '1641718053394796544', '2023-03-31 16:25:48');
COMMIT;

-- ----------------------------
-- Table structure for mt_user_info
-- ----------------------------
DROP TABLE IF EXISTS `mt_user_info`;
CREATE TABLE `mt_user_info` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `nick_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '昵称',
  `sex` smallint DEFAULT '0' COMMENT '性别. 0：未知；1：男；2：女',
  `full_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '全名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '头像',
  `card_no` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '身份证号',
  `logout_time` timestamp NULL DEFAULT NULL COMMENT '最近登出时间',
  `user_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户等级',
  `balance` int DEFAULT '0' COMMENT '余额',
  `income_history` int DEFAULT '0' COMMENT '累计收益',
  `extract_money` int DEFAULT '0' COMMENT '已提现',
  `not_over_money` int DEFAULT '0' COMMENT '未结算',
  `inviter` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邀请人',
  `version` int DEFAULT '0' COMMENT '版本号',
  `valid_status` smallint DEFAULT '1' COMMENT '有效状态. 1：有效；0：无效',
  `creator` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updater` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户信息';

-- ----------------------------
-- Records of mt_user_info
-- ----------------------------
BEGIN;
INSERT INTO `mt_user_info` VALUES ('1641718053394796544', 'mt_2ckmmyos59', NULL, NULL, NULL, 'mt_2ckmmyos59', 0, NULL, NULL, NULL, NULL, 'M1', 0, 0, 0, 0, NULL, 0, 1, NULL, '2023-03-31 16:24:20', NULL, '2023-03-31 16:24:20');
COMMIT;

-- ----------------------------
-- Table structure for mt_user_oauth
-- ----------------------------
DROP TABLE IF EXISTS `mt_user_oauth`;
CREATE TABLE `mt_user_oauth` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户ID',
  `login_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录类型. Wechat/Alipay',
  `nick_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '昵称',
  `openid` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `valid_status` smallint DEFAULT '1' COMMENT '有效状态. 1：有效；0：无效',
  `creator` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updater` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户第三方登录信息';

-- ----------------------------
-- Records of mt_user_oauth
-- ----------------------------
BEGIN;
INSERT INTO `mt_user_oauth` VALUES ('1641718053428350976', '1641718053394796544', 'WXMP', NULL, 'oDwBF4_TITj9UuctIaQfxDINd5RE', 1, NULL, '2023-03-31 16:24:20', NULL, '2023-03-31 16:24:20');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
