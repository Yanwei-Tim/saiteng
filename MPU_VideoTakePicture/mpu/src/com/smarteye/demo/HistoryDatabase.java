package com.smarteye.demo;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smarteye.mpu.bean.RegisterInfo;

public class HistoryDatabase extends SQLiteOpenHelper {
	private static final String name = "history_mpu";
	private static final int version = 1;
	private SQLiteDatabase db;

	public HistoryDatabase(Context context) {
		super(context, name, null, version);
		db = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS history_mpu (name varchar(32), serveraddr varchar(32), serverport varchar(32), mpuname varchar(32), mpuid varchar(32))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void insert(RegisterInfo info) {
		ContentValues c = new ContentValues();
		c.put("name", info.getServerAliasName());
		c.put("serveraddr", info.getServerAddr());
		c.put("serverport", info.getServerPort());
		c.put("mpuname", info.getDeviceName());
		c.put("mpuid", info.getDeviceId());
		db.insert("history_mpu ", null, c);
	}

	public void deleteAll() {
		db.delete("history_mpu", null, null);
	}

	public void update(RegisterInfo info) {
		ContentValues c = new ContentValues();
		c.put("name", info.getServerAliasName());
		c.put("serveraddr", info.getServerAddr());
		c.put("serverport", info.getServerPort());
		c.put("mpuname", info.getDeviceName());
		c.put("mpuid", info.getDeviceId());
		db.update("history_mpu ", c, "name=?",
				new String[] { info.getServerAliasName() });
	}

	public ArrayList<RegisterInfo> getAllRegisterInfo() {
		ArrayList<RegisterInfo> loginInfos = new ArrayList<RegisterInfo>();
		Cursor cursor = db.rawQuery("select * from history_mpu", null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			String serverAddr = cursor.getString(1);
			String serverPort = cursor.getString(2);
			String mpuName = cursor.getString(3);
			String mpuId = cursor.getString(4);

			RegisterInfo loginInfo = new RegisterInfo();
			loginInfo.setServerAliasName(name);
			loginInfo.setServerAddr(serverAddr);
			loginInfo.setServerPort(Integer.valueOf(serverPort));
			loginInfo.setDeviceName(mpuName);
			loginInfo.setDeviceId(Integer.valueOf(mpuId));
			loginInfos.add(loginInfo);
		}
		cursor.close();
		return loginInfos;
	}

	public void close() {
		db.close();
	}
}
