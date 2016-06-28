/*======================================================
    Record File Playblack Library.
    Copyright(c) BesoVideo Ltd. 2013
    changjin.liu@besovideo.com
========================================================*/
extern "C" {
#ifndef __RECORDPLAY_H__
#define __RECORDPLAY_H__

/*================================================================*/
#include "SAVCodec.h"
#include "BVCUConst.h"
#include "BVEvent.h"
#include "PUConfig.h"

//==========================录像文件回放相关接口===============================

/*NOTE: 所有字符串必须是UTF-8编码*/
typedef struct _BVCU_RecordPlay_FileParam{
    char* szMediaFileName;
    char* szGPSFileName;
    char* szEventFileName;  
}BVCU_RecordPlay_FileParam;


typedef struct _BVCU_RecordPlay_FileInfo{
    //开始时间，单位 1/SAV_TIME_BASE。从 Epoch, 1970-01-01 00:00:00 +0000 (UTC)开始经过的时间
    SAV_TYPE_INT64 iCreationTime;   
    int iDuration;//持续时间，单位毫秒      

    //视频宽度和高度
    int iVideoWidth;
    int iVideoHeight;

    //录像原因,BVCU_STORAGE_RECORDTYPE_*
    int iReason;

    //音频通道数,0-没有音频，1-单声道道，2-双声道,...
    int iAudioChannel;  

    //是否有GPS,0-没有，1-有
    int bGPS;   

    //是否有事件,0-没有，1-有
    int bEvent;
}BVCU_RecordPlay_FileInfo;

//播放状态
enum
{
    BVCU_RECORDPLAY_STATE_IDLE = 0,//未打开任何文件
    BVCU_RECORDPLAY_STATE_OPENED,//打开了文件
    BVCU_RECORDPLAY_STATE_PLAYING,//正在播放
    BVCU_RECORDPLAY_STATE_PAUSE,//暂停
	BVCU_RECORDPLAY_STATE_STEP,//单帧步进
	BVCU_RECORDPLAY_STATE_CLOSEING,//正在关闭
    BVCU_RECORDPLAY_STATE_CLOSEED,//关闭了文件，可能是手动关闭或播放完毕后自动关闭
};

typedef struct _BVCU_RecordPlay_SizeCtrl{     
    BVCU_HWND hWnd;//显示窗口句柄
    BVCU_Display_Rect rcSource;//视频源矩形，大小不能超过BVCU_RecordPlay_FileInfo中的视频宽高
    BVCU_Display_Rect rcDisplay;//显示矩形
}BVCU_RecordPlay_SizeCtrl;

typedef struct _BVCU_RecordPlay_ColorCtrl{
    int iBrightness;//亮度，取值范围0-100
    int iContrast;//对比度，取值范围0-100
    int iSaturation;//饱和度，取值范围0-100
}BVCU_RecordPlay_ColorCtrl;

#define SPEEDBASE 64
//播放信息
typedef struct _BVCU_RecordPlay_PlayInfo{   
    int iTimeOffset;//当前播放时刻，从0开始，单位毫秒
    BVCU_RecordPlay_SizeCtrl stSizeCtrl;//视频显示    
    BVCU_RecordPlay_ColorCtrl stColorCtrl;//色彩控制

    //音频回放设备的状态。0-音频设备未打开，1-打开音频回放设备成功，2-尝试过打开音频设备但失败
    int iAudioDevice;

    //正在播放的音频声道，以bit位表示每个声道的播放状态，0-不播放，1-播放。bit 0-左声道（单声道），bit 1-右声道
    int iPlaybackChannel;

    //播放音量，取值范围0-100
    int  iPlaybackVolume;

    //播放速度，单位1/64   
    int iSpeed;

    //播放状态，BVCU_RECORDPLAY_STATE_*
    int iPlayState;

	//同步组ID。>=1表示同步组ID，<=0表示不加入同步组。默认值为0。加入同一组的文件播放时同步
	int  iSyncGroupID;

	//边放边存的文件名。用于远程播放
	char* szSaveFileName;

	//下载速度，单位KB/s。用于远程播放
	int iKBps;

	//播放缓冲区的满度百分比。取值范围0-100。用于远程播放时衡量是否卡顿
	int  iFullness;
}BVCU_RecordPlay_PlayInfo;


typedef void* BVCU_RecordPlay_Handler;

typedef struct _BVCU_RecordPlay_GPSData{
    SAV_TYPE_INT64 iTimeOffset;//相对时间，以BVCU_RecordPlay_FileInfo.iCreationTime为开始
    BVCU_PUCFG_GPSData stData;//GPS数据
}BVCU_RecordPlay_GPSData;

//录像关联的事件
typedef struct _BVCU_RecordPlay_EventData{  
    SAV_TYPE_INT64 iTimeOffset;//相对时间，以FileInfo.iCreationTime为开始
    BVCU_WallTime stTime;//事件发生时间
    char szID[BVCU_MAX_ID_LEN+1];//事件源ID
//    BVCU_AlarmContent stAlarm; //事件内容
}BVCU_RecordPlay_EventData;

typedef struct _BVCU_RecordPlay_CallbackParam{
    int bStateChanged; //状态是否发生改变
    BVCU_Result iErrorCode; //状态码
    BVCU_RecordPlay_GPSData* pGPSData; //GPS数据
    BVCU_RecordPlay_EventData* pEventData; //Event数据
    SAV_Packet* pVideoData; //视频数据
    SAV_Packet* pAudioData; //音频数据
}BVCU_RecordPlay_CallbackParam;

//通知应用层的回调，注意不能在该回调中执行阻塞或耗时的操作。
typedef void (*BVCU_RecordPlay_Callback)(BVCU_RecordPlay_Handler hHandler, BVCU_RecordPlay_CallbackParam* pParam);

//播放控制
enum {
    BVCU_RECORDPLAY_CTRL_RESIZE = 1,//缩放，如全屏、跟随窗口大小、保持宽高比、视频局部放大等，应用层根据窗口大小变化消息调用此功能，参数：BVCU_RecordPlay_SizeCtrl指针
    BVCU_RECORDPLAY_CTRL_VOLUME,//音量，参数: int iVolume
    BVCU_RECORDPLAY_CTRL_AUDIOCHANNEL,//声道，参数:int iPlaybackChannel
    BVCU_RECORDPLAY_CTRL_SPEED,//速度,参数: int iSpeed
    BVCU_RECORDPLAY_CTRL_COLOR,//色彩,参数：BVCU_RECORDPLAY_ColorCtrl指针
    BVCU_RECORDPLAY_CTRL_JUMP,//跳转，参数：int iTimeOffset
    BVCU_RECORDPLAY_CTRL_PAUSE,//暂停，参数: 无
    BVCU_RECORDPLAY_CTRL_PLAY,//播放，参数：无
    BVCU_RECORDPLAY_CTRL_STEP,//单帧播放，每播放完一帧后处于PAUSE状态，参数：无
    BVCU_RECORDPLAY_CTRL_CALLBACK,//GPS/Event回调，参数BVCU_RecordPlay_Callback函数指针
    BVCU_RECORDPLAY_CTRL_SYNCGROUPID,//同步组ID,参数：int  iSyncGroupID
    BVCU_RECORDPLAY_CTRL_SAVEFILE,//边放边存文件，用于播放远端录像文件，参数: const char* 文件名指针，设为NULL表示不使用边放边存。
};

//打开文件
LIBBVCU_API BVCU_Result BVCU_RecordPlay_Open(BVCU_RecordPlay_Handler* pHandler, BVCU_RecordPlay_FileParam* pParam, BVCU_RecordPlay_FileInfo* pInfo, BVCU_HSession hSession = NULL);

//执行播放控制
LIBBVCU_API BVCU_Result BVCU_RecordPlay_Control(BVCU_RecordPlay_Handler hHandler, int iCtrlCode, void* pParam);

//查询播放信息
LIBBVCU_API BVCU_Result BVCU_RecordPlay_Query(BVCU_RecordPlay_Handler hHandler, BVCU_RecordPlay_PlayInfo* pInfo);

//关闭文件
LIBBVCU_API BVCU_Result BVCU_RecordPlay_Close(BVCU_RecordPlay_Handler hHandler);
#endif
};