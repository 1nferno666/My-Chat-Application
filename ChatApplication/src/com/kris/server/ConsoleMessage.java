package com.kris.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConsoleMessage {
	
	public static void print(String message) {
		
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
		System.out.println(sdf.format(calendar.getTime()) + ": " + message);
	}
	
	public static String getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(calendar.getTime());
	}
}
