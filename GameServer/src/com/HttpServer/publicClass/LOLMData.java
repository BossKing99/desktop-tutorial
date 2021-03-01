package com.HttpServer.publicClass;

import java.io.BufferedReader;
import java.io.FileReader;

import org.json.JSONObject;

public class LOLMData {
	private static JSONObject _jdata;
	private static int _maxHeroCount;

	public static void LoadData() {
		try {
			FileReader fr = new FileReader("Config/LOLMData.json");
			BufferedReader br = new BufferedReader(fr);
			String Data = "";
			while (true) {
				String s = br.readLine();
				if (s == null)
					break;
				Data += s;
			}
			JSONObject jData = new JSONObject(Data);
			_jdata = jData;
			try {
				_maxHeroCount = jData.getJSONArray("ChampoinTable").length();
			} catch (Exception e) {
			}
			fr.close();
			br.close();
		} catch (Exception e) {
			Console.Err("Config LoadData error");
		}
	}

	public static JSONObject GetJData() {
		return _jdata;
	}

	public static int MaxHeroCount() {
		return _maxHeroCount;
	}
}
