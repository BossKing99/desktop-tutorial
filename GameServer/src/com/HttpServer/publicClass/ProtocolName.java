package com.HttpServer.publicClass;

public class ProtocolName {
    private ProtocolName() {
    }

    public static final int LOGIN = 1;
    public static final int CREATE_ROOM = 2; // 創房
    public static final int LINK_ROOM = 3; // 連進房
    public static final int CHOOSE = 4; // 選角
    public static final int SYNC = 5; // 同步房間資訊
    public static final int PREVIEW = 6; // 預選角
    public static final int GET_DATA = 7;
    public static final int READY = 8;
    public static final int Compose = 9;
    public static final int GetCompose = 10;
}
