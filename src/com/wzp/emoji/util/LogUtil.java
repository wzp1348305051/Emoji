package com.wzp.emoji.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.util.Log;

/**
 * 自定义Log类，只有当LEVEL常量的值小于或等于对应日志级别值的时候，才会将日志打印出来.
 * 需设置日志在外设中的输出文件路径否则在Android系统中因系统路径只读权限无法输出至外设。
 * */
public class LogUtil {
	private static final String TAG = "LogUtil";
	private static String LOGPATH = "." + System.getProperty("file.separator") + "log.txt";
	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	private static final int NOTHING = 6;
	private static int LEVEL = VERBOSE;
	
	/**
	 * 关闭日志功能
	 * */
	public static void closeLog() {
		LEVEL = NOTHING;
	}
	
	/**
	 * 屏蔽部分日志输出
	 * @param level ：日志输出级别，从VERBOSE，DEBUG等中选择
	 * */
	public static void shieldPartialLog(int level) {
		LEVEL = level;
	}
	
	/**
	 * 设置日志在外设中的输出文件路径
	 * */
	public static void setLogPath(String logPath) {
		LOGPATH = logPath;
	}
	
	public static void v(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(tag, msg);
		}
	}
	
	/**
	 * 输出日志到文件(VERBOSE TO FILE)
	 * */
	public static void vtf(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(tag, msg);
			printMsgToFile("VERBOSE_" + tag, msg);
		}
	}
	
	public static void d(String tag, String msg) {
		if (LEVEL <= DEBUG) {
			Log.d(tag, msg);
		}
	}
	
	/**
	 * 输出日志到文件(DEBUG TO FILE)
	 * */
	public static void dtf(String tag, String msg) {
		if (LEVEL <= DEBUG) {
			Log.v(tag, msg);
			printMsgToFile("DEBUG_" + tag, msg);
		}
	}
	
	public static void i(String tag, String msg) {
		if (LEVEL <= INFO) {
			Log.i(tag, msg);
		}
	}
	
	/**
	 * 输出日志到文件(INFO TO FILE)
	 * */
	public static void itf(String tag, String msg) {
		if (LEVEL <= INFO) {
			Log.v(tag, msg);
			printMsgToFile("INFO_" + tag, msg);
		}
	}
	
	public static void w(String tag, String msg) {
		if (LEVEL <= WARN) {
			Log.w(tag, msg);
		}
	}
	
	/**
	 * 输出日志到文件(WARN TO FILE)
	 * */
	public static void wtf(String tag, String msg) {
		if (LEVEL <= WARN) {
			Log.v(tag, msg);
			printMsgToFile("WARN_" + tag, msg);
		}
	}
	
	public static void e(String tag, String msg) {
		if (LEVEL <= ERROR) {
			Log.e(tag, msg);
		}
	}
	
	/**
	 * 输出日志到文件(ERROR TO FILE)
	 * */
	public static void etf(String tag, String msg) {
		if (LEVEL <= ERROR) {
			Log.v(tag, msg);
			printMsgToFile("ERROR_" + tag, msg);
		}
	}
	
	/**
	 * 打印日志到外存储卡
	 */
	private static synchronized void printMsgToFile(String tag, String msg) {
		try {
			File file = new File(LOGPATH);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(LOGPATH, true);
			StringBuffer content = new StringBuffer();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			String time = format.format(calendar.getTime());
			content.append("tag:").append(tag).append(System.getProperty("line.separator"));
			content.append("time:").append(time).append(System.getProperty("line.separator"));
			content.append("content:").append(msg).append(System.getProperty("line.separator"));
            writer.write(content.toString());
            writer.close();
		} catch (IOException e) {
			LogUtil.e(TAG, e.getMessage());
		}
	}
	
}
