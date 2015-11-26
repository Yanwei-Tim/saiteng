/*======================================================
    Besovideo Client Unit Library.
    Copyright(c) BesoVideo Ltd. 2011
    changjin.liu@besovideo.com
========================================================*/
extern "C" {
#ifndef __LIBBVCU_H__
#define __LIBBVCU_H__
/*================================================================*/
#include "SAVCodec.h"
#include "BVCUConst.h"
#include "BVCUCommon.h"

/*NOTE: 所有字符串必须是UTF-8编码*/
/*事件码*/
/*全局事件*/
enum{
    
    BVCU_EVENT_AUDIOINPUT_DISCONNECTED = 1, /*没有插入音频输入设备。事件参数：无*/
    BVCU_EVENT_AUDIOINPUT_CONNECTED, /*插入音频输入设备。事件参数：无*/
};
/*Session事件*/
enum{
    BVCU_EVENT_SESSION_OPEN = 1,  /* 创建Session。事件参数：BVCU_Event_Common*/
    BVCU_EVENT_SESSION_CLOSE,     /*关闭Session。事件参数：BVCU_Event_Common*/
    BVCU_EVENT_SESSION_CMD_COMPLETE, /*命令完成。事件参数：BVCU_Event_SessionCmd*/
};
/*Dialog事件*/
enum{
    BVCU_EVENT_DIALOG_OPEN = 1,    /* 创建Dialog。事件参数：BVCU_Event_Common*/
    BVCU_EVENT_DIALOG_UPDATE,    /* 更新Dialog。事件参数：BVCU_Event_Common*/
    BVCU_EVENT_DIALOG_CLOSE,       /* 关闭Dialog。事件参数：BVCU_Event_Common*/
    BVCU_EVENT_STORAGE_FILE_REQUIRENAME,  /*获取文件名，该事件提供了自定义文件名机制。事件参数: BVCU_Event_Storage*/
    BVCU_EVENT_STORAGE_FILE_OPEN,  /*创建文件。事件参数: BVCU_Event_Storage*/
    BVCU_EVENT_STORAGE_FILE_CLOSE, /*关闭文件。事件参数：BVCU_Event_Storage*/
    BVCU_EVENT_STORAGE_ERROR,      /*存储出错。事件参数：BVCU_Event_Storage*/
};

//Subscribe
typedef struct _BVCU_Subscribe_Status{
    int iSubscribeStatus;//订阅状态。BVCU_SUBSCRIBE_*
    int iReserved[4];
}BVCU_Subscribe_Status;

/*
CU发送的通知包和收到的命令回响内容
*/
typedef struct _BVCU_CmdMsgContent BVCU_CmdMsgContent;
struct _BVCU_CmdMsgContent{
    /*一个通知/回响可能包含多条信息。pNext指向下一条信息。最后一条信息的pNext应指向NULL 
        每个通知/回响的信息类型和顺序是固定的。大多数通知/回响只支持一种数据类型(pNext是NULL)*/
    BVCU_CmdMsgContent* pNext;
    
    /*信息数目*/
    int iDataCount;

    /*信息数组，数组元素个数等于iDataCount，pData[0]表示第一个成员，pData[1]表示第2个成员。
    类型由具体命令决定*/
    void* pData;
};

typedef struct _BVCU_NotifyMsgContent BVCU_NotifyMsgContent;
struct _BVCU_NotifyMsgContent{
    /*一个通知可能包含多条信息。pNext指向下一条信息。最后一条信息的pNext应指向NULL。*/
    BVCU_NotifyMsgContent* pNext;

    /*通知内容的类型，BVCU_SUBMETHOD_*/
    int iSubMethod;

    /*信息源（系统中的网络实体)ID。为空表示是当前登录的Server*/
    char szSourceID[BVCU_MAX_ID_LEN+1];

     /*信息源的附属设备的索引，从0开始，例如PU的云台/通道/音视频IO等。设为-1表示无意义*/
    int iSourceIndex;
    
    /*目标ID。为空表示命令目标是当前登录的Server*/
    char szTargetID[BVCU_MAX_ID_LEN+1];

     /*从0开始的目标附属设备的索引，例如PU的云台/通道/音视频IO等。设为-1表示无意义*/
    int iTargetIndex;
    
    /*信息数目*/
    int iDataCount;

    /*信息数组，数组元素个数等于iDataCount，pData[0]表示第一个成员，pData[1]表示第2个成员。 
        类型由iSubMethod决定*/
    void* pData;
};

/*所有事件参数中，涉及到指针的部分，调用者都应意识到库可能是在栈上分配内存。OnEvent函数返回后指针可能失效*/
typedef struct _BVCU_Event_Common
{
    BVCU_Result iResult;/*错误码*/
}BVCU_Event_Common;

typedef struct _BVCU_Event_SessionCmd
{
    BVCU_Result iResult;/*错误码*/
    int iPercent;//命令完成百分比，取值范围0~100。一个命令的返回可能很长，BVCU通过多次调用OnEvent来通知应用程序，
    //每次OnEvent的iPercent成员会递增，100表示彻底完成，只有最后一个OnEvent才能设置为100。
    //如果出错，iResult会被设置成错误码，iPercent设置成出错时完成的百分比。
    BVCU_CmdMsgContent stContent; /*命令回响的数据*/
}BVCU_Event_SessionCmd;

typedef struct _BVCU_Event_Storage
{
    BVCU_Result iResult;/*错误码。
        BVCU_EVENT_STORAGE_FILE_REQUIRENAME：只有BVCU_RESULT_S_OK一种
        BVCU_EVENT_STORAGE_ERROR：
        （1）如果iResult==BVCU_RESULT_E_OUTOFSPACE,库内部会立即关闭当前文件并发出BVCU_EVENT_STORAGE_FILE_CLOSE，然后持续监测硬盘空间，
        如果硬盘空间足够大，则自动再次开始录像。
        （2）如果iResult==BVCU_RESULT_E_FAILED，表示未知原因错误，库保证在一个文件关闭之前，无论遇到几次错误，都只发送一次该事件，
        发出事件后，库仍然正常录像*/

    char* szFileName;/*文件名，要求（1）必须是绝对路径（2）以'\0'结尾。该字符串指向库内部的长度为(BVCU_MAX_FILE_NAME_LEN+1)字节的字符数组
    应用程序可以在FILE_REQUIRENAME事件中修改成任何合法的文件名，
    以实现文件管理策略。其他事件中禁止修改*/
    SAV_TYPE_INT64 iTimestamp;/*REQUIRENAME/FILE_OPEN:文件开始时间戳，FILE_CLOSE:文件结束时间戳。从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
}BVCU_Event_Storage;

typedef  void* BVCU_HSession;
typedef  void* BVCU_HDialog;

typedef struct _BVCU_GlobalParam{
    /*本结构体的大小，分配者应初始化为sizeof(BVCU_GlobalParam)*/
    int iSize;

    /*库把库内部检测到的全局事件通知应用程序
     iEventCode:事件码，参见BVCU_EVENT_*
     pParam: 每个事件对应的参数，具体类型参考各个事件码的说明。如果pParam是NULL，表示无参数。
    */
    void (*OnEvent)(int iEventCode, void* pParam);

    /*保留*/
    int iReserved[4];
}BVCU_GlobalParam;


/*
CU发出的命令
*/
typedef struct _BVCU_Command BVCU_Command;
struct _BVCU_Command{
    /*本结构体的大小，分配者应初始化为sizeof(BVCU_Command)*/
    int iSize;
    
    /*用户自定义数据。通常用于回调通知。应用程序/库可以用该成员来区分不同的命令*/
    void* pUserData;

    /*命令类型，BVCU_METHOD_* */
    int iMethod;
    
    /*子命令类型，BVCU_SUBMETHOD_*，决定了BVCU_CmdMsgContent.pData类型*/
    int iSubMethod;

    /*系统中的网络实体目标ID。设置为空表示命令目标是当前登录的Server*/
    char szTargetID[BVCU_MAX_ID_LEN+1];

    /*从0开始的目标附属设备的索引，例如PU的云台/通道/音视频IO等。设为-1表示无意义*/
    int iTargetIndex;
    
    /*命令超过iTimeOut未收到回响则认为失败，单位毫秒。如果设置为0，则采用BVCU_ServerParam.iTimeout*/
    int iTimeOut;

    /*命令负载*/
    BVCU_CmdMsgContent stMsgContent;

    /*事件。
    pCommand:本命令指针。注意该指针指向的是SDK内部维护的一个BVCU_Command浅拷贝
    iEventCode:事件码，参见BVCU_EVENT_SESSION_CMD_*。目前iEventCode总是等于BVCU_EVENT_SESSION_CMD_COMPLETE
     pParam: 每个事件对应的参数，具体类型参考各个事件码的说明*/
    void (*OnEvent)(BVCU_HSession hSession, BVCU_Command* pCommand, int iEventCode, void* pParam);

    /*保留*/
    int iReserved[2];
};

/*Server信息*/
typedef struct _BVCU_ServerParam{
    /*本结构体的大小，分配者应初始化为sizeof(BVCU_ServerParam)*/
    int iSize;
    
    /*用户自定义数据。通常用于回调通知*/
    void* pUserData;

    /*Server地址，域名或者IP*/
    char szServerAddr[BVCU_MAX_HOST_NAME_LEN+1];
    
    /*Server端口号*/
    int  iServerPort;
    
    /*Client ID。必须以应用程序类型（"CU_"/"NRU_"/"PU_"等）开始（之后的部分禁止包含'_')，调用者应选择一种ID分配方式，尽量使每台计算机上的ID不同。如果为空，则由库内部生成ID*/
    char szClientID[BVCU_MAX_ID_LEN+1];

    /*应用程序名称。该名称被Server端记录到Log中*/
    char szUserAgent[BVCU_MAX_NAME_LEN+1];

    /*登录用户名*/
    char szUserName[BVCU_MAX_NAME_LEN+1];
    
    /*登录密码*/
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];
    
    /*CU与Server之间命令通道使用的传输层协议类型，参见BVCU_PROTOTYPE_*。目前仅支持TCP*/
    int iCmdProtoType;

    /*命令超过iTimeOut未收到回响则认为失败，单位毫秒。必须>0*/
    int iTimeOut;

    /*CU/NRU收到Server的Notify通知后的回调函数
    返回：库会根据返回值构造给Server的回响包
    */
    BVCU_Result (*OnNotify)(BVCU_HSession hSession, BVCU_NotifyMsgContent* pData);

    /*该Session相关的事件。函数BVCU_GetSessionInfo可以用来获得BVCU_ServerParam参数。
    iEventCode:事件码，参见Session事件
     pParam: 每个事件对应的参数，具体类型参考各个事件码的说明*/
    void (*OnEvent)(BVCU_HSession hSession, int iEventCode, void* pParam);

    /*
    CU/NRU收到的Control/Query命令 
    pCommand：库内部的一个BVCU_Command对象指针。应用程序完成命令处理后，应调用pCommand->OnEvent 
    返回：BVCU_RESULT_S_OK表示应用程序要处理本命令，其他值表示应用程序忽略该命令，由库决定如何处理。
    */
    BVCU_Result (*OnCommand)(BVCU_HSession hSession, BVCU_Command* pCommand);

    /*保留，必须初始化为0*/
    int iReserved[4];
}BVCU_ServerParam;


/*
Session信息。一次登录创建一个Session。
*/
typedef struct _BVCU_SessionInfo{
    /*创建该Session的Param*/
    BVCU_ServerParam stParam;

    /*服务器ID*/
    char szServerID[BVCU_MAX_ID_LEN+1];

    /*SmartEye域名*/
    char szDomain[BVCU_MAX_SEDOMAIN_NAME_LEN+1];

    /*服务器名*/
    char szServerName[BVCU_MAX_NAME_LEN+1];

    /*Server 代码名称*/
    char szServerCodeName[BVCU_MAX_NAME_LEN+1];

    /*Server版本号*/
    char szServerVersion[BVCU_MAX_NAME_LEN+1];

    /*CU本地IP*/
    char szLocalIP[BVCU_MAX_HOST_NAME_LEN+1];
    
    /*CU本地命令端口*/
    int iLocalPort;
    
    /*CU是否在线*/
    int iOnlineStatus;
    
    /*登录时刻，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
    SAV_TYPE_INT64 iLoginTime;
    
    /*本次登录持续时间，单位微秒*/
    SAV_TYPE_INT64 iOnlineTime;
    
    /*CMS分配的用户标识*/
    int iApplierID; 
    
    /*保留，必须初始化为0*/
    int iReserved[3];
}BVCU_SessionInfo;


/*
描述PU的通道媒体信息
*/
typedef struct _BVCU_PUOneChannelInfo{
    /*Channel Name*/
    char   szName[BVCU_MAX_NAME_LEN + 1];

    /*PU通道号*/
    int    iChannelIndex;

    /*PU每个通道的流体流方向。参见BVCU_MEDIADIR_* */
    char   iMediaDir;
    
    /*PU每个通道的云台索引。-1表示无云台*/
    char   iPTZIndex;

    char   cReserved[2];//对齐
}BVCU_PUOneChannelInfo;

//通道信息：被 BVCU_MSGCONTENT_TYPE_PU_CHANNELINFO（上下线通知）/BVCU_SUBMETHOD_GETPULIST（获取PU列表）使用
typedef struct _BVCU_PUChannelInfo{
    /*PU ID*/
    char  szPUID[BVCU_MAX_ID_LEN+1];
    
    /*PU Name*/
    char  szPUName[BVCU_MAX_NAME_LEN+1];
    
    /*pChannel数组成员个数(PU通道数)。在上线通知和BVCU_SUBMETHOD_GETPULIST中有效，在下线通知中无意义*/
    int   iChannelCount;
    
    /*PU通道信息数组。在上线通知和BVCU_SUBMETHOD_GETPULIST中有效，在下线通知中无意义*/
    BVCU_PUOneChannelInfo* pChannel;

    /*在线状态，参见BVCU_ONLINE_STATUS_*。在上下线通知中，根据iOnlineStatus判断是上线通知还是下线通知*/
    int   iOnlineStatus;
    
    //下面的GPS信息来自于PUConfig.h的BVCU_PUCFG_DeviceInfo
    /*GPS设备数目*/
    int iGPSCount;

    /*PU位置，GPS坐标*/
    int  iLongitude; //经度，东经是正值，西经负值，单位1/10000000度。大于180度或小于-180度表示无效值
    int  iLatitude; //纬度，北纬是正值，南纬是负值，单位1/10000000度。大于180度或小于-180度表示无效值
}BVCU_PUChannelInfo;

/*
描述PU广播的状态
*/
typedef struct _BVCU_BroadcastStatus
{
    /*目标ID，例如PU ID*/
    char szID[BVCU_MAX_ID_LEN+1];

    /*PU通道号*/
    int iChannelIndex;

    /*广播状态，参见BVCU_BROADCAST_STATUS_* */
    int   iBroadcastStatus;
}BVCU_BroadcastStatus;


/*会话相关。一个会话是指浏览一个设备的一个通道音视频内容或者对讲*/

typedef struct _BVCU_DialogTarget{
    /*目标ID，例如PU ID,NRU ID*/
    char szID[BVCU_MAX_ID_LEN+1];
    
    /*目标下属的子设备的主要号，BVCU_SUBDEV_INDEXMAJOR_* */
    int iIndexMajor;

    /*目标下属的子设备的次要号，例如PU通道下属的流号。设置为-1表示由Server决定传输哪个流。
      bit 0～5：BVCU_ENCODERSTREAMTYPE_* 
      bit 6~31：由BVCU_ENCODERSTREAMTYPE_决定的参数，默认为0
      	对BVCU_ENCODERSTREAMTYPE_STORAGE/BVCU_ENCODERSTREAMTYPE_PREVIEW，设置为0，表示未使用
      	对BVCU_ENCODERSTREAMTYPE_PICTURE，bit 6~9：连拍张数-1（即设置为0表示连拍1张）；bit 10~15: 抓拍间隔，单位秒，最大允许值为60秒
    */
    int iIndexMinor;
    
}BVCU_DialogTarget;

typedef struct _BVCU_DialogParam
{
    /*本结构体的大小，分配者应初始化为sizeof(BVCU_DialogParam)*/
    int iSize;

    /*用户自定义数据。通常用于回调通知*/
    void* pUserData;

    /*登录Session*/
    BVCU_HSession hSession;
    
    /*会话目标个数*/
    int iTargetCount;

    /*会话目标数组。pTarget内存由调用者分配/释放。调用BVCU_Dialog_Open/BVCU_Dialog_Update时， 
        SDK会保留pTarget的拷贝。调用BVCU_GetDialogInfo时，pTarget会指向SDK内部的拷贝，调用者
        不可以对拷贝做任何修改*/
    const BVCU_DialogTarget* pTarget;
    
    /*会话的数据流方向*/
    int iAVStreamDir;

    /*CodecThread：对音视频数据，解码完成后调用。调用者可以对解码后的数据进行各种处理。对于视频数据，必须做备份后修改备份，
                                 并把pFrame中ppData数据指针指向修改后的备份。
            对纯数据(SAVCodec_Context.eMediaType==SAV_MEDIATYPE_DATA)，从缓冲区得到数据后立即回调
    pCodec: Codec信息
    pFrame：音视频数据：解码得到的原始媒体数据；纯数据：组好帧后的数据
        返回：对纯数据无意义。对音视频数据，
                BVCU_RESULT_S_OK：pFrame被显示/播放。
        BVCU_RESULT_E_FAILED：pFrame不被显示/播放。
    */
    BVCU_Result (*afterDecode)(BVCU_HDialog hDialog, SAVCodec_Context* pCodec, SAV_Frame* pFrame);
    
    /*VideoRenderThread/AudioRenderThread：显示/播放完一个音频或视频帧后调用。用户可以在此处叠加其他效果如文字等。
    pCodec: Codec信息
    pFrame：解码得到的原始媒体数据
    返回：目前库会忽略返回值*/
    BVCU_Result (*afterRender)(BVCU_HDialog hDialog, SAVCodec_Context* pCodec,SAV_Frame* pFrame);

    /* 
    事件回调。函数BVCU_GetDialogInfo可以用来获得BVCU_DialogParam
    iEventCode:事件码，参见Dialog事件
     pParam: 每个事件对应的参数，具体类型参考各个事件码的说明。如果pParam是NULL，表示无参数。
     */
    void (*OnEvent)(BVCU_HDialog hDialog, int iEventCode, void* pParam);

    /*保留*/
    int iReserved[4];
}BVCU_DialogParam;


/*
控制会话的网络参数
*/
#define BVCU_NETWORK_DVSS_MIN 1
#define BVCU_NETWORK_DVSS_MAX 7
#define BVCU_NETWORK_DELAY_MAX 10000
typedef struct _BVCU_DialogControl_Network{
    /*会话命令超过iTimeOut未收到回响则认为失败，单位毫秒。如果设置为0，则采用BVCU_ServerParam.iTimeout*/
    int iTimeOut;

    /*A/V数据从接收到输出，最大允许的延迟，不能超过BVCU_NETWORK_DELAY_MAX。单位：毫秒（参考值：5000）*/
    int iDelayMax;
    
    /*A/V数据从接收到输出，最小允许的延迟，不能超过BVCU_NETWORK_DELAY_MAX。单位：毫秒（参考值：1000）*/
    int iDelayMin;
    
    /*播放延迟与平滑选择。取值范围BVCU_NETWORK_DVSS_MIN～BVCU_NETWORK_DVSS_MAX，越小则播放延迟越小，但平滑变差，越大则播放越平滑，但延迟变大（参考值：3）。*/
    int iDelayVsSmooth;
}BVCU_DialogControlParam_Network;

/*
控制会话的视频显示/音频回放
*/
#define BVCU_RENDER_NO_VIDEO NULL
#define BVCU_RENDER_NO_AUDIO -1

typedef struct _BVCU_DialogControl_Render{
    /*显示窗口句柄, BVCU_RENDER_NO_VIDEO表示不显示，并且不要执行视频解码。*/
    BVCU_HWND hWnd;
    
    /*显示矩形，设置成(0,0,0,0)表示不显示，但执行视频解码*/
    BVCU_Display_Rect rcDisplay;
    
    /*播放音量，正常范围0～100.如果设为BVCU_RENDER_NO_AUDIO 表示不播放音频，并且不执行音频解码*/
    int  iPlackbackVolume;
    
    /*采集音量，正常范围0～100.如果设为BVCU_RENDER_NO_AUDIO 表示不采集音频*/
    int  iCaptureVolume;
    
    /*使能或禁止音视频同步。0：使能；1：禁止*/
    int bDisableAVSync;
}BVCU_DialogControlParam_Render;

/*
控制会话的录像
*/
#define BVCU_STORAGE_MAX_FILELENINSEC 7200
typedef struct _BVCU_DialogControl_Storage{
    /*录像路径*/
    char szFilePath[BVCU_MAX_FILE_NAME_LEN+1];
    
    /*每个录像文件的时间长度，单位秒。设置为<=0表示停止存储，不能超过BVCU_STORAGE_MAX_FILELENINSEC*/
    int   iFileLenInSeconds;

    /*保留*/
    int iReserved[2];
}BVCU_DialogControlParam_Storage;

/*
控制会话
*/
typedef struct _BVCU_DialogControlParam{
    /*本结构体的大小，分配者应初始化为sizeof(BVCU_DialogControlParam)*/
    int iSize;

    BVCU_DialogControlParam_Network stNetwork;
    BVCU_DialogControlParam_Render  stRender;
    BVCU_DialogControlParam_Storage stStorage;
}BVCU_DialogControlParam;

/*
会话信息
*/
typedef struct _BVCU_DialogInfo{
    /*Dialog参数*/
    BVCU_DialogParam stParam;
    BVCU_DialogControlParam stControlParam;
    
    /*媒体信息*/
    SAVCodec_Context stVideoCodecRemote;
    SAVCodec_Context stAudioCodecRemote;
    SAVCodec_Context stAudioCodecLocal;
    
    /*会话开始时刻，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
    SAV_TYPE_INT64 iCreateTime;
    
    /*会话持续时间，单位微秒*/
    SAV_TYPE_INT64 iOnlineTime;
    
    /*会话是否在线*/
    int iOnlineStatus;

    /*统计信息*/
    
    /*收到的总包数*/
    SAV_TYPE_INT64 iVideoTotalPackets;
    SAV_TYPE_INT64 iAudioTotalPackets;
    
    /*收到的总帧数*/
    SAV_TYPE_INT64 iVideoTotalFrames;
    SAV_TYPE_INT64 iAudioTotalFrames;
    
    /*网络部分长时间统计数据*/
    int iVideoLostRateLongTerm;/*丢包(或帧)率，单位1/10000*/
    int iAudioLostRateLongTerm;/*丢包(或帧)率，单位1/10000*/
    int iVideoRecvFPSLongTerm;/*网络接收帧率，单位1/10000帧每秒*/
    int iVideoKbpsLongTerm;/*视频数据码率，单位 Kbits/second*/
    int iAudioKbpsLongTerm;/*音频数据码率，单位 Kbits/second*/
    

    /*网络部分短时间时间统计数据*/
    int iVideoLostRateShortTerm;
    int iAudioLostRateShortTerm;
    int iVideoRecvFPSShortTerm;
    int iVideoKbpsShortTerm;
    int iAudioKbpsShortTerm;

    /*VideoRender显示帧率*/
    int iVideoRenderFPS;/*帧率，单位1/10000帧每秒*/
}BVCU_DialogInfo;

enum{
    BVCU_LOG_LEVEL_UNKNOWN = -1,
    BVCU_LOG_LEVEL_DEBUG,
    BVCU_LOG_LEVEL_INFO,
    BVCU_LOG_LEVEL_NOTICE,
    BVCU_LOG_LEVEL_WARN,
    BVCU_LOG_LEVEL_ERROR,
    BVCU_LOG_LEVEL_CRIT,
    BVCU_LOG_LEVEL_ALERT,
    BVCU_LOG_LEVEL_FATAL,
    BVCU_LOG_LEVEL_MAX_COUNT
};

enum{
    BVCU_LOG_DEVICE_UNKNOWN = -1,
    BVCU_LOG_DEVICE_CONSOLE,
    BVCU_LOG_DEVICE_FILE,
    BVCU_LOG_DEVICE_MAX_COUNT,
};

/*=====================语音会议===================================*/
//会议类型
enum {
    //会议发言类型,占用4bit
    BVCU_CONF_MODE_SPEAK_DISCUSSIONGROUP = (0<<0),//讨论组模式。自由发言，无需申请。可以多个人同时发言。
    BVCU_CONF_MODE_SPEAK_CHAIRMAN = (1<<0),//演讲培训模式。发言者需申请，或者由chairman点名发言。同一时刻只能有一个人发言

    //会议加入类型,占用3bit
    BVCU_CONF_MODE_JOIN_INVITE = (0<<4),//不能主动加入。Opener拉人进来，被拉者可以接受或拒绝。
    BVCU_CONF_MODE_JOIN_PASSWORD = (1<<4),//主动加入，但需要输入密码
    BVCU_CONF_MODE_JOIN_FREE = (2<<4),//主动加入，不提示密码
};

#define BVCU_CONF_GetModeSpeak(ConfMode) (((unsigned int)(ConfMode)) & (0x0F))
#define BVCU_CONF_GetModeJoin(ConfMode) (((unsigned int)(ConfMode)) & (0x070))
#define BVCU_CONF_SetModeSpeak(ConfMode,newMode) ((((unsigned int)(ConfMode)) & (~0x0F)) | newMode)
#define BVCU_CONF_SetModeJoin(ConfMode,newMode) ((((unsigned int)(ConfMode)) & (~0x070)) | newMode)


//会议状态
enum{
    BVCU_CONF_STATUS_STOPPED = 0,
    BVCU_CONF_STATUS_STARTED,
};

//participator的会议权限。会议创建者creator拥有所有权限
enum{
    BVCU_CONF_PARTICIPATOR_POWER_ADMIN = (1<<0),//会议基本管理。可打开/关闭会议、添加/删除会议成员等。一个会议必须至少有一个参与者有权限ADMIN。
    //如果最后一个ADMIN退出会议，会议被自动关闭。
    BVCU_CONF_PARTICIPATOR_POWER_MODETATOR = (1<<1),//发言管理。可允许/禁止某个成员发言
};

//participator的会议状态
enum {
    BVCU_CONF_PARTICIPATOR_STATUS_UNKNOWN = -1,
    BVCU_CONF_PARTICIPATOR_STATUS_OFFLINE = BVCU_ONLINE_STATUS_OFFLINE,//下线
    BVCU_CONF_PARTICIPATOR_STATUS_ONLINE_LEAVED,//上线，但临时离开会议。这种情况下participator不可发言，也听不到其他人的发言
    BVCU_CONF_PARTICIPATOR_STATUS_ONLINE_INSEAT,//上线，并且正在参与会议
    BVCU_CONF_PARTICIPATOR_STATUS_ONLINE_SPEAKING,//上线，并且正在发言。只对BVCU_CONF_MODE_SPEAK_CHAIRMAN有效
};

//会议基本信息
typedef struct _BVCU_Conf_BaseInfo{
    char szName[BVCU_MAX_NAME_LEN+1];//名字。必须设置为非空。
    char szID[BVCU_MAX_ID_LEN+1];//会议ID。Create时保持为空，返回CMS设置的ID；其他命令必须设置
    int  iMode;//BVCU_CONF_MODE_*。必须设置
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//密码，仅对BVCU_CONF_NODE_JOIN_PASSWORD和BVCU_SUBMETHOD_CONF_CREATE命令有意义，其他情况设置为空
    int  iConfStatus;//会议状态,BVCU_CONF_STATUS_*
}BVCU_Conf_BaseInfo;


//会议参与者
typedef struct _BVCU_Conf_Participator_Info{
    char szID[BVCU_MAX_ID_LEN+1];//participator ID。通常是PU/CU ID
    char szUserName[BVCU_MAX_NAME_LEN+1];//登录用户名，目前仅对CU有意义。PU设置为空
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1];//地址信息，CU必须设置。PU可选
    char szAliasName[BVCU_MAX_NAME_LEN+1];//会议中使用的别名。可以为空。仅在BVCU_SUBMETHOD_CONF_PARTICIPATOR_INFO和CMS返回中填充
    int  iApplierID;//CMS分配的用户标识。添加CU参与者时(BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD),必须设置该值。其他情况下设置为无效值，应设置为0
    int  iPID;//用户加入或被加入会议时，CMS分配给该participator的participator ID，仅对该会议有效。0表示无效值。
                //CU participator应保存该值，在断线重连等异常情况下，继续会议使用RETURN命令时，CMS根据登录用户名和iPID的组合来确定该用户继续会议。
    int  iAllowedMediaDir;//BVCU_MEDIADIR_*。仅对CONF_PARTICIPATOR_ADD/MODIFY命令有意义，其他命令无意义
    int  iStatus;//当前状态。BVCU_CONF_PARTICIPATOR_STATUS_*。由CMS填充为有意义的值。CU必须填写为BVCU_CONF_PARTICIPATOR_STATUS_UNKNOWN
    int  iPower;//权限，BVCU_CONF_PARTICIPATOR_POWER_*。作为control命令输入时，只有BVCU_CONF_PARTICIPATOR_POWER_ADMIN权限者的设置值起作用。
    int  iVolume;//当前participator看到的szID participator音量。取值范围0~128，128表示原始音量。默认值应设置为128。
    //仅对BVCU_SUBMETHOD_CONF_PARTICIPATOR_VOLUME/BVCU_SUBMETHOD_CONF_INFO命令有意义
    int  iReserved[2];//保留，必须设置为0
}BVCU_Conf_Participator_Info;

#define BVCU_CONF_MAX_PARTICIPATOR_ONETIME 1024 //一次CONF_PARTICIPATOR_ADD/REMOVE命令允许添加的participator最大数目
//CMS对"添加会议参与者”命令处理结果
typedef struct _BVCU_Conf_Participator_AddResult{
    int iResultBits[BVCU_CONF_MAX_PARTICIPATOR_ONETIME/32];//按照BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD命令中数组顺序，
                                                             //成功添加的位设置为1，失败的位设置为0。数组顺序i=>bit位映射关系：第[i/32]个int的[31-(i&31)]位
}BVCU_Conf_Participator_AddResult;

//会议信息
typedef struct _BVCU_Conf_Info{
    BVCU_Conf_BaseInfo baseInfo;//会议基本信息
    int iParticipatorCount;//会议参与者个数
    BVCU_Conf_Participator_Info* pParticipators;//会议参与者列表
    BVCU_Conf_Participator_Info* pCreator;//会议创建者，必须出现在参与者列表中
    int  iReserved[2];//保留，必须设置为0
}BVCU_Conf_Info;

//申请加入会议
typedef struct _BVCU_Conf_Participator_Join{
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//密码，仅对BVCU_CONF_NODE_JOIN_PASSWORD和BVCU_SUBMETHOD_CONF_CREATE命令有意义，其他情况设置为空
    char szAliasName[BVCU_MAX_NAME_LEN+1];//会议中使用的别名。可以为空。
}BVCU_Conf_Participator_Join;
/*================================================================*/
/**
*初始化BVCU库，只能在应用程序启动时调用一次。任何其他BVCU库函数只有在 
 BVCU_Initialize成功后才可以调用
*/
LIBBVCU_API BVCU_Result BVCU_Initialize(const BVCU_GlobalParam* pParam);

/**
*停止使用BVCU库
*/
LIBBVCU_API BVCU_Result BVCU_Finish();


/**
 * 
 * 设置日志输出级别
 * @author lcj (2012/1/17)
 * 
 * @param iLogDevice    BVCU_LOG_DEVICE_*
 * @param iLevel    BVCU_LOG_LEVEL*
 * 
 * @return LIBBVCU_API BVCU_Result 
 */
LIBBVCU_API BVCU_Result BVCU_SetLogLevel(int iLogDevice, int iLevel);

/**
 * 
 * 
 * @author lcj (2012/1/17)
 * 
 * @param iLogDevice BVCU_LOG_DEVICE_*
 * @param iLevel 
 * 
 * @return LIBBVCU_API int  BVCU_LOG_LEVEL*
 */
LIBBVCU_API int BVCU_GetLogLevel(int iLogDevice);
//=======================login/logout=========================================
/*
注意：登录完成后，应用程序随时可能收到BVCU_EVENT_SESSION_CLOSE事件回调，
回调之后，Session被SDK摧毁，BVCU_HSession变成无效值
*/

/**
*登录Server。该函数是异步的。如果登录成功，在返回前或者返回后会产生OnEvent回调。
* @param[out] phSession: 返回登录Session
* @param[in] pParam: Server信息
* @return: 常见返回值
*        BVCU_RESULT_S_OK: 正确完成。结果通过OnEvent通知调用者。
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_Login(BVCU_HSession* phSession, BVCU_ServerParam* pParam);

/**
*获得登录Session相关信息
*@param[in] hSession: BVCU_Login返回的登录Session.
*@param[out] pInfo: BVCU_SessionInfo 
*@return: BVCU_Result
*/
LIBBVCU_API BVCU_Result BVCU_GetSessionInfo(BVCU_HSession hSession, BVCU_SessionInfo* pInfo);

/**
* 退出登录。该函数是异步的，在返回前或者返回后会产生OnEvent回调。
* 注意：(1)该函数必须在BVCU_Login登录成功且BVCU_Login的OnEvent回调函数被调用之后才可以调用 
*  (2)不能在任何OnEvent/OnNotify中调用BVCU_Logout
* @param[in] hSession: BVCU_Login返回的登录Session.
* @return: 常见返回值
*    BVCU_RESULT_S_OK: 正确完成。结果通过OnEvent通知调用者。
*    BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_Logout(BVCU_HSession hSession);

/*=======================command=========================================*/
/**
* CU发送命令。该函数是异步的，命令完成后触发BVCU_Command.OnEvent回调通知。
* @param[in] hSession: BVCU_Login返回的登录Session.
* @return: 常见返回值
*        BVCU_RESULT_S_OK: 调用正确完成。结果通过OnEvent通知调用者。
*        BVCU_RESULT_E_NOTEXIST: 登录Session不存在，即未登录
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_SendCmd(BVCU_HSession hSession, BVCU_Command* pCommand);

/*=======================dialog=========================================*/
/*
注意：会话过程中，随时可能收到BVCU_EVENT_DIALOG_CLOSE事件回调，
回调之后，会话被SDK摧毁，BVCU_HDialog变成无效值
*/

/**
* 创建会话。该函数是异步的。如果创建会话成功，在返回前或者返回后会产生OnEvent回调函数，
* 事件码是BVCU_EVENT_DIALOG_OPEN，如果事件参数的iResult是失败代码，则会话创建失败，调用者不必调用BVCU_Dialog_Close
* @param[out] phDialog: 返回会话句柄.
* @param[in] pParam: 会话参数。
* @param[in] pControl: 控制参数。
* @return: 常见返回值 
*        BVCU_RESULT_S_OK: 调用正确完成。结果通过OnEvent通知调用者。
*        BVCU_RESULT_E_UNSUPPORTED: 不支持的操作，例如在不支持对讲的通道上要求对讲
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Open(BVCU_HDialog* phDialog, BVCU_DialogParam* pParam, BVCU_DialogControlParam* pControl);

/**
*获得会话相关信息
*@param[in] hDialog: BVCU_Dialog_Open返回的hDialog. 
*@param[out] pInfo: BVCU_DialogInfo 
*@return: BVCU_Result
*/
LIBBVCU_API BVCU_Result BVCU_GetDialogInfo(BVCU_HDialog hDialog, BVCU_DialogInfo* pInfo);

/**
* 更改已建立的会话，需要与Server通讯。对已建立的会话，允许修改pParam->iAVStreamDir。 对只有一个Target的Dialog，
* 还允许修改Target的iMajorIndex，用于实现通道切换。 
* 该函数是异步的。 
* 该函数可能发出异步BVCU_EVENT_DIALOG_UPDATE事件，携带结果状态码。如果结果状态码失败，可能会出现两种情况： 
* (1)Dialog仍然处于Update之前的打开状态。例如只传音/视频=>音视频同传失败
* (2)Dialog关闭，接着会发送BVCU_EVENT_DIALOG_CLOSE事件。例如只传音频=>只传视频或相反；更改iMajorIndex等
* @param[in] hDialog: BVCU_Dialog_Open返回的hDialog.
* @param[in] pParam: 会话参数。
* @return: 常见返回值 
*        BVCU_RESULT_S_OK:调用正确完成。结果通过OnEvent通知调用者，事件码是BVCU_EVENT_DIALOG_UPDATE/BVCU_EVENT_DIALOG_CLOSE。
*        BVCU_RESULT_E_NOTEXIST: 会话不存在 
*        BVCU_RESULT_E_BUSY:上一次的会话操作还未完成 
*        BVCU_RESULT_E_UNSUPPORTED:不支持的操作，例如在不支持对讲的通道上要求对讲 BVCU_RESULT_E_FAILED： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Update(BVCU_HDialog hDialog, BVCU_DialogParam* pParam);

/**
*本地抓取会话中接收到的一帧视频，存为图像文件。目前仅支持JPG格式
* @param[in] hDialog: BVCU_Dialog_Open返回的hDialog.
* @param[in] szFileName: 抓取文件名。
* @param[in] iQuality:
*       JPG压缩质量，取值范围1～100，数值越大压缩后的图像质量越好，但文件会越大。建议质量不小于80
* @return: 常见返回值
*        BVCU_RESULT_S_OK: 成功
*        BVCU_RESULT_E_FAILED： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Snapshot(BVCU_HDialog hDialog, const char* szFileName, int iQuality);

/**
* 更改会话的本地设置，包括接收/存储/回放等。此函数不需要与Server通讯。
* @param[in] hDialog: BVCU_Dialog_Open返回的Dialog句柄.
* @param[in] pParam: 控制参数。
* @return: 常见返回值
*        BVCU_RESULT_S_OK: 成功
*        BVCU_RESULT_E_NOTEXIST: 会话不存在
*        BVCU_RESULT_E_UNSUPPORTED: 不支持的操作
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Control(BVCU_HDialog hDialog, BVCU_DialogControlParam* pParam);

/**
* 关闭会话。该函数是异步的，在返回前或者返回后会产生OnEvent回调函数，
* 注意：(1)该函数必须在BVCU_Dialog_Open成功且BVCU_Dialog_Open的OnEvent回调函数被调用之后才可以调用 
* (2)不能在任何OnEvent/OnNotify中调用BVCU_Dialog_Close 
* @param[in] hDialog: BVCU_Dialog_Open返回的Dialog句柄.
* @return: 常见返回值
*        BVCU_RESULT_S_OK: 正确完成。结果通过OnEvent通知调用者。 
*        BVCU_RESULT_S_IGNORE:  会话不存在
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Close(BVCU_HDialog hDialog);

/*================================================================*/

//==========================FTP Client相关接口===============================
typedef void* BVCU_FTP_HSession;
typedef void* BVCU_FTP_HTransfer;

/*
    登录服务器信息。可以使用FTP Server ID或者IP/Port登录，如果BVCU_FTP_ServerParam中设置了ID，则忽略IP/Port
*/
typedef struct _BVCU_FTP_ServerParam{        
    int iSize;/*本结构体的大小，分配者应初始化为sizeof(BVCU_FTP_ServerParam)*/
    BVCU_HSession hSession;/*登录CMS的Session句柄。如果设置为0，表示使用szServerAddr/iServerPort直接连接FTP Server，
                             如果非0，表示使用szID连接FTP Server*/
    char szID[BVCU_MAX_ID_LEN+1];//FTP Server ID
    
    char szServerAddr[BVCU_MAX_HOST_NAME_LEN+1];//FTP Server地址，域名或者IP
    int  iServerPort;    //FTP Server端口号        
    char szUserName[BVCU_MAX_NAME_LEN+1];//登录用户名，目前没有使用，必须设置为空
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//登录密码，目前没有使用，必须设置为空
    
    int iTimeOut;//超过iTimeOut未收到回响则认为失败，单位毫秒。必须>0
    int iKeepAliveInterval;//发送KeepAlive包的间隔，单位毫秒。设置<=0表示不发送
    /*事件。
     iEventCode:事件码，参见Session事件BVCU_EVENT_SESSION_*
     pParam: 每个事件对应的参数，具体类型参考各个事件码的说明*/
    void (*OnEvent)(BVCU_FTP_HSession hSession, int iEventCode, void* pParam);    
    
    int iReserved[2];//保留，必须为0
}BVCU_FTP_ServerParam;


/*
FTP_Session信息。一次登录创建一个FTP Session
*/
enum{
    BVCU_FTP_SERVER_CAP_RESUMEBREAKTRANSFER = (1<<0),//断点续传
    BVCU_FTP_SERVER_CAP_UPLOAD              = (1<<1),//允许上传
    BVCU_FTP_SERVER_CAP_OVERWRITE           = (1<<2),//上传允许覆盖旧文件
};
typedef struct _BVCU_FTP_SessionInfo{    
    BVCU_FTP_ServerParam stParam;//创建该Session的Param
    unsigned int bCapability;//支持的功能。是BVCU_FTP_SERVER_CAP_*的组合
    unsigned int iMaxSession;//该Server支持的并发Session最大数目。0表示无限制
    unsigned int iMaxTransfer;//该Server支持的并发传输最大数目。0表示无限制
    unsigned int iMaxTransferPerSession;//每个Session支持的并发传输最大数目。0表示无限制
    unsigned int iBandwidthLimit;//带宽限制。单位Kbytes，0表示无限制
    int iTimeIdle;//允许的Idle时间长度，超过该长度且没有收到Client的任何命令则server主动断开连接。单位秒。-1表示无限制
    
    /*保留，必须初始化为0*/
    int iReserved[4];
}BVCU_FTP_SessionInfo;

//传输参数
enum {
    BVCU_FTP_RULE_SAMEFILENAME_OVERWRITE = 0,
    BVCU_FTP_RULE_SAMEFILENAME_SKIP,
    BVCU_FTP_RULE_SAMEFILENAME_RESUME,//如果已有的同名文件长度较小则续传
};

enum {
    BVCU_FTP_TRANSFER_EVENT_CLOSE = 0,  /* Transfer 关闭。事件参数：BVCU_Event_Common*/
    BVCU_FTP_TRANSFER_EVENT_PROGRESS = 1, /* Transfer 进度。事件参数：整形数组 int[2]，分别为已传输KB、总KB*/
};

typedef struct _BVCU_FTP_TransferParam{
    int iSize;/*本结构体的大小，分配者应初始化为sizeof(BVCU_FTP_TransferParam)*/
    BVCU_FTP_HSession hSession;//BVCU_FTP_Login返回的句柄
    char szRemoteFileName[BVCU_MAX_FILE_NAME_LEN+1];//远端文件名
    char* szLocalFileName;//本地文件。如果OnData设置!=NULL，则忽略该值，否则库内部把数据保存到szLocalFileName命名的本地文件中
    int iRuleSameFileName;//遇到同名文件的处理规则，BVCU_FTP_RULE_SAMEFILENAME_*
    int iTimeout;//连接超时
    int bUpload;//0-下载，1-上传
    void* pUserData;//自定义数据
    
    /*回调函数。
    hTransfer:传输句柄，
    pUserData：本结构体中的pUserData
    pBuffer：库内部缓冲区。
            upload：应用程序应在pBuffer写入不超过iSizeBytes字节的数据。
            download：应用程序应把pBuffer中的数据保存，长度iSizeBytes
    iSizeBytes: 
        iSizeBytes==0,则表示下载完毕
        iSizeBytes > 0，对upload表示应用程序需提供的数据长度，对download表示应用程序需消费的数据长度
    
    返回值：upload：实际写入pBuffer的数据长度，如果返回值<iSizeBytes，则认为是最后一个上传数据包，停止传输
            download：实际读取pBuffer的数据长度，如果返回值<iSizeBytes，则认为出错，停止传输
    */
    int (*OnData)(BVCU_FTP_HTransfer hTransfer, void* pUserData, void* pBuffer, int iSizeBytes);
    /* iEventCode: 事件码，参见BVCU_FTP_TRANSFER_EVENT_*
     * pParam: 每个事件对应的参数，具体类型参考各个事件码的说明
     */
    void (*OnEvent)(BVCU_FTP_HTransfer hTransfer, int iEventCode, void* pParam, void* pUserData);
    int iReserved[2];
}BVCU_FTP_TransferParam;

//传输控制参数
typedef struct _BVCU_FTP_TransferControlParam{    
    int iSize;/*本结构体的大小，分配者应初始化为sizeof(BVCU_FTP_TransferControlParam)*/
    int iBufferSize;//库内部使用的传输缓冲区大小，单位byte。建议不小于16K
    int iBufferCount;//库内部使用的传输缓冲区个数，必须>=2
    unsigned int iBandwidthLimit;//带宽限制。单位Kbytes，0表示无限制
    int iResumeLimit;//断线重连次数限制，超过设定次数停止传输。-1表示无限制。
    int iReserved[2];
}BVCU_FTP_TransferControlParam;

/*
传输信息
*/
typedef struct _BVCU_FTP_TransferInfo{
    /*传输参数*/
    BVCU_FTP_TransferParam stParam;
    BVCU_FTP_TransferControlParam stControlParam;
        
    /*Transfer开始时刻，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
    SAV_TYPE_INT64 iCreateTime;
    
    /*Transfer持续时间，单位微秒*/
    SAV_TYPE_INT64 iOnlineTime;     
    
    /*已经收到的字节数*/
    int iReceivedKb;
    /*总字节数*/
    int iTotalKb;
    
    int iSpeedKBpsLongTerm;/*长时间传输速率，单位 KBytes/second*/
    int iSpeedKBpsShortTerm;/*短时间传输速率，单位 KBytes/second*/
    
    int iResumeCount;//已进行的断线重连次数
    int iReseved;
}BVCU_FTP_TransferInfo;

//FTP命令
enum {
    BVCU_FTP_METHOD_SEARCH_RECORDFILE = 1,//查找录像文件。输入类型:BVCU_FTP_RecordFileFilter; 输出类型:BVCU_FTP_RecordFileInfo
    BVCU_FTP_METHOD_SEARCH_FILE,//查找普通文件。输入类型:BVCU_FTP_FileFilter; 输出类型:BVCU_FTP_FileInfo
    BVCU_FTP_METHOD_DEL_RECORDFILE,//删除录像文件。输入类型:BVCU_FTP_RecordFileFilter; 输出类型:无
    BVCU_FTP_METHOD_DEL_FILE,//删除普通文件或目录。输入类型:BVCU_FTP_FileFilter; 输出类型:无
    BVCU_FTP_METHOD_CD,//切换目录。输入类型:变长字符数组; 输出类型:无
    BVCU_FTP_METHOD_PWD,//获取当前目录。输入类型:无; 输出类型:变长字符数组
    BVCU_FTP_METHOD_MKD,//创建目录。输入类型:变长字符数组; 输出类型:无
};
typedef struct _BVCU_FTP_Command BVCU_FTP_Command;
struct _BVCU_FTP_Command{    
    int iSize;/*本结构体的大小，分配者应初始化为sizeof(BVCU_Command)*/
    
    /*用户自定义数据。通常用于回调通知。应用程序/库可以用该成员来区分不同的命令*/
    void* pUserData;

    /*命令类型，BVCU_FTP_METHOD_*，决定了BVCU_CmdMsgContent.pData类型 */
    int iMethod;    
    
    /*命令超过iTimeOut未收到回响则认为失败，单位毫秒。如果设置为0，则采用BVCU_ServerParam.iTimeout*/
    int iTimeOut;

    /*命令负载*/
    BVCU_CmdMsgContent stMsgContent;

    /*事件。
    pCommand:本命令指针。注意该指针指向的是SDK内部维护的一个BVCU_FTP_Command浅拷贝
    iEventCode:事件码，参见BVCU_EVENT_SESSION_CMD_*。目前iEventCode总是等于BVCU_EVENT_SESSION_CMD_COMPLETE
    pParam: 每个事件对应的参数，具体类型参考各个事件码的说明*/
    void (*OnEvent)(BVCU_FTP_HSession hSession, BVCU_FTP_Command* pCommand, int iEventCode, void* pParam);

    /*保留*/
    int iReserved[2];
};

enum{
    BVCU_FTP_FILTER_INDEX_PUID            = (1<<0),
    BVCU_FTP_FILTER_INDEX_PUNAME          = (1<<1),
    BVCU_FTP_FILTER_INDEX_CHANNELINDEX    = (1<<2),
    BVCU_FTP_FILTER_INDEX_CHANNELNAME     = (1<<3),
    BVCU_FTP_FILTER_INDEX_RECORDTYPE      = (1<<4),
    BVCU_FTP_FILTER_INDEX_TIMEBEGIN       = (1<<5),
    BVCU_FTP_FILTER_INDEX_TIMEEND         = (1<<6),
    BVCU_FTP_FILTER_INDEX_FILESIZEMIN     = (1<<7),
    BVCU_FTP_FILTER_INDEX_FILESIZEMAX     = (1<<8),
    BVCU_FTP_FILTER_INDEX_PATTERN         = (1<<9),
    BVCU_FTP_FILTER_INDEX_PATTERNTYPE     = (1<<10),
};
//录像文件查找
typedef struct _BVCU_FTP_RecordFileFilter{
    int iValidIndex;//BVCU_FTP_FILTER_INDEX_的组合。表示对应的有效成员
    char szPUID[BVCU_MAX_ID_LEN+1];//PU ID
    char szPUName[BVCU_MAX_NAME_LEN+1];//PU Name
    int  iChannelIndex;//Channel号
    char szChannelName[BVCU_MAX_NAME_LEN+1];//Channel Name    
    SAV_TYPE_INT64 iTimeBegin; /*录像文件开始时刻，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
    SAV_TYPE_INT64 iTimeEnd;   /*录像文件结束时刻，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
    SAV_TYPE_INT64 iFileSizeMin; //文件大小的下限
    SAV_TYPE_INT64 iFileSizeMax; //文件大小的上限
    int  iRecordType;//录像原因，BVCU_STORAGE_RECORDTYPE_*
    int iReserved[1];//保留，必须为0
}BVCU_FTP_RecordFileFilter;

//录像文件信息
typedef struct _BVCU_FTP_RecordFileInfo{
    char szFilePath[BVCU_MAX_FILE_NAME_LEN+1];//文件路径
    char szFileName[BVCU_MAX_FILE_NAME_LEN+1];//文件名
    char szPUID[BVCU_MAX_ID_LEN+1];//PU ID
    char szPUName[BVCU_MAX_NAME_LEN+1];//PU Name
    int  iChannelIndex;//Channel号
    char szChannelName[BVCU_MAX_NAME_LEN+1];//Channel Name
    int  iRecordType;//录像原因，BVCU_STORAGE_RECORDTYPE_*    
    SAV_TYPE_INT64 iTimeBegin; /*录像文件开始时刻，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
    SAV_TYPE_INT64 iTimeEnd;   /*录像文件结束时刻，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
    SAV_TYPE_INT64 iFileSize; //文件大小，单位字节
    int iReserved[2];//保留，必须为0
}BVCU_FTP_RecordFileInfo;

//普通文件查找
enum{
    BVCU_FTP_PATTERNTYPE_FILE = (1<<0),//文件
    BVCU_FTP_PATTERNTYPE_DIRECTORY = (1<<1),//目录
};
/*
 * 查找文件时通过iPatternType指定类型，可以同时查找符合条件的文件和目录，如果不设置
 * 过滤条件，则返回当前目录下所有文件和目录列表
 */
typedef struct _BVCU_FTP_FileFilter{
    int iValidIndex;//BVCU_FTP_FILTER_INDEX_的组合。表示对应的有效成员
    char* szPattern;//文件名模板
    int iPatternType;//文件类型
    SAV_TYPE_INT64 iTimeBegin; /*录像文件开始时刻，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
    SAV_TYPE_INT64 iTimeEnd;   /*录像文件结束时刻，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/
    SAV_TYPE_INT64 iFileSizeMin; //文件大小的下限
    SAV_TYPE_INT64 iFileSizeMax; //文件大小的上限
    int iReserved[2];//保留，必须为0
}BVCU_FTP_FileFilter;

//普通文件信息
typedef struct _BVCU_FTP_FileInfo{
    char szFilePath[BVCU_MAX_FILE_NAME_LEN+1];//文件路径
    char szFileName[BVCU_MAX_FILE_NAME_LEN+1];//文件名    
    SAV_TYPE_INT64 iTime; /*文件修改时间，从1970-01-01 00:00:00 +0000 (UTC)开始的微秒数*/    
    SAV_TYPE_INT64 iFileSize; //文件大小，单位字节
    int iReserved[2];//保留，必须为0
}BVCU_FTP_FileInfo;

/*
注意：登录完成后，应用程序随时可能收到BVCU_EVENT_SESSION_CLOSE事件回调，回调之后，Session被SDK摧毁，BVCU_FTP_HSession变成无效值
*/

/**
*登录Server。该函数是异步的。如果登录成功，在返回前或者返回后会产生OnEvent回调。
* @param[out] phSession: 返回登录Session
* @param[in] pParam: Server信息
* @return: 常见返回值
*        BVCU_RESULT_S_OK: 正确完成。结果通过OnEvent通知调用者。
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Login(BVCU_FTP_HSession* phSession, BVCU_FTP_ServerParam* pParam);

/**
*获得登录Session相关信息
*@param[in] hSession: BVCU_FTP_Login返回的登录Session.
*@param[out] pInfo: BVCU_FTP_SessionInfo 
*@return: BVCU_Result
*/
LIBBVCU_API BVCU_Result BVCU_FTP_GetSessionInfo(BVCU_FTP_HSession hSession, BVCU_FTP_SessionInfo* pInfo);

/**
* 退出登录。该函数是异步的，在返回前或者返回后会产生OnEvent回调。
* 注意：(1)该函数必须在BVCU_FTP_Login登录成功且BVCU_FTP_Login的OnEvent回调函数被调用之后才可以调用 
*  (2)不能在任何OnEvent/OnNotify中调用BVCU_FTP_Logout
* @param[in] hSession: BVCU_FTP_Login返回的登录Session.
* @return: 常见返回值
*        BVCU_RESULT_S_OK: 正确完成。结果通过OnEvent通知调用者。
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Logout(BVCU_FTP_HSession hSession);

/**
* CU发送命令。该函数是异步的，命令完成后触发BVCU_FTP_Command.OnEvent回调通知。
* @param[in] hSession: BVCU_FTP_Login返回的登录Session.
* @return: 常见返回值
*       BVCU_RESULT_S_OK: 调用正确完成。结果通过OnEvent通知调用者。
*      BVCU_RESULT_E_NOTEXIST: 登录Session不存在，即未登录
*      BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_FTP_SendCommand(BVCU_FTP_HSession hSession, BVCU_FTP_Command* pCommand);

/**
* 创建传输。该函数是异步的。如果创建成功，在返回前或者返回后会产生OnData回调函数，
* @param[out] phTransfer: 返回句柄.
* @param[in] pParam: 传输参数。
* @param[in] pControlParam: 控制参数。
* @return: 常见返回值 
*     BVCU_RESULT_S_OK: 调用正确完成。
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
/*
注意：如果下载/上传正常完成或者发生异常，库会在最后一个OnData回调后自动销毁Transfer对象，这种情况下应用程序无需调用BVCU_FTP_Transer_Close
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Transer_Open(BVCU_FTP_HTransfer* phTransfer, BVCU_FTP_TransferParam* pParam, BVCU_FTP_TransferControlParam* pControlParam);

/**
*获得会话相关信息。必须在Transfer生存期内调用
* @param[in] hTransfer: BVCU_FTP_Transer_Open返回的hTransfer. 
* @param[out] pInfo: BVCU_FTP_TransferInfo 
* @return: BVCU_Result
*/
LIBBVCU_API BVCU_Result BVCU_FTP_GetTransferInfo(BVCU_FTP_HTransfer hTransfer, BVCU_FTP_TransferInfo* pInfo);

/**
* 更改Transfer的本地设置，此函数不需要与Server通讯。
* @param[in] hTransfer: BVCU_FTP_Transer_Open返回的hTransfer. 
* @param[in] pParam: 控制参数。
* @return: 常见返回值
*        BVCU_RESULT_S_OK: 成功
*        BVCU_RESULT_E_NOTEXIST: Transfer不存在
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Transer_Control(BVCU_FTP_HTransfer hTransfer, BVCU_FTP_TransferControlParam* pControlParam);

/**
* 关闭传输。该函数是异步的，在返回前或者返回后会产生OnData回调函数，
* 注意： (1)不能在OnData回调中调用BVCU_FTP_Transer_Close 
* @param[in] hTransfer: BVCU_FTP_Login返回的登录Session.
* @return: 常见返回值
*        BVCU_RESULT_S_OK: 正确完成。结果通过OnData通知调用者。 
*        BVCU_RESULT_S_IGNORE:  会话不存在
*        BVCU_RESULT_E_FAILED或其他： 其他错误导致失败
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Transfer_Close(BVCU_FTP_HTransfer hTransfer);

/*================================================================*/


/*================================================================*/
#endif
};