package com.project.json2sql.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceUtil {

	public static String fileSuffix() {
		String fileSuffix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		return fileSuffix;
	}
}
