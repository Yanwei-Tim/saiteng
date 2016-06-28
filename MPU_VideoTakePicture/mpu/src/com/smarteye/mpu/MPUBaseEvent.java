package com.smarteye.mpu;

// MPU基本事件接口
public interface MPUBaseEvent {
	// MPU登录消息，dwUserId表示自己的ID号，dwErrorCode表示登录结果：0 成功，否则为出错代码
	public void onMPULoginMessage(int dwUserId, int dwErrorCode);
}