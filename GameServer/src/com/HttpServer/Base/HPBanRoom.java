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
    private int[][][] pickData;
    private Timer timer = new Timer();
    private TimerTask task;
    private List<LOLMPlayer> allPlayers = new ArrayList<>();
    private long chooseTime = 60000;
    private long composeTime = 300000;
    // 選角用
    private int nowCtrl = 0;
    private boolean[] hide = { true, true, true, true, false, false, false, false, true, true, false, true };
    private int composeCount;

    public HPBanRoom(JSONObject jdata, String key) {
        super(jdata, key);
        String[] pass = new String[2];

        try {
            composeCount = jdata.getInt("composeCount");
            pickData = new int[2][composeCount][10];
            RoomJData.put("Status", _status);
            RoomJData.put("PickList", pickData);
            RoomJData.put("Ready", isReady);
            RoomInfo = new JSONObject();
            RoomInfo.put("blue", jdata.get("blueTeamName"));
            RoomInfo.put("red", jdata.get("redTeamName"));
            RoomInfo.put("game", jdata.get("gameName"));

            pass[0] = GameRoomManager.GetKey(RoomJData.toString() + System.currentTimeMillis() + "asdasfgdfth");
            pass[1] = GameRoomManager.GetKey(RoomJData.toString() + System.currentTimeMillis() + "thrthrthtrth");
            RoomInfo.put("pass", pass);
            RoomInfo.put("pickCount", composeCount);
            SetRoomPass(pass);
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
            LOLMPlayer player = findPlayer(ctxId);
            if (player != null && player.team == nowCtrl) {
                nextProcess(jdata.optInt("choose"));
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
                int chose = jdata.optInt("chose");
                int no = jdata.optInt("no");
                int group = jdata.optInt("group");
                int team = jdata.optInt("group");
                pickData[team][group][no] = chose;
                broadcast(jdata.toString(), ProtocolName.Compose, team);
            }
        } catch (Exception e) {
            Console.Err("HPBanRoom Compose Error");
        }
    }

    @Override
    public JSONObject GetCompose(String pass, int team) {
        if (!pass.equals(GetRoomPass()[team])) {
            return null;
        }
        JSONObject jdata = new JSONObject();
        try {
            jdata.put("pick", pickData[team]);
        } catch (Exception e) {
            Console.Log("GetCompose pick data conversion error");
        }

        return jdata;
    }

    public JSONObject GetAllCompose(boolean isHaid) {
        JSONObject jdata = new JSONObject();
        try {
            if (isHaid) {
                JSONArray bulePicks = new JSONArray();
                JSONArray redPicks = new JSONArray();

                for (int j = 0; j < composeCount; j++) {
                    JSONArray bulePick = new JSONArray();
                    JSONArray redPick = new JSONArray();
                    for (int i = 0; i < 10; i++) {
                        bulePick.put(hide[i] ? pickData[0][j][i] : -1);
                        redPick.put(hide[i] ? pickData[0][j][i] : -1);
                    }
                    bulePicks.put(bulePick);
                    redPicks.put(redPick);
                }
                jdata.put("bulePick", bulePicks);
                jdata.put("redPick", redPicks);
            } else {
                jdata.put("bulePick", pickData[0]);
                jdata.put("redPick", pickData[1]);
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
                    RoomJData.put("nowCtrl", nowCtrl);
                } catch (Exception e) {
                }
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
                banData[nowCtrl] = choose;
                nowCtrl++;
                if (nowCtrl == banData.length) {
                    SetStatus(RoomStatus.END);
                    nowCtrl = 0;
                    RoomJData.put("bluePick", pickData[0]);
                    RoomJData.put("redPick", pickData[1]);
                }
                RoomJData.put("nowCtrl", nowCtrl);
                RoomJData.put("BanList", banData);
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
