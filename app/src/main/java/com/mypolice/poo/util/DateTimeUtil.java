package com.mypolice.poo.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**   
 * @Title: DateTimeUtil.java 
 * @Package com.mypolice.poo.util
 * @Description: 时间工具类
 * @author wangjl  
 * @crdate 2017-8-29
 * @update 2017-9-1
 * @version v1.0.0(1)
 */
public class DateTimeUtil {

	/**
	 * 日期统一格式
	 */
	private final static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取下一秒的时间
	 * 
	 * @param currentDate
	 * @return
	 */
	public static String getDateAddOneSecond(String currentDate) {

		String nextSecondDate = "";

		if (currentDate != null && !currentDate.equals("")) {

			try {
				Date date = format.parse(currentDate); // 将当前时间格式化
				// System.out.println("front:" + format.format(date)); //
				// 显示输入的日期
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.SECOND, 1); // 当前时间加1秒
				date = cal.getTime();
				// System.out.println("after:" + format.format(date));
				nextSecondDate = format.format(date); // 加一秒后的时间
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nextSecondDate;
	}

	/**
	 * 获取 n 分钟后的时间
	 * 
	 * @param minutes
	 * @return
	 */
	public static String getDateAddMinutes(int minutes) {

		String nextDate = "";
		
		// 显示输入的日期
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes); // 当前时间加 n 分钟
		Date date = cal.getTime();
		nextDate = format.format(date); // 加 n 分钟后的时间

		return nextDate;
	}

	/**
	 * 获取 n 小时后的时间
	 * 
	 * @param hours
	 * @return
	 */
	public static String getDateAddHours(int hours) {

		String nextDate = "";
		
		// 显示输入的日期
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, hours); // 当前时间加 n 小时
		Date date = cal.getTime();
		nextDate = format.format(date); // 加 n 小时后的时间

		return nextDate;
	}

	/**
	 * 获取 n 天后的时间
	 * 
	 * @param days
	 * @return
	 */
	public static String getDateAddDays(int days) {

		String nextDate = "";

		// 显示输入的日期
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, days); // 当前时间加 n 天
		Date date = cal.getTime();
		nextDate = format.format(date); // 加 n 天后的时间

		return nextDate;
	}

	/**
	 * 获取 n 个月后的时间
	 * 
	 * @param months
	 * @return
	 */
	public static String getDateAddMonths(int months) {

		String nextDate = "";

		// 显示输入的日期
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, months); // 当前时间加 n 个月
		Date date = cal.getTime();
		nextDate = format.format(date); // 加 n 个月后的时间

		return nextDate;
	}

	/**
	 * 获取 n 年后的时间
	 * 
	 * @param years
	 * @return
	 */
	public static String getDateAddYears(int years) {

		String nextDate = "";

		// 显示输入的日期
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, years); // 当前时间加 n 年
		Date date = cal.getTime();
		nextDate = format.format(date); // 加 n 年后的时间

		return nextDate;
	}

	/**
	 * 获取剩余时间 几天几时几分几秒
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String getRemainTime(String startTime, String endTime) {

		String remainTime = "0"; // 剩余时间

		long dayMsec = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long hourMsec = 1000 * 60 * 60;// 一小时的毫秒数
		long minuteMsec = 1000 * 60;// 一分钟的毫秒数
		long secondMsec = 1000;// 一秒钟的毫秒数
		long diffMsec; // 毫秒差

		if (startTime != null && !startTime.equals("") && endTime != null
				&& !endTime.equals("")) {
			try {
				// 获得两个时间的毫秒时间差异
				diffMsec = format.parse(endTime).getTime()
						- format.parse(startTime).getTime();
				if(diffMsec > 0){
					/*判断结束时间是否大于开始时间*/
					long diffDay = diffMsec / dayMsec;// 计算差多少天
					long diffHour = diffMsec % dayMsec / hourMsec;// 计算差多少小时
					long diffMin = diffMsec % dayMsec % hourMsec / minuteMsec;// 计算差多少分钟
					long diffSec = diffMsec % dayMsec % dayMsec % minuteMsec
							/ secondMsec;// 计算差多少秒//输出结果
					remainTime = diffDay + "," + diffHour + "," + diffMin + ","
							+ diffSec + ",";	// 返回天时分秒以逗号分隔，方便adapter里进行解析
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return remainTime;
	}

	/**
	 * 格式化日期格式
	 * 
	 * @param dateTimeString
	 * @return
	 */
	public static String formatDateType(String dateTimeString) {

		String formatAfterDateTimeString = "";
		// System.out.println(dateTimeString);

		if (dateTimeString != null && !dateTimeString.equals("")) {
			/* 判断字符串是否有值 */
			formatAfterDateTimeString = dateTimeString;

			if (formatAfterDateTimeString.contains("/")) {
				/* 判断日期中是否包含'/' */
				formatAfterDateTimeString = formatAfterDateTimeString.replace(
						"/", "-");
			}

			if ((formatAfterDateTimeString.lastIndexOf("-") - formatAfterDateTimeString
					.indexOf("-")) == 2) {
				/* 判断月份格式是否是MM格式 */
				String frontSubString = formatAfterDateTimeString.substring(0,
						formatAfterDateTimeString.indexOf("-") + 1);
				String afterSubString = "0" + formatAfterDateTimeString.substring(
						formatAfterDateTimeString.indexOf("-") + 1,
						formatAfterDateTimeString.length());
				
				formatAfterDateTimeString = frontSubString + afterSubString; //拼接字符串
			}
		}
		return formatAfterDateTimeString;
	}
	
	public static String getDateCN() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日? HH:mm:ss");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;// 2012年10月03日 23:41:31
	}

	public static String getDateEN() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		return date1;// 2012-10-03 23:41:31
	}

	public static Date getDateENToDate() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		Date date = null;
		try {
			date = format1.parse(date1);// 2012/10/03
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date getDateENToDate2() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		Date date = null;
		try {
			date = format1.parse(date1);// 2012/10/03 23:41:31
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}
	
	/**
	 * 将Linux时间戳转换为制式时间 
	 * @param timestamp Linux时间戳
	 * @return
	 */
	public static String getDateFromTimestamp(long timestamp) {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date1 = format1.format(new Date(timestamp * 1000L));
		return date1;// 2012-10-03 23:41:31
	}
	
	/**
	 * 获取当前时间是白天还是黑夜
	 * @return true 白天，false 黑夜
	 */
	public static boolean getCurrentTimeIsDayOrNight(){  
        SimpleDateFormat sdf = new SimpleDateFormat("HH");  
        String hour= sdf.format(new Date());  
        int k  = Integer.parseInt(hour)  ;  
        if ((k>=0 && k<6) ||(k >=18 && k<24)){  
        	return false;  
        } else {  
            return true;  
        }  
    }  
}
