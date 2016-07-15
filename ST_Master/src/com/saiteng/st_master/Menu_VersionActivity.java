package com.saiteng.st_master;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class Menu_VersionActivity extends Activity{
	private TextView mView_Version;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_window);
		MyApplication.getInstance().addActivity(this);
		mView_Version = (TextView)findViewById(R.id.about_version);
		mView_Version.setText(getVersion());
	}
	 /**
	   * 获取版本号
	   * @return 当前应用的版本号
	   */
	  public String getVersion() {
	      try {
	          PackageManager manager = this.getPackageManager();
	          PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	          String version = info.versionName;
	        return this.getString(R.string.version_name) + version;
	     } catch (Exception e) {
	         e.printStackTrace();
	         return this.getString(R.string.can_not_find_version_name);
	     }
	 }

}
