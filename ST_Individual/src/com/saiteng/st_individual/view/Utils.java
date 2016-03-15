package com.saiteng.st_individual.view;

import com.saiteng.st_individual.MainActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Utils {
	public static void showDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						((MainActivity) context).exitSystem();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();

	}
	public static void ExitDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						((MainActivity) context).finish();
					}
				}).setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();

	}

}
