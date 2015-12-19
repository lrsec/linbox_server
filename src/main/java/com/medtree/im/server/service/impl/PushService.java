package com.medtree.im.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import com.medtree.im.message.Message;
import com.medtree.im.server.constant.RedisKey;
import com.medtree.im.server.service.IInboxService;
import com.medtree.im.server.service.IPushService;
import com.medtree.im.server.storage.dao.IUserDAO;
import com.medtree.im.utils.IdGenerator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lrsec on 7/16/15.
 */
@Service
public class PushService implements IPushService {
    private static Logger logger = LoggerFactory.getLogger(PushService.class);

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private IInboxService inboxService;

    @Autowired
    private IUserDAO userDAO;

    public void sendPush(Message message) {
        if (message == null) {
            logger.error("Get an empty message for sending push");
            return;
        }

        PushMessage pushMsg = new PushMessage();

        pushMsg.ID = IdGenerator.getUUID();
        pushMsg.Type = "InstanceMessage";
        pushMsg.Title = "医树";

        String realName = message.fromUserId.equals("10000") || message.fromUserId.equals("10001") ? "医树" : userDAO.getUserName(message.fromUserId);
        pushMsg.Title = realName;

        if (StringUtils.startsWith(message.mineType, "text")) {
            pushMsg.Description = message.content;
            pushMsg.Message = message.content;
        } else if (StringUtils.startsWith(message.mineType, "image")) {
            pushMsg.Description = "[图片]";
            pushMsg.Message = "[图片]";
        } else if (StringUtils.startsWith(message.mineType, "audio")) {
            pushMsg.Description = "[音频]";
            pushMsg.Message = "[音频]";
        } else if (StringUtils.startsWith(message.mineType, "video")){
            pushMsg.Description = "[视频]";
            pushMsg.Message = "[视频]";
        }

        pushMsg.ActionType = "Text";
        pushMsg.BatchId = 0;
        pushMsg.From = Long.parseLong(message.fromUserId);
        pushMsg.To = Long.parseLong( message.toUserId );
        pushMsg.Badge = getTotalUnread(message.toUserId);
        pushMsg.Channel = "unknown";
        pushMsg.Creator = Long.parseLong(message.fromUserId);
        pushMsg.Created = System.currentTimeMillis() / 1000l;
        pushMsg.Updated = pushMsg.Created;
        pushMsg.State = "Readed";

        UserMessage<PushMessage> msg = new UserMessage<PushMessage>();
        msg.data = pushMsg;
        msg.addMeta("isPush", true);

        //TODO push not work now
//        NSQHelper.produce(NSQTopic.TOPIC_PUSH, msg);

        logger.info("join push message nsq: {}", pushMsg.ID);
    }

    private int getTotalUnread(String userId) {
        int unread=0;

        if (StringUtils.isBlank(userId)) {
            logger.debug("Blank userId when getting total unread");
            return unread;
        }

        try (Jedis jedis = jedisPool.getResource()) {
            //获取缓存中新朋友的集合的长度 即新朋友的个数
            unread += Math.max(0, jedis.hlen(RedisKey.getFriendRequestsKey(Long.parseLong(userId))).intValue());

            //获取缓存中新评论的个数
            String newCount = jedis.hget(RedisKey.getUserUnReadCountsKey(Long.parseLong(userId)), NotifyMessageDataItems.NewCommentsCount);
            if (StringUtils.isNotBlank(newCount)) {
                unread += Math.max(0, Integer.parseInt(newCount));
            }

            //获取缓存中未读帮帮忙的个数
            newCount = jedis.hget(RedisKey.getUserUnReadCountsKey(Long.parseLong(userId)), NotifyMessageDataItems.NewForumHelpCommentCount);
            if (StringUtils.isNotBlank(newCount)) {
                unread += Math.max(0, Integer.parseInt(newCount));
            }

            newCount = jedis.hget(RedisKey.getUserUnReadCountsKey(Long.parseLong(userId)), NotifyMessageDataItems.NewForumHelpInviteCount);
            if (StringUtils.isNotBlank(newCount)) {
                unread += Math.max(0, Integer.parseInt(newCount));
            }

            newCount = jedis.hget(RedisKey.getUserUnReadCountsKey(Long.parseLong(userId)), NotifyMessageDataItems.NewForumHelpPointCount);
            if (StringUtils.isNotBlank(newCount)) {
                unread += Math.max(0, Integer.parseInt(newCount));
            }

            newCount = jedis.hget(RedisKey.getUserUnReadCountsKey(Long.parseLong(userId)), NotifyMessageDataItems.NewForumHelpCommentLikeCount);
            if (StringUtils.isNotBlank(newCount)) {
                unread += Math.max(0, Integer.parseInt(newCount));
            }

            //获取缓存中未读赞的个数
            newCount = jedis.hget(RedisKey.getUserUnReadCountsKey(Long.parseLong(userId)), NotifyMessageDataItems.NewLikesCount);
            if (StringUtils.isNotBlank(newCount)) {
                unread += Math.max(0, Integer.parseInt(newCount));
            }

            // 获取未读消息总数
            unread += inboxService.getTotalUnreadCount(userId);
        } catch (Exception e) {
            logger.error("Get exception for getting total unread for user " + userId, e);
        }

        unread = Math.max(1, unread);

        return unread;
    }

    private static class PushMessage implements Serializable {
        public long ID;
        public String Type = "Unknown";
        public String Title;
        public String Description;
        public String Message;
        public String ActionType = "Text";
        public long BatchId;
        public String URL;
        public long From;
        public Long To;
        public int Badge=1;
        public String Channel = "unknown";
        public long Creator;
        public long Created;
        public long Updated;
        public String State = "Readed";

        @Override
        public String toString() {
            return "ID:"+ID+",Type:"+Type+",Title:"+Title+",Description:"+ Description+
                    ",Message:"+Message+",ActionType:"+ActionType+",BatchId:"+BatchId+
                    ",URL:"+URL+",From:"+From+",To:"+To+",Badge:"+Badge+",Channel:"+Channel+",Creator:"+Creator;
        }
    }

    //TODO this should be deleted
    private static class UserMessage<T> extends NSQMessageBase {
        public UserMessage() {
        }

        @JSONField(
                serialize = false
        )
        public long getUserId() {
            Object uid = this.getMeta("user_id");
            return uid != null?Long.parseLong(uid.toString()):0L;
        }

        public void setUserId(long userId) {
            this.addMeta("user_id", String.valueOf(userId));
        }

        @JSONField(
                serialize = false
        )
        public UserMessage<T> getMessage(String json) {
            return (UserMessage) JSON.parseObject(json, new TypeReference() {
            }, new Feature[0]);
        }
    }

    //TODO This should be deleted
    private static class NSQMessageBase<T> implements Serializable {
        public HashMap<String, Object> meta;
        public List<T> list = new ArrayList();
        public T data;

        public NSQMessageBase() {
        }

        public Object getMeta(String key) {
            return this.meta != null && this.meta.containsKey(key)?this.meta.get(key):null;
        }

        public NSQMessageBase addMeta(String key, Object data) {
            if(this.meta == null) {
                this.meta = new HashMap();
            }

            this.meta.put(key, data);
            return this;
        }

        public NSQMessageBase<T> getMessage(String json) {
            return (NSQMessageBase)JSON.parseObject(json, new TypeReference() {
            }, new Feature[0]);
        }
    }

    private static  class NotifyMessageDataItems{
        public static String NewLikesCount="new_likes_count";
        public static String NewCommentsCount="new_comments_count";

        public static String NewForumHelpCount="new_forum_help_count";
        public static String NewForumHelpInviteCount="new_forum_help_invite_count";
        public static String NewForumHelpCommentCount="new_forum_help_comment_count";
        public static String NewForumHelpPointCount="new_forum_help_point_count";
        public static String NewForumHelpCommentLikeCount="new_forum_help_comment_like_count";

        public static String ContactMobileMatchedUserCount="contact_mobile_match_count";
        public static String ContactNameMatchedUserCount="contact_name_match_count";
        public static String FriendRequestCount="friend_request_count";

        public static String IMNewMessageCount="im_new_message_count";

        //推荐好友和好友动态最后一条的用户头像
        @Deprecated
        public static String UserAvatar="user_avatar";
        public static String NewFriendFeedUserAvatar="new_feeds_user_avatar";
        public static String NewFriendRecommendUserAvatar="new_recommend_user_avatar";
    }
}


