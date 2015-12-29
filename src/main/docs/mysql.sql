
DROP TABLE IF EXISTS `im_session_message`;

CREATE TABLE `im_session_message` (
  `RId` BIGINT(20) NOT NULL COMMENT '消息发送方的 rid',
  `SessionId`  VARCHAR(32) NOT NULL COMMENT '对话 session id',
  `FromUserID` BIGINT(20)  NOT NULL,
  `ToUserID`   BIGINT(20)  NOT NULL,
  `MsgID`      BIGINT(20)  NOT NULL COMMENT '消息编号',
  `MimeType`   VARCHAR(64) NOT NULL COMMENT '多媒体文件类型',
  `Content`    VARCHAR(500) DEFAULT NULL COMMENT '文件内容',
  `SendTime`   BIGINT(20)  NOT NULL COMMENT '服务器端接收到消息的时间,消息发送时间',
  `Created`    BIGINT(20)  NOT NULL
  COMMENT '记录创建时间',
  UNIQUE KEY `session_msg` (`SessionId`, `MsgID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话信息记录';


DROP TABLE IF EXISTS `im_group_message`;

CREATE TABLE `im_group_message` (
  `RId` BIGINT(20) NOT NULL COMMENT '消息发送方的 rid',
  `GroupId`  VARCHAR(32) NOT NULL COMMENT '对话 session id',
  `FromUserID` BIGINT(20)  NOT NULL,
  `MsgID`      BIGINT(20)  NOT NULL COMMENT '消息编号',
  `MimeType`   VARCHAR(64) NOT NULL COMMENT '多媒体文件类型',
  `Content`    VARCHAR(500) DEFAULT NULL COMMENT '文件内容',
  `SendTime`   BIGINT(20)  NOT NULL COMMENT '服务器端接收到消息的时间,消息发送时间',
  `Created`    BIGINT(20)  NOT NULL COMMENT '记录创建时间',
  UNIQUE KEY `group_msg` (`GroupId`, `MsgID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话信息记录';

DROP TABLE IF EXISTS `group_members`;

CREATE TABLE `group_members` (
  `Id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id，用于表维护目的',
  `GroupId` bigint(20) unsigned NOT NULL COMMENT '群组id',
  `AccountId` bigint(20) unsigned NOT NULL COMMENT '系统用户 id',
  `Role` int(2) DEFAULT '0' COMMENT '用户在群组中的身份',
  `Visible` tinyint(1) DEFAULT '1' COMMENT '消息是否在用户列表可见',
  `Created` bigint(20) unsigned NOT NULL,
  `Updated` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `group_id` (`GroupId`),
  KEY `account_id` (`AccountId`)
) ENGINE=InnoDB AUTO_INCREMENT=555 DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `profile`;

CREATE TABLE `profile` (
  `AccountID` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '账户ID',
  `MDTCode` bigint(20) NOT NULL,
  `ChatID` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `Birthday` date DEFAULT NULL COMMENT '生日',
  `Profession` int(2) NOT NULL DEFAULT '0' COMMENT '职业：\n0-未知\n10-医生\n20-护士\n30-医学生\n90-医护工作者',
  `Signature` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Phone` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '电话',
  `OftenPlace` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '出没地点',
  `Interest` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL,
  `ActiveState` int(1) NOT NULL DEFAULT '0' COMMENT '账户激活状态：\n0-未激活;\n1-已激活;\n9-停用;',
  `RealName` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
  `NickName` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Age` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `Gender` int(2) DEFAULT '0' COMMENT 'Secrecy(0,"保密"), /** * 男性 */ Male(1,"男"), /** * 女性 */ Female(2,"女");',
  `Constellation` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `LastActive` int(11) DEFAULT NULL,
  `Status` int(2) DEFAULT '0' COMMENT '状态\n0-离线\n1-在线\n2-手术中\n3-门诊中\n4-工作中\n5-休假中',
  `Avatar` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `OrgName` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '机构名称',
  `OrgID` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '机构ID',
  `DeptName` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '部门名称',
  `DeptID` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '部门ID',
  `Title` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '学历或职位名称',
  `TitleType` int(1) DEFAULT '0' COMMENT '学历或职位类型（不同经历枚举含义不同）：\n0-未设置\n\n学校：\n1-大专\n2-本科\n3-硕士\n4-博士\n5-其他\n\n医院',
  `Created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `Updated` timestamp NULL DEFAULT NULL,
  `IsCertificated` int(1) DEFAULT '0' COMMENT '是否认证',
  `CertificateInfo` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '认证信息，由审核人员添加',
  `FirstDeptName` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '一级部门名称',
  `CreatedNew` int(11) DEFAULT '0',
  `UpdatedNew` int(11) DEFAULT '0',
  `PinYin` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名拼音字段',
  `Province` varchar(50) CHARACTER SET utf8mb4 DEFAULT '0',
  `City` varchar(50) CHARACTER SET utf8mb4 DEFAULT '0',
  `Achievement` varchar(250) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Sideline` varchar(130) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Zone` int(2) DEFAULT '2',
  PRIMARY KEY (`AccountID`),
  UNIQUE KEY `MDTCode_UNIQUE123` (`MDTCode`),
  UNIQUE KEY `AccountID_UNIQUE` (`AccountID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT COMMENT='用户信息表'