DROP TABLE IF EXISTS `mdt_im_session_message`;

CREATE TABLE `mdt_im_session_message` (
  `RId` BIGINT(20) NOT NULL COMMENT '消息发送方的 rid',
  `SessionId`  VARCHAR(32) NOT NULL COMMENT '对话 session id',
  `FromUserID` BIGINT(20)  NOT NULL,
  `ToUserID`   BIGINT(20)  NOT NULL,
  `MsgID`      BIGINT(20)  NOT NULL COMMENT '消息编号',
  `MineType`   VARCHAR(64) NOT NULL COMMENT '多媒体文件类型',
  `Content`    VARCHAR(500) DEFAULT NULL COMMENT '文件内容',
  `SendTime`   BIGINT(20)  NOT NULL COMMENT '服务器端接收到消息的时间,消息发送时间',
  `Created`    BIGINT(20)  NOT NULL
  COMMENT '记录创建时间',
  UNIQUE KEY `session_msg` (`SessionId`, `MsgID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话信息记录';


DROP TABLE IF EXISTS `mdt_im_group_message`;

CREATE TABLE `mdt_im_group_message` (
  `RId` BIGINT(20) NOT NULL COMMENT '消息发送方的 rid',
  `GroupId`  VARCHAR(32) NOT NULL COMMENT '对话 session id',
  `FromUserID` BIGINT(20)  NOT NULL,
  `MsgID`      BIGINT(20)  NOT NULL COMMENT '消息编号',
  `MineType`   VARCHAR(64) NOT NULL COMMENT '多媒体文件类型',
  `Content`    VARCHAR(500) DEFAULT NULL COMMENT '文件内容',
  `SendTime`   BIGINT(20)  NOT NULL COMMENT '服务器端接收到消息的时间,消息发送时间',
  `Created`    BIGINT(20)  NOT NULL COMMENT '记录创建时间',
  UNIQUE KEY `group_msg` (`GroupId`, `MsgID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话信息记录';



