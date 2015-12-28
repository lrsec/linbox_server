package com.linbox.im.server.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lrsec on 7/2/15.
 *
 * 注意，该类为了使用泛型，因此取消了对 locks pool 使用 static 变量，因此请确保使用单例模式。
 * 非单例模式下无法正常工作。
 */
public class StopcockService<T> {
    private final ConcurrentHashMap<T, ReentrantLock> locks = new ConcurrentHashMap();
    private ThreadLocal<ReentrantLock> lockHolder = new ThreadLocal<ReentrantLock>();

    protected void lock(T id) {
        ReentrantLock myLock = new ReentrantLock();
        myLock.lock();

        lockHolder.set(myLock);

        ReentrantLock waitLock = locks.putIfAbsent(id, myLock);

        while (waitLock != null) {
            waitLock.lock();

            ReentrantLock newLock = null;
            try {
                newLock = locks.putIfAbsent(id, myLock);
            } finally {
                waitLock.unlock();
                waitLock = newLock;
            }
        }
    }

    protected void unlock(T id) {
        ReentrantLock myLock = lockHolder.get();
        locks.remove(id, myLock);

        if (myLock != null) {
            myLock.unlock();
        }
    }
}
