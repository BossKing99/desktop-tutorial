package com.HttpServer.Base;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.HttpServer.Base.PlayerBase.Player;
import com.HttpServer.Manager.GameRoomManager;
import com.HttpServer.Portocol.NetJson;
import com.HttpServer.publicClass.Console;
import com.HttpServer.publicClass.ProtocolName;

import org.json.JSONArray;
import org.json.JSONObject;

public class HPBanRoom extends GameRoom {
    // 0是藍隊 1是紅隊
    private Boolean[] isReady = { false, false };
    private int[] banData = { -1, -1 };
    private int[][][] composeData;
    private Timer timer = new Timer();
    private TimerTask task;
    private List<LOLMPlayer> allPlayers = new ArrayList<>();
    private long chooseTime = 180000;
    private long composeTime = 480000;
    // 選角用
    private int nowCtrl = 0;
    private boolean[] hide = { true, true, true, true, false, false, false, false, true, true, false, true };
    private int composeCount;

    public HPBanRoom(JSONObject jdata, String key) {
        super(jdata, key);
        String[] pass = new String[2];

        try {
            composeCount = jdata.getInt("composeCount");
            composeData = new int[2][composeCount][12];
            RoomJData.put("Status", _status);
            RoomJData.put("ComposeCount", composeCount);
            RoomJData.put("Ready", isReady);
            RoomInfo = new JSONObject();
            RoomInfo.put("blue", jdata.get("blueTeamName"));
            RoomInfo.put("red", jdata.get("redTeamName"));
            RoomInfo.put("game", jdata.get("gameName"));

            pass[0] = GameRoomManager.GetKey(RoomJData.toString() + System.currentTimeMillis() + "asdasfgdfth")
                    .substring(5, 12);
            ;
            pass[1] = GameRoomManager.GetKey(RoomJData.toString() + System.currentTimeMillis() + "thrthrthtrth")
                    .substring(5, 12);
            ;
            RoomInfo.put("pass", pass);
            RoomInfo.put("composeCount", composeCount);
            SetRoomPass(pass);
        } catch (Exception e) {
            Console.Err("HPBanRoom Create Error e = " + e);
        }
    }

    @Override
    public void Ready(JSONObject jdata) {
        try {
            if (_status == RoomStatus.WAIT) {
                isReady[jdata.getInt("team")] = true;
                RoomJData.put("Ready", isReady);
                if (isReady[0] && isReady[1])
                    SetStatus(RoomStatus.COMPOSE);
                broadcast(RoomJData.toString(), ProtocolName.SYNC);
            }
        } catch (Exception e) {
            Console.Err("HPBanRoom Ready Error");
        }
    }

    @Override
    public void Choose(JSONObject jdata, String ctxId) {
        try {
            if (_status == RoomStatus.BAN) {
                LOLMPlayer player = findPlayer(ctxId);
                if (player != null && player.team == nowCtrl) {
                    banData[nowCtrl] = jdata.optInt("choose");
                    RoomJData.put("BanList", banData);
                    broadcast(RoomJData.toString(), ProtocolName.SYNC);
                }
            }
        } catch (Exception e) {
            Console.Err("HPBanRoom Choose Error");
        }

    }

    @Override
    public void Preview(JSONObject data, String ctxId) {
    }

    @Override
    public void Compose(JSONObject jdata) {
        try {
            if (_status == RoomStatus.COMPOSE) {
                int choose = jdata.optInt("choose");
                int no = jdata.optInt("no");
                int group = jdata.optInt("group");
                int team = jdata.optInt("team");
                composeData[team][group][no] = choose;
                broadcast(jdata.toString(), ProtocolName.Compose, team);
            }
        } catch (Exception e) {
            Console.Err("HPBanRoom Compose Error e = " + e);
        }
    }

    @Override
    public JSONObject GetCompose(int team) {
        JSONObject jdata = new JSONObject();
        try {
            jdata.put("pick", composeData[team]);
        } catch (Exception e) {
            Console.Log("GetCompose pick data conversion error");
        }

        return jdata;
    }

    public JSONObject GetAllCompose(boolean isHaid) {
        JSONObject jdata = new JSONObject();
        try {
            if (isHaid) {
                JSONArray buleComposeList = new JSONArray();
                JSONArray redComposeList = new JSONArray();

                for (int j = 0; j < composeCount; j++) {
                    JSONArray buleCompose = new JSONArray();
                    JSONArray redCompose = new JSONArray();
                    for (int i = 0; i < 12; i++) {
                        buleCompose.put(hide[i] ? composeData[0][j][i] : -1);
                        redCompose.put(hide[i] ? composeData[1][j][i] : -1);
                    }
                    buleComposeList.put(buleCompose);
                    redComposeList.put(redCompose);
                }
                jdata.put("buleCompose", redComposeList);
                jdata.put("redCompose", buleComposeList);
            } else {
                jdata.put("buleCompose", composeData[0]);
                jdata.put("redCompose", composeData[1]);
            }
        } catch (Exception e) {
            Console.Log("GetCompose pick data conversion error");
        }

        return jdata;
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
            case PICK:
                break;
            case WAIT:
                break;
            case BAN:
                task = new TimerTask() {
                    public void run() {
                        nextProcess(0);
                    }
                };
                timer = new Timer();
                timer.schedule(task, chooseTime);
                try {
                    RoomJData.put("NextTime", System.currentTimeMillis() + chooseTime);
                    nowCtrl = 0;
                    RoomJData.put("NowCtrl", nowCtrl);
                    RoomJData.put("HideCompose", GetAllCompose(true));
                } catch (Exception e) {
                }
                broadcast(RoomJData.toString(), ProtocolName.SYNC);
                break;

            case COMPOSE:
                task = new TimerTask() {
                    public void run() {
                        SetStatus(RoomStatus.BAN);
                    }
                };
                timer = new Timer();
                timer.schedule(task, composeTime);
                try {
                    RoomJData.put("NextTime", System.currentTimeMillis() + composeTime);
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
        try {
            if (!(choose < composeCount && choose >= 0))
                return;
            timer.cancel();
            if (_status == RoomStatus.BAN) {
                if (banData[nowCtrl] == -1) {
                    banData[nowCtrl] = 0;
                    RoomJData.put("BanList", banData);
                }
                nowCtrl++;
                RoomJData.put("NowCtrl", nowCtrl);
                if (nowCtrl == banData.length) {
                    SetStatus(RoomStatus.END);
                    nowCtrl = 0;
                    RoomJData.put("Compose", GetAllCompose(false));
                }
            }

            if (_status != RoomStatus.END) {
                task = new TimerTask() {
                    public void run() {
                        nextProcess(0);
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

    private void broadcast(String data, int pt, int team) {
        String jdata = NetJson.CreatePack(data, pt);
        Console.Log(jdata);
        for (LOLMPlayer lolmPlayer : allPlayers) {
            try {
                if (lolmPlayer != null && lolmPlayer.team == team)
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
