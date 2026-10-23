-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: Debuff
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `debuff_announcement`
--

DROP TABLE IF EXISTS `debuff_announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) NOT NULL COMMENT '公告标题',
  `content` text COMMENT '公告内容',
  `announcement_type` char(1) DEFAULT '1' COMMENT '类型(0通知 1公告)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1关闭)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(256) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_announcement`
--

LOCK TABLES `debuff_announcement` WRITE;
/*!40000 ALTER TABLE `debuff_announcement` DISABLE KEYS */;
INSERT INTO `debuff_announcement` VALUES (4,'扣1送火麒麟','<p>在评论区扣1送火麒麟到邮件</p>','2','0','','2026-08-27 18:39:16','2026-08-27 18:39:15','');
/*!40000 ALTER TABLE `debuff_announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_buy_order`
--

DROP TABLE IF EXISTS `debuff_buy_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_buy_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '求购用户ID',
  `skin_id` bigint NOT NULL COMMENT '饰品定义ID',
  `price` decimal(10,2) NOT NULL COMMENT '求购单价',
  `status` tinyint DEFAULT '0' COMMENT '状态(0有效 1已完成 2已取消)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_skin_id` (`skin_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='求购单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_buy_order`
--

LOCK TABLES `debuff_buy_order` WRITE;
/*!40000 ALTER TABLE `debuff_buy_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `debuff_buy_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_favorite`
--

DROP TABLE IF EXISTS `debuff_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `listing_id` bigint NOT NULL COMMENT '在售饰品ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_listing` (`user_id`,`listing_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_favorite`
--

LOCK TABLES `debuff_favorite` WRITE;
/*!40000 ALTER TABLE `debuff_favorite` DISABLE KEYS */;
/*!40000 ALTER TABLE `debuff_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_inventory`
--

DROP TABLE IF EXISTS `debuff_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `skin_id` bigint NOT NULL COMMENT '关联饰品定义ID',
  `float_value` decimal(8,6) DEFAULT '0.000000' COMMENT '磨损度数值(0.00~1.00)',
  `wear_name` varchar(32) DEFAULT '' COMMENT '磨损名称(自动生成)',
  `sticker_info` varchar(512) DEFAULT '' COMMENT '贴纸信息(JSON格式)',
  `inspect_link` varchar(512) DEFAULT '' COMMENT '游戏内检视链接',
  `source` varchar(32) DEFAULT 'upload' COMMENT '来源(purchase/upload/trade)',
  `status` tinyint DEFAULT '0' COMMENT '状态(0在库 1在售 2冻结)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_skin_id` (`skin_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户库存表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_inventory`
--

LOCK TABLES `debuff_inventory` WRITE;
/*!40000 ALTER TABLE `debuff_inventory` DISABLE KEYS */;
INSERT INTO `debuff_inventory` VALUES (3,1,5,0.050000,'崭新出厂','','','upload',1,'2026-08-25 13:04:23','2026-08-27 18:03:34'),(4,2,3,0.350000,'久经沙场','','','upload',1,'2026-08-25 13:04:23','2026-08-27 18:25:46'),(5,2,4,0.030000,'崭新出厂','','','upload',1,'2026-08-25 13:04:23','2026-08-26 17:34:05'),(9,100,2,0.120000,'略有磨损','','','purchase',1,'2026-08-26 17:32:34','2026-08-27 11:59:05'),(10,1,6,0.000000,'','','','upload',1,'2026-08-26 17:34:05','2026-08-27 18:03:25'),(11,1,8,0.000000,'','','','upload',1,'2026-08-26 17:34:05','2026-08-27 18:03:29'),(12,2,9,0.000000,'','','','upload',1,'2026-08-26 17:34:05','2026-08-26 17:34:05'),(18,100,1,0.250000,'久经沙场','','','purchase',1,'2026-08-26 18:54:10','2026-08-27 11:15:20'),(20,1,17,NULL,NULL,NULL,NULL,'upload',1,'2026-08-27 11:56:50','2026-08-27 18:03:20'),(21,100,7,0.100000,'略有磨损','','','purchase',0,'2026-08-27 18:02:41','2026-08-27 18:02:41'),(22,1,11,NULL,'',NULL,NULL,'purchase',1,'2026-08-27 18:08:00','2026-08-27 18:08:06');
/*!40000 ALTER TABLE `debuff_inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_order`
--

DROP TABLE IF EXISTS `debuff_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_no` varchar(32) NOT NULL DEFAULT '' COMMENT '??????',
  `skin_id` bigint NOT NULL COMMENT '饰品ID',
  `inventory_id` bigint DEFAULT NULL COMMENT '????ID(????)',
  `listing_id` bigint DEFAULT NULL COMMENT '????ID(????)',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `price` decimal(10,2) NOT NULL COMMENT '成交价格',
  `platform_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `seller_income` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time` datetime DEFAULT NULL,
  `cancel_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_skin` (`skin_id`),
  KEY `idx_buyer` (`buyer_id`),
  KEY `idx_seller` (`seller_id`),
  KEY `idx_status` (`status`),
  KEY `idx_inventory_id` (`inventory_id`),
  KEY `idx_listing_id` (`listing_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_order`
--

LOCK TABLES `debuff_order` WRITE;
/*!40000 ALTER TABLE `debuff_order` DISABLE KEYS */;
INSERT INTO `debuff_order` VALUES (7,'ORD202608261540197430',1,1,2,2,1,1000.00,50.00,950.00,1,'2026-08-26 15:40:20','2026-08-26 15:40:37',NULL,'2026-08-26 15:40:19'),(8,'ORD202608261540500336',2,2,3,2,1,2000.00,100.00,1900.00,2,'2026-08-26 15:40:50',NULL,'2026-08-26 15:48:31','2026-08-26 15:40:50'),(9,'ORD202608261731396673',2,2,3,100,1,2000.00,100.00,1900.00,1,'2026-08-26 17:31:40','2026-08-26 17:32:34',NULL,'2026-08-26 17:31:39'),(11,'ORD202608261854027401',1,8,9,100,2,388.00,19.40,368.60,1,'2026-08-26 18:54:02','2026-08-26 18:54:11',NULL,'2026-08-26 18:54:02'),(12,'ORD202608271106210932',11,13,17,100,1,160.00,8.00,152.00,2,'2026-08-27 11:06:22',NULL,'2026-08-27 11:06:42','2026-08-27 11:06:22'),(13,'ORD202608271115037918',11,13,17,100,1,160.00,8.00,152.00,1,'2026-08-27 11:15:04','2026-08-27 11:15:08',NULL,'2026-08-27 11:15:03'),(14,'ORD202608271802009918',7,6,7,100,2,167.00,8.35,158.65,1,'2026-08-27 18:02:00','2026-08-27 18:02:42',NULL,'2026-08-27 18:02:00'),(15,'ORD202608271803135169',11,19,22,1,100,500.00,25.00,475.00,1,'2026-08-27 18:03:14','2026-08-27 18:08:01',NULL,'2026-08-27 18:03:13'),(16,'ORD202608271825287225',3,4,5,100,2,336.00,16.80,319.20,2,'2026-08-27 18:05:38',NULL,'2026-08-27 18:25:47','2026-08-27 18:25:38');
/*!40000 ALTER TABLE `debuff_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_price_history`
--

DROP TABLE IF EXISTS `debuff_price_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_price_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `skin_id` bigint NOT NULL COMMENT '饰品定义ID',
  `avg_price` decimal(10,2) DEFAULT '0.00' COMMENT '当日均价',
  `min_price` decimal(10,2) DEFAULT '0.00' COMMENT '当日最低价',
  `max_price` decimal(10,2) DEFAULT '0.00' COMMENT '当日最高价',
  `volume` int DEFAULT '0' COMMENT '当日成交量',
  `record_date` date NOT NULL COMMENT '记录日期',
  PRIMARY KEY (`id`),
  KEY `idx_skin_date` (`skin_id`,`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='价格历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_price_history`
--

LOCK TABLES `debuff_price_history` WRITE;
/*!40000 ALTER TABLE `debuff_price_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `debuff_price_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_skin`
--

DROP TABLE IF EXISTS `debuff_skin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_skin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `skin_name` varchar(128) NOT NULL COMMENT '饰品名称',
  `weapon_type_id` bigint NOT NULL COMMENT '关联武器类型ID',
  `quality_id` bigint NOT NULL COMMENT '关联品质ID',
  `image_url` varchar(512) DEFAULT '' COMMENT '饰品图片URL',
  `description` text COMMENT '饰品描述',
  `min_price` decimal(10,2) DEFAULT '0.00' COMMENT '建议最低价格',
  `status` char(1) DEFAULT '0' COMMENT '状态(0上架 1下架)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_weapon_type` (`weapon_type_id`),
  KEY `idx_quality` (`quality_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='饰品定义表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_skin`
--

LOCK TABLES `debuff_skin` WRITE;
/*!40000 ALTER TABLE `debuff_skin` DISABLE KEYS */;
INSERT INTO `debuff_skin` VALUES (1,'火蛇',1,6,'','AK-47 | 火蛇 (Field-Tested)，经典热门皮肤',522.00,'0','2026-08-25 13:04:23','2026-08-25 13:04:23'),(2,'二龙',18,5,'','AWP | 二龙 (Field-Tested)，龙主题涂装',50000.00,'0','2026-08-25 13:04:23','2026-08-27 11:59:05'),(3,'皇帝',2,6,'','M4A4 | 皇帝 (Minimal Wear)，金色龙纹',336.00,'0','2026-08-25 13:04:23','2026-08-27 11:23:54'),(4,'印花集',8,6,'','Desert Eagle | 印花集 (Factory New)',115.00,'0','2026-08-25 13:04:23','2026-08-25 13:04:23'),(5,'霓虹骑士',1,6,'','AK-47 | 霓虹骑士 (Factory New)',36999.00,'0','2026-08-25 13:04:23','2026-08-27 18:03:34'),(6,'印花集',3,5,'','M4A1-S | 印花集 (Field-Tested)',6222.00,'0','2026-08-25 13:04:23','2026-08-27 18:03:25'),(7,'杀意确认',9,6,'','USP-S | 杀意确认 (Minimal Wear)',167.00,'0','2026-08-25 13:04:23','2026-08-25 13:04:23'),(8,'渐变之色',38,6,'','爪子刀 | 渐变之色 (Factory New)',33333.00,'0','2026-08-25 13:04:23','2026-08-27 18:03:29'),(9,'虎牙',37,6,'','蝴蝶刀 | 虎牙 (Factory New)',4549.00,'0','2026-08-25 13:04:23','2026-08-25 13:04:23'),(11,'火麒麟',1,5,'/profile/upload/2026/08/26/毛血旺_20260826180614A003.jpg','nb',5000.00,'0','2026-08-26 18:06:16','2026-08-27 18:08:06'),(17,'阿卡四七',1,5,NULL,NULL,5000.00,'0','2026-08-27 11:56:50','2026-08-27 18:03:20');
/*!40000 ALTER TABLE `debuff_skin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_skin_listing`
--

DROP TABLE IF EXISTS `debuff_skin_listing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_skin_listing` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inventory_id` bigint NOT NULL COMMENT '关联库存ID',
  `seller_id` bigint NOT NULL COMMENT '卖家用户ID',
  `skin_id` bigint NOT NULL COMMENT '关联饰品定义ID(冗余)',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `status` tinyint DEFAULT '0' COMMENT '状态(0在售 1已售 2已下架 3已取消)',
  `listing_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
  `sold_time` datetime DEFAULT NULL COMMENT '售出时间',
  `expire_time` datetime DEFAULT NULL COMMENT '自动下架时间',
  PRIMARY KEY (`id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_skin_id` (`skin_id`),
  KEY `idx_status` (`status`),
  KEY `idx_inventory_id` (`inventory_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='在售饰品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_skin_listing`
--

LOCK TABLES `debuff_skin_listing` WRITE;
/*!40000 ALTER TABLE `debuff_skin_listing` DISABLE KEYS */;
INSERT INTO `debuff_skin_listing` VALUES (2,1,1,1,1000.00,1,'2026-08-26 15:29:33','2026-08-26 15:40:19',NULL),(3,2,1,2,2000.00,1,'2026-08-26 15:29:33','2026-08-26 17:31:39',NULL),(4,3,1,5,596.00,2,'2026-08-26 17:34:05',NULL,NULL),(5,4,2,3,336.00,0,'2026-08-26 17:34:05',NULL,NULL),(6,5,2,4,115.00,0,'2026-08-26 17:34:05',NULL,NULL),(7,6,2,7,167.00,1,'2026-08-26 17:34:05','2026-08-27 18:02:00',NULL),(9,8,2,1,388.00,1,'2026-08-26 17:34:05','2026-08-26 18:54:02',NULL),(10,9,100,2,245.00,2,'2026-08-26 17:34:05',NULL,NULL),(11,10,1,6,219.00,2,'2026-08-26 17:34:05',NULL,NULL),(12,11,1,8,3899.00,2,'2026-08-26 17:34:05',NULL,NULL),(13,12,2,9,4549.00,0,'2026-08-26 17:34:05',NULL,NULL),(15,9,100,2,5000.00,2,'2026-08-26 17:45:07',NULL,NULL),(16,13,1,11,160.00,2,'2026-08-26 18:14:35',NULL,NULL),(17,13,1,11,160.00,1,'2026-08-26 18:15:58','2026-08-27 11:15:03',NULL),(22,19,100,11,500.00,1,'2026-08-27 11:15:14','2026-08-27 18:03:13',NULL),(23,18,100,1,522.00,0,'2026-08-27 11:15:20',NULL,NULL),(24,9,100,2,250.00,2,'2026-08-27 11:29:45',NULL,NULL),(25,20,1,17,666.00,2,'2026-08-27 11:56:50',NULL,NULL),(26,9,100,2,50000.00,0,'2026-08-27 11:59:05',NULL,NULL),(27,20,1,17,5000.00,0,'2026-08-27 18:03:20',NULL,NULL),(28,10,1,6,6222.00,0,'2026-08-27 18:03:25',NULL,NULL),(29,11,1,8,33333.00,0,'2026-08-27 18:03:29',NULL,NULL),(30,3,1,5,36999.00,0,'2026-08-27 18:03:34',NULL,NULL),(31,22,1,11,5000.00,0,'2026-08-27 18:08:06',NULL,NULL);
/*!40000 ALTER TABLE `debuff_skin_listing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_skin_quality`
--

DROP TABLE IF EXISTS `debuff_skin_quality`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_skin_quality` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `quality_name` varchar(32) NOT NULL COMMENT '品质名称',
  `quality_color` varchar(16) DEFAULT '#B0C3D9' COMMENT '品质颜色(十六进制)',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='饰品品质表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_skin_quality`
--

LOCK TABLES `debuff_skin_quality` WRITE;
/*!40000 ALTER TABLE `debuff_skin_quality` DISABLE KEYS */;
INSERT INTO `debuff_skin_quality` VALUES (1,'消费级','#B0C3D9',1),(2,'工业级','#5E98D9',2),(3,'军规级','#4B69FF',3),(4,'受限','#8847FF',4),(5,'保密','#D32CE6',5),(6,'隐秘','#EB4B4B',6),(7,'违禁','#E4AE39',7);
/*!40000 ALTER TABLE `debuff_skin_quality` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_user`
--

DROP TABLE IF EXISTS `debuff_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_user` (
  `id` bigint NOT NULL COMMENT '用户ID(与sys_user.user_id一致)',
  `username` varchar(64) NOT NULL COMMENT '登录用户名',
  `password` varchar(128) NOT NULL COMMENT '密码(BCrypt加密)',
  `nickname` varchar(64) DEFAULT '' COMMENT '昵称',
  `avatar` varchar(256) DEFAULT '' COMMENT '头像URL',
  `email` varchar(128) DEFAULT '' COMMENT '邮箱',
  `phone` varchar(32) DEFAULT '' COMMENT '手机号',
  `status` int DEFAULT '1' COMMENT '状态(1正常 0禁用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Debuff业务用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_user`
--

LOCK TABLES `debuff_user` WRITE;
/*!40000 ALTER TABLE `debuff_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `debuff_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_user_wallet`
--

DROP TABLE IF EXISTS `debuff_user_wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_user_wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `balance` decimal(12,2) DEFAULT '0.00' COMMENT '可用余额',
  `frozen_amount` decimal(12,2) DEFAULT '0.00' COMMENT '冻结金额',
  `total_recharge` decimal(12,2) DEFAULT '0.00' COMMENT '累计充值',
  `total_withdraw` decimal(12,2) DEFAULT '0.00' COMMENT '累计提现',
  `total_income` decimal(12,2) DEFAULT '0.00' COMMENT '累计收入',
  `total_expense` decimal(12,2) DEFAULT '0.00' COMMENT '累计支出',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户钱包表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_user_wallet`
--

LOCK TABLES `debuff_user_wallet` WRITE;
/*!40000 ALTER TABLE `debuff_user_wallet` DISABLE KEYS */;
INSERT INTO `debuff_user_wallet` VALUES (1,1,12502.00,0.00,10000.00,0.00,3002.00,500.00,'2026-08-25 13:04:23','2026-08-27 18:08:00'),(2,2,4527.25,0.00,5000.00,0.00,527.25,3000.00,'2026-08-25 13:04:23','2026-08-27 18:02:41'),(3,100,2860.00,0.00,5700.00,600.00,475.00,3371.00,'2026-08-26 17:25:08','2026-08-27 18:25:46');
/*!40000 ALTER TABLE `debuff_user_wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_wallet_log`
--

DROP TABLE IF EXISTS `debuff_wallet_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_wallet_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `wallet_id` bigint NOT NULL COMMENT '钱包ID',
  `type` tinyint NOT NULL COMMENT '类型(1充值 2提现 3买入支出 4卖出收入 5退款 6手续费)',
  `amount` decimal(12,2) NOT NULL COMMENT '金额(正收入 负支出)',
  `balance_before` decimal(12,2) DEFAULT '0.00' COMMENT '变动前余额',
  `balance_after` decimal(12,2) DEFAULT '0.00' COMMENT '变动后余额',
  `related_order_no` varchar(64) DEFAULT '' COMMENT '关联订单号',
  `remark` varchar(256) DEFAULT '' COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_wallet_id` (`wallet_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='钱包流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_wallet_log`
--

LOCK TABLES `debuff_wallet_log` WRITE;
/*!40000 ALTER TABLE `debuff_wallet_log` DISABLE KEYS */;
INSERT INTO `debuff_wallet_log` VALUES (2,2,2,3,-1000.00,5000.00,4000.00,'ORD202608261540197430','购买饰品冻结扣款','2026-08-26 15:40:19'),(3,1,1,4,950.00,10000.00,10950.00,'ORD202608261540197430','订单卖出收入','2026-08-26 15:40:36'),(4,2,2,3,-2000.00,4000.00,2000.00,'ORD202608261540500336','购买饰品冻结扣款','2026-08-26 15:40:50'),(7,2,2,5,2000.00,2000.00,4000.00,'ORD202608261540500336','订单取消退款','2026-08-26 15:48:30'),(14,100,3,3,-2000.00,5100.00,3100.00,'ORD202608261731396673','购买饰品冻结扣款','2026-08-26 17:31:39'),(15,1,1,4,1900.00,10950.00,12850.00,'ORD202608261731396673','订单卖出收入','2026-08-26 17:32:34'),(16,100,3,3,-160.00,3100.00,2940.00,'ORD202608261815289100','购买饰品冻结扣款','2026-08-26 18:15:28'),(17,100,3,5,160.00,2940.00,3100.00,'ORD202608261815289100','订单取消退款','2026-08-26 18:15:39'),(18,100,3,3,-388.00,3100.00,2712.00,'ORD202608261854027401','购买饰品冻结扣款','2026-08-26 18:54:02'),(19,2,2,4,368.60,4000.00,4368.60,'ORD202608261854027401','订单卖出收入','2026-08-26 18:54:10'),(20,100,3,3,-160.00,2712.00,2552.00,'ORD202608271106210932','购买饰品冻结扣款','2026-08-27 11:06:21'),(21,100,3,5,160.00,2552.00,2712.00,'ORD202608271106210932','订单取消退款','2026-08-27 11:06:42'),(22,100,3,3,-160.00,2712.00,2552.00,'ORD202608271115037918','购买饰品冻结扣款','2026-08-27 11:15:03'),(23,1,1,4,152.00,12850.00,13002.00,'ORD202608271115037918','订单卖出收入','2026-08-27 11:15:07'),(24,100,3,3,-167.00,2552.00,2385.00,'ORD202608271802009918','购买饰品冻结扣款','2026-08-27 18:02:00'),(25,2,2,4,158.65,4368.60,4527.25,'ORD202608271802009918','订单卖出收入','2026-08-27 18:02:41'),(26,1,1,3,-500.00,13002.00,12502.00,'ORD202608271803135169','购买饰品冻结扣款','2026-08-27 18:03:13'),(27,100,3,4,475.00,2385.00,2860.00,'ORD202608271803135169','订单卖出收入','2026-08-27 18:08:00'),(28,100,3,3,-336.00,2860.00,2524.00,'ORD202608271825287225','购买饰品冻结扣款','2026-08-27 18:25:28'),(29,100,3,5,336.00,2524.00,2860.00,'ORD202608271825287225','订单取消退款','2026-08-27 18:25:46');
/*!40000 ALTER TABLE `debuff_wallet_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debuff_weapon_type`
--

DROP TABLE IF EXISTS `debuff_weapon_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debuff_weapon_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `weapon_name` varchar(64) NOT NULL COMMENT '武器名称',
  `weapon_category` varchar(32) NOT NULL COMMENT '武器分类(rifle/pistol/smg/sniper/shotgun/knife/glove)',
  `icon` varchar(256) DEFAULT '' COMMENT '武器图标URL',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='武器类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debuff_weapon_type`
--

LOCK TABLES `debuff_weapon_type` WRITE;
/*!40000 ALTER TABLE `debuff_weapon_type` DISABLE KEYS */;
INSERT INTO `debuff_weapon_type` VALUES (1,'AK-47','rifle','',1,'2026-08-25 13:04:23'),(2,'M4A4','rifle','',2,'2026-08-25 13:04:23'),(3,'M4A1-S','rifle','',3,'2026-08-25 13:04:23'),(4,'FAMAS','rifle','',4,'2026-08-25 13:04:23'),(5,'Galil AR','rifle','',5,'2026-08-25 13:04:23'),(6,'SG 553','rifle','',6,'2026-08-25 13:04:23'),(7,'AUG','rifle','',7,'2026-08-25 13:04:23'),(8,'Desert Eagle','pistol','',10,'2026-08-25 13:04:23'),(9,'USP-S','pistol','',11,'2026-08-25 13:04:23'),(10,'Glock-18','pistol','',12,'2026-08-25 13:04:23'),(11,'P250','pistol','',13,'2026-08-25 13:04:23'),(12,'Five-SeveN','pistol','',14,'2026-08-25 13:04:23'),(13,'Tec-9','pistol','',15,'2026-08-25 13:04:23'),(14,'CZ75 Auto','pistol','',16,'2026-08-25 13:04:23'),(15,'Dual Berettas','pistol','',17,'2026-08-25 13:04:23'),(16,'P2000','pistol','',18,'2026-08-25 13:04:23'),(17,'R8 Revolver','pistol','',19,'2026-08-25 13:04:23'),(18,'MP9','smg','',20,'2026-08-25 13:04:23'),(19,'MAC-10','smg','',21,'2026-08-25 13:04:23'),(20,'UMP-45','smg','',22,'2026-08-25 13:04:23'),(21,'P90','smg','',23,'2026-08-25 13:04:23'),(22,'PP-Bizon','smg','',24,'2026-08-25 13:04:23'),(23,'MP7','smg','',25,'2026-08-25 13:04:23'),(24,'AWP','sniper','',30,'2026-08-25 13:04:23'),(25,'SSG 08','sniper','',31,'2026-08-25 13:04:23'),(26,'SCAR-20','sniper','',32,'2026-08-25 13:04:23'),(27,'G3SG1','sniper','',33,'2026-08-25 13:04:23'),(28,'Nova','shotgun','',40,'2026-08-25 13:04:23'),(29,'XM1014','shotgun','',41,'2026-08-25 13:04:23'),(30,'MAG-7','shotgun','',42,'2026-08-25 13:04:23'),(31,'Sawed-Off','shotgun','',43,'2026-08-25 13:04:23'),(32,'蝴蝶刀','knife','',50,'2026-08-25 13:04:23'),(33,'爪子刀','knife','',51,'2026-08-25 13:04:23'),(34,'M9刺刀','knife','',52,'2026-08-25 13:04:23'),(35,'弯刀','knife','',53,'2026-08-25 13:04:23'),(36,'猎杀者匕首','knife','',54,'2026-08-25 13:04:23'),(37,'暗影双匕','knife','',55,'2026-08-25 13:04:23'),(38,'刺刀','knife','',56,'2026-08-25 13:04:23'),(39,'折叠刀','knife','',57,'2026-08-25 13:04:23'),(40,'穿肠刀','knife','',58,'2026-08-25 13:04:23'),(41,'锯齿爪刀','knife','',59,'2026-08-25 13:04:23'),(42,'运动手套','glove','',60,'2026-08-25 13:04:23'),(43,'驾驶手套','glove','',61,'2026-08-25 13:04:23'),(44,'专业手套','glove','',62,'2026-08-25 13:04:23'),(45,'摩托手套','glove','',63,'2026-08-25 13:04:23'),(46,'迷彩手套','glove','',64,'2026-08-25 13:04:23');
/*!40000 ALTER TABLE `debuff_weapon_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
INSERT INTO `gen_table` VALUES (6,'debuff_order','交易订单表',NULL,NULL,'Order','crud','element-plus','com.debuff.trade','trade','order','交易订单','debuff','0','/','{\"parentMenuId\":\"\"}','admin','2026-08-25 19:28:35','','2026-08-25 20:37:47',NULL),(7,'debuff_favorite','收藏表',NULL,NULL,'Favorite','crud','element-plus','com.debuff.trade','trade','favorite','收藏','debuff','0','/','{\"parentMenuId\":2000}','admin','2026-08-26 09:34:56','','2026-08-26 09:55:23',NULL);
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
INSERT INTO `gen_table_column` VALUES (49,6,'id','自增主键','bigint','Long','id','1','1','0','0',NULL,NULL,NULL,'EQ','input','',1,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(50,6,'order_no','订单编号','varchar(32)','String','orderNo','0','0','1','1','0','1','1','EQ','input','',2,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(51,6,'skin_id','饰品ID','bigint','Long','skinId','0','0','1','1','1','1','1','EQ','input','',3,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(52,6,'buyer_id','买家ID','bigint','Long','buyerId','0','0','1','1','1','1','1','EQ','input','',4,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(53,6,'seller_id','卖家ID','bigint','Long','sellerId','0','0','1','1','1','1','1','EQ','input','',5,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(54,6,'price','成交价格','decimal(10,2)','BigDecimal','price','0','0','1','1','1','1','1','EQ','input','',6,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(55,6,'platform_fee','平台手续费','decimal(10,2)','BigDecimal','platformFee','0','0','1','1','1','1','1','EQ','input','',7,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(56,6,'seller_income','卖家实收','decimal(10,2)','BigDecimal','sellerIncome','0','0','1','1','1','1','1','EQ','input','',8,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(57,6,'status','状态','tinyint','Integer','status','0','0','0','1','1','1','1','EQ','radio','trade_order_status',9,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(58,6,'create_time','下单时间','datetime','Date','createTime','0','0','0','1',NULL,NULL,NULL,'EQ','datetime','',10,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(59,6,'pay_time','支付时间','datetime','Date','payTime','0','0','0','1','1','1','0','EQ','datetime','',11,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(60,6,'cancel_time','取消时间','datetime','Date','cancelTime','0','0','0','1','1','1','0','EQ','datetime','',12,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(61,6,'update_time','更新时间','datetime','Date','updateTime','0','0','0','1','1',NULL,NULL,'EQ','datetime','',13,'admin','2026-08-25 19:28:35','','2026-08-25 20:37:47'),(62,7,'id','主键','bigint','Long','id','1','1','0','0',NULL,NULL,NULL,'EQ','input','',1,'admin','2026-08-26 09:34:56','','2026-08-26 09:55:23'),(63,7,'user_id','用户ID','bigint','Long','userId','0','0','1','1','0','1','1','EQ','input','',2,'admin','2026-08-26 09:34:56','','2026-08-26 09:55:23'),(64,7,'listing_id','在售饰品ID','bigint','Long','listingId','0','0','1','1','0','1','1','EQ','input','',3,'admin','2026-08-26 09:34:56','','2026-08-26 09:55:23'),(65,7,'create_time','收藏时间','datetime','Date','createTime','0','0','0','0',NULL,'1',NULL,'EQ','datetime','',4,'admin','2026-08-26 09:34:56','','2026-08-26 09:55:23');
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--

LOCK TABLES `qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2026-08-25 11:11:16','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2026-08-25 11:11:16','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2026-08-25 11:11:16','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','false','Y','admin','2026-08-25 11:11:16','admin','2026-08-26 09:05:58','是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2026-08-25 11:11:16','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2026-08-25 11:11:16','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','若依科技',0,'若依','15888888888','ry@qq.com','0','0','admin','2026-08-25 11:11:15','',NULL),(101,100,'0,100','深圳总公司',1,'若依','15888888888','ry@qq.com','0','0','admin','2026-08-25 11:11:15','',NULL),(102,100,'0,100','长沙分公司',2,'若依','15888888888','ry@qq.com','0','2','admin','2026-08-25 11:11:15','',NULL),(103,101,'0,100,101','研发部门',1,'若依','15888888888','ry@qq.com','0','0','admin','2026-08-25 11:11:15','',NULL),(104,101,'0,100,101','市场部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2026-08-25 11:11:15','',NULL),(105,101,'0,100,101','测试部门',3,'若依','15888888888','ry@qq.com','0','0','admin','2026-08-25 11:11:15','',NULL),(106,101,'0,100,101','财务部门',4,'若依','15888888888','ry@qq.com','0','2','admin','2026-08-25 11:11:15','',NULL),(107,101,'0,100,101','运维部门',5,'若依','15888888888','ry@qq.com','0','2','admin','2026-08-25 11:11:15','',NULL),(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com','0','2','admin','2026-08-25 11:11:15','',NULL),(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2026-08-25 11:11:15','',NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2026-08-25 11:11:15','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2026-08-25 11:11:15','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2026-08-25 11:11:15','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2026-08-25 11:11:15','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2026-08-25 11:11:15','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2026-08-25 11:11:15','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2026-08-25 11:11:15','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2026-08-25 11:11:15','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2026-08-25 11:11:15','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2026-08-25 11:11:15','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2026-08-25 11:11:15','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2026-08-25 11:11:15','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2026-08-25 11:11:15','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2026-08-25 11:11:15','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2026-08-25 11:11:15','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2026-08-25 11:11:15','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2026-08-25 11:11:16','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2026-08-25 11:11:16','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2026-08-25 11:11:16','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2026-08-25 11:11:16','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2026-08-25 11:11:16','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2026-08-25 11:11:16','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2026-08-25 11:11:16','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2026-08-25 11:11:16','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2026-08-25 11:11:16','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2026-08-25 11:11:16','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2026-08-25 11:11:16','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2026-08-25 11:11:16','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2026-08-25 11:11:16','',NULL,'停用状态'),(104,1,'待确认','0','trade_order_status',NULL,'default','N','0','admin','2026-08-25 20:14:56','',NULL,NULL),(105,2,'已完成','1','trade_order_status',NULL,'default','N','0','admin','2026-08-25 20:15:12','',NULL,NULL),(106,3,'已取消','2','trade_order_status',NULL,'default','N','0','admin','2026-08-25 20:15:26','',NULL,NULL),(107,1,'充值','1','wallet_log_type','','success','N','0','admin','2026-08-27 09:48:10','',NULL,NULL),(108,2,'提现','2','wallet_log_type','','warning','N','0','admin','2026-08-27 09:48:10','',NULL,NULL),(109,3,'买入','3','wallet_log_type','','danger','N','0','admin','2026-08-27 09:48:10','',NULL,NULL),(110,4,'卖出','4','wallet_log_type','','success','N','0','admin','2026-08-27 09:48:10','',NULL,NULL),(111,5,'退款','5','wallet_log_type','','success','N','0','admin','2026-08-27 09:48:10','',NULL,NULL),(112,6,'手续费','6','wallet_log_type','','danger','N','0','admin','2026-08-27 09:48:10','',NULL,NULL),(113,1,'特价','1','announcement_type','','danger','N','0','admin','2026-08-27 11:00:49','',NULL,NULL),(114,2,'活动','2','announcement_type','','warning','N','0','admin','2026-08-27 11:00:49','',NULL,NULL),(115,3,'更新','3','announcement_type','','primary','N','0','admin','2026-08-27 11:00:49','',NULL,NULL);
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2026-08-25 11:11:15','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2026-08-25 11:11:15','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2026-08-25 11:11:15','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2026-08-25 11:11:15','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2026-08-25 11:11:15','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2026-08-25 11:11:15','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2026-08-25 11:11:15','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2026-08-25 11:11:15','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2026-08-25 11:11:15','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2026-08-25 11:11:15','',NULL,'登录状态列表'),(101,'订单状态','trade_order_status','0','admin','2026-08-25 20:14:35','',NULL,'订单状态'),(102,'钱包流水类型','wallet_log_type','0','admin','2026-08-27 09:48:10','',NULL,'钱包流水类型'),(103,'公告类型','announcement_type','0','admin','2026-08-27 11:00:49','',NULL,'公告类型');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2026-08-25 11:11:16','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2026-08-25 11:11:16','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2026-08-25 11:11:16','',NULL,'');
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=263 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
INSERT INTO `sys_logininfor` VALUES (100,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 11:13:42'),(101,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 13:05:46'),(102,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 14:15:56'),(103,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','1','验证码错误','2026-08-25 15:20:52'),(104,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 15:20:56'),(105,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 16:30:51'),(106,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 16:35:36'),(107,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 16:38:41'),(108,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 19:10:33'),(109,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 20:44:12'),(110,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-25 20:49:10'),(111,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 08:54:09'),(112,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 11:49:51'),(113,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 12:37:26'),(114,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 15:16:39'),(115,'ry','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 15:29:39'),(116,'ry','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 15:40:13'),(117,'ry','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 15:44:34'),(118,'ry','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 15:48:30'),(119,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 15:49:04'),(120,'admin','127.0.0.1','内网IP','Mozilla','Windows 10','0','登录成功','2026-08-26 15:50:50'),(121,'ry','127.0.0.1','内网IP','Mozilla','Windows 10','0','登录成功','2026-08-26 15:50:50'),(122,'admin','127.0.0.1','内网IP','Mozilla','Windows 10','0','登录成功','2026-08-26 16:03:40'),(123,'admin','127.0.0.1','内网IP','Mozilla','Windows 10','0','登录成功','2026-08-26 16:03:49'),(124,'admin','127.0.0.1','内网IP','Mozilla','Windows 10','0','登录成功','2026-08-26 16:40:34'),(125,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 16:43:24'),(126,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 16:59:38'),(127,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 16:59:38'),(128,'admin','127.0.0.1','内网IP','Mozilla','Windows 10','0','登录成功','2026-08-26 16:59:46'),(129,'test1','127.0.0.1','内网IP','Mozilla','Windows 10','0','登录成功','2026-08-26 16:59:47'),(130,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 17:08:13'),(131,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 17:08:25'),(132,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 17:09:31'),(133,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 17:09:33'),(134,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 17:14:21'),(135,'test1','127.0.0.1','内网IP','Mozilla','Windows 10','0','登录成功','2026-08-26 17:22:14'),(136,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 17:22:28'),(137,'test1','127.0.0.1','内网IP','Mozilla','Windows 10','0','登录成功','2026-08-26 17:25:07'),(138,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 17:27:43'),(139,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 17:27:51'),(140,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 17:30:26'),(141,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 17:30:40'),(142,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 17:31:39'),(143,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 17:34:11'),(144,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 17:41:08'),(145,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 17:41:35'),(146,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 18:03:15'),(147,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:03:36'),(148,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:03:39'),(149,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:06:22'),(150,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:06:31'),(151,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 18:14:16'),(152,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 18:15:01'),(153,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:20:44'),(154,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:20:46'),(155,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:22:03'),(156,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:22:11'),(157,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 18:27:20'),(158,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:30:08'),(159,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:30:10'),(160,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:31:08'),(161,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:31:14'),(162,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:31:33'),(163,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:31:35'),(164,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:31:55'),(165,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:32:03'),(166,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:32:21'),(167,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:32:23'),(168,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:32:29'),(169,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:32:31'),(170,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:41:51'),(171,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:41:57'),(172,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:44:45'),(173,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:44:47'),(174,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 18:49:18'),(175,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:52:03'),(176,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:52:12'),(177,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 18:54:43'),(178,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 18:54:46'),(179,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 19:02:25'),(180,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 19:02:33'),(181,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 19:10:50'),(182,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 19:10:57'),(183,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-26 19:12:05'),(184,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-26 19:12:11'),(185,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 20:20:49'),(186,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-26 20:23:33'),(187,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 09:12:38'),(188,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 09:41:22'),(189,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 09:41:27'),(190,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 09:41:32'),(191,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 09:48:48'),(192,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 10:56:16'),(193,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 10:56:22'),(194,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 10:58:22'),(195,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 10:58:23'),(196,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:03:11'),(197,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:06:08'),(198,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:06:15'),(199,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:13:57'),(200,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:16:37'),(201,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:16:38'),(202,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:23:44'),(203,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:23:54'),(204,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:29:45'),(205,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:29:52'),(206,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:30:00'),(207,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:30:11'),(208,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:31:08'),(209,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:31:14'),(210,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:31:30'),(211,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:31:31'),(212,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:34:47'),(213,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:34:53'),(214,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:40:23'),(215,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:43:36'),(216,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:43:37'),(217,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 11:49:52'),(218,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:51:35'),(219,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:51:41'),(220,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:52:18'),(221,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:52:20'),(222,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:55:12'),(223,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:55:19'),(224,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:55:28'),(225,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:55:30'),(226,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 11:58:48'),(227,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 11:58:53'),(228,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 12:00:20'),(229,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 12:00:21'),(230,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 12:06:12'),(231,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 12:06:25'),(232,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 13:10:17'),(233,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 17:28:05'),(234,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 17:34:41'),(235,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 17:40:14'),(236,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 17:40:21'),(237,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 17:40:29'),(238,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 17:51:55'),(239,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 17:52:35'),(240,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:01:36'),(241,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:01:42'),(242,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:02:04'),(243,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:02:05'),(244,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:02:28'),(245,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:02:34'),(246,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:02:47'),(247,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:02:48'),(248,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:07:20'),(249,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:07:26'),(250,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:07:51'),(251,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:07:52'),(252,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:08:08'),(253,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:08:14'),(254,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:08:36'),(255,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:08:37'),(256,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:18:54'),(257,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:19:01'),(258,'test1','127.0.0.1','内网IP','Chrome 15','Windows 10','0','退出成功','2026-08-27 18:19:42'),(259,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 18:19:44'),(260,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 18:25:04'),(261,'test1','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-08-27 18:25:17'),(262,'admin','127.0.0.1','内网IP','Chrome 15','Windows 10','0','登录成功','2026-08-27 19:28:12');
/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2061 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,1,'system',NULL,'',1,0,'M','0','0','','system','admin','2026-08-25 11:11:15','',NULL,'系统管理目录'),(100,'用户管理',1,1,'user','system/user/index','',1,0,'C','0','0','system:user:list','user','admin','2026-08-25 11:11:15','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','',1,0,'C','0','0','system:role:list','peoples','admin','2026-08-25 11:11:15','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','',1,0,'C','0','0','system:menu:list','tree-table','admin','2026-08-25 11:11:15','',NULL,'菜单管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','',1,0,'C','0','0','system:dict:list','dict','admin','2026-08-25 11:11:15','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','',1,0,'C','0','0','system:config:list','edit','admin','2026-08-25 11:11:15','',NULL,'参数设置菜单'),(1000,'用户查询',100,1,'','','',1,0,'F','0','0','system:user:query','#','admin','2026-08-25 11:11:15','',NULL,''),(1001,'用户新增',100,2,'','','',1,0,'F','0','0','system:user:add','#','admin','2026-08-25 11:11:15','',NULL,''),(1002,'用户修改',100,3,'','','',1,0,'F','0','0','system:user:edit','#','admin','2026-08-25 11:11:15','',NULL,''),(1003,'用户删除',100,4,'','','',1,0,'F','0','0','system:user:remove','#','admin','2026-08-25 11:11:15','',NULL,''),(1004,'用户导出',100,5,'','','',1,0,'F','0','0','system:user:export','#','admin','2026-08-25 11:11:15','',NULL,''),(1005,'用户导入',100,6,'','','',1,0,'F','0','0','system:user:import','#','admin','2026-08-25 11:11:15','',NULL,''),(1006,'重置密码',100,7,'','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2026-08-25 11:11:15','',NULL,''),(1007,'角色查询',101,1,'','','',1,0,'F','0','0','system:role:query','#','admin','2026-08-25 11:11:15','',NULL,''),(1008,'角色新增',101,2,'','','',1,0,'F','0','0','system:role:add','#','admin','2026-08-25 11:11:15','',NULL,''),(1009,'角色修改',101,3,'','','',1,0,'F','0','0','system:role:edit','#','admin','2026-08-25 11:11:15','',NULL,''),(1010,'角色删除',101,4,'','','',1,0,'F','0','0','system:role:remove','#','admin','2026-08-25 11:11:15','',NULL,''),(1011,'角色导出',101,5,'','','',1,0,'F','0','0','system:role:export','#','admin','2026-08-25 11:11:15','',NULL,''),(1012,'菜单查询',102,1,'','','',1,0,'F','0','0','system:menu:query','#','admin','2026-08-25 11:11:15','',NULL,''),(1013,'菜单新增',102,2,'','','',1,0,'F','0','0','system:menu:add','#','admin','2026-08-25 11:11:15','',NULL,''),(1014,'菜单修改',102,3,'','','',1,0,'F','0','0','system:menu:edit','#','admin','2026-08-25 11:11:15','',NULL,''),(1015,'菜单删除',102,4,'','','',1,0,'F','0','0','system:menu:remove','#','admin','2026-08-25 11:11:15','',NULL,''),(1025,'字典查询',105,1,'#','','',1,0,'F','0','0','system:dict:query','#','admin','2026-08-25 11:11:15','',NULL,''),(1026,'字典新增',105,2,'#','','',1,0,'F','0','0','system:dict:add','#','admin','2026-08-25 11:11:15','',NULL,''),(1027,'字典修改',105,3,'#','','',1,0,'F','0','0','system:dict:edit','#','admin','2026-08-25 11:11:15','',NULL,''),(1028,'字典删除',105,4,'#','','',1,0,'F','0','0','system:dict:remove','#','admin','2026-08-25 11:11:15','',NULL,''),(1029,'字典导出',105,5,'#','','',1,0,'F','0','0','system:dict:export','#','admin','2026-08-25 11:11:15','',NULL,''),(1030,'参数查询',106,1,'#','','',1,0,'F','0','0','system:config:query','#','admin','2026-08-25 11:11:15','',NULL,''),(1031,'参数新增',106,2,'#','','',1,0,'F','0','0','system:config:add','#','admin','2026-08-25 11:11:15','',NULL,''),(1032,'参数修改',106,3,'#','','',1,0,'F','0','0','system:config:edit','#','admin','2026-08-25 11:11:15','',NULL,''),(1033,'参数删除',106,4,'#','','',1,0,'F','0','0','system:config:remove','#','admin','2026-08-25 11:11:15','',NULL,''),(1034,'参数导出',106,5,'#','','',1,0,'F','0','0','system:config:export','#','admin','2026-08-25 11:11:15','',NULL,''),(2007,'饰品管理',0,2,'skin','admin/skin/index',NULL,0,0,'C','0','0','admin:skin:list','component','admin','2026-08-25 13:04:23','',NULL,'饰品审核管理'),(2008,'订单管理',0,3,'order','trade/order/index',NULL,0,0,'C','0','0','trade:order:list','log','admin','2026-08-25 13:04:23','admin','2026-08-25 20:02:39','交易订单管理'),(2011,'数据看板',0,5,'dashboard','dashboard/index',NULL,0,0,'C','0','0','admin:dashboard:view','chart','admin','2026-08-25 13:04:23','',NULL,'数据统计看板'),(2018,'订单查询',2008,1,'',NULL,NULL,1,0,'F','0','0','trade:order:query','search','admin','2026-08-25 19:53:42','',NULL,''),(2019,'订单新增',2008,2,'',NULL,NULL,1,0,'F','0','0','trade:order:add','size','admin','2026-08-25 19:54:44','',NULL,''),(2020,'订单修改',2008,3,'',NULL,NULL,1,0,'F','0','0','trade:order:edit','edit','admin','2026-08-25 19:56:57','',NULL,''),(2021,'订单删除',2008,4,'',NULL,NULL,1,0,'F','0','0','trade:order:remove','drag','admin','2026-08-25 19:57:54','',NULL,''),(2022,'订单导出',2008,5,'',NULL,NULL,1,0,'F','0','0','trade:order:export','link','admin','2026-08-25 19:58:14','admin','2026-08-25 19:58:36',''),(2035,'钱包管理',0,4,'user-wallet','trade/wallet/index',NULL,0,0,'C','0','0','trade:wallet:list','icon','admin','2026-08-26 13:09:26','admin','2026-08-26 14:26:23',''),(2036,'钱包查询',2035,1,'','',NULL,1,0,'F','0','0','trade:wallet:query','#','admin','2026-08-26 13:09:26','',NULL,''),(2037,'钱包新增',2035,2,'','',NULL,1,0,'F','0','0','trade:wallet:add','#','admin','2026-08-26 13:09:26','',NULL,''),(2038,'钱包修改',2035,3,'','',NULL,1,0,'F','0','0','trade:wallet:edit','#','admin','2026-08-26 13:09:26','',NULL,''),(2039,'钱包删除',2035,4,'','',NULL,1,0,'F','0','0','trade:wallet:remove','#','admin','2026-08-26 13:09:26','',NULL,''),(2040,'钱包导出',2035,5,'','',NULL,1,0,'F','0','0','trade:wallet:export','#','admin','2026-08-26 13:09:26','',NULL,''),(2041,'流水管理',0,6,'walletLog','trade/walletLog/index',NULL,0,0,'C','0','0','trade:walletLog:list','time-range','admin','2026-08-26 13:09:26','admin','2026-08-26 14:26:46',''),(2042,'流水查询',2041,1,'','',NULL,1,0,'F','0','0','trade:walletLog:query','#','admin','2026-08-26 13:09:26','',NULL,''),(2043,'流水新增',2041,2,'','',NULL,1,0,'F','0','0','trade:walletLog:add','#','admin','2026-08-26 13:09:26','',NULL,''),(2044,'流水修改',2041,3,'','',NULL,1,0,'F','0','0','trade:walletLog:edit','#','admin','2026-08-26 13:09:26','',NULL,''),(2045,'流水删除',2041,4,'','',NULL,1,0,'F','0','0','trade:walletLog:remove','#','admin','2026-08-26 13:09:26','',NULL,''),(2046,'流水导出',2041,5,'','',NULL,1,0,'F','0','0','trade:walletLog:export','#','admin','2026-08-26 13:09:26','',NULL,''),(2047,'公告管理',0,7,'announcement','trade/announcement/index',NULL,0,0,'C','0','0','trade:announcement:list','table','admin','2026-08-26 13:09:26','admin','2026-08-26 14:26:55',''),(2048,'公告查询',2047,1,'','',NULL,1,0,'F','0','0','trade:announcement:query','#','admin','2026-08-26 13:09:26','',NULL,''),(2049,'公告新增',2047,2,'','',NULL,1,0,'F','0','0','trade:announcement:add','#','admin','2026-08-26 13:09:26','',NULL,''),(2050,'公告修改',2047,3,'','',NULL,1,0,'F','0','0','trade:announcement:edit','#','admin','2026-08-26 13:09:26','',NULL,''),(2051,'公告删除',2047,4,'','',NULL,1,0,'F','0','0','trade:announcement:remove','#','admin','2026-08-26 13:09:26','',NULL,''),(2052,'公告导出',2047,5,'','',NULL,1,0,'F','0','0','trade:announcement:export','#','admin','2026-08-26 13:09:26','',NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 若依新版本发布啦','2',_binary '新版本内容','0','admin','2026-08-25 11:11:16','',NULL,'管理员'),(2,'维护通知：2018-07-01 若依系统凌晨维护','1',_binary '维护内容','0','admin','2026-08-25 11:11:16','',NULL,'管理员');
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=298 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (100,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/4','127.0.0.1','内网IP','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-08-25 11:14:20',27),(101,'角色管理',2,'com.debuff.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":true,\"createTime\":\"2026-08-25 11:11:15\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[4],\"params\":{},\"remark\":\"超级管理员\",\"roleId\":1,\"roleKey\":\"admin\",\"roleName\":\"超级管理员\",\"roleSort\":1,\"status\":\"0\"}',NULL,1,'不允许操作超级管理员角色','2026-08-25 11:14:57',3),(102,'角色管理',2,'com.debuff.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-08-25 11:11:15\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,114,3,115,116,1055,1056,1057,1058,1059,1060,117,4],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 11:15:11',62),(103,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/4','127.0.0.1','内网IP','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-08-25 11:15:22',9),(104,'角色管理',2,'com.debuff.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-08-25 11:11:15\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,114,3,115,116,1055,1056,1057,1058,1059,1060,117],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 11:15:34',42),(105,'角色管理',2,'com.debuff.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-08-25 11:11:15\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,114,3,115,116,1055,1056,1057,1058,1059,1060,117],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 11:15:46',45),(106,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/4','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 11:15:56',19),(107,'代码生成',6,'com.debuff.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":\"debuff_price_history\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 13:09:06',130),(108,'代码生成',3,'com.debuff.generator.controller.GenController.remove()','DELETE',1,'admin','研发部门','/tool/gen/1','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 13:13:11',23),(109,'角色管理',2,'com.debuff.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-08-25 11:11:15\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2000,2001,2002,2003,2004,2005,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,114,2006,2007,2008,2009,2010,2011,3,115,116,1055,1056,1057,1058,1059,1060,117],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 15:22:43',93),(110,'代码生成',6,'com.debuff.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":\"debuff_skin_listing,debuff_trade_order,debuff_price_history,debuff_announcement\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 15:47:22',829),(111,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"announcement\",\"className\":\"Announcement\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":8,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":2,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Title\",\"columnComment\":\"公告标题\",\"columnId\":9,\"columnName\":\"title\",\"columnType\":\"varchar(128)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"title\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":2,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Content\",\"columnComment\":\"公告内容\",\"columnId\":10,\"columnName\":\"content\",\"columnType\":\"text\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"editor\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"content\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":3,\"superColumn\":false,\"tableId\":2,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"AnnouncementType\",\"columnComment\":\"类型(0通知 1公告)\",\"columnId\":11,\"columnName\":\"announcement_type\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"select\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"j','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 15:52:00',96),(112,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"listing\",\"className\":\"SkinListing\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":24,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isList\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"InventoryId\",\"columnComment\":\"关联库存ID\",\"columnId\":25,\"columnName\":\"inventory_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"inventoryId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"SellerId\",\"columnComment\":\"卖家用户ID\",\"columnId\":26,\"columnName\":\"seller_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"sellerId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"关联饰品定义ID(冗余)\",\"columnId\":27,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:18:55',46),(113,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"listing\",\"className\":\"SkinListing\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":24,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isList\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:18:55\",\"usableColumn\":false},{\"capJavaField\":\"InventoryId\",\"columnComment\":\"关联库存ID\",\"columnId\":25,\"columnName\":\"inventory_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"inventoryId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:18:55\",\"usableColumn\":false},{\"capJavaField\":\"SellerId\",\"columnComment\":\"卖家用户ID\",\"columnId\":26,\"columnName\":\"seller_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"sellerId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:18:55\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"关联饰品定义ID(冗余)\",\"columnId\":27,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"ins','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:20:17',38),(114,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"listing\",\"className\":\"SkinListing\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":24,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isList\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:20:17\",\"usableColumn\":false},{\"capJavaField\":\"InventoryId\",\"columnComment\":\"关联库存ID\",\"columnId\":25,\"columnName\":\"inventory_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"inventoryId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:20:17\",\"usableColumn\":false},{\"capJavaField\":\"SellerId\",\"columnComment\":\"卖家用户ID\",\"columnId\":26,\"columnName\":\"seller_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"sellerId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:20:17\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"关联饰品定义ID(冗余)\",\"columnId\":27,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"ins','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:22:32',43),(115,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"TradeOrder\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":33,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":5,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"OrderNo\",\"columnComment\":\"订单编号(唯一)\",\"columnId\":34,\"columnName\":\"order_no\",\"columnType\":\"varchar(64)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"orderNo\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":5,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"ListingId\",\"columnComment\":\"关联在售饰品ID\",\"columnId\":35,\"columnName\":\"listing_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"listingId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":5,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"关联饰品定义ID(冗余)\",\"columnId\":36,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\"','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:23:53',61),(116,'字典类型',1,'com.debuff.web.controller.system.SysDictTypeController.add()','POST',1,'admin','研发部门','/system/dict/type','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"dictName\":\"饰品上架状态\",\"dictType\":\"listing_status\",\"params\":{},\"remark\":\"饰品上架状态\",\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:25:15',51),(117,'字典数据',1,'com.debuff.web.controller.system.SysDictDataController.add()','POST',1,'admin','研发部门','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"在售\",\"dictSort\":1,\"dictType\":\"listing_status\",\"dictValue\":\"0\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:25:45',14),(118,'字典数据',1,'com.debuff.web.controller.system.SysDictDataController.add()','POST',1,'admin','研发部门','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"已售出\",\"dictSort\":2,\"dictType\":\"listing_status\",\"dictValue\":\"1\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:26:06',12),(119,'字典数据',1,'com.debuff.web.controller.system.SysDictDataController.add()','POST',1,'admin','研发部门','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"已下架\",\"dictSort\":3,\"dictType\":\"listing_status\",\"dictValue\":\"2\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:26:22',10),(120,'字典数据',1,'com.debuff.web.controller.system.SysDictDataController.add()','POST',1,'admin','研发部门','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"已过期\",\"dictSort\":4,\"dictType\":\"listing_status\",\"dictValue\":\"3\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:26:40',14),(121,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"listing\",\"className\":\"SkinListing\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":24,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isList\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:22:32\",\"usableColumn\":false},{\"capJavaField\":\"InventoryId\",\"columnComment\":\"关联库存ID\",\"columnId\":25,\"columnName\":\"inventory_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"inventoryId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:22:32\",\"usableColumn\":false},{\"capJavaField\":\"SellerId\",\"columnComment\":\"卖家用户ID\",\"columnId\":26,\"columnName\":\"seller_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"sellerId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:22:32\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"关联饰品定义ID(冗余)\",\"columnId\":27,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"ins','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:27:11',33),(122,'代码生成',8,'com.debuff.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"在售饰品\"}',NULL,0,NULL,'2026-08-25 16:27:19',469),(123,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/listing/index\",\"createTime\":\"2026-08-25 16:29:30\",\"icon\":\"eye-open\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2012,\"menuName\":\"在售饰品\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"listing\",\"perms\":\"trade:listing:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:32:12',40),(124,'在售饰品',1,'com.debuff.trade.controller.SkinListingController.add()','POST',1,'admin','研发部门','/trade/listing','127.0.0.1','内网IP','{\"id\":1,\"inventoryId\":1,\"params\":{},\"price\":1,\"sellerId\":1,\"skinId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:45:44',48),(125,'在售饰品',3,'com.debuff.trade.controller.SkinListingController.remove()','DELETE',1,'admin','研发部门','/trade/listing/1','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:45:50',18),(126,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"TradeOrder\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":33,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isList\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":5,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:23:53\",\"usableColumn\":false},{\"capJavaField\":\"OrderNo\",\"columnComment\":\"订单编号\",\"columnId\":34,\"columnName\":\"order_no\",\"columnType\":\"varchar(64)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"orderNo\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":5,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:23:53\",\"usableColumn\":false},{\"capJavaField\":\"ListingId\",\"columnComment\":\"关联在售饰品ID\",\"columnId\":35,\"columnName\":\"listing_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"listingId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":5,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 16:23:53\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"关联饰品定义ID(冗余)\",\"columnId\":36,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 15:47:21\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\"','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 16:52:39',135),(127,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2012','127.0.0.1','内网IP','{}','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2026-08-25 19:11:13',6),(128,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2012','127.0.0.1','内网IP','{}','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2026-08-25 19:11:23',0),(129,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2012','127.0.0.1','内网IP','{}','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2026-08-25 19:12:07',5),(130,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/listing/index\",\"createTime\":\"2026-08-25 16:29:30\",\"icon\":\"eye-open\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2012,\"menuName\":\"在售饰品\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"listing\",\"perms\":\"trade:listing:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:12:19',19),(131,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2012','127.0.0.1','内网IP','{}','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2026-08-25 19:12:28',5),(132,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2013','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:12:36',17),(133,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2014','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:12:37',11),(134,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2015','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:12:39',16),(135,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2016','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:12:41',33),(136,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2017','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:12:42',6),(137,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2012','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:12:49',14),(138,'部门管理',3,'com.debuff.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/109','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:12:58',19),(139,'部门管理',3,'com.debuff.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/108','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:12:59',12),(140,'部门管理',3,'com.debuff.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/102','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:13:01',11),(141,'部门管理',3,'com.debuff.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/107','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:13:02',31),(142,'部门管理',3,'com.debuff.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/106','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:13:03',8),(143,'部门管理',3,'com.debuff.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/105','127.0.0.1','内网IP','{}','{\"msg\":\"部门存在用户,不允许删除\",\"code\":601}',0,NULL,'2026-08-25 19:13:04',7),(144,'部门管理',3,'com.debuff.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/104','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:13:06',23),(145,'部门管理',3,'com.debuff.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/103','127.0.0.1','内网IP','{}','{\"msg\":\"部门存在用户,不允许删除\",\"code\":601}',0,NULL,'2026-08-25 19:13:08',16),(146,'部门管理',3,'com.debuff.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/101','127.0.0.1','内网IP','{}','{\"msg\":\"存在下级部门,不允许删除\",\"code\":601}',0,NULL,'2026-08-25 19:13:10',4),(147,'代码生成',3,'com.debuff.generator.controller.GenController.remove()','DELETE',1,'admin','研发部门','/tool/gen/5','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:28:19',70),(148,'代码生成',3,'com.debuff.generator.controller.GenController.remove()','DELETE',1,'admin','研发部门','/tool/gen/4','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:28:21',16),(149,'代码生成',3,'com.debuff.generator.controller.GenController.remove()','DELETE',1,'admin','研发部门','/tool/gen/3','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:28:23',18),(150,'代码生成',3,'com.debuff.generator.controller.GenController.remove()','DELETE',1,'admin','研发部门','/tool/gen/2','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:28:25',14),(151,'代码生成',6,'com.debuff.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":\"debuff_order\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:28:35',196),(152,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"Order\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"自增主键\",\"columnId\":49,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"OrderNo\",\"columnComment\":\"订单编号\",\"columnId\":50,\"columnName\":\"order_no\",\"columnType\":\"varchar(32)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"orderNo\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"饰品ID\",\"columnId\":51,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"skinId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"BuyerId\",\"columnComment\":\"买家ID\",\"columnId\":52,\"columnName\":\"buyer_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"buyerId\",\"javaType\":\"L','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:41:39',101),(153,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"Order\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"自增主键\",\"columnId\":49,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 19:41:39\",\"usableColumn\":false},{\"capJavaField\":\"OrderNo\",\"columnComment\":\"订单编号\",\"columnId\":50,\"columnName\":\"order_no\",\"columnType\":\"varchar(32)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"orderNo\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 19:41:39\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"饰品ID\",\"columnId\":51,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"skinId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 19:41:39\",\"usableColumn\":false},{\"capJavaField\":\"BuyerId\",\"columnComment\":\"买家ID\",\"columnId\":52,\"columnName\":\"buyer_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:42:27',109),(154,'菜单管理',1,'com.debuff.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"search\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"订单查询\",\"menuType\":\"F\",\"orderNum\":1,\"params\":{},\"parentId\":2008,\"perms\":\"trade:order:query\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:53:42',27),(155,'菜单管理',1,'com.debuff.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"size\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"订单新增\",\"menuType\":\"F\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"perms\":\"trade:order:add\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:54:44',10),(156,'菜单管理',1,'com.debuff.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"edit\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"订单修改\",\"menuType\":\"F\",\"orderNum\":3,\"params\":{},\"parentId\":2008,\"perms\":\"trade:order:edit\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:56:57',11),(157,'菜单管理',1,'com.debuff.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"drag\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"订单删除\",\"menuType\":\"F\",\"orderNum\":4,\"params\":{},\"parentId\":2008,\"perms\":\"trade:order:remove\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:57:54',13),(158,'菜单管理',1,'com.debuff.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"订单导出\",\"menuType\":\"F\",\"orderNum\":5,\"params\":{},\"parentId\":2008,\"perms\":\"trade:order:export\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:58:14',10),(159,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-08-25 19:58:14\",\"icon\":\"link\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2022,\"menuName\":\"订单导出\",\"menuType\":\"F\",\"orderNum\":5,\"params\":{},\"parentId\":2008,\"path\":\"\",\"perms\":\"trade:order:export\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 19:58:36',14),(160,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/order/index\",\"createTime\":\"2026-08-25 13:04:23\",\"icon\":\"log\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2008,\"menuName\":\"订单管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2006,\"path\":\"order\",\"perms\":\"admin:order:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:02:22',10),(161,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/order/index\",\"createTime\":\"2026-08-25 13:04:23\",\"icon\":\"log\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2008,\"menuName\":\"订单管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2006,\"path\":\"order\",\"perms\":\"trade:order:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:02:39',11),(162,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"Order\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"自增主键\",\"columnId\":49,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 19:42:27\",\"usableColumn\":false},{\"capJavaField\":\"OrderNo\",\"columnComment\":\"订单编号\",\"columnId\":50,\"columnName\":\"order_no\",\"columnType\":\"varchar(32)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"orderNo\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 19:42:27\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"饰品ID\",\"columnId\":51,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"skinId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 19:42:27\",\"usableColumn\":false},{\"capJavaField\":\"BuyerId\",\"columnComment\":\"买家ID\",\"columnId\":52,\"columnName\":\"buyer_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:03:53',65),(163,'字典类型',3,'com.debuff.web.controller.system.SysDictTypeController.remove()','DELETE',1,'admin','研发部门','/system/dict/type/100','127.0.0.1','内网IP','{}',NULL,1,'饰品上架状态已分配,不能删除','2026-08-25 20:06:36',11),(164,'字典类型',2,'com.debuff.web.controller.system.SysDictTypeController.edit()','PUT',1,'admin','研发部门','/system/dict/type','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-08-25 16:25:15\",\"dictId\":100,\"dictName\":\"饰品上架状态\",\"dictType\":\"listing_status\",\"params\":{},\"remark\":\"饰品上架状态\",\"status\":\"1\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:06:46',24),(165,'字典类型',3,'com.debuff.web.controller.system.SysDictTypeController.remove()','DELETE',1,'admin','研发部门','/system/dict/type/100','127.0.0.1','内网IP','{}',NULL,1,'饰品上架状态已分配,不能删除','2026-08-25 20:06:47',8),(166,'字典类型',3,'com.debuff.web.controller.system.SysDictTypeController.remove()','DELETE',1,'admin','研发部门','/system/dict/type/100','127.0.0.1','内网IP','{}',NULL,1,'饰品上架状态已分配,不能删除','2026-08-25 20:07:14',7),(167,'字典类型',3,'com.debuff.web.controller.system.SysDictTypeController.remove()','DELETE',1,'admin','研发部门','/system/dict/type/100','127.0.0.1','内网IP','{}',NULL,1,'饰品上架状态已分配,不能删除','2026-08-25 20:08:08',11),(168,'字典类型',2,'com.debuff.web.controller.system.SysDictTypeController.edit()','PUT',1,'admin','研发部门','/system/dict/type','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-08-25 16:25:15\",\"dictId\":100,\"dictName\":\"饰品上架状态\",\"dictType\":\"listing_status\",\"params\":{},\"remark\":\"饰品上架状态\",\"status\":\"1\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:08:12',36),(169,'字典类型',3,'com.debuff.web.controller.system.SysDictTypeController.remove()','DELETE',1,'admin','研发部门','/system/dict/type/100','127.0.0.1','内网IP','{}',NULL,1,'饰品上架状态已分配,不能删除','2026-08-25 20:08:15',7),(170,'字典类型',9,'com.debuff.web.controller.system.SysDictTypeController.refreshCache()','DELETE',1,'admin','研发部门','/system/dict/type/refreshCache','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:08:18',53),(171,'字典类型',3,'com.debuff.web.controller.system.SysDictTypeController.remove()','DELETE',1,'admin','研发部门','/system/dict/type/100','127.0.0.1','内网IP','{}',NULL,1,'饰品上架状态已分配,不能删除','2026-08-25 20:08:21',7),(172,'字典类型',1,'com.debuff.web.controller.system.SysDictTypeController.add()','POST',1,'admin','研发部门','/system/dict/type','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"dictName\":\"订单状态\",\"dictType\":\"trade_order_status\",\"params\":{},\"remark\":\"订单状态\",\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:14:35',12),(173,'字典数据',1,'com.debuff.web.controller.system.SysDictDataController.add()','POST',1,'admin','研发部门','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"待支付\",\"dictSort\":1,\"dictType\":\"trade_order_status\",\"dictValue\":\"0\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:14:56',17),(174,'字典数据',1,'com.debuff.web.controller.system.SysDictDataController.add()','POST',1,'admin','研发部门','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"已支付\",\"dictSort\":2,\"dictType\":\"trade_order_status\",\"dictValue\":\"1\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:15:12',11),(175,'字典数据',1,'com.debuff.web.controller.system.SysDictDataController.add()','POST',1,'admin','研发部门','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"已取消\",\"dictSort\":3,\"dictType\":\"trade_order_status\",\"dictValue\":\"2\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:15:26',13),(176,'代码生成',8,'com.debuff.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"debuff_order\"}',NULL,0,NULL,'2026-08-25 20:15:56',241),(177,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"Order\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"自增主键\",\"columnId\":49,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 20:03:53\",\"usableColumn\":false},{\"capJavaField\":\"OrderNo\",\"columnComment\":\"订单编号\",\"columnId\":50,\"columnName\":\"order_no\",\"columnType\":\"varchar(32)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"orderNo\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 20:03:53\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"饰品ID\",\"columnId\":51,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"skinId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 20:03:53\",\"usableColumn\":false},{\"capJavaField\":\"BuyerId\",\"columnComment\":\"买家ID\",\"columnId\":52,\"columnName\":\"buyer_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:16:46',46),(178,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"Order\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"自增主键\",\"columnId\":49,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 20:16:46\",\"usableColumn\":false},{\"capJavaField\":\"OrderNo\",\"columnComment\":\"订单编号\",\"columnId\":50,\"columnName\":\"order_no\",\"columnType\":\"varchar(32)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"orderNo\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 20:16:46\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"饰品ID\",\"columnId\":51,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"skinId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 20:16:46\",\"usableColumn\":false},{\"capJavaField\":\"BuyerId\",\"columnComment\":\"买家ID\",\"columnId\":52,\"columnName\":\"buyer_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:17:41',39),(179,'代码生成',8,'com.debuff.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"debuff_order\"}',NULL,0,NULL,'2026-08-25 20:17:43',213),(180,'交易订单',1,'com.debuff.trade.controller.OrderController.add()','POST',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":1,\"createTime\":\"2026-08-25 20:30:49\",\"params\":{},\"price\":333,\"sellerId\":1,\"skinId\":1}',NULL,1,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'order_no\' doesn\'t have a default value\r\n### The error may exist in file [D:\\study\\Debuff\\Debuff\\debuff-trade\\target\\classes\\mapper\\trade\\OrderMapper.xml]\r\n### The error may involve com.debuff.trade.mapper.OrderMapper.insertOrder-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into debuff_order          ( skin_id,             buyer_id,             seller_id,             price,                                                    create_time )           values ( ?,             ?,             ?,             ?,                                                    ? )\r\n### Cause: java.sql.SQLException: Field \'order_no\' doesn\'t have a default value\n; Field \'order_no\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'order_no\' doesn\'t have a default value','2026-08-25 20:30:49',170),(181,'交易订单',1,'com.debuff.trade.controller.OrderController.add()','POST',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":1,\"createTime\":\"2026-08-25 20:31:51\",\"params\":{},\"price\":222,\"sellerId\":3,\"skinId\":1}',NULL,1,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'platform_fee\' doesn\'t have a default value\r\n### The error may exist in file [D:\\study\\Debuff\\Debuff\\debuff-trade\\target\\classes\\mapper\\trade\\OrderMapper.xml]\r\n### The error may involve com.debuff.trade.mapper.OrderMapper.insertOrder-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into debuff_order          ( skin_id,             buyer_id,             seller_id,             price,                                                    create_time )           values ( ?,             ?,             ?,             ?,                                                    ? )\r\n### Cause: java.sql.SQLException: Field \'platform_fee\' doesn\'t have a default value\n; Field \'platform_fee\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'platform_fee\' doesn\'t have a default value','2026-08-25 20:31:51',8),(182,'交易订单',1,'com.debuff.trade.controller.OrderController.add()','POST',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":3,\"createTime\":\"2026-08-25 20:34:41\",\"id\":1,\"params\":{},\"price\":424,\"sellerId\":1,\"skinId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:34:41',62),(183,'交易订单',2,'com.debuff.trade.controller.OrderController.edit()','PUT',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":3,\"createTime\":\"2026-08-25 20:34:41\",\"id\":1,\"orderNo\":\"\",\"params\":{},\"platformFee\":0,\"price\":424,\"sellerId\":1,\"sellerIncome\":0,\"skinId\":1,\"updateTime\":\"2026-08-25 20:34:45\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:34:45',13),(184,'交易订单',3,'com.debuff.trade.controller.OrderController.remove()','DELETE',1,'admin','研发部门','/trade/order/1','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:35:09',29),(185,'交易订单',1,'com.debuff.trade.controller.OrderController.add()','POST',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":2,\"createTime\":\"2026-08-25 20:36:07\",\"id\":2,\"params\":{},\"price\":4,\"sellerId\":3,\"skinId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:36:07',10),(186,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"order\",\"className\":\"Order\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"自增主键\",\"columnId\":49,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 20:17:41\",\"usableColumn\":false},{\"capJavaField\":\"OrderNo\",\"columnComment\":\"订单编号\",\"columnId\":50,\"columnName\":\"order_no\",\"columnType\":\"varchar(32)\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"orderNo\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 20:17:41\",\"usableColumn\":false},{\"capJavaField\":\"SkinId\",\"columnComment\":\"饰品ID\",\"columnId\":51,\"columnName\":\"skin_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"skinId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":6,\"updateBy\":\"\",\"updateTime\":\"2026-08-25 20:17:41\",\"usableColumn\":false},{\"capJavaField\":\"BuyerId\",\"columnComment\":\"买家ID\",\"columnId\":52,\"columnName\":\"buyer_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 19:28:35\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:37:47',99),(187,'交易订单',3,'com.debuff.trade.controller.OrderController.remove()','DELETE',1,'admin','研发部门','/trade/order/2','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:44:20',19),(188,'交易订单',1,'com.debuff.trade.controller.OrderController.add()','POST',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":2,\"createTime\":\"2026-08-25 20:44:28\",\"id\":3,\"params\":{},\"price\":666,\"sellerId\":3,\"skinId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:44:28',20),(189,'交易订单',3,'com.debuff.trade.controller.OrderController.remove()','DELETE',1,'admin','研发部门','/trade/order/3','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:47:05',12),(190,'交易订单',1,'com.debuff.trade.controller.OrderController.add()','POST',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":2,\"createTime\":\"2026-08-25 20:47:14\",\"id\":4,\"params\":{},\"price\":444,\"sellerId\":3,\"skinId\":1,\"status\":0}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:47:15',11),(191,'交易订单',3,'com.debuff.trade.controller.OrderController.remove()','DELETE',1,'admin','研发部门','/trade/order/4','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:48:44',14),(192,'交易订单',1,'com.debuff.trade.controller.OrderController.add()','POST',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":2,\"createTime\":\"2026-08-25 20:48:53\",\"id\":5,\"params\":{},\"price\":1111,\"sellerId\":3,\"skinId\":1,\"status\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:48:53',6),(193,'交易订单',2,'com.debuff.trade.controller.OrderController.edit()','PUT',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":2,\"createTime\":\"2026-08-25 20:48:54\",\"id\":5,\"orderNo\":\"\",\"params\":{},\"platformFee\":0,\"price\":1111,\"sellerId\":3,\"sellerIncome\":0,\"skinId\":1,\"status\":1,\"updateTime\":\"2026-08-25 20:49:20\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:49:20',12),(194,'交易订单',3,'com.debuff.trade.controller.OrderController.remove()','DELETE',1,'admin','研发部门','/trade/order/5','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:50:58',6),(195,'交易订单',1,'com.debuff.trade.controller.OrderController.add()','POST',1,'admin','研发部门','/trade/order','127.0.0.1','内网IP','{\"buyerId\":141,\"createTime\":\"2026-08-25 20:51:13\",\"id\":6,\"orderNo\":\"516651\",\"params\":{},\"price\":422,\"sellerId\":242,\"skinId\":4,\"status\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:51:13',12),(196,'交易订单',3,'com.debuff.trade.controller.OrderController.remove()','DELETE',1,'admin','研发部门','/trade/order/6','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-25 20:51:51',8),(197,'交易订单',5,'com.debuff.trade.controller.OrderController.export()','POST',1,'admin','研发部门','/trade/order/export','127.0.0.1','内网IP','{\"pageSize\":\"10\",\"pageNum\":\"1\"}',NULL,0,NULL,'2026-08-26 09:04:02',908),(198,'参数管理',2,'com.debuff.web.controller.system.SysConfigController.edit()','PUT',1,'admin','研发部门','/system/config','127.0.0.1','内网IP','{\"configId\":4,\"configKey\":\"sys.account.captchaEnabled\",\"configName\":\"账号自助-验证码开关\",\"configType\":\"Y\",\"configValue\":\"false\",\"createBy\":\"admin\",\"createTime\":\"2026-08-25 11:11:16\",\"params\":{},\"remark\":\"是否开启验证码功能（true开启，false关闭）\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 09:05:58',28),(199,'代码生成',6,'com.debuff.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":\"debuff_favorite\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 09:34:56',141),(200,'代码生成',2,'com.debuff.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"favorite\",\"className\":\"Favorite\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键\",\"columnId\":62,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-26 09:34:56\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":false,\"isIncrement\":\"1\",\"isInsert\":\"0\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"UserId\",\"columnComment\":\"用户ID\",\"columnId\":63,\"columnName\":\"user_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-26 09:34:56\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"userId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"ListingId\",\"columnComment\":\"在售饰品ID\",\"columnId\":64,\"columnName\":\"listing_id\",\"columnType\":\"bigint\",\"createBy\":\"admin\",\"createTime\":\"2026-08-26 09:34:56\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"listingId\",\"javaType\":\"Long\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":7,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"CreateTime\",\"columnComment\":\"收藏时间\",\"columnId\":65,\"columnName\":\"create_time\",\"columnType\":\"datetime\",\"createBy\":\"admin\",\"createTime\":\"2026-08-26 09:34:56\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"datetime\",\"increment\":false,\"insert\":false,\"isIncrement\":\"0\",\"isInsert\":\"0\",\"isList\":\"1\",\"isPk\":\"0\",\"isRequired\":\"0\",\"javaField\":\"createTime\",\"javaType\":\"Date\",','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 09:55:23',87),(201,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"user/favorites\",\"createTime\":\"2026-08-25 13:04:23\",\"icon\":\"star\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2005,\"menuName\":\"我的收藏\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2000,\"path\":\"favorites\",\"perms\":\"trade:favorite:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 09:55:53',20),(202,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"market/index\",\"createTime\":\"2026-08-25 13:04:23\",\"icon\":\"redis-list\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"市场浏览\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"list\",\"perms\":\"trade:market:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 09:56:26',9),(203,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/orders\",\"createTime\":\"2026-08-25 13:04:23\",\"icon\":\"monitor\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2003,\"menuName\":\"我的交易\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2000,\"path\":\"trade\",\"perms\":\"trade:order:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 09:56:54',7),(204,'代码生成',8,'com.debuff.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"debuff_favorite\"}',NULL,0,NULL,'2026-08-26 10:00:47',355),(205,'收藏',1,'com.debuff.trade.controller.FavoriteController.add()','POST',1,'admin','研发部门','/trade/favorite','127.0.0.1','内网IP','{\"createTime\":\"2026-08-26 10:04:40\",\"id\":1,\"listingId\":2,\"params\":{},\"userId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 10:04:40',49),(206,'收藏',3,'com.debuff.trade.controller.FavoriteController.remove()','DELETE',1,'admin','研发部门','/trade/favorite/1','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 10:04:44',22),(207,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2005','127.0.0.1','内网IP','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-08-26 10:04:56',15),(208,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"user/favorites\",\"createTime\":\"2026-08-25 13:04:23\",\"icon\":\"star\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2005,\"menuName\":\"我的收藏\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"favorites\",\"perms\":\"trade:favorite:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 10:05:04',44),(209,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2005','127.0.0.1','内网IP','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-08-26 10:05:09',11),(210,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/favorite/index\",\"createTime\":\"2026-08-26 10:03:14\",\"icon\":\"star\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2029,\"menuName\":\"收藏\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"favorite\",\"perms\":\"trade:favorite:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 10:06:41',16),(211,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-08-26 10:13:51\",\"icon\":\"star\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2005,\"menuName\":\"我的收藏\",\"menuType\":\"M\",\"orderNum\":5,\"params\":{},\"parentId\":2000,\"path\":\"favorite\",\"perms\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 10:18:05',18),(212,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/favorite/index\",\"createTime\":\"2026-08-26 10:13:51\",\"icon\":\"star\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2029,\"menuName\":\"收藏\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"list\",\"perms\":\"trade:favorite:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 10:18:57',18),(213,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2005','127.0.0.1','内网IP','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-08-26 10:19:27',8),(214,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/favorite/index\",\"createTime\":\"2026-08-26 10:13:51\",\"icon\":\"star\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2029,\"menuName\":\"我的收藏\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2000,\"path\":\"favorite\",\"perms\":\"trade:favorite:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 10:21:07',11),(215,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2004','127.0.0.1','内网IP','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-08-26 14:12:51',20),(216,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2009','127.0.0.1','内网IP','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-08-26 14:14:33',9),(217,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2011','127.0.0.1','内网IP','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-08-26 14:14:39',8),(218,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/wallet/index\",\"createTime\":\"2026-08-26 13:09:26\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2035,\"menuName\":\"钱包管理\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2006,\"path\":\"user-wallet\",\"perms\":\"trade:wallet:list\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"修改菜单\'钱包管理\'失败，菜单名称已存在\",\"code\":500}',0,NULL,'2026-08-26 14:23:48',15),(219,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/wallet/index\",\"createTime\":\"2026-08-26 13:09:26\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2035,\"menuName\":\"钱包管理\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2006,\"path\":\"user-wallet\",\"perms\":\"trade:wallet:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 14:24:59',26),(220,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/walletLog/index\",\"createTime\":\"2026-08-26 13:09:26\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2041,\"menuName\":\"流水管理\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2006,\"path\":\"walletLog\",\"perms\":\"trade:walletLog:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 14:25:06',14),(221,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/announcement/index\",\"createTime\":\"2026-08-26 13:09:26\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2047,\"menuName\":\"公告管理\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":2006,\"path\":\"announcement\",\"perms\":\"trade:announcement:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 14:25:12',14),(222,'菜单管理',3,'com.debuff.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2060','127.0.0.1','内网IP','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-08-26 14:25:14',12),(223,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/wallet/index\",\"createTime\":\"2026-08-26 13:09:26\",\"icon\":\"icon\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2035,\"menuName\":\"钱包管理\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2006,\"path\":\"user-wallet\",\"perms\":\"trade:wallet:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 14:26:23',9),(224,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/walletLog/index\",\"createTime\":\"2026-08-26 13:09:26\",\"icon\":\"time-range\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2041,\"menuName\":\"流水管理\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2006,\"path\":\"walletLog\",\"perms\":\"trade:walletLog:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 14:26:46',14),(225,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"trade/announcement/index\",\"createTime\":\"2026-08-26 13:09:26\",\"icon\":\"table\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2047,\"menuName\":\"公告管理\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":2006,\"path\":\"announcement\",\"perms\":\"trade:announcement:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 14:26:55',15),(226,'个人信息',2,'com.debuff.web.controller.system.SysProfileController.updateProfile()','PUT',1,'test1',NULL,'/system/user/profile','127.0.0.1','内网IP','{\"admin\":false,\"email\":\"\",\"nickName\":\"TestNick\",\"params\":{},\"phonenumber\":\"\",\"sex\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 17:53:57',19),(227,'用户头像',2,'com.debuff.web.controller.system.SysProfileController.avatar()','POST',1,'test1',NULL,'/system/user/profile/avatar','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2026/08/26/ava_20260826175358A001.png\",\"code\":200}',0,NULL,'2026-08-26 17:53:58',340),(228,'个人信息',2,'com.debuff.web.controller.system.SysProfileController.updateProfile()','PUT',1,'test1',NULL,'/system/user/profile','127.0.0.1','内网IP','{\"admin\":false,\"email\":\"\",\"nickName\":\"测试用户\",\"params\":{},\"phonenumber\":\"\",\"sex\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 17:54:06',15),(229,'个人信息',2,'com.debuff.web.controller.system.SysProfileController.updateProfile()','PUT',1,'test1',NULL,'/system/user/profile','127.0.0.1','内网IP','{\"admin\":false,\"email\":\"\",\"nickName\":\"test1\",\"params\":{},\"phonenumber\":\"\",\"sex\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 17:54:53',18),(230,'用户头像',2,'com.debuff.web.controller.system.SysProfileController.avatar()','POST',1,'test1',NULL,'/system/user/profile/avatar','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2026/08/26/鱼香肉丝_20260826175506A002.png\",\"code\":200}',0,NULL,'2026-08-26 17:55:06',13),(231,'饰品管理',1,'com.debuff.trade.controller.admin.AdminSkinController.add()','POST',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"description\":\"nb\",\"id\":11,\"imageUrl\":\"/profile/upload/2026/08/26/毛血旺_20260826180614A003.jpg\",\"minPrice\":160,\"params\":{},\"qualityId\":5,\"skinName\":\"火麒麟\",\"status\":\"0\",\"weaponTypeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:06:16',22),(232,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.changeStatus()','PUT',1,'admin','研发部门','/api/admin/skin/changeStatus','127.0.0.1','内网IP','{\"id\":11,\"params\":{},\"status\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:14:35',52),(233,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.changeStatus()','PUT',1,'admin','研发部门','/api/admin/skin/changeStatus','127.0.0.1','内网IP','{\"id\":11,\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:14:35',83),(234,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.changeStatus()','PUT',1,'admin','研发部门','/api/admin/skin/changeStatus','127.0.0.1','内网IP','{\"id\":11,\"params\":{},\"status\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:15:44',27),(235,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.changeStatus()','PUT',1,'admin','研发部门','/api/admin/skin/changeStatus','127.0.0.1','内网IP','{\"id\":11,\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:15:58',50),(236,'饰品管理',1,'com.debuff.trade.controller.admin.AdminSkinController.add()','POST',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"description\":\"nb\",\"id\":12,\"imageUrl\":\"/profile/upload/2026/08/26/鱼香肉丝_20260826182157A001.png\",\"minPrice\":16666,\"params\":{},\"qualityId\":7,\"skinName\":\"雷神\",\"status\":\"0\",\"weaponTypeId\":3}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:21:58',8),(237,'饰品管理',1,'com.debuff.trade.controller.admin.AdminSkinController.add()','POST',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"description\":\"auto provision test\",\"id\":13,\"minPrice\":99.5,\"params\":{},\"qualityId\":2,\"skinName\":\"TestSkinA\",\"status\":\"0\",\"weaponTypeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:27:30',109),(238,'饰品管理',1,'com.debuff.trade.controller.admin.AdminSkinController.add()','POST',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"description\":\"no price test\",\"id\":14,\"params\":{},\"qualityId\":2,\"skinName\":\"TestSkinB\",\"status\":\"0\",\"weaponTypeId\":1}',NULL,1,'饰品「TestSkinB」需设置参考价（大于0）才能上架','2026-08-26 18:27:30',11),(239,'饰品管理',1,'com.debuff.trade.controller.admin.AdminSkinController.add()','POST',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"description\":\"down then up test\",\"id\":15,\"minPrice\":50,\"params\":{},\"qualityId\":3,\"skinName\":\"TestSkinC\",\"status\":\"1\",\"weaponTypeId\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:27:30',9),(240,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.edit()','PUT',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"description\":\"down then up test\",\"id\":15,\"minPrice\":50,\"params\":{},\"qualityId\":3,\"skinName\":\"TestSkinC\",\"status\":\"0\",\"weaponTypeId\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:27:45',51),(241,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/12','127.0.0.1','内网IP','{}',NULL,1,'饰品「雷神」存在关联数据（库存 0 / 求购 1 / 在售 0），无法删除','2026-08-26 18:30:30',24),(242,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/12','127.0.0.1','内网IP','{}',NULL,1,'饰品「雷神」存在关联数据（库存 0 / 求购 1 / 在售 0），无法删除','2026-08-26 18:30:35',7),(243,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.changeStatus()','PUT',1,'admin','研发部门','/api/admin/skin/changeStatus','127.0.0.1','内网IP','{\"id\":12,\"params\":{},\"status\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:30:43',43),(244,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/12','127.0.0.1','内网IP','{}',NULL,1,'饰品「雷神」存在关联数据（库存 0 / 求购 1 / 在售 0），无法删除','2026-08-26 18:30:45',10),(245,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/12','127.0.0.1','内网IP','{}',NULL,1,'饰品「雷神」存在关联数据（库存 0 / 求购 1 / 在售 0），无法删除','2026-08-26 18:30:56',29),(246,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.edit()','PUT',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"description\":\"nb\",\"id\":12,\"imageUrl\":\"/profile/upload/2026/08/26/鱼香肉丝_20260826182157A001.png\",\"minPrice\":16556,\"params\":{},\"qualityId\":7,\"skinName\":\"雷神\",\"status\":\"1\",\"weaponTypeId\":3}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:31:03',25),(247,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.changeStatus()','PUT',1,'admin','研发部门','/api/admin/skin/changeStatus','127.0.0.1','内网IP','{\"id\":12,\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:31:05',31),(248,'饰品管理',1,'com.debuff.trade.controller.admin.AdminSkinController.add()','POST',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"id\":16,\"minPrice\":60,\"params\":{},\"qualityId\":3,\"skinName\":\"6666\",\"status\":\"0\",\"weaponTypeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:31:52',29),(249,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/16','127.0.0.1','内网IP','{}',NULL,1,'饰品「6666」存在关联数据（库存 1 / 求购 0 / 在售 1），无法删除','2026-08-26 18:45:50',8),(250,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/16','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 18:49:25',151),(251,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/1','127.0.0.1','内网IP','{}',NULL,1,'饰品「火蛇」存在求购/订单记录，无法删除','2026-08-26 18:49:25',20),(252,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/16','127.0.0.1','内网IP','{}','{\"msg\":\"操作失败\",\"code\":500}',0,NULL,'2026-08-26 18:51:41',120),(253,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.changeStatus()','PUT',1,'admin','研发部门','/api/admin/skin/changeStatus','127.0.0.1','内网IP','{\"id\":16,\"params\":{},\"status\":\"1\"}',NULL,1,'饰品不存在','2026-08-26 18:51:46',27),(254,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/16','127.0.0.1','内网IP','{}','{\"msg\":\"操作失败\",\"code\":500}',0,NULL,'2026-08-26 18:51:49',28),(255,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/dept/index\",\"createTime\":\"2026-08-25 11:11:15\",\"icon\":\"tree\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":103,\"menuName\":\"部门管理\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":1,\"path\":\"dept\",\"perms\":\"system:dept:list\",\"query\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 19:27:43',40),(256,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/post/index\",\"createTime\":\"2026-08-25 11:11:15\",\"icon\":\"post\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":104,\"menuName\":\"岗位管理\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":1,\"path\":\"post\",\"perms\":\"system:post:list\",\"query\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 19:27:47',13),(257,'菜单管理',2,'com.debuff.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/notice/index\",\"createTime\":\"2026-08-25 11:11:15\",\"icon\":\"message\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":107,\"menuName\":\"通知公告\",\"menuType\":\"C\",\"orderNum\":8,\"params\":{},\"parentId\":1,\"path\":\"notice\",\"perms\":\"system:notice:list\",\"query\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 19:28:00',11),(258,'钱包流水',3,'com.debuff.trade.controller.WalletLogController.remove()','DELETE',1,'admin','研发部门','/trade/walletLog/8','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 20:57:10',19),(259,'钱包流水',3,'com.debuff.trade.controller.WalletLogController.remove()','DELETE',1,'admin','研发部门','/trade/walletLog/9','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 20:57:12',7),(260,'钱包流水',3,'com.debuff.trade.controller.WalletLogController.remove()','DELETE',1,'admin','研发部门','/trade/walletLog/10','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 20:57:13',6),(261,'钱包流水',3,'com.debuff.trade.controller.WalletLogController.remove()','DELETE',1,'admin','研发部门','/trade/walletLog/11','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 20:57:14',7),(262,'钱包流水',3,'com.debuff.trade.controller.WalletLogController.remove()','DELETE',1,'admin','研发部门','/trade/walletLog/12','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 20:57:16',7),(263,'钱包流水',3,'com.debuff.trade.controller.WalletLogController.remove()','DELETE',1,'admin','研发部门','/trade/walletLog/13','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-26 20:57:17',9),(264,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/10','127.0.0.1','内网IP','{}',NULL,1,'饰品「潘多拉魔盒」存在求购/订单记录，无法删除','2026-08-26 21:20:47',63),(265,'系统公告',1,'com.debuff.trade.controller.AnnouncementController.add()','POST',1,'admin','研发部门','/trade/announcement','127.0.0.1','内网IP','{\"content\":\"<p>66+6</p>\",\"createTime\":\"2026-08-27 10:56:09\",\"id\":1,\"params\":{},\"title\":\"特价\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 10:56:09',48),(266,'个人信息',2,'com.debuff.web.controller.system.SysProfileController.updateProfile()','PUT',1,'test1',NULL,'/system/user/profile','127.0.0.1','内网IP','{\"admin\":false,\"email\":\"\",\"nickName\":\"test1\",\"params\":{},\"phonenumber\":\"\",\"sex\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 10:57:27',14),(267,'个人信息',2,'com.debuff.web.controller.system.SysProfileController.updateProfile()','PUT',1,'test1',NULL,'/system/user/profile','127.0.0.1','内网IP','{\"admin\":false,\"email\":\"\",\"nickName\":\"test1\",\"params\":{},\"phonenumber\":\"18888888888\",\"sex\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 10:57:42',18),(268,'个人信息',2,'com.debuff.web.controller.system.SysProfileController.updateProfile()','PUT',1,'test1',NULL,'/system/user/profile','127.0.0.1','内网IP','{\"admin\":false,\"email\":\"516516@s.com\",\"nickName\":\"test1\",\"params\":{},\"phonenumber\":\"18888888888\",\"sex\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 10:57:57',21),(269,'系统公告',2,'com.debuff.trade.controller.AnnouncementController.edit()','PUT',1,'admin','研发部门','/trade/announcement','127.0.0.1','内网IP','{\"announcementType\":\"1\",\"content\":\"<p>klzncsfisfsnois</p>\",\"createBy\":\"\",\"createTime\":\"2026-08-27 10:56:10\",\"id\":1,\"params\":{},\"remark\":\"\",\"status\":\"0\",\"title\":\"特价\",\"updateTime\":\"2026-08-27 11:04:07\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 11:04:07',25),(270,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.changeStatus()','PUT',1,'admin','研发部门','/api/admin/skin/changeStatus','127.0.0.1','内网IP','{\"id\":12,\"params\":{},\"status\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 11:04:49',70),(271,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/12','127.0.0.1','内网IP','{}',NULL,1,'饰品「雷神」存在求购/订单记录，无法删除','2026-08-27 11:04:51',27),(272,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/12','127.0.0.1','内网IP','{}',NULL,1,'饰品「雷神」存在求购/订单记录，无法删除','2026-08-27 11:04:56',14),(273,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.edit()','PUT',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"id\":1,\"minPrice\":300,\"params\":{}}',NULL,1,'饰品「火蛇」已被购买，无法修改价格','2026-08-27 11:23:44',94),(274,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.edit()','PUT',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"id\":3,\"minPrice\":260,\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 11:23:44',34),(275,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.edit()','PUT',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"id\":3,\"minPrice\":259,\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 11:23:54',27),(276,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.edit()','PUT',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"description\":\"nb\",\"id\":11,\"imageUrl\":\"/profile/upload/2026/08/26/毛血旺_20260826180614A003.jpg\",\"minPrice\":200,\"params\":{},\"qualityId\":5,\"skinName\":\"火麒麟\",\"status\":\"0\",\"weaponTypeId\":1}',NULL,1,'饰品「火麒麟」已被购买，无法修改价格','2026-08-27 11:26:41',20),(277,'饰品管理',2,'com.debuff.trade.controller.admin.AdminSkinController.edit()','PUT',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"description\":\"nb\",\"id\":11,\"imageUrl\":\"/profile/upload/2026/08/26/毛血旺_20260826180614A003.jpg\",\"minPrice\":540,\"params\":{},\"qualityId\":5,\"skinName\":\"火麒麟\",\"status\":\"0\",\"weaponTypeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 11:34:38',41),(278,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/10','127.0.0.1','内网IP','{}',NULL,1,'饰品「潘多拉魔盒」存在求购/订单记录，无法删除','2026-08-27 11:43:54',48),(279,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/10','127.0.0.1','内网IP','{}',NULL,1,'饰品「潘多拉魔盒」存在求购/订单记录，无法删除','2026-08-27 11:45:01',22),(280,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/9','127.0.0.1','内网IP','{}',NULL,1,'饰品「虎牙」存在求购/订单记录，无法删除','2026-08-27 11:45:04',11),(281,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/10','127.0.0.1','内网IP','{}',NULL,1,'饰品「潘多拉魔盒」存在求购/订单记录，无法删除','2026-08-27 11:47:49',15),(282,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/9','127.0.0.1','内网IP','{}',NULL,1,'饰品「虎牙」存在求购/订单记录，无法删除','2026-08-27 11:47:53',18),(283,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/12','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 11:49:53',133),(284,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/1','127.0.0.1','内网IP','{}',NULL,1,'饰品「火蛇」存在求购/订单记录，无法删除','2026-08-27 11:49:53',17),(285,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/2','127.0.0.1','内网IP','{}',NULL,1,'饰品「二龙」存在求购/订单记录，无法删除','2026-08-27 11:49:53',16),(286,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/10','127.0.0.1','内网IP','{}',NULL,1,'饰品「潘多拉魔盒」存在求购/订单记录，无法删除','2026-08-27 11:51:16',24),(287,'饰品管理',3,'com.debuff.trade.controller.admin.AdminSkinController.remove()','DELETE',1,'admin','研发部门','/api/admin/skin/10','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 11:55:37',27),(288,'饰品管理',1,'com.debuff.trade.controller.admin.AdminSkinController.add()','POST',1,'admin','研发部门','/api/admin/skin','127.0.0.1','内网IP','{\"id\":17,\"minPrice\":666,\"params\":{},\"qualityId\":5,\"skinName\":\"阿卡四七\",\"status\":\"0\",\"weaponTypeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 11:56:51',51),(289,'系统公告',3,'com.debuff.trade.controller.AnnouncementController.remove()','DELETE',1,'admin','研发部门','/trade/announcement/1','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 12:02:06',9),(290,'系统公告',1,'com.debuff.trade.controller.AnnouncementController.add()','POST',1,'admin','研发部门','/trade/announcement','127.0.0.1','内网IP','{\"announcementType\":\"2\",\"content\":\"<p>666</p>\",\"createTime\":\"2026-08-27 12:02:13\",\"id\":2,\"params\":{},\"title\":\"63563\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 12:02:13',17),(291,'系统公告',1,'com.debuff.trade.controller.AnnouncementController.add()','POST',1,'admin','研发部门','/trade/announcement','127.0.0.1','内网IP','{\"content\":\"<p>hgchgcwg</p>\",\"createTime\":\"2026-08-27 12:06:47\",\"id\":3,\"params\":{},\"title\":\"ewa4te\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 12:06:47',31),(292,'系统公告',3,'com.debuff.trade.controller.AnnouncementController.remove()','DELETE',1,'admin','研发部门','/trade/announcement/2','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 12:06:50',12),(293,'系统公告',3,'com.debuff.trade.controller.AnnouncementController.remove()','DELETE',1,'admin','研发部门','/trade/announcement/3','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 12:06:52',10),(294,'个人信息',2,'com.debuff.web.controller.system.SysProfileController.updateProfile()','PUT',1,'admin','研发部门','/system/user/profile','127.0.0.1','内网IP','{\"admin\":false,\"email\":\"ry@163.com\",\"nickName\":\"管理员\",\"params\":{},\"phonenumber\":\"15888888888\",\"sex\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 18:03:53',18),(295,'用户头像',2,'com.debuff.web.controller.system.SysProfileController.avatar()','POST',1,'admin','研发部门','/system/user/profile/avatar','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2026/08/27/微信图片_20260827180434_20_3_20260827180453A001.jpg\",\"code\":200}',0,NULL,'2026-08-27 18:04:54',507),(296,'个人信息',2,'com.debuff.web.controller.system.SysProfileController.updateProfile()','PUT',1,'admin','研发部门','/system/user/profile','127.0.0.1','内网IP','{\"admin\":false,\"email\":\"ry@163.com\",\"nickName\":\"管理员\",\"params\":{},\"phonenumber\":\"15888888888\",\"sex\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 18:04:57',22),(297,'系统公告',1,'com.debuff.trade.controller.AnnouncementController.add()','POST',1,'admin','研发部门','/trade/announcement','127.0.0.1','内网IP','{\"announcementType\":\"2\",\"content\":\"<p>在评论区扣1送火麒麟到邮件</p>\",\"createTime\":\"2026-08-27 18:39:15\",\"id\":4,\"params\":{},\"title\":\"扣1送火麒麟\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-08-27 18:39:15',48);
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2026-08-25 11:11:15','',NULL,''),(2,'se','项目经理',2,'0','admin','2026-08-25 11:11:15','',NULL,''),(3,'hr','人力资源',3,'0','admin','2026-08-25 11:11:15','',NULL,''),(4,'user','普通员工',4,'0','admin','2026-08-25 11:11:15','',NULL,'');
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2026-08-25 11:11:15','',NULL,'超级管理员'),(2,'普通角色','common',2,'2',1,1,'0','0','admin','2026-08-25 11:11:15','admin','2026-08-25 15:22:43','普通角色');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (1,2005),(1,2007),(1,2008),(1,2011),(1,2035),(1,2041),(1,2047),(2,1),(2,100),(2,101),(2,102),(2,105),(2,106),(2,1000),(2,1001),(2,1002),(2,1003),(2,1004),(2,1005),(2,1006),(2,1007),(2,1008),(2,1009),(2,1010),(2,1011),(2,1012),(2,1013),(2,1014),(2,1015),(2,1025),(2,1026),(2,1027),(2,1028),(2,1029),(2,1030),(2,1031),(2,1032),(2,1033),(2,1034),(2,2007),(2,2008),(2,2011);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `steam_id` varchar(64) DEFAULT '' COMMENT 'Steam64位ID',
  `avatar_url` varchar(256) DEFAULT '' COMMENT '头像URL',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,103,'admin','管理员','','','00','ry@163.com','15888888888','1','/profile/avatar/2026/08/27/微信图片_20260827180434_20_3_20260827180453A001.jpg','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-08-27 19:28:13','admin','2026-08-25 11:11:15','','2026-08-27 19:28:12','管理员'),(2,105,'ry','若依','','','00','ry@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-08-26 15:50:51','admin','2026-08-25 11:11:15','','2026-08-26 15:50:50','测试员'),(100,NULL,'test1','test1','','','00','516516@s.com','18888888888','0','/profile/avatar/2026/08/26/鱼香肉丝_20260826175506A002.png','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-08-27 18:25:17','admin','2026-08-26 16:58:09','admin','2026-08-27 18:25:17',NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,2),(100,2);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed
