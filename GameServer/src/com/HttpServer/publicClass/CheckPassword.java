package com.HttpServer.publicClass;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class CheckPassword {
    private CheckPassword() {
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static TimeZone locol_UTC = TimeZone.getTimeZone("GMT+8");

    public static boolean Check(String pass) {
        String todayPass = "";
        sdf.setTimeZone(locol_UTC);
        Date date = new Date(System.currentTimeMillis());
        float y = Float.parseFloat(new SimpleDateFormat("yyyy").format(date));
        float md = Float.parseFloat(new SimpleDateFormat("MMdd").format(date));
        todayPass = (md / y) + "";
        todayPass = todayPass.substring(6, 10);
        return pass.equals(todayPass);
    }
}
