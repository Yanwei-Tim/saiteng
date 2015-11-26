package com.smarteye.mpu;

public class MPUDefine {
	// ý�巽��
	public static final int BVCU_MEDIADIR_VIDEOSEND = (1 << 0);
	public static final int BVCU_MEDIADIR_VIDEORECV = (1 << 1);
	public static final int BVCU_MEDIADIR_AUDIOSEND = (1 << 2);
	public static final int BVCU_MEDIADIR_AUDIORECV = (1 << 3);
	public static final int BVCU_MEDIADIR_TALKONLY = (BVCU_MEDIADIR_AUDIOSEND | BVCU_MEDIADIR_AUDIORECV);
	public static final int BVCU_MEDIADIR_DATASEND = (1 << 4);
	public static final int BVCU_MEDIADIR_DATARECV = (1 << 5);

	public static final int BVCU_EVENT_DIALOG_OPEN = 1;
	public static final int BVCU_EVENT_DIALOG_UPDATE = 2;
	public static final int BVCU_EVENT_DIALOG_CLOSE = 3;

	// ��Ƶͼ���ʽ����
	// Packed RGB 8:8:8, 24bpp, RGBRGB...��MEDIASUBTYPE_RGB24��
	public static final int MPU_PIX_FMT_RGB24 = 0;
	// ��Ӧ�ڣ�MEDIASUBTYPE_RGB32��Packed RGB 8:8:8, 32bpp, (msb)8A 8R 8G 8B(lsb), in
	// cpu endianness
	public static final int MPU_PIX_FMT_RGB32 = 1;
	// ��Ӧ�ڣ�MEDIASUBTYPE_YV12��Planar YUV 4:2:0, 12bpp, (1 Cr & Cb sample per 2x2
	// Y samples)
	public static final int MPU_PIX_FMT_YV12 = 2;
	// ��Ӧ�ڣ�MEDIASUBTYPE_YUY2��Packed YUV 4:2:2, 16bpp, Y0 Cb Y1 Cr
	public static final int MPU_PIX_FMT_YUY2 = 3;
	// Planar YUV 4:2:0, 12bpp, (1 Cr & Cb sample per 2x2 Y samples)
	public static final int MPU_PIX_FMT_YUV420P = 4;
	// ��Ӧ�ڣ�MEDIASUBTYPE_RGB565
	public static final int MPU_PIX_FMT_RGB565 = 5;
	// ��Ӧ�ڣ�MEDIASUBTYPE_RGB555
	public static final int MPU_PIX_FMT_RGB555 = 6;
	// Planar YUV 4:2:0, 12bpp, Two arrays, one is all Y, the other is U and V
	public static final int MPU_PIX_FMT_NV12 = 7;
	// Planar YUV 4:2:0, 12bpp, Two arrays, one is all Y, the other is V and U
	public static final int MPU_PIX_FMT_NV21 = 8;
	// YUV422SP
	public static final int MPU_PIX_FMT_NV16 = 9;

	/* ¼���ܱ�ʶ */
	// ¼����Ƶ
	public static final int MPU_RECORD_MEDIA_VIDEO = 0x00000001;
	// ¼����Ƶ
	public static final int MPU_RECORD_MEDIA_AUDIO = 0x00000002;

	/* ¼����� */
	// ¼����Ƶ������
	public static final int MPU_I_RECORD_VIDEOBR = 1;
	// ¼����Ƶ֡��
	public static final int MPU_I_RECORD_VIDEOFR = 2;
	// ¼����Ƶ�ؼ�֡���
	public static final int MPU_I_RECORD_VIDEOII = 3;
	// ¼����Ƶ���
	public static final int MPU_I_RECORD_VIDEOW = 4;
	// ¼����Ƶ�߶�
	public static final int MPU_I_RECORD_VIDEOH = 5;
	// ¼��ý������
	public static final int MPU_I_RECORD_MEDIA = 6;
	// ¼����Ƶʱ���
	public static final int MPU_I_RECORD_VIDEOTS = 7;
	// ¼����Ƶʱ���
	public static final int MPU_I_RECORD_AUDIOTS = 8;

	public static final int MPU_I_RECORD_VIDEOKT = 9;

	public static final int MPU_I_RECORD_AUDIOKT = 10;
	
	public static final int MPU_I_RECORD_FILESECONDS = 11;

	/* SDK�汾��Ϣ */
	// ���汾��
	public static final int MPU_I_SDK_MAINVERSION = 29;
	// �ΰ汾��
	public static final int MPU_I_SDK_SUBVERSION = 30;
	// ����ʱ��
	public static final int MPU_S_SDK_BUILDTIME = 31 | 0x80;

	/* ϵͳ��Ϣ */
	// �ֻ��ͺ�
	public static final int MPU_S_SYSTEM_MODEL = 52 | 0x80;
	// Android API Level
	public static final int MPU_I_SYSTEM_APILEVEL = 53;
	public static final int MPU_S_SYSTEM_MANUFACTURE = 54 | 0x80;
	public static final int MPU_S_SYSTEM_VERSION = 55 | 0x80;

	/* ����ͷ���� */
	// ��������ͷ
	public static final int MPU_CAMERA_BACK_INDEX = 1;
	// ǰ������ͷ
	public static final int MPU_CAMERA_FRONT_INDEX = 2;
	// �������ͷ
	public static final int MPU_CAMERA_EXTERNAL_INDEX = 3;

	/* �û�״̬���� */
	// ����ͷ����
	public static final int MPU_I_USERSTATE_CAMERA_INDEX = 74;
	// �û�ID
	public static final int MPU_I_USERSTATE_APPLIERID = 75;
	// ý�巽��
	public static final int MPU_I_USERSTATE_MEDIADIR = 76;
	// �û���¼״̬
	public static final int MPU_I_USERSTATE_STATUS = 77;

	/* ʵʱ������� */
	// ʵʱ������Ƶ���
	public static final int MPU_I_CODEC_VIDEOW = MPU_I_RECORD_VIDEOW;
	// ʵʱ������Ƶ�߶�
	public static final int MPU_I_CODEC_VIDEOH = MPU_I_RECORD_VIDEOH;
	// ʵʱ������Ƶ������
	public static final int MPU_I_CODEC_VIDEOBR = 98;
	// ʵʱ������Ƶ֡��
	public static final int MPU_I_CODEC_VIDEOFR = MPU_I_RECORD_VIDEOFR;
	// ʵʱ������Ƶ�ؼ�֡���
	public static final int MPU_I_CODEC_VIDEOII = 99;
	// ʵʱ������Ƶʱ���
	public static final int MPU_I_CODEC_VIDEOTS = 100;
	public static final int MPU_I_CODEC_VIDEOKT = 101;
	// ʵʱ������Ƶʱ���
	public static final int MPU_I_CODEC_AUDIOTS = 102;
	public static final int MPU_I_CODEC_AUDIOKT = 103;

}