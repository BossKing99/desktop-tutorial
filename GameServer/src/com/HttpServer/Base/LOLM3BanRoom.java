package com.HttpServer.Base;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.HttpServer.Base.PlayerBase.Player;
import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

public class LOLM3BanRoom extends GameRoom {
    // 0是藍隊 1是紅隊
    private Boolean[] isReady = { false, false };
    private int[][] _banData = { { -1, -1, -1 }, { -1, -1, -1 } };
    private int[][] _pickData = { { -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1, } };
    private Timer timer = new Timer();
    private TimerTask task;
    private List<LOLMPlayer> _allPlayers = new ArrayList<LOLMPlayer>();

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
                timer.schedule(task, 30000);
                break;
            case PICK:
                break;
            case END:
                break;
        }
    }

    @Override
    protected void PlayerAdd(Player p) {
        _allPlayers.add(new LOLMPlayer(p));
    }

    private int _nowCtrl = 0;
    private int[] _banProcess = { 0, 1, 0, 1, 0, 1 };
    private int[] _pickProcess = { 0, 1, 1, 0, 0, 1, 1, 0, 0, 1 };
    private int _banFlage = 0;
    private int _pickFlage = 0;

    private void NextProcess(int choose) {
        timer.cancel();
        if (_status == RoomStatus.BAN) {
            _banData[_nowCtrl][_banFlage / 2] = choose;
            _nowCtrl = _banProcess[_banFlage++];
            if (_banFlage == 5) {
                SetStatus(RoomStatus.PICK);
                _nowCtrl = 0;
            }
            try {
                RoomJData.put("BuleBan", _banData[0]);
                RoomJData.put("RedBan", _banData[1]);
            } catch (Exception e) {
                Console.Err("LOLM3BanRoom NextProcess BAN Error");
            }
        } else if (_status == RoomStatus.PICK) {
            _pickData[_nowCtrl][_pickFlage / 2] = choose;
            _nowCtrl = _pickProcess[_pickFlage++];
            if (_pickFlage == 9)
                SetStatus(RoomStatus.END);
            try {
                RoomJData.put("BulePick", _pickData[0]);
                RoomJData.put("RedPick", _pickData[1]);
            } catch (Exception e) {
                Console.Err("LOLM3BanRoom NextProcess PICK Error");
            }
        }

        if (_status != RoomStatus.END)
            timer.schedule(task, 30000);
    }
}
