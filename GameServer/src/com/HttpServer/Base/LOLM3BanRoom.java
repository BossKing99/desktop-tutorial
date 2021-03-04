package com.HttpServer.Base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.HttpServer.Base.PlayerBase.Player;
import com.HttpServer.Portocol.NetJson;
import com.HttpServer.publicClass.Console;
import com.HttpServer.publicClass.LOLMData;
import com.HttpServer.publicClass.ProtocolName;

import org.json.JSONObject;

public class LOLM3BanRoom extends GameRoom {
    // 0是藍隊 1是紅隊
    private Map<Integer, Integer> chosen = new HashMap<>();
    private Boolean[] isReady = { false, false };
    private int[][] banData;
    private int[][] pickData = new int[2][5];
    private Timer timer = new Timer();
    private TimerTask task;
    private List<LOLMPlayer> allPlayers = new ArrayList<>();
    private long chooseTime = 30000;
    // 選角用
    private int nowCtrl = 0;
    private int[] banProcess;
    private int[] pickProcess = { 0, 1, 1, 0, 0, 1, 1, 0, 0, 1 };
    private int banFlage = 0;
    private int pickFlage = 0;

    public LOLM3BanRoom(JSONObject jdata, String[] key) {
        super(jdata, key);
        try {
            int banCount = jdata.getInt("banCount");
            banData = new int[2][banCount];
            initIntArray(banData);
            initIntArray(pickData);
            banProcess = new int[banCount * 2];
            for (int i = 0; i < banCount; i++) {
                banProcess[i * 2] = 0;
                banProcess[i * 2 + 1] = 1;
            }
            RoomJData.put("Status", _status);
            RoomJData.put("BuleBan", banData[0]);
            RoomJData.put("RedBan", banData[1]);
            RoomJData.put("BulePick", pickData[0]);
            RoomJData.put("RedPick", pickData[1]);

        } catch (Exception e) {
            Console.Err("LOLM3BanRoom Create Error");
        }
        task = new TimerTask() {
            public void run() {
                nextProcess(0);
            }
        };
    }

    private void initIntArray(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++)
                arr[i][j] = -1;
        }
    }

    @Override
    public void Ready(JSONObject jdata) {
        try {
            if (_status == RoomStatus.WAIT && jdata.optString("pass").equals(_pass)) {
                isReady[jdata.getInt("team")] = true;
                if (isReady[0] && isReady[1])
                    SetStatus(RoomStatus.BAN);
            }
        } catch (Exception e) {
            Console.Err("LOLM3BanRoom Ready Error");
        }
    }

    @Override
    public void Choose(JSONObject jdata) {
        try {
            if (jdata.optString("pass").equals(_pass)) {
                nextProcess(jdata.optInt("choose"));
            }
        } catch (Exception e) {
            Console.Err("LOLM3BanRoom Choose Error");
        }

    }

    @Override
    public void Preview(JSONObject data, String ctxId) {
        try {
            int num = data.getInt("num");
            if ((num == 0 || !chosen.containsKey(num)) && data.optString("pass").equals(_pass)) {
                LOLMPlayer player = findPlayer(ctxId);
                if (player != null && player.team == nowCtrl) {
                    JSONObject jdata = new JSONObject();
                    if (_status == RoomStatus.BAN)
                        jdata.put("banNum", banFlage);
                    else if (_status == RoomStatus.PICK)
                        jdata.put("pickNum", pickFlage);
                    else
                        return;
                    jdata.put("team", nowCtrl);
                    jdata.put("preview", num);
                    broadcast(jdata.toString(), ProtocolName.PREVIEW);
                }
            }
        } catch (Exception e) {
            Console.Err("LOLM3BanRoom Choose Error");
        }
    }

    @Override
    protected void SetStatus(RoomStatus s) {
        if (s == _status)
            return;
        _status = s;
        try {
            RoomJData.put("Status", _status);
        } catch (Exception e) {
            Console.Err("LOLM3BanRoom SetStatus Error");
        }
        switch (_status) {
            case WAIT:
                break;
            case BAN:
                timer.schedule(task, chooseTime);
                try {
                    RoomJData.put("NextTime", System.currentTimeMillis() + chooseTime);
                } catch (Exception e) {
                }
                break;
            case PICK:
                break;
            case END:
                break;
        }
    }

    @Override
    public void PlayerAdd(Player p) {
        allPlayers.add(new LOLMPlayer(p));
        broadcast(RoomJData.toString(), ProtocolName.SYNC);
    }

    private void nextProcess(int choose) {
        try {
            if (!(choose < LOLMData.MaxHeroCount() && (choose == 0 || !chosen.containsKey(choose))))
                return;
            if (choose != 0)
                chosen.put(choose, 0);
            timer.cancel();
            if (_status == RoomStatus.BAN) {
                banData[nowCtrl][banFlage / 2] = choose;
                banFlage++;
                if (banFlage == 6) {
                    SetStatus(RoomStatus.PICK);
                    nowCtrl = 0;
                } else
                    nowCtrl = banProcess[banFlage];

                RoomJData.put("BuleBan", banData[0]);
                RoomJData.put("RedBan", banData[1]);

                broadcast(RoomJData.toString(), ProtocolName.SYNC);
            } else if (_status == RoomStatus.PICK) {
                pickData[nowCtrl][pickFlage / 2] = choose;
                pickFlage++;
                if (pickFlage == 10)
                    SetStatus(RoomStatus.END);
                else
                    nowCtrl = pickProcess[pickFlage++];
                RoomJData.put("BulePick", pickData[0]);
                RoomJData.put("RedPick", pickData[1]);
                broadcast(RoomJData.toString(), ProtocolName.SYNC);
            }

            if (_status != RoomStatus.END) {
                timer.schedule(task, chooseTime);
                RoomJData.put("NextTime", System.currentTimeMillis() + chooseTime);
            }

        } catch (Exception e) {
            Console.Err("LOLM3BanRoom NextProcess Error");
        }
    }

    private void broadcast(String data, int pt) {
        for (LOLMPlayer lolmPlayer : allPlayers) {
            try {
                lolmPlayer.Write(NetJson.CreatePack(data, pt));
            } catch (Exception e) {
            }
        }
    }

    private LOLMPlayer findPlayer(String ctxId) {
        for (LOLMPlayer lolmPlayer : allPlayers) {
            if (lolmPlayer.GetCtxId().equals(ctxId))
                return lolmPlayer;
        }
        return null;
    }
}
