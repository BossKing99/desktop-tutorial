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

public class HPBanRoom extends GameRoom {
    // 0是藍隊 1是紅隊
    private Map<Integer, Integer> chosen = new HashMap<>();
    private Boolean[] isReady = { false, false };
    private int[] banData = new int[2];
    private int[][] pickData = new int[3][10];
    private Timer timer = new Timer();
    private TimerTask task;
    private List<LOLMPlayer> allPlayers = new ArrayList<>();
    private long chooseTime = 60000;
    private long composeTime = 300000;
    // 選角用
    private int nowCtrl = 0;
    private int previewNum = 0;

    public HPBanRoom(JSONObject jdata, String[] key) {
        super(jdata, key);
        try {
            initIntArray(banData);
            RoomJData.put("Status", _status);
            RoomJData.put("PickList", pickData);
            RoomJData.put("Ready", isReady);
            RoomInfo = new JSONObject();
            RoomInfo.put("blue", jdata.get("blueTeamName"));
            RoomInfo.put("red", jdata.get("redTeamName"));
            RoomInfo.put("game", jdata.get("gameName"));
        } catch (Exception e) {
            Console.Err("HPBanRoom Create Error");
        }
    }

    private void initIntArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = -1;
        }
    }

    @Override
    public void Ready(JSONObject jdata) {
        try {
            Console.Log(jdata.optString("pass"));
            Console.Log(_pass);
            if (_status == RoomStatus.WAIT && jdata.optString("pass").equals(_pass)) {
                isReady[jdata.getInt("team")] = true;
                RoomJData.put("Ready", isReady);
                if (isReady[0] && isReady[1])
                    SetStatus(RoomStatus.BAN);
                broadcast(RoomJData.toString(), ProtocolName.SYNC);
            }
        } catch (Exception e) {
            Console.Err("HPBanRoom Ready Error");
        }
    }

    @Override
    public void Choose(JSONObject jdata, String ctxId) {
        try {
            if (jdata.optString("pass").equals(_pass)) {
                LOLMPlayer player = findPlayer(ctxId);
                if (player != null && player.team == nowCtrl) {
                    nextProcess(jdata.optInt("choose"));
                }
            }
        } catch (Exception e) {
            Console.Err("HPBanRoom Choose Error");
        }

    }

    @Override
    public void Preview(JSONObject data, String ctxId) {
        try {
            int num = data.getInt("num");
            if ((num == 0 || !chosen.containsKey(num)) && data.optString("pass").equals(_pass)) {
                previewNum = num;
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
                    jdata.put("preview", previewNum);
                    broadcast(jdata.toString(), ProtocolName.PREVIEW);
                }
            }
        } catch (Exception e) {
            Console.Err("HPBanRoom Choose Error");
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
            Console.Err("HPBanRoom SetStatus Error");
        }
        switch (_status) {
        case WAIT:
            break;
        case BAN:
            task = new TimerTask() {
                public void run() {
                    nextProcess(previewNum);
                }
            };
            timer = new Timer();
            timer.schedule(task, chooseTime);
            try {
                RoomJData.put("NextTime", System.currentTimeMillis() + chooseTime);
                RoomJData.put("banFlage", banFlage);
                nowCtrl = 0;
                RoomJData.put("nowCtrl", nowCtrl);
            } catch (Exception e) {
            }
            break;
        case PICK:
            try {
                RoomJData.put("pickFlage", pickFlage);
            } catch (Exception e) {
            }
            break;
        case END:
            break;
        }
    }

    @Override
    public void PlayerAdd(Player p, int team) {
        p.SetRoom(this);
        synchronized (allPlayers) {
            allPlayers.add(new LOLMPlayer(p, team));
        }
        broadcast(RoomJData.toString(), ProtocolName.SYNC);
    }

    @Override
    public void RmPlayer(Player p) {
        Console.Log("RmPlayer start count = " + allPlayers.size());
        LOLMPlayer lolp = findPlayer(p.GetCtxId());
        synchronized (allPlayers) {
            if (lolp != null)
                allPlayers.remove(lolp);
        }
        Console.Log("RmPlayer end count = " + allPlayers.size());
    }

    private void nextProcess(int choose) {
        previewNum = 0;
        try {
            if (!(choose < LOLMData.MaxHeroCount() && (choose == 0 || !chosen.containsKey(choose))))
                return;
            if (choose != 0)
                chosen.put(choose, 0);
            timer.cancel();
            if (_status == RoomStatus.BAN) {
                banData[banFlage] = choose;
                banFlage++;
                if (banFlage == banData.length) {
                    SetStatus(RoomStatus.PICK);
                    nowCtrl = 0;
                } else
                    nowCtrl = banProcess[banFlage];
                RoomJData.put("nowCtrl", nowCtrl);
                RoomJData.put("BanList", banData);
                RoomJData.put("banFlage", banFlage);
            } else if (_status == RoomStatus.PICK) {
                pickData[pickFlage] = choose;
                pickFlage++;
                if (pickFlage == 10)
                    SetStatus(RoomStatus.END);
                else
                    nowCtrl = pickProcess[pickFlage];
                RoomJData.put("nowCtrl", nowCtrl);
                RoomJData.put("PickList", pickData);
                RoomJData.put("pickFlage", pickFlage);
            }

            if (_status != RoomStatus.END) {
                task = new TimerTask() {
                    public void run() {
                        nextProcess(previewNum);
                    }
                };
                timer = new Timer();
                timer.schedule(task, chooseTime);
                RoomJData.put("NextTime", System.currentTimeMillis() + chooseTime);
            }
            broadcast(RoomJData.toString(), ProtocolName.SYNC);
        } catch (Exception e) {
            Console.Err("HPBanRoom NextProcess Error e = " + e);
        }
    }

    private void broadcast(String data, int pt) {
        String jdata = NetJson.CreatePack(data, pt);
        Console.Log(jdata);
        for (LOLMPlayer lolmPlayer : allPlayers) {
            try {
                if (lolmPlayer != null)
                    lolmPlayer.Write(jdata);
            } catch (Exception e) {
                Console.Err("HPBanRoom broadcast Error e = " + e);
            }
        }
    }

    private LOLMPlayer findPlayer(String ctxId) {
        for (LOLMPlayer lolmPlayer : allPlayers) {
            if (lolmPlayer != null && lolmPlayer.GetCtxId().equals(ctxId))
                return lolmPlayer;
        }
        return null;
    }
}
