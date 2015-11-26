package com.bvpu.wifip2p;

import java.util.List;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

	private List<WifiP2pDevice> items;
	private Context context;
	private MPUWifiManage manage;
	public final static int HOST = 11;
	public final static int SLAVE = 12;
	private int deviceType;

	public WiFiPeerListAdapter(Context context, int textViewResourceId,
			List<WifiP2pDevice> objects, MPUWifiManage manage) {
		super(context, textViewResourceId, objects);
		items = objects;
		this.context = context;
		this.manage = manage;
	}

	private WifiP2pDevice device;

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_devices, null);
		}
		device = items.get(position);
		if (device != null) {
			TextView top = (TextView) v.findViewById(R.id.device_name);
			TextView bottom = (TextView) v.findViewById(R.id.device_details);
			Button button = (Button) v.findViewById(R.id.btn_connect);
			if (top != null) {
				top.setText(device.deviceName);
			}
			if (bottom != null && button != null) {
				bottom.setText(getDeviceStatus(device.status, button));
			}
			if (deviceType == HOST) {
				button.setEnabled(true);
			} else {
				button.setEnabled(false);
			}
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					connectEvent(device);
				}
			});
		}
		return v;
	}

	private void connectEvent(WifiP2pDevice device) {
		int deviceStatus = device.status;
		switch (deviceStatus) {
		case WifiP2pDevice.AVAILABLE:
			connectDevice(device);
			return;
		case WifiP2pDevice.INVITED:
			manage.cancelDisconnect();
			return;
		case WifiP2pDevice.CONNECTED:
			manage.disconnect();
			return;
		case WifiP2pDevice.FAILED:
			connectDevice(device);
			return;
		case WifiP2pDevice.UNAVAILABLE:
			return;
		default:
			return;
		}
	}

	private void connectDevice(WifiP2pDevice device) {
		WifiP2pConfig config = new WifiP2pConfig();
		config.groupOwnerIntent = 15;
		config.deviceAddress = device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		manage.connect(config);
	}

	private String getDeviceStatus(int deviceStatus, Button button) {
		switch (deviceStatus) {
		case WifiP2pDevice.AVAILABLE:
			button.setText(context.getString(R.string.Connect));
			button.setEnabled(true);
			return "Available";
		case WifiP2pDevice.INVITED:
			button.setText(context.getString(R.string.Invited));
			button.setEnabled(true);
			return "Invited";
		case WifiP2pDevice.CONNECTED:
			button.setText(context.getString(R.string.Disconnect));
			button.setEnabled(true);
			return "Connected";
		case WifiP2pDevice.FAILED:
			button.setText(context.getString(R.string.Connect));
			button.setEnabled(true);
			return "Failed";
		case WifiP2pDevice.UNAVAILABLE:
			button.setText(context.getString(R.string.Connect));
			button.setEnabled(false);
			return "Unavailable";
		default:
			button.setText(context.getString(R.string.Connect));
			button.setEnabled(false);
			return "Unknown";
		}
	}
}
