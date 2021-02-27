package com.HttpServer.publicClass;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class StackTraceUtil {
	public static String getStackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	  }
	public static String getExceptionText(Exception e){
		String text = "" ;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		text = sw.toString();
		return text;
	}
}
