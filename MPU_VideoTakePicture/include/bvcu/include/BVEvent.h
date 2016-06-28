#ifndef __BVEVENT_H__
#define __BVEVENT_H__

#include "BVCUConst.h"
#include "BVCUCommon.h"

//事件类型
enum {
    BVCU_EVENT_TYPE_NONE = 0,        //无效值
    
    //通用
	BVCU_EVENT_TYPE_DISKERROR = 0x0001,       //磁盘错误。源：PU/NRU的Storage
	
    //PU相关
    BVCU_EVENT_TYPE_VIDEOLOST = 0x1000,    //视频丢失。源：PU 的 VideoIn
    BVCU_EVENT_TYPE_VIDEOMD,               //运动检测。源：PU 的 VideoIn
    BVCU_EVENT_TYPE_VIDEOOCCLUSION,        //视频遮挡。源：PU 的 VideoIn
    BVCU_EVENT_TYPE_ALERTIN,               //报警输入。源：PU的AlertIn
    BVCU_EVENT_TYPE_PERIOD,                //周期报警。源：PU的周期报警    
    BVCU_EVENT_TYPE_PUONLINE,              //PU上线。源：PU
    BVCU_EVENT_TYPE_PUOFFLINE,             //PU下线。源：PU
    
    //NRU相关
    BVCU_EVENT_TYPE_NRUONLINE = 0x2000,   //NRU上线
    BVCU_EVENT_TYPE_NRUOFFLINE,           //NRU下线
    
    //VTDU相关
    BVCU_EVENT_TYPE_VTDUONLINE = 0x3000,
    BVCU_EVENT_TYPE_VTDUOFFLINE,
    
    //CMS相关
    BVCU_EVENT_TYPE_CMSONLINE = 0x4000,
    BVCU_EVENT_TYPE_CMSOFFLINE,
    
    //用户(User)相关
    BVCU_EVENT_TYPE_USERLOGIN = 0x5000,   //用户登录
    BVCU_EVENT_TYPE_USERLOGOUT,           //用户注销
        
	//该值及往后的值为自定义类型
    BVCU_EVENT_TYPE_CUSTOM = 0x10000000,
};

//事件动作
enum {
    BVCU_EVENT_ACTION_NONE = 0,
    BVCU_EVENT_ACTION_PURECORD,     //执行者:PU。 PU录像。对应结构体：BVCU_Event_Action_PURecord
    BVCU_EVENT_ACTION_ALERTOUT,     //执行者:PU。 报警输出。对应结构体：BVCU_Event_Action_AlertOut
    BVCU_EVENT_ACTION_SNAPSHOT,     //执行者:PU。 抓拍。对应结构体：BVCU_Event_Action_Snapshot
    BVCU_EVENT_ACTION_PTZ,          //执行者:PU。 移动云台。对应结构体：BVCU_Event_Action_PTZ
    BVCU_EVENT_ACTION_AUDIOOUT,     //执行者:PU。 播放报警声音。对应结构体：BVCU_Event_Action_AudioOut
    BVCU_EVENT_ACTION_SMS,          //执行者:PU。 发送手机短信。只有BVCU_PUCFG_DeviceInfo.bSupportSMS=1时才可用。对应结构体：BVCU_Event_Action_SMS
    BVCU_EVENT_ACTION_EMAIL,        //执行者:CMS。发送Email。对应结构体：BVCU_Event_Action_Email
    BVCU_EVENT_ACTION_SHOWMSG,      //执行者:PU/CU。 向PU/CU发送文字消息。对应结构体：BVCU_Event_Action_ShowMsg
	BVCU_EVENT_ACTION_EventRECORD,  //执行者:NRU。平台录像。对应结构体：BVCU_Event_Action_EventRecord

    BVCU_EVENT_ACTION_CUSTOM = 0x10000000,//该值及往后的值为自定义动作。对应结构体：BVCU_Event_Action_Custom
};

//事件触发者 发送事件使用的结构体
typedef struct _BVCU_Event_Source{
	int  iEventType;//事件类型，BVCU_EVENT_TYPE_*
	BVCU_WallTime stTime;//事件发生时刻
	char szID[BVCU_MAX_ID_NAME_LEN+1];//源ID，例如PU ID/用户名等
	int  iSubDevIdx;//子设备索引，如PU的视频输入等。BVCU_ALARM_TYPE_PERIOD：定时器触发间隔，单位秒
	int  iReserved[2];//保留，必须为0
}BVCU_Event_Source;

//客户端查询返回的存储的事件。CU可以修改其中的处警部分
typedef struct _BVCU_Event_SourceSaved{
	BVCU_Event_Source stEvent;//Event
	int  iEventIndex;//返回的数据库中的唯一标识，客户端必须保存并且不做任何改动, -1表示批量处警，所有符合stEvent条件的事件均按当前信息处理
	int  bProcessed;//是否处警。0-未处警，1-已处警
	char szProcesserID[BVCU_MAX_ID_LEN+1];//处警者ID
	char szProcesserDesc[128];//处警描述
}BVCU_Event_SourceSaved;

//事件触发的动作
typedef struct _BVCU_Event_Action{
	int iAction; //动作。BVCU_EVENT_ACTION_*
	int iCount; //动作次数
	int iDelay;//第一次执行动作的延时时间。单位毫秒。
	int iInterval;//每次动作执行完后等待的时间间隔。单位毫秒
	void* pAction;//动作，BVCU_Event_Action_*
}BVCU_Event_Action;

typedef struct _BVCU_Event_Action_PURecord{
	char  szID[BVCU_MAX_ID_LEN+1];//PU ID
    int   iIndex;//Channel 号
    short iPreRecord; //报警预录时间长度,单位秒，通常不超过30秒(受PU可用内存限制).
    short iPostRecord; //报警消失后延录时间长度,单位秒
}BVCU_Event_Action_PURecord;

typedef struct _BVCU_Event_Action_EventRecord{
	char  szPUID[BVCU_MAX_ID_LEN+1];//PU ID
	int   iIndex;//Channel
	char  szNRUID[BVCU_MAX_ID_LEN+1];//存储到的NRU ID
	int   iFileLen;//录像文件时间长度，单位秒
}BVCU_Event_Action_EventRecord;

typedef struct _BVCU_Event_Action_AlertOut{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    int  iIndex;//AlertOut设备
    int  bAction; //0-通，1-断，
}BVCU_Event_Action_AlertOut;

typedef struct _BVCU_Event_Action_Email{
    char szReceiverAddr[BVCU_MAX_HOST_NAME_LEN+1];//接收者邮件地址
    char sCcAddr[BVCU_MAX_HOST_NAME_LEN+1];        // 抄送地址
    char sBccAddr[BVCU_MAX_HOST_NAME_LEN+1];    // 暗抄地址
    char szTitle[256];//邮件主题    
}BVCU_Event_Action_Email;

typedef struct _BVCU_Event_Action_Snapshot{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    int iIndex;//VideoIn设备

    int iDuration;//抓拍持续时间，单位秒。    

    int bLocal;//是否在本地存储抓拍的图片。0-不存储，1-存储。
    int bUpload;//是否上传。
    int bEmail;//是否发送Email。

    //上传服务器（NRU）。只有在bUpload=1时才有意义
    char szNRUID[BVCU_MAX_ID_LEN+1];//NRU ID指针

    //Email。只有在bEmail=1时才有意义    
    BVCU_Event_Action_Email stEmail;    
    int bAttach;//是否把图片作为邮件的附件。0-不添加附件，1-添加附件
}BVCU_Event_Action_Snapshot;

typedef struct _BVCU_Event_Action_SMS{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    char szCardNum[BVCU_MAX_MOBILEPHONE_NUM_LEN+1];//手机卡号
}BVCU_Event_Action_SMS;

typedef struct _BVCU_Event_Action_PTZ{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    int  iIndex;//PTZ设备
    int  iCommand;//PTZ命令。参考PUConfig.h中的BVCU_PTZ_COMMAND_*
    int  iParam;//预置点号/巡航路径号等
    int  iReserved[4];
}BVCU_Event_Action_PTZ;

typedef struct _BVCU_Event_Action_AudioOut{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    int  iIndex;//AudioOut设备
}BVCU_Event_Action_AudioOut;

typedef struct _BVCU_Event_Action_ShowMsg{
	char szID[BVCU_MAX_ID_NAME_LEN+1];//发送到的CU用户名或者PU ID
	char szText[128];//文字内容
	int  bSoundAlert;//0-不使用声音提示，1-要求CU使用声音提示用户
}BVCU_Event_Action_ShowMsg;

typedef struct _BVCU_Event_Action_Custom{
	char  szID[BVCU_MAX_ID_NAME_LEN+1];//ID或用户名
	int   iIndex;//子设备号
	int   iContentLength;//动作内容长度，单位Bytes
	void* pContentData;//动作内容
	int   iReserved[2];
}BVCU_Event_Action_Custom;

//联动基本信息，用于联动管理
typedef struct _BVCU_Event_LinkAction_Base{
    char szName[BVCU_MAX_NAME_LEN+1];//联动名字
    int  bEnable;//是否使能。0-未使能，1-使能
    int  bInAction;//是否正在触发。0-未触发，1-正在触发

    //时间设置
    BVCU_WallTime stBegin;//开始时间
    BVCU_WallTime stEnd;//结束时间。在开始/结束时间这段范围内，报警时间片设置有效。按绝对时间设置   
}BVCU_Event_LinkAction_Base;

//联动详细信息，用于联动管理
typedef struct _BVCU_Event_LinkAction{
    //基本信息
    BVCU_Event_LinkAction_Base stBase;

    //时间设置    
    BVCU_DayTimeSlice stWeek[7][BVCU_MAX_DAYTIMESLICE_COUNT];//Event有效的时间片段，按周设置。该设置与BVCU_Event_EventLinkAction_Base中的事件设置是“And”关系

    //事件源设置
    int iSourceCount;//事件源个数
    BVCU_Event_Source* pSource;//多个Event源之间是“And”的关系
    int iSourceInterval;//单位毫秒。对Event同时发生，如果发生时间间隔小于iSourceInterval，则触发联动    

    //触发动作
    int iActionCount;//动作个数
    BVCU_Event_Action* pAction;//Event触发动作
}BVCU_Event_LinkAction;

//联动通知，用于通知CU/PU/NRU等
typedef struct _BVCU_Event_LinkAction_Notify{
	//事件源
	int iSourceCount;//事件源个数
	BVCU_Event_Source* pSource;//多个Event源之间是“And”的关系
	
	//触发动作
	int iActionCount;//动作个数，可以为0
	BVCU_Event_Action* pAction;//Event触发动作
}BVCU_Event_LinkAction_Notify;

#if 0
//报警内容
typedef struct _BVCU_AlarmContent{    
	BVCU_CFG_AlarmSource stSource;//报警源
	BVCU_WallTime stTime;//报警发生时刻
	int iContentLength;//报警内容长度，单位Bytes
	void* pContentData;//报警内容
	int iReserved[2];
}BVCU_AlarmContent;
#endif
#endif