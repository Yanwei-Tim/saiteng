package com.saiteng.smartlisten.common;

import java.io.File;

import android.os.Environment;

public class MPUPath {
	public static String MPU_PATH_ROOT = Environment
			.getExternalStorageDirectory() + "/MPUListen";
	public static String MPU_PATH_FACE_ROOT = MPU_PATH_ROOT + "/Face";
	public static String MPU_PATH_FACE_ALL = MPU_PATH_FACE_ROOT + "/all_face";
	public static String MPU_PATH_FACE_COM = MPU_PATH_FACE_ROOT
			+ "/comparison_face";
	public static String MPU_PATH_FACE_SPECI = MPU_PATH_FACE_ROOT
			+ "/specimen_face";
	public static String MPU_PATH_IMAGE = MPU_PATH_ROOT + "/Image";
	public static String MPU_PATH_RECORD = MPU_PATH_ROOT + "/Storage";
	public static String MPU_USER_PATH_RECORD = "";// 用户自定义录像路径
	public static String MPU_PATH_VIDEO = MPU_PATH_RECORD + "/Video";
	public static String MPU_PATH_PHOTO = MPU_PATH_RECORD + "/Photo";
	public static String MPU_PATH_AUDIO = MPU_PATH_RECORD + "/Audio";
	public static String MPU_FACE_STORAGE_PATH = Environment
			.getExternalStorageDirectory() + "/MobileFARS/localdb/pic";
	public static String MPU_FACE_STORAGE_FEAT = Environment
			.getExternalStorageDirectory() + "/MobileFARS/localdb";

	public static void creatFile(String path) {
		File destDir = new File(path);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
	}

	public static void createFileMPU() {
	
		creatFile(MPU_PATH_AUDIO);
	}

	public static boolean isFileExist(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}

}
