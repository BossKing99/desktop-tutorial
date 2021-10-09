package com.HttpServer.Base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.HttpServer.Base.PlayerBase.Player;
import com.HttpServer.Manager.GameRoomManager;
import com.HttpServer.Portocol.NetJson;
import com.HttpServer.publicClass.Console;
import com.HttpServer.publicClass.LOLMData;
import com.HttpServer.publicClass.ProtocolName;

import org.json.JSONObject;

public class LOLM3BanRoom extends GameRoom {
    // 0是藍隊 1是紅隊
    private Map<Integer, Integer> chosen = new HashMap<>();
    private Boolean[] isReady = { false, false };
    private int[] banData;
    private int[] pickData = new int[10];
    private Timer timer = new Timer();
    private TimerTask task;
    private List<LOLMPlayer> allPlayers = new ArrayList<>();
    private long chooseTime = 60000;
    // 選角用
    private int nowCtrl = 0;
    private int[] banProcess;
    private int[] pickProcess = { 0, 1, 1, 0, 0, 1, 1, 0, 0, 1 };
    private int banFlage = 0;
    private int pickFlage = 0;
    private int previewNum = 0;

    public LOLM3BanRoom(JSONObject jdata, String key) {
        super(jdata, key);
        String[] pass = new String[2];

        try {
            int banCount = jdata.getInt("banCount");
            banData = new int[2 * banCount];
            initIntArray(banData);
            initIntArray(pickData);
            banProcess = new int[banCount * 2];
            for (int i = 0; i < banCount; i++) {
                banProcess[i * 2] = 0;
                banProcess[i * 2 + 1] = 1;
            }
            RoomJData.put("Status", _status);
            RoomJData.put("BanList", banData);
            RoomJData.put("PickList", pickData);
            RoomJData.put("Ready", isReady);
            RoomInfo = new JSONObject();
            RoomInfo.put("blue", jdata.get("blueTeamName"));
            RoomInfo.put("red", jdata.get("redTeamName"));
            RoomInfo.put("game", jdata.get("gameName"));
            RoomInfo.put("banCount", jdata.get("banCount"));

            pass[0] = GameRoomManager.GetKey(RoomJData.toString() + System.currentTimeMillis() + "asdasfgdfth")
                    .substring(5, 12);
            pass[1] = GameRoomManager.GetKey(RoomJData.toString() + System.currentTimeMillis() + "thrthrthtrth")
                    .substring(5, 12);
            RoomInfo.put("pass", pass);
            SetRoomPass(pass);
        } catch (Exception e) {
            Console.Err("LOLM3BanRoom Create Error");
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
            if (_status == RoomStatus.WAIT) {
                isReady[jdata.getInt("team")] = true;
                RoomJData.put("Ready", isReady);
                if (isReady[0] && isReady[1])
                    SetStatus(RoomStatus.BAN);
                broadcast(RoomJData.toString(), ProtocolName.SYNC);
            }
        } catch (Exception e) {
            Console.Err("LOLM3BanRoom Ready Error");
        }
    }

    @Override
    public void Choose(JSONObject jdata, String ctxId) {
        try {

            LOLMPlayer player = findPlayer(ctxId);
            if (player != null && player.team == nowCtrl) {
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
            if ((num == 0 || !chosen.containsKey(num))) {
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
            Console.Err("LOLM3BanRoom Choose Error");
        }
    }

    @Override
    public void Compose(JSONObject jdata) {
    }

    @Override
    public JSONObject GetCompose(String pass, int team) {
        return null;
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
            case COMPOSE:
                break;
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
            Console.Err("LOLM3BanRoom NextProcess Error e = " + e);
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
                Console.Err("LOLM3BanRoom broadcast Error e = " + e);
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
