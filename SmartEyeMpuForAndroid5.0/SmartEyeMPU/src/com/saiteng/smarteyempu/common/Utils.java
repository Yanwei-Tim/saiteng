package com.saiteng.smarteyempu.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Vibrator;

public class Utils {
	private static final String PATH = "/MPU";
	private static final String ExtSDDir = "/storage/extSdCard";
	private static final String IntSDDir = "/storage/sdcard0";

	public static String time2String(int duration) {
		int minDuration = duration / 60;
		int secDuration = duration % 60;
		return "00:" + (minDuration >= 10 ? minDuration : "0" + minDuration)
				+ ":" + (secDuration >= 10 ? secDuration : "0" + secDuration);
	}

	public static void showDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {

					
					public void onClick(DialogInterface dialog, int which) {
						((Activity) context).finish();
					}
				}).show();

	}

	//检测是否存在外置sdCard
	public static String getStoragePath(Context context) {
		
		File ExtSDPath = new File( ExtSDDir + PATH );
		File IntSDPath = new File( IntSDDir  + PATH );
		if (!ExtSDPath.exists()) {
			boolean success = (ExtSDPath.mkdir()); 
			if (!success) {
				if (!IntSDPath.exists()) {
					success = IntSDPath.mkdir();	
					if(!success){
						return "";
					}
				}
				return IntSDPath.toString() + "/";
			}
		}
		
		return ExtSDPath.toString() + "/";
	}
		
			
	public static String generateFileName() {
		SimpleDateFormat simpleDateFormate = new SimpleDateFormat(
				"yyyyMMdd_HHmmss");
			
		File ExtSD = new File( ExtSDDir+ PATH );
		if (ExtSD.exists()) {
			return  ExtSDDir + PATH + "/."
					+ simpleDateFormate.format(new Date()) + "mpu.rar";
			
			} else {
				
				return IntSDDir + PATH + "/."
						+ simpleDateFormate.format(new Date()) + "mpu.rar";
				}
		
		
	}

	public static void vibrateOnce(Context context) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 400 };
		vibrator.vibrate(pattern, -1);
	}

	public static void vibrateTwice(Context context) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 400, 100, 400 };
		vibrator.vibrate(pattern, -1);
	}

	public static void deleteFiles() {
		
		File file1 = new File(ExtSDDir + PATH);
		if (file1.exists()) {
			File[] files = file1.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].exists()) {
					files[i].delete();
				}
			}
			file1.delete();
		}
	
	
	File file2 = new File(IntSDDir + PATH);
	if (file2.exists()) {
		File[] files = file2.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].exists()) {
				files[i].delete();
			}
		}
		file2.delete();
		}
	}

	/**
	 * 随机产生六位数的id
	 * @return
	 */
	public static int getRandomID(){
		int[] array = {0,1,2,3,4,5,6,7,8,9};
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
		    int index = rand.nextInt(i);
		    int tmp = array[index];
		    array[index] = array[i - 1];
		    array[i - 1] = tmp;
		}
		int result = 0;
		for(int i = 0; i < 6; i++)
		    result = result * 10 + array[i];
		return result;
	}
	
}