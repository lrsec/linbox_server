package com.linbox.im.server.message;

/**
 * Created by lrsec on 7/19/15.
 */
public class MonitorMeta {
    private long start = 0;

    private long aesDecryptStart = 0;
    private long aesDecryptEnd = 0;

    private long nsqStart = 0;
    private long nsqEnd = 0;

    private long dataComputeEnd = 0;

    private long tcpSendStart = 0;

    private long aesEncryptStart = 0;
    private long aesEncryptEnd = 0;

    private long tcpSendEnd = 0;

    public MonitorMeta() {
        long current = System.currentTimeMillis();

        start = current;
        aesDecryptStart = current;
        aesDecryptEnd = current;
        nsqStart = current;
        nsqEnd = current;
        dataComputeEnd = current;
        tcpSendStart = current;
        aesEncryptStart = current;
        aesEncryptEnd = current;
        tcpSendEnd = current;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getAesDecryptStart() {
        return aesDecryptStart;
    }

    public void setAesDecryptStart(long aesDecryptStart) {
        this.aesDecryptStart = aesDecryptStart;
    }

    public long getAesDecryptEnd() {
        return aesDecryptEnd;
    }

    public void setAesDecryptEnd(long aesDecryptEnd) {
        this.aesDecryptEnd = aesDecryptEnd;
    }

    public long getNsqStart() {
        return nsqStart;
    }

    public void setNsqStart(long nsqStart) {
        this.nsqStart = nsqStart;
    }

    public long getNsqEnd() {
        return nsqEnd;
    }

    public void setNsqEnd(long nsqEnd) {
        this.nsqEnd = nsqEnd;
    }

    public long getTcpSendStart() {
        return tcpSendStart;
    }

    public void setTcpSendStart(long tcpSendStart) {
        this.tcpSendStart = tcpSendStart;
    }

    public long getTcpSendEnd() {
        return tcpSendEnd;
    }

    public void setTcpSendEnd(long tcpSendEnd) {
        this.tcpSendEnd = tcpSendEnd;
    }

    public long getDataComputeEnd() {
        return dataComputeEnd;
    }

    public long getAesEncryptStart() {
        return aesEncryptStart;
    }

    public void setAesEncryptStart(long aesEncryptStart) {
        this.aesEncryptStart = aesEncryptStart;
    }

    public long getAesEncryptEnd() {
        return aesEncryptEnd;
    }

    public void setAesEncryptEnd(long aesEncryptEnd) {
        this.aesEncryptEnd = aesEncryptEnd;
    }

    public void setDataComputeEnd(long dataComputeEnd) {
        this.dataComputeEnd = dataComputeEnd;
    }

    public long getDataComputeCost() {
        return dataComputeEnd - start;
    }

    public long getAESDecryptCost() {
        return aesDecryptEnd - aesDecryptStart;
    }

    public long getNSQCost() {
        return nsqEnd - nsqStart;
    }

    public long getStayInOutboxTime() {
        return tcpSendStart - dataComputeEnd;
    }

    public long getTcpSendCost() {
        return tcpSendEnd - tcpSendStart;
    }

    public long getAESEncrytCost() {
        return aesEncryptEnd - aesEncryptStart;
    }
}
