package com.saiteng.st_lc32xcam.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastMsg {
	public void ToastShow(Context context,String msg) {
		Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

}
