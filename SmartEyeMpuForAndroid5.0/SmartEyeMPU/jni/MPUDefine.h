#pragma once

/* ¼���ܱ�ʶ */
// ¼����Ƶ
#define MPU_RECORD_MEDIA_VIDEO  0x00000001
// ¼����Ƶ
#define MPU_RECORD_MEDIA_AUDIO  0x00000002

/* ¼����� */
// ¼����Ƶ������
#define MPU_I_RECORD_VIDEOBR  1
// ¼����Ƶ֡��
#define MPU_I_RECORD_VIDEOFR  2
// ¼����Ƶ�ؼ�֡���
#define MPU_I_RECORD_VIDEOII  3
// ¼����Ƶ���
#define MPU_I_RECORD_VIDEOW  4
// ¼����Ƶ�߶�
#define MPU_I_RECORD_VIDEOH  5
// ¼��ý������
#define MPU_I_RECORD_MEDIA  6
// ¼����Ƶʱ���
#define MPU_I_RECORD_VIDEOTS  7
// ¼����Ƶʱ���
#define MPU_I_RECORD_AUDIOTS  8

#define MPU_I_RECORD_VIDEOKT  9

#define MPU_I_RECORD_AUDIOKT  10

#define MPU_I_RECORD_FILESECONDS 11

/* SDK�汾��Ϣ */
// ���汾��
#define MPU_I_SDK_MAINVERSION  29
// �ΰ汾��
#define MPU_I_SDK_SUBVERSION  30
// ����ʱ��
#define MPU_S_SDK_BUILDTIME  (31 | 0x80)

/* ϵͳ��Ϣ */
// �ֻ��ͺ�
#define MPU_S_SYSTEM_MODEL  (52 | 0x80)
// Android API Level
#define MPU_I_SYSTEM_APILEVEL  53
#define MPU_S_SYSTEM_MANUFACTURE  (54 | 0x80)
#define MPU_S_SYSTEM_VERSION  (55 | 0x80)

/* ����ͷ���� */
// ��������ͷ
#define MPU_CAMERA_BACK_INDEX  1
// ǰ������ͷ
#define MPU_CAMERA_FRONT_INDEX  2
// �������ͷ
#define MPU_CAMERA_EXTERNAL_INDEX  3

/* �û�״̬���� */
// ����ͷ����
#define MPU_I_USERSTATE_CAMERA_INDEX  74
// �û�ID
#define MPU_I_USERSTATE_APPLIERID  75
// ý�巽��
#define MPU_I_USERSTATE_MEDIADIR  76
// �û���¼״̬
#define MPU_I_USERSTATE_STATUS  77

/* ʵʱ������� */
// ʵʱ������Ƶ���
#define MPU_I_CODEC_VIDEOW  MPU_I_RECORD_VIDEOW
// ʵʱ������Ƶ�߶�
#define MPU_I_CODEC_VIDEOH  MPU_I_RECORD_VIDEOH
// ʵʱ������Ƶ������
#define MPU_I_CODEC_VIDEOBR  98
// ʵʱ������Ƶ֡��
#define MPU_I_CODEC_VIDEOFR  MPU_I_RECORD_VIDEOFR
// ʵʱ������Ƶ�ؼ�֡���
#define MPU_I_CODEC_VIDEOII  99
// ʵʱ������Ƶʱ���
#define MPU_I_CODEC_VIDEOTS  100
// ʵʱ������Ƶ����ʱ��
#define MPU_I_CODEC_VIDEOKT  101
// ʵʱ������Ƶʱ���
#define MPU_I_CODEC_AUDIOTS  102
// ʵʱ������Ƶ����ʱ��
#define MPU_I_CODEC_AUDIOKT  103

struct COptionDesc {
	int iOption;
	char* sOption;
};

#define MPU_OPTION(a) {MPU_##a,#a}
#define MPU_OPTION_END {-1,NULL}

extern COptionDesc gOptionDesc[];
