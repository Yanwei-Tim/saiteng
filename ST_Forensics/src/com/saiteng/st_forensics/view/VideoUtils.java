package com.saiteng.st_forensics.view;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.saiteng.st_forensics.Config;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.Vibrator;
import android.util.Log;

public class VideoUtils {

	private static String[] paths;
	private static final String PATH = "/forensics";
	private static String ExtSDDir =  null;
	private static String IntSDDir =null;
	private static final String filePath = "/forensics/pic";
	public static Activity mActivity;

	public VideoUtils(Activity mActivity) {
		VideoUtils.mActivity = mActivity;
		StorageList mStorageList = new StorageList(mActivity);
		paths = mStorageList.getVolumnPaths();
		if(paths.length!=1){
			IntSDDir=paths[0];
			ExtSDDir = paths[1];
		}else{
			IntSDDir = paths[0];//for MX4
		    ExtSDDir = paths[0];
		}
	}

	// 录像路径
	public static void createDirectory2Store(Context context) {

		File ExtSDPath = new File(ExtSDDir + PATH);
		File IntSDPath = new File(IntSDDir + PATH);
		Log.d("geek", IntSDPath.toString()+" 内置");
		Log.d("geek", ExtSDPath.toString() + "外置");
		if (!ExtSDPath.exists()) {
			boolean success = (ExtSDPath.mkdir());
			Log.d("geek", success+"");
			if (!success) {
				if (!IntSDPath.exists()) {
					IntSDPath.mkdir();
				}
			}
		}
	}
    //照片路径
	public static void createFilePath(Context context) {

		File ExtSDPath = new File(ExtSDDir + filePath);
		File IntSDPath = new File(IntSDDir + filePath);
		if (!ExtSDPath.exists()) {
			boolean success = (ExtSDPath.mkdir());
			if (!success) {
				if (!IntSDPath.exists()) {
					IntSDPath.mkdir();
				}
			}
		}
	}

	@SuppressWarnings("unused")
	public static int pathIsExist() {
		File ExtSDPath = new File(ExtSDDir + filePath);
		File IntSDPath = new File(IntSDDir + filePath);
		if (ExtSDPath.exists()) {
			return 0;
		} else {
			return 1;
		}
	}

	public static String filePath() {
		File ExtSDPath = new File(ExtSDDir + filePath);
		File IntSDPath = new File(IntSDDir + filePath);
		if (ExtSDPath.exists()) {
			return ExtSDPath.getPath();
		} else {
			return IntSDPath.getPath();
		}
	}

	public static void deleteFiles(Handler handler) {

		File file1 = new File(ExtSDDir + PATH);
		if (file1.exists()) {
			File[] files = file1.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].exists()) {
					files[i].delete();
				}
			}
			file1.delete();
			 handler.sendEmptyMessage(1);
		}else
			handler.sendEmptyMessage(0);

		File file2 = new File(IntSDDir + PATH);
		if (file2.exists()) {
			File[] files = file2.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].exists()) {
					files[i].delete();
				}
			}
			file2.delete();
			 handler.sendEmptyMessage(1);
		}else
			handler.sendEmptyMessage(0);
	}

	public static String generateFileName() {
		SimpleDateFormat simpleDateFormate = new SimpleDateFormat(
				"yyyyMMdd_HHmmss");

		File ExtSD = new File(ExtSDDir + PATH);
		if (ExtSD.exists()) {
			if(!Config.mIsencryption){//是否加密保存
				return ExtSDDir + PATH + "/" + simpleDateFormate.format(new Date())
					+ ".mp4";
			}else
				return ExtSDDir + PATH + "/." + simpleDateFormate.format(new Date())
				+ ".rar";

		} else {
			if(!Config.mIsencryption){
			   return IntSDDir + PATH + "/" + simpleDateFormate.format(new Date())
					+ ".mp4";
			}else
				return IntSDDir + PATH + "/." + simpleDateFormate.format(new Date())
				+ ".rar";
		}

	}
	
	//删除指定文件加下的所有文件
	public static void deletefile(String path,Handler handler){
		
		 File file = new File(path); 
		 if(file.exists()){
			 file.delete();
			 handler.sendEmptyMessage(1);
		 }else{
			 handler.sendEmptyMessage(0); 
		 }
			 
	}

	public static void showDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						((Activity) context).finish();
						System.exit(0);
					}
				}).show();

	}
	

	public static String time2String(int duration) {
		int minDuration = duration / 60;
		int secDuration = duration % 60;
		return "00:" + (minDuration >= 10 ? minDuration : "0" + minDuration)
				+ ":" + (secDuration >= 10 ? secDuration : "0" + secDuration);
	}

	// 震动1次
	public static void vibrateOnce(Context context) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 400 };
		vibrator.vibrate(pattern, -1);
	}

	// 震动两次
	public static void vibrateTwice(Context context) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 400, 100, 400 };
		vibrator.vibrate(pattern, -1);
	}

	/**
	 * 震动三次
	 * 
	 * @param context
	 */
	public static void vibrateThrice(Context context) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 600, 100, 600, 100, 600 };
		vibrator.vibrate(pattern, -1);
	}

	/**
	 * 将时间戳格式化 HH:mm:ss ---24小时制的 hh:mm:ss ---12小时制的
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getDate(long timestamp) {
		String date = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new java.util.Date(timestamp));
		return date;
	}
	
	public static String getSimpleDate(long timestamp) {
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd")
				.format(new java.util.Date(timestamp));
		return date;
	}
	
	/**
	 * 获取手机内置的sdcard的总大小
	 * @return
	 */
	// 获取手机可用的内存空间 返回 单位 M
	public static double getMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		float number = (availableBlocks * blockSize) * 1.0f / (1024 * 1024 * 1024);
		BigDecimal b = new BigDecimal(number);
		double size = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return size;
	}
	
	/**
	 * 获取手机内置sdcard的可用空间
	 * @return
	 */
	 public static double getTotalInternalMemorySize() {
	        File path = Environment.getDataDirectory();
	        StatFs stat = new StatFs(path.getPath());
	        long blockSize = stat.getBlockSize();
	        long totalBlocks = stat.getBlockCount();
	        float number = (totalBlocks * blockSize) * 1.0f / (1024 * 1024 * 1024);
	        BigDecimal b = new BigDecimal(number);
			double size = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	        return size;
	    }
	
	
	// 获取sdcard可用的内存空间 返回 单位 G
	public static double getSDSize() {
		String state = Environment.getExternalStorageState();
		// SD卡不可用
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			return -1;
		}
		
		File path = new File(ExtSDDir);
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		
		float number = (availableBlocks * blockSize) * 1.0f / (1024 * 1024 * 1024);
		BigDecimal b = new BigDecimal(number);
		double size = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		return size;

	}
	
	//获取sdcard的总大小
	public static double getAllSize() {

		String state = Environment.getExternalStorageState();
		// SD卡不可用
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			return -1;
		}

		File path = new File(ExtSDDir);
		StatFs stat = new StatFs(path.getPath());

		long blockSize = stat.getBlockSize();

		long availableBlocks = stat.getBlockCount();
		
		float number = (availableBlocks * blockSize)* 1.0f / (1024 * 1024 * 1024);
		
		BigDecimal b = new BigDecimal(number);
		double size = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		return size;

	}
	
	/**
	 * 判断是否存在外置sdcard 拿到可用空间大小；
	 * @return
	 */
	public static double getAvailableSizeData(){
		File ExtSD = new File(ExtSDDir + PATH);
		if(ExtSD.exists()){
			return getSDSize();
		}else{
			return getMemorySize();
		}
	} 
	
	/**
	 * 判断是否存在外置sdcard 拿到总空间大小
	 * @return
	 */
	public static double getTotalSizeData(){
		File ExtSD = new File(ExtSDDir + PATH);
		if(ExtSD.exists()){
			return getAllSize(); 
		}else{
			return getTotalInternalMemorySize();
		}
	}
	
	

	

}