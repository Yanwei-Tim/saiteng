package com.smarteye.mpu;

// MPU�����¼��ӿ�
public interface MPUBaseEvent {
	// MPU��¼��Ϣ��dwUserId��ʾ�Լ���ID�ţ�dwErrorCode��ʾ��¼�����0 �ɹ�������Ϊ�������
	public void onMPULoginMessage(int dwUserId, int dwErrorCode);
}