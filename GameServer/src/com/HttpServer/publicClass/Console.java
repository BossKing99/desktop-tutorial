package com.HttpServer.publicClass;

public class Console {
    public static void Log(String t) {
        StringBuffer log = new StringBuffer();
        log.append("[");
        log.append(UtilFun.ins.getTimeString(System.currentTimeMillis()));
        log.append("] : ");
        log.append(t);
        System.out.println(log);
    }

    public static void Err(String t) {
        StringBuffer log = new StringBuffer();
        log.append("[");
        log.append(UtilFun.ins.getTimeString(System.currentTimeMillis()));
        log.append("] : ");
        log.append(t);
        System.err.println(log);
    }
}