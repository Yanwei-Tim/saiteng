package com.bvpu.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

public class MPUWifiManage {
	private Context context;
	private final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pManager manager;
	private Channel channel;
	private BroadcastReceiver receiver = null;
	private final static String TAG = "MPUWifiManage";
	private boolean isWifiP2pEnabled = false;
	private PeerListListener peerListListener;
	private DeviceActionChangeListener deviceActionChangeListener;

	public MPUWifiManage(Context context) {
		this.context = context;
		initWifiManage();
	}

	private void initWifiManage() {
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		manager = (WifiP2pManager) context
				.getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(context, context.getMainLooper(), null);

		receiver = new WiFiDirectBroadcastReceiver(this, context);
		context.registerReceiver(receiver, intentFilter);
		disconnect();
	}

	public void onDestroy() {
		disconnect();
		context.unregisterReceiver(receiver);
	}

	// 连接
	public void connect(WifiP2pConfig config) {
		manager.connect(channel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				// WiFiDirectBroadcastReceiver will notify us. Ignore for now.
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(context, "Connect failed. Retry.",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	// 取消连接
	public void disconnect() {
		manager.removeGroup(channel, new ActionListener() {

			@Override
			public void onFailure(int reasonCode) {
				Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

			}

			@Override
			public void onSuccess() {
			}

		});
	}

	public void setdiscoverPeers() {
		manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() {
				Log.i(TAG, "setdiscoverPeers--------->onSuccess");
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.i(TAG, "setdiscoverPeers--------->onFailure");
			}
		});
	}

	public void cancelDisconnect() {
		/*
		 * A cancel abort request by user. Disconnect i.e. removeGroup if
		 * already connected. Else, request WifiP2pManager to abort the ongoing
		 * request
		 */
		if (manager != null) {
			manager.cancelConnect(channel, new ActionListener() {
				@Override
				public void onSuccess() {
				}

				@Override
				public void onFailure(int reasonCode) {
				}
			});
		}
	}

	public WifiP2pManager getManager() {
		return manager;
	}

	public Channel getChannel() {
		return channel;
	}

	public boolean isWifiP2pEnabled() {
		return isWifiP2pEnabled;
	}

	public void setWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	public PeerListListener getPeerListListener() {
		return peerListListener;
	}

	public void setPeerListListener(PeerListListener peerListListener) {
		this.peerListListener = peerListListener;
	}

	public void setDeviceActionChangeListener(
			DeviceActionChangeListener deviceActionChangeListener) {
		this.deviceActionChangeListener = deviceActionChangeListener;
	}

	public DeviceActionChangeListener getDeviceActionChangeListener() {
		return deviceActionChangeListener;
	}
}
