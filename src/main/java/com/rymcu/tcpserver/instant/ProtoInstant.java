package com.rymcu.tcpserver.instant;

/**
 * 编解码常量配置
 *
 * @author 程就人生
 * @date 2020年7月13日
 * @Description
 */
public class ProtoInstant {

    /**
     * 字头
     */
    public static final int FIELD_HEAD = 0xcc;

    /**
     * 字头长度（字头 + 数据长度）
     */
    public static final int FILED_LEN = 3;

    //心跳
    public static final int HEART_BEAT = 0;

    //登录
    public static final int LOGIN = 1;

    public static final Integer DEVICE_ID = 1;
}
