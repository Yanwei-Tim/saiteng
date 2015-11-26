#ifndef __NRU_CONFIG_H__
#define __NRU_CONFIG_H__

#include "BVCUConst.h"
#include "BVCUCommon.h"
//##!!!!!!!注意：所有结构体的Reserved成员必须初始化为0!!!!!!!!!!!!!
//============NRU信息=======================
//存储介质
typedef struct _BVCU_NRUCFG_StorageMedia{
    char szName[BVCU_MAX_NAME_LEN+1];//名字。只读
    unsigned int  iTotalSpace;//总空间，单位MB。只读
    unsigned int  iFreeSpace;//剩余空间，单位MB。只读
    int  bInUse;//是否使用该存储器。可写
}BVCU_NRUCFG_StorageMedia;

typedef struct _BVCU_NRUCFG_NRUItem{
    char szID[BVCU_MAX_ID_LEN+1];      //ID。只读
    char szName[BVCU_MAX_NAME_LEN+1];  //名字。可写
    int  iStorageMediaCount;//存储器个数。只读
    int  iOnlineStatus; //在线状态，参见BVCU_ONLINE_STATUS_*。在上下线通知中，根据iOnlineStatus判断是上线通知还是下线通知
    int  iReserved[2];//保留，必须为0
}BVCU_NRUCFG_NRUItem;

typedef struct _BVCU_NRUCFG_NRUInfo{
    char szID[BVCU_MAX_ID_LEN+1];             //ID。只读
    char szManufacturer[BVCU_MAX_NAME_LEN+1]; //制造商名字。只读
    char szSoftwareVersion[BVCU_MAX_NAME_LEN+1]; //软件版本。只读
    char szHardwareVersion[BVCU_MAX_NAME_LEN+1]; //硬件版本。只读
    char szName[BVCU_MAX_NAME_LEN+1];//名字。可写
    int  iNoSpaceRule; //磁盘空间不足处理规则。0-停止录像，1-覆盖旧录像。可写
    int  iReserveRecordDays;//录像文件保留天数。可写
    int  iRecordFileLength;//录像文件时间长度。单位秒。。可写
    int  iReservePicDays;//抓图文件保留天数。可写
    int  iReserveGPSDays; // GPS数据保留天数。可写
    int  iStorageMediaCount;//存储器个数。只读
    BVCU_NRUCFG_StorageMedia* pStorageMedia;//存储器数组
    int  iOnlineStatus; //在线状态，参见BVCU_ONLINE_STATUS_*。在上下线通知中，根据iOnlineStatus判断是上线通知还是下线通知
    int  iReserved[2];//保留，必须为0
}BVCU_NRUCFG_NRUInfo;

//============存储计划=====================
typedef struct _BVCU_NRUCFG_Storage_Schedule_ListItem{
    char szName[BVCU_MAX_NAME_LEN+1];//存储计划的名字
    BVCU_WallTime stBegin;//开始时间。全0表示立即生效。
    BVCU_WallTime stEnd;//结束时间。全FF表示永远不结束。在开始/结束时间这段范围内，计划有效。
}BVCU_NRUCFG_Storage_Schedule_ListItem;

typedef struct _BVCU_NRUCFG_Storage_Channel{
    int  iChannelIndex; //通道号, BVCU_SUBDEV_INDEXMAJOR_*
    int  bRecordAudio;  //录像时是否录制音频 0-不录 1-录
}BVCU_NRUCFG_Storage_Channel;

typedef struct _BVCU_NRUCFG_Storage_PU{
    char szID[BVCU_MAX_ID_LEN+1];  //设备ID
    int  iChannelCount; //pChannel数组成员个数
    BVCU_NRUCFG_Storage_Channel* pChannel; //通道数组
}BVCU_NRUCFG_Storage_PU;

typedef struct _BVCU_NRUCFG_Storage_Schedule{
    char szName[BVCU_MAX_NAME_LEN+1];//名字
    BVCU_DayTimeSlice stWeek[7][BVCU_MAX_DAYTIMESLICE_COUNT];//一周的时间片划分，每天BVCU_MAX_DAYTIMESLICE_COUNT个时间片
    BVCU_WallTime stBegin;//开始时间。全0表示立即生效。
    BVCU_WallTime stEnd;//结束时间。全FF表示永远不结束。在开始/结束时间这段范围内，计划有效。
    int  iPUCount; //pStoragePU数组成员个数
    BVCU_NRUCFG_Storage_PU* pStoragePU; //该计划涉及到的PU列表
}BVCU_NRUCFG_Storage_Schedule;

//手工远程录像到NRU
typedef struct _BVCU_NRUCFG_ManualRecord{
    char szID[BVCU_MAX_ID_LEN+1];  //设备ID
    int iChannelIndex;//通道号, BVCU_SUBDEV_INDEXMAJOR_*
    int bStart;//1-开始录像，0-停止录像
    int iLength;//存储时间长度，单位秒
}BVCU_NRUCFG_ManualRecord;

//手工远程抓拍到NRU
typedef struct _BVCU_NRUCFG_Snapshot{
    char szID[BVCU_MAX_ID_LEN+1];  //设备ID
    int iChannelIndex;//通道号, BVCU_SUBDEV_INDEXMAJOR_*
    int iCount;//抓拍张数，最大允许值为15
    int iInterval;//抓拍间隔，单位秒。最大允许值为60秒	
}BVCU_NRUCFG_Snapshot;
#endif
