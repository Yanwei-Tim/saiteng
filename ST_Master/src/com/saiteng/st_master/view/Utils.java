package com.saiteng.st_master.view;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.LoginActivity;
import com.saiteng.st_master.MainActivity;
import com.saiteng.st_master.conn.DelDiviceTask;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class Utils {
	public static void DeleteDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						//((Activity) context).finish();
						if(Config.phonenum==null){
							Toast.makeText(context, "无法删除!", Toast.LENGTH_LONG).show();
						}else
							Toast.makeText(context, "删除"+Config.phonenum, Toast.LENGTH_LONG).show();
							new DelDiviceTask().execute();
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
