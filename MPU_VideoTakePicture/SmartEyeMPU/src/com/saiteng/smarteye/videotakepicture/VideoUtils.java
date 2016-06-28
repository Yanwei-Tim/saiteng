package com.saiteng.smarteye.videotakepicture;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.saiteng.smarteyempu.common.Config;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.os.Vibrator;
import android.util.Log;

public class VideoUtils {
	private static String[] paths;
	private static final String PATH = "/MPU";
	private static String ExtSDDir = null;
	private static final String IntSDDir = Environment
			.getExternalStorageDirectory().getPath();
	private static final String video_PATH = "/MPU/.video";
	private static final String Pic_PATH="/MPU/.pic";
	public static Activity mActivity;

	public VideoUtils(Activity mActivity) {
		VideoUtils.mActivity = mActivity;
		StorageList mStorageList = new StorageList(mActivity);
		paths = mStorageList.getVolumnPaths();
		//ExtSDDir = paths[0];
		ExtSDDir = paths[1];
	}

	public static void createDirectory2Store(Context context) {

		File ExtSDPath = new File(ExtSDDir + PATH);
		File IntSDPath = new File(IntSDDir + PATH);
		Log.d("geek", IntSDPath.toString()+" ����");
		Log.d("geek", ExtSDPath.toString() + "����");
		if (!ExtSDPath.exists()) {
			boolean success = (ExtSDPath.mkdir());
			Log.d("geek", success+"");
			if (!success) {///storage/emulated/0/MPU
				if (!IntSDPath.exists()) {
					success = IntSDPath.mkdir();
				}
			}
		}
	}

	public static void createFilePath(Context context) {
        //¼���ļ���
		File ExtSDPath = new File(ExtSDDir + video_PATH);
		File IntSDPath = new File(IntSDDir + video_PATH);
		if (!ExtSDPath.exists()) {
			boolean success = (ExtSDPath.mkdir());
			if (!success) {
				if (!IntSDPath.exists()) {
					success =IntSDPath.mkdir();
				}
			}
		}
		
		//�����ļ���
		File ExtSDPath1 = new File(ExtSDDir + Pic_PATH);
		File IntSDPath1 = new File(IntSDDir + Pic_PATH);
		if (!ExtSDPath1.exists()) {
			boolean success = (ExtSDPath1.mkdir());
			if (!success) {
				if (!IntSDPath1.exists()) {
					success =IntSDPath1.mkdir();
				}
			}
		}
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

	@SuppressLint("SimpleDateFormat")
	public static String generateFileName() {
		SimpleDateFormat simpleDateFormate = new SimpleDateFormat(
				"yyyyMMdd_HHmmss");

		File ExtSD = new File(ExtSDDir + PATH);
		if (ExtSD.exists()) {
			return ExtSDDir + PATH + "/" + simpleDateFormate.format(new Date())
					+ ".rar";

		} else {

			return IntSDDir + PATH + "/" + simpleDateFormate.format(new Date())
					+ ".rar";
		}

	}

	public static void showDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("��ʾ").setMessage(message)
				.setPositiveButton("ȷ��", new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						((Activity) context).finish();
						System.exit(0);
					}
				}).show();

	}
	public static void openNet(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("��ʾ").setMessage(message)
		.setPositiveButton("����", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				 Message message = Config.mhandler.obtainMessage();
		         message.obj= "network";
		         Config.mhandler.sendMessage(message);
			}
		}).setNegativeButton("ȡ��", new OnClickListener() {
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

	// ��1��
	public static void vibrateOnce(Context context) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 400 };
		vibrator.vibrate(pattern, -1);
	}

	// ������
	public static void vibrateTwice(Context context) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 400, 100, 400 };
		vibrator.vibrate(pattern, -1);
	}

	/**
	 * ������
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
	 * ��ʱ�����ʽ�� HH:mm:ss ---24Сʱ�Ƶ� hh:mm:ss ---12Сʱ�Ƶ�
	 * 
	 * @param timestamp
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDate(long timestamp) {
		String date = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new java.util.Date(timestamp));
		return date;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getSimpleDate(long timestamp) {
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd")
				.format(new java.util.Date(timestamp));
		return date;
	}
	
	/**
	 * ��ȡ�ֻ����õ�sdcard���ܴ�С
	 * @return
	 */
	// ��ȡ�ֻ����õ��ڴ�ռ� ���� ��λ M
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
	 * ��ȡ�ֻ�����sdcard�Ŀ��ÿռ�
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
	
	
	// ��ȡsdcard���õ��ڴ�ռ� ���� ��λ G
	public static double getSDSize() {
		String state = Environment.getExternalStorageState();
		// SD��������
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
	
	//��ȡsdcard���ܴ�С
	public static double getAllSize() {

		String state = Environment.getExternalStorageState();
		// SD��������
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
	 * �ж��Ƿ��������sdcard �õ����ÿռ��С��
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
	 * �ж��Ƿ��������sdcard �õ��ܿռ��С
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