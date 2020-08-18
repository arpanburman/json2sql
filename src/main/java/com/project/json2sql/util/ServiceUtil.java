package com.project.json2sql.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServiceUtil {

	public static String fileSuffix() {
		String fileSuffix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		return fileSuffix;
	}
	
	public static Map<String, Integer> currentHour() {
		Map<String, Integer> time = new HashMap<>();
		
		Calendar c = Calendar.getInstance();

		int Hr24=c.get(Calendar.HOUR_OF_DAY);
		int Min=c.get(Calendar.MINUTE);
		
		time.put("hour", Hr24);
		time.put("min", Min);
		System.out.println(Hr24 +"::"+ Min);
		return time;
	}
}
