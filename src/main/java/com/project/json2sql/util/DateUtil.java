package com.project.json2sql.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


public class DateUtil {

	public static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	/**
	 * Format Date object to format yyyy-MM-dd and returns the DateString
	 * 
	 * @param date
	 * @return Date String in format - yyyy-MM-dd
	 */
	public static String getFormattedDateString(Date date) {
		String dateString = "";
		if (date != null) {
			final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(date);
		}
		return dateString;

    }
	
    /**
     * Return the DateObject from String by parsing into a format of MM/dd/yyyy
     * @param dateString
     * @return
     */
    public static Date getDatefromString(String dateString) {
    	final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    	Date date = null;
    	 try {
             if (!StringUtils.isEmpty(dateString)) {
                 date = format.parse(dateString);
             }
         } catch (ParseException e) {
			      logger.error(" Parser exception while parsing the date {}, reason could be :: {} ", dateString, e.getMessage());
         }
        
         return date;
    }
    
    public static Date getLocalDatefromString(String dateString) {
    	final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = null;
    	 try {
             if (!StringUtils.isEmpty(dateString)) {
                 date = format.parse(dateString);
             }
         } catch (ParseException e) {
			      logger.error(" Parser exception while parsing the date {}, reason could be :: {} ", dateString, e.getMessage());
         }
        
         return date;
    }
    
    public static String getFormattedDateStringmmddyy(Date date) {
		String dateString = "";
		if (date != null) {
			final SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
			return format.format(date);
		}
		return dateString;

    }
    
    public static String getCurrentDateTime() {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
    	LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
    }

}
