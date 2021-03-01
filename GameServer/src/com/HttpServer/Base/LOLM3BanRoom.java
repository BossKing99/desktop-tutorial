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
    private Map<Integer, Integer> _chosen = new HashMap<Integer, Integer>();
    private Boolean[] isReady = { false, false };
    private int[][] _banData = { { -1, -1, -1 }, { -1, -1, -1 } };
    private int[][] _pickData = { { -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, } };
    private Timer timer = new Timer();
    private TimerTask task;
    private List<LOLMPlayer> _allPlayers = new ArrayList<LOLMPlayer>();
    private long ChooseTime = 30000;

    public LOLM3BanRoom(JSONObject jdata, String[] key) {
        super(jdata, key);
        try {
            RoomJData.put("Status", _status);
            RoomJData.put("BuleBan", _banData[0]);
            RoomJData.put("RedBan", _banData[1]);
            RoomJData.put("BulePick", _pickData[0]);
            RoomJData.put("RedPick", _pickData[1]);
        } catch (Exception e) {
            Console.Err("LOLM3BanRoom Create Error");
        }
        task = new TimerTask() {
            public void run() {
                NextProcess(0);
            }
        };
    }

    @Override
    public void Ready(JSONObject jdata) {
        try {
            if (_status == RoomStatus.WAIT && jdata.optString("pass") == _pass) {
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
            if (jdata.optString("pass") == _pass) {
                NextProcess(jdata.optInt("choose"));
            }
        } catch (Exception e) {
            Console.Err("LOLM3BanRoom Choose Error");
        }

    }

    @Override
    public void Preview(JSONObject data, String ctxId) {
        try {
            int num = data.getInt("num");
            if ((num == 0 || !_chosen.containsKey(num)) && data.optString("pass") == _pass) {
                LOLMPlayer player = FindPlayer(ctxId);
                if (player != null && player.team == _nowCtrl) {
                    JSONObject jdata = new JSONObject();
                    if (_status == RoomStatus.BAN)
                        jdata.put("banNum", _banFlage);
                    else if (_status == RoomStatus.PICK)
                        jdata.put("pickNum", _pickFlage);
                    else
                        return;
                    jdata.put("team", _nowCtrl);
                    jdata.put("preview", num);
                    Broadcast(jdata.toString(), ProtocolName.PREVIEW);
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
                timer.schedule(task, ChooseTime);
                try {
                    RoomJData.put("NextTime", System.currentTimeMillis() + ChooseTime);
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
        _allPlayers.add(new LOLMPlayer(p));
        Broadcast(RoomJData.toString(), ProtocolName.SYNC);
    }

    private int _nowCtrl = 0;
    private int[] _banProcess = { 0, 1, 0, 1, 0, 1 };
    private int[] _pickProcess = { 0, 1, 1, 0, 0, 1, 1, 0, 0, 1 };
    private int _banFlage = 0;
    private int _pickFlage = 0;

    private void NextProcess(int choose) {
        if (choose < LOLMData.MaxHeroCount() && (choose == 0 || !_chosen.containsKey(choose))) {
            if (choose != 0)
                _chosen.put(choose, 0);
            timer.cancel();
            if (_status == RoomStatus.BAN) {
                _banData[_nowCtrl][_banFlage / 2] = choose;
                _banFlage++;
                if (_banFlage == 6) {
                    SetStatus(RoomStatus.PICK);
                    _nowCtrl = 0;
                } else
                    _nowCtrl = _banProcess[_banFlage];

                try {
                    RoomJData.put("BuleBan", _banData[0]);
                    RoomJData.put("RedBan", _banData[1]);
                } catch (Exception e) {
                    Console.Err("LOLM3BanRoom NextProcess BAN Error");
                }
                Broadcast(RoomJData.toString(), ProtocolName.SYNC);
            } else if (_status == RoomStatus.PICK) {
                _pickData[_nowCtrl][_pickFlage / 2] = choose;
                _pickFlage++;
                if (_pickFlage == 10)
                    SetStatus(RoomStatus.END);
                else
                    _nowCtrl = _pickProcess[_pickFlage++];

                try {
                    RoomJData.put("BulePick", _pickData[0]);
                    RoomJData.put("RedPick", _pickData[1]);
                } catch (Exception e) {
                    Console.Err("LOLM3BanRoom NextProcess PICK Error");
                }
                Broadcast(RoomJData.toString(), ProtocolName.SYNC);
            }

            if (_status != RoomStatus.END) {
                timer.schedule(task, ChooseTime);
                try {
                    RoomJData.put("NextTime", System.currentTimeMillis() + ChooseTime);
                } catch (Exception e) {
                }
            }
        }
    }

    private void Broadcast(String data, int pt) {
        for (LOLMPlayer lolmPlayer : _allPlayers) {
            try {
                lolmPlayer.Write(NetJson.CreatePack(data, pt));
            } catch (Exception e) {
            }
        }
    }

    private LOLMPlayer FindPlayer(String ctxId) {
        for (LOLMPlayer lolmPlayer : _allPlayers) {
            if (lolmPlayer.GetCtxId() == ctxId)
                return lolmPlayer;
        }
        return null;
    }
}
