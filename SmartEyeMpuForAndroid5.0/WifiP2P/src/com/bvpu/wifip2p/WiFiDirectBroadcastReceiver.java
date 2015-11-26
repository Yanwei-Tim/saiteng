package com.bvpu.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
	private Channel channel;
	private WifiP2pManager wifiP2pManager;
	private MPUWifiManage manage;
	private final static String TAG = "WiFiDirectBroadcastReceiver";

	public WiFiDirectBroadcastReceiver(MPUWifiManage manage, Context context) {
		this.manage = manage;
		this.wifiP2pManager = manage.getManager();
		this.channel = manage.getChannel();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				manage.setWifiP2pEnabled(true);
			} else {
				manage.setWifiP2pEnabled(false);
			}
		}

		if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			if (wifiP2pManager != null && manage != null) {
				wifiP2pManager.requestPeers(channel,
						manage.getPeerListListener());
			}
		}

		// 连接状态改变
		if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

		}

		// 本机设备状态改变
		if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
			if (manage.getDeviceActionChangeListener() != null) {
				Log.i(TAG, "deviceActionChangeListener-------->WifiP2pDevice");
				manage.getDeviceActionChangeListener()
						.deviceActionChangeListener(
								(WifiP2pDevice) intent
										.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
			} else {
				Log.i(TAG, "deviceActionChangeListener-------->null");
			}
		}
	}
}
