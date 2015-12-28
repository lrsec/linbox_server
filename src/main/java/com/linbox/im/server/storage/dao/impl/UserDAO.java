package com.linbox.im.server.storage.dao.impl;

import com.google.common.base.Strings;
import com.linbox.im.server.storage.dao.IUserDAO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

/**
 * Created by lrsec on 7/3/15.
 */
@Service
public class UserDAO implements IUserDAO {

    public static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    @Autowired
    private Sql2o sql2o;

    public String getUserName(String userId) {
        if (StringUtils.isBlank(userId)) {
            logger.warn("Query user name for null or empty user id");
            return "";
        }

        String sql = "select RealName from profile where AccountID = :accountId";


        String realName;

        try (Connection conn = sql2o.open()) {
            realName = conn.createQuery(sql)
                    .addParameter("accountId", userId)
                    .executeAndFetchFirst(String.class);
        }

        return Strings.nullToEmpty(realName);
    }

    @Override
    public Boolean isUserValid(String userID, String token) {
        return true;
    }
}
