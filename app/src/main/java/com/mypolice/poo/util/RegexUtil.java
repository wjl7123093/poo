package com.mypolice.poo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;

/**   
 * @Title: RegexUtil.java 
 * @Package com.mypolice.poo.util
 * @Description: 正则表达式验证工具类
 * @author wangjl  
 * @date 2017-8-28
 * @update 2017-9-1
 * @version v1.0.0(1)
 */
public class RegexUtil {
	
	/** 身份证号码 Pattern */
	public static final Pattern ID_CARD_PATTERN = Pattern
			.compile("^([1-9]\\d{13}[0-9a-zA-Z])|([1-9]\\d{16}[0-9a-zA-Z])$");

	/**
	 * 固定电话编码Pattern
	 */
	public static final Pattern PHONE_NUMBER_PATTERN = Pattern
			.compile("^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$");

	/**
	 * 邮政编码Pattern
	 */
	public static final Pattern POST_CODE_PATTERN = Pattern.compile("\\d{6}");

	/**
	 * 手机号码Pattern
	 */
	// public static final Pattern MOBILE_NUMBER_PATTERN = Pattern
	// .compile("[1][34578]\\d{9}");
	public static final Pattern MOBILE_NUMBER_PATTERN = Pattern
			.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");

	/**
	 * 网址Pattern
	 */
	public static final Pattern WEB_SITE_PATTERN = Pattern
			.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
	
	/**
	 * 身份证号码是否正确
	 * @param s
	 * @return
	 */
	public static boolean isIDCard(String s) {
		Matcher m = ID_CARD_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 固话编码是否正确
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isPhoneNumber(String s) {
		Matcher m = PHONE_NUMBER_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 邮政编码是否正确
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isPostCode(String s) {
		Matcher m = POST_CODE_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 手机号码是否正确
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isMobileNumber(String s) {
		Matcher m = MOBILE_NUMBER_PATTERN.matcher(s);
		return m.matches();
	}

	public static boolean isMobileNumber(EditText w) {
		if (!RegexUtil.isMobileNumber(w.getText().toString().trim())) {
//			w.setError("手机号码格式不正确！");
//			w.setFocusable(true);
			return false;
		}
		return true;
	}

	/**
	 * 网址是否正确
	 * @param s
	 * @return
     */
	public static boolean isWebSite(String s) {
		Matcher m = WEB_SITE_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * Email是否正确
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		// String strPattern =
		// "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}
	
	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}


}
