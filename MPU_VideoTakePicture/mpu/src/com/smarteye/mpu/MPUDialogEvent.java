package com.smarteye.mpu;

// MPU会话事件通知接口
public interface MPUDialogEvent {
	// 收到会话请求
	public void onMPUDialogEvent(int userId, int status, int mediaDir);
}