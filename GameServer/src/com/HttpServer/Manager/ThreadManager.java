package com.HttpServer.Manager;

import java.util.ArrayList;
import java.util.List;

import com.HttpServer.Thread.PackThread;

public class ThreadManager {

    private static List<Thread> RoundRecord = new ArrayList<Thread>();
    
    public static void Init() {
        int i = 0;
        for (i = 0; i < 2; i++) {
            PackThread RoundLoop = new PackThread();
            RoundLoop.setName("PackThreadLoop" + i);
            RoundRecord.add(RoundLoop);
            RoundLoop.start();
        }
    }
}