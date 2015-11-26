#pragma once

/* 录像功能标识 */
// 录制视频
#define MPU_RECORD_MEDIA_VIDEO  0x00000001
// 录制音频
#define MPU_RECORD_MEDIA_AUDIO  0x00000002

/* 录像参数 */
// 录像视频比特率
#define MPU_I_RECORD_VIDEOBR  1
// 录像视频帧率
#define MPU_I_RECORD_VIDEOFR  2
// 录像视频关键帧间隔
#define MPU_I_RECORD_VIDEOII  3
// 录像视频宽度
#define MPU_I_RECORD_VIDEOW  4
// 录像视频高度
#define MPU_I_RECORD_VIDEOH  5
// 录像媒体类型
#define MPU_I_RECORD_MEDIA  6
// 录像视频时间戳
#define MPU_I_RECORD_VIDEOTS  7
// 录像音频时间戳
#define MPU_I_RECORD_AUDIOTS  8

#define MPU_I_RECORD_VIDEOKT  9

#define MPU_I_RECORD_AUDIOKT  10

#define MPU_I_RECORD_FILESECONDS 11

/* SDK版本信息 */
// 主版本号
#define MPU_I_SDK_MAINVERSION  29
// 次版本号
#define MPU_I_SDK_SUBVERSION  30
// 构建时间
#define MPU_S_SDK_BUILDTIME  (31 | 0x80)

/* 系统信息 */
// 手机型号
#define MPU_S_SYSTEM_MODEL  (52 | 0x80)
// Android API Level
#define MPU_I_SYSTEM_APILEVEL  53
#define MPU_S_SYSTEM_MANUFACTURE  (54 | 0x80)
#define MPU_S_SYSTEM_VERSION  (55 | 0x80)

/* 摄像头索引 */
// 后置摄像头
#define MPU_CAMERA_BACK_INDEX  1
// 前置摄像头
#define MPU_CAMERA_FRONT_INDEX  2
// 外接摄像头
#define MPU_CAMERA_EXTERNAL_INDEX  3

/* 用户状态定义 */
// 摄像头索引
#define MPU_I_USERSTATE_CAMERA_INDEX  74
// 用户ID
#define MPU_I_USERSTATE_APPLIERID  75
// 媒体方向
#define MPU_I_USERSTATE_MEDIADIR  76
// 用户登录状态
#define MPU_I_USERSTATE_STATUS  77

/* 实时传输参数 */
// 实时传输视频宽度
#define MPU_I_CODEC_VIDEOW  MPU_I_RECORD_VIDEOW
// 实时传输视频高度
#define MPU_I_CODEC_VIDEOH  MPU_I_RECORD_VIDEOH
// 实时传输视频比特率
#define MPU_I_CODEC_VIDEOBR  98
// 实时传输视频帧率
#define MPU_I_CODEC_VIDEOFR  MPU_I_RECORD_VIDEOFR
// 实时传输视频关键帧间隔
#define MPU_I_CODEC_VIDEOII  99
// 实时传输视频时间戳
#define MPU_I_CODEC_VIDEOTS  100
// 实时传输视频编码时间
#define MPU_I_CODEC_VIDEOKT  101
// 实时传输音频时间戳
#define MPU_I_CODEC_AUDIOTS  102
// 实时传输音频编码时间
#define MPU_I_CODEC_AUDIOKT  103

struct COptionDesc {
	int iOption;
	char* sOption;
};

#define MPU_OPTION(a) {MPU_##a,#a}
#define MPU_OPTION_END {-1,NULL}

extern COptionDesc gOptionDesc[];
