package com.HttpServer.publicClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UtilFun {;
	public static UtilFun ins = new UtilFun();
	private UtilFun()
	{	
		Init();
	}
	// 計算主機時區和UTC+0差幾毫秒
	private long Offset = TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)
			- TimeZone.getTimeZone("").getOffset(Calendar.ZONE_OFFSET);


	private ThreadLocal<SimpleDateFormat> SDF_Local = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			s.setTimeZone(Locol_UTC);
			return s;
		}
	};

	public TimeZone Locol_UTC; // 設定本地時區

	public void Init() {
		String gmtString = "GMT";
		if (Offset > 0)
			gmtString += "+" + Offset / 1000 / 60 / 60;
		else
			gmtString += "-" + Offset / 1000 / 60 / 60;
		Locol_UTC = TimeZone.getTimeZone(gmtString);
		SDF_Local.get().setTimeZone(Locol_UTC);
	}

	public String cleanBadChar(String str) {
		int msg_len = str.length();
		if (str.contains("\r\n")) { // 如果字串出現\r\n要處理掉
			return str.substring(0, msg_len - 2);
		} else
			return str;
	}

	public String padLeftZeros(String inputString, int length) {
		if (inputString.length() >= length) {
			return inputString;
		}
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length - inputString.length()) {
			sb.append('0');
		}
		sb.append(inputString);

		return sb.toString();
	}

	public String getTimeString(long time) {
		return SDF_Local.get().format(new Date(time));
	}

	public String getDate(long time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(Locol_UTC);
		Date date = new Date(time);
		return sdf.format(date);
	}

	// 帶入這個時間的時區是多少 GAM = 0 : BT_UTC | GAM = 1 : Locol_UTC
	public long getTimestamp(String dateString) {
		// SDF.setTimeZone(GAM);
		long time = 0;
		try {
			// 進行轉換
			Date date = SDF_Local.get().parse(dateString);
			time = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	public String getDateString(long time) {
		Date date = new Date(time);
		return new SimpleDateFormat("yyyyMMdd").format(date);

	}

	public String getHRTimeString() {
		Date date = new Date(System.currentTimeMillis());
		return new SimpleDateFormat("yyyyMMddHH").format(date);

	}
}
