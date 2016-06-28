package com.smarteye.mpu;

public class MPUDefine {
	// 媒体方向
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

	// 视频图像格式定义
	// Packed RGB 8:8:8, 24bpp, RGBRGB...（MEDIASUBTYPE_RGB24）
	public static final int MPU_PIX_FMT_RGB24 = 0;
	// 对应于：MEDIASUBTYPE_RGB32，Packed RGB 8:8:8, 32bpp, (msb)8A 8R 8G 8B(lsb), in
	// cpu endianness
	public static final int MPU_PIX_FMT_RGB32 = 1;
	// 对应于：MEDIASUBTYPE_YV12，Planar YUV 4:2:0, 12bpp, (1 Cr & Cb sample per 2x2
	// Y samples)
	public static final int MPU_PIX_FMT_YV12 = 2;
	// 对应于：MEDIASUBTYPE_YUY2，Packed YUV 4:2:2, 16bpp, Y0 Cb Y1 Cr
	public static final int MPU_PIX_FMT_YUY2 = 3;
	// Planar YUV 4:2:0, 12bpp, (1 Cr & Cb sample per 2x2 Y samples)
	public static final int MPU_PIX_FMT_YUV420P = 4;
	// 对应于：MEDIASUBTYPE_RGB565
	public static final int MPU_PIX_FMT_RGB565 = 5;
	// 对应于：MEDIASUBTYPE_RGB555
	public static final int MPU_PIX_FMT_RGB555 = 6;
	// Planar YUV 4:2:0, 12bpp, Two arrays, one is all Y, the other is U and V
	public static final int MPU_PIX_FMT_NV12 = 7;
	// Planar YUV 4:2:0, 12bpp, Two arrays, one is all Y, the other is V and U
	public static final int MPU_PIX_FMT_NV21 = 8;
	// YUV422SP
	public static final int MPU_PIX_FMT_NV16 = 9;

	/* 录像功能标识 */
	// 录制视频
	public static final int MPU_RECORD_MEDIA_VIDEO = 0x00000001;
	// 录制音频
	public static final int MPU_RECORD_MEDIA_AUDIO = 0x00000002;

	/* 录像参数 */
	// 录像视频比特率
	public static final int MPU_I_RECORD_VIDEOBR = 1;
	// 录像视频帧率
	public static final int MPU_I_RECORD_VIDEOFR = 2;
	// 录像视频关键帧间隔
	public static final int MPU_I_RECORD_VIDEOII = 3;
	// 录像视频宽度
	public static final int MPU_I_RECORD_VIDEOW = 4;
	// 录像视频高度
	public static final int MPU_I_RECORD_VIDEOH = 5;
	// 录像媒体类型
	public static final int MPU_I_RECORD_MEDIA = 6;
	// 录像视频时间戳
	public static final int MPU_I_RECORD_VIDEOTS = 7;
	// 录像音频时间戳
	public static final int MPU_I_RECORD_AUDIOTS = 8;

	public static final int MPU_I_RECORD_VIDEOKT = 9;

	public static final int MPU_I_RECORD_AUDIOKT = 10;
	
	public static final int MPU_I_RECORD_FILESECONDS = 11;

	/* SDK版本信息 */
	// 主版本号
	public static final int MPU_I_SDK_MAINVERSION = 29;
	// 次版本号
	public static final int MPU_I_SDK_SUBVERSION = 30;
	// 构建时间
	public static final int MPU_S_SDK_BUILDTIME = 31 | 0x80;

	/* 系统信息 */
	// 手机型号
	public static final int MPU_S_SYSTEM_MODEL = 52 | 0x80;
	// Android API Level
	public static final int MPU_I_SYSTEM_APILEVEL = 53;
	public static final int MPU_S_SYSTEM_MANUFACTURE = 54 | 0x80;
	public static final int MPU_S_SYSTEM_VERSION = 55 | 0x80;

	/* 摄像头索引 */
	// 后置摄像头
	public static final int MPU_CAMERA_BACK_INDEX = 1;
	// 前置摄像头
	public static final int MPU_CAMERA_FRONT_INDEX = 2;
	// 外接摄像头
	public static final int MPU_CAMERA_EXTERNAL_INDEX = 3;

	/* 用户状态定义 */
	// 摄像头索引
	public static final int MPU_I_USERSTATE_CAMERA_INDEX = 74;
	// 用户ID
	public static final int MPU_I_USERSTATE_APPLIERID = 75;
	// 媒体方向
	public static final int MPU_I_USERSTATE_MEDIADIR = 76;
	// 用户登录状态
	public static final int MPU_I_USERSTATE_STATUS = 77;

	/* 实时传输参数 */
	// 实时传输视频宽度
	public static final int MPU_I_CODEC_VIDEOW = MPU_I_RECORD_VIDEOW;
	// 实时传输视频高度
	public static final int MPU_I_CODEC_VIDEOH = MPU_I_RECORD_VIDEOH;
	// 实时传输视频比特率
	public static final int MPU_I_CODEC_VIDEOBR = 98;
	// 实时传输视频帧率
	public static final int MPU_I_CODEC_VIDEOFR = MPU_I_RECORD_VIDEOFR;
	// 实时传输视频关键帧间隔
	public static final int MPU_I_CODEC_VIDEOII = 99;
	// 实时传输视频时间戳
	public static final int MPU_I_CODEC_VIDEOTS = 100;
	public static final int MPU_I_CODEC_VIDEOKT = 101;
	// 实时传输音频时间戳
	public static final int MPU_I_CODEC_AUDIOTS = 102;
	public static final int MPU_I_CODEC_AUDIOKT = 103;

}