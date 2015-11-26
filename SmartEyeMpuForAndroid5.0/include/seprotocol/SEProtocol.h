/*======================================================
	SmartEye SIP Protocol Library.
	Copyright(c) BesoVideo Ltd. 2012
	wei.du@besovideo.com
========================================================*/

extern "C" {
#ifndef __LIBSEProtoCOL_H__
#define __LIBSEProtoCOL_H__
#include "pj/types.h"
#include "pj/sock.h"
#include "pjsip/sip_types.h"
#include "pjsip/sip_msg.h"
/*函数返回值*/
typedef enum _SEProto_Result 
{
	SEProto_RESULT_E_FAILED = -0x10000,
	SEProto_RESULT_E_NOMEM,		/** 分配内存失败 */
	SEProto_RESULT_E_INVAL,		/** 非法参数 */
	SEProto_RESULT_E_INVALIDOP,	/** 非法操作 */
	SEProto_RESULT_E_SESSIONTERMINATED,	/** 会话已经终止 */
	SEProto_RESULT_E_TWOWAYLARGE, /** 双向是大包 */
	SEProto_RESULT_E_NOTREQUESTMSG,
	SEProto_RESULT_E_INVALIDURI,
	SEProto_RESULT_E_NOTRESPONSEMSG,
	SEProto_RESULT_E_NOTEXIST,
	SEProto_RESULT_E_TIMEOUT,
	SEProto_RESULT_E_BUSYHERE,

	SEProto_RESULT_S_OK = 0,
	SEProto_RESULT_S_PEINDING,	/** 操作正在进行中，稍后完成 */
	SEProto_RESULT_S_PARTIALMSG,	/** 消息不完整 */
	SEProto_RESULT_S_PROVISIONAL,	/** 临时的 */
	SEProto_RESULT_S_TRYING,	/** 正在尝试 */
} SEProto_Result;
#define SEProto_Result_SUCCEEDED(a) ( ((int)(a)) >= 0 )
#define SEProto_Result_FAILED(a) ( ((int)(a)) < 0 )

/************************************************************************/
/* 传输方式（可靠的，或者不可靠的），影响请求或者响应是否要重传                 */
/************************************************************************/
typedef enum _SEProto_HTransport_Flag
{
	SEProto_HTransport_UNRELIABLE,		/** 不可靠的传输方式，比如UDP*/
	SEProto_HTransport_RELIABLE,		/** 可靠的传输方式，比如TCP*/
} SEProto_HTransport_Flag;

/************************************************************************/
/* 命令类型                                                              */
/************************************************************************/
typedef enum _SEProto_Method
{
	SEProto_UNKNOW_Method = 0,
	SEProto_INVITE_Method,
	SEProto_REGISTER_Method,
	SEProto_QUERY_Method,		/** 查询命令 */
	SEProto_CONTROL_Method,	/** 控制命令 */
	SEProto_NOTIFY_Method,
	SEProto_KEEPALIVE_Method,
} SEProto_Method;

/************************************************************************/
/* 这个只有在处理大包是有用                                                */
/* SEProto_Multi_Packet：大包分段接收。比如，获取设备列表时，可能设备很多，边接收边解析边显示 */
/* SEProto_Single_Packet：大包一次性接收完成	                                            */
/************************************************************************/
enum 
{
	SEProto_Multi_Packet = 1,
	SEProto_Single_Packet = 2,
};

/*操作类型*/
enum
{
	SEProto_Event_CMD_COMPLETED = 1,
	SEProto_Event_DIALOG_OPEN,
	SEProto_Event_DIALOG_UPDATE,
	SEProto_Event_DIALOG_CLOSE,
};

typedef void* SEProto_HObject;
typedef SEProto_HObject SEProto_HTransport;
typedef SEProto_HObject SEProto_HCommand;
typedef SEProto_HObject SEProto_HDialog;
typedef struct _SEProto_Transaction SEProto_Transaction;

typedef struct _TransportConfig
{
	SEProto_HTransport_Flag iFlag;
	int iMtu;		/** 这是一个估计值，是最大一次传输负载长度（不包括命令头） */
	/**
		这是由应用层处理发送，因为协议库不包括传输层，由应用层负责发送。
		返回值只能返回SEProto_RESULT_S_OK或者SEProto_RESULT_E_FAILED
	 */
	SEProto_Result (*SendMsg)(const SEProto_Transaction* pTransaction, char* pData, int iDataLen, pj_sockaddr_in* pDstAddr);
} TransportConfig;

typedef struct _SEProto_MsgHeader SEProto_MsgHeader;
struct _SEProto_MsgHeader
{
	/** 消息类型 (是请求还是响应). */
	pjsip_msg_type_e type;

	union
	{
		/** 请求行. */
		char szTargetID[128];

		/** 状态行. */
		int iStatusCode;
	} line;

	SEProto_Method iMethod;

	char szSubMethod[64];

	char szUserAgent[64];

	char szFrom[128];

	char szTo[128];

	char szContact[128];

	char szCallID[128];

	/*初始化值必须是0*/
	int iApplierID;

	int iSeq;

	/*初始化值必须是-1*/
	int iExpire;
};

typedef struct _SEProto_MsgContent SEProto_MsgContent;
struct _SEProto_MsgContent
{
	/*pData的类型，比如protobuf，text*/
	char szContentType[128];

	/*负载数据的首地址*/
	char *pData;

	/*负载数据的大小*/
	int iDataLength;

	/*在接收大包时，大包的总长度，一般情况下等于iDataLength*/
	int iContentLength;
};

typedef struct _SEProto_Msg SEProto_Msg;
struct _SEProto_Msg
{
	/*消息头部*/
	SEProto_MsgHeader stMsgHeader;

	/*消息负载*/
	SEProto_MsgContent stMsgContent;

	union
	{
		/*收到消息的源地址*/
		pj_sockaddr_in src;
		/*要从哪个地址发消息(本机地址)*/
		pj_sockaddr_in via;
	} u;
};

typedef struct _SEProto_Req_Callback SEProto_Req_Callback;
struct _SEProto_Req_Callback
{
	union
	{
		/** OnResponse/OnComplete只调用一次，而OnDataRead可能调用多次 */
		struct _multi
		{
			/*
				只负责通知应用层，有多少数据要传输过来，没有具体数据
			*/
			void (*OnResponse)(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);
			/*
				每收到一份数据，就回调此方法
			*/
			void (*OnDataRead)(SEProto_Transaction* pTransaction, SEProto_MsgContent* pMsgContent);
			/*
				只通知应用层，当前数据已经接收完成，没有数据
			*/
			void (*OnComplete)(SEProto_Transaction* pTransaction, SEProto_Result iResult, SEProto_Msg* pMsg);
		} multi;
		struct _single
		{
			/*
				不管数据有多大，只回调一次，即使有1M数据。
			*/
			void (*OnDataRead)(SEProto_Transaction* pTransaction, SEProto_Result iResult, SEProto_Msg* pMsg);
		} single;
	} func;

	//SEProto_*_Packet
	int iFlag;
};

struct _SEProto_Transaction
{
	/*操作类型*/
	/*SEProto_Event_CMD_COMPLETED = 1,
		SEProto_Event_DIALOG_OPEN,
		SEProto_Event_DIALOG_UPDATE,
		SEProto_Event_DIALOG_CLOSE,*/
	int iCallType;

	/*该次操作属于哪个对象，可能是SEProto_Command或者是SEProto_Dialog*/
	union
	{
		SEProto_HCommand hCommand;
		SEProto_HDialog hDialog;
	} u;

	/*该命令来自于哪个逻辑传输层*/
	SEProto_HTransport hTransport;

	/*命令超时值，单位：毫秒*/
	int iTimeOut;

	/*在发送命令时，要设置该函数*/
	SEProto_Req_Callback cb;

	/*该条命令是否成功还是失败*/
	/**
		@param iResult: 可能有以下几种可能：
			SEProto_RESULT_S_OK 表示该命令成功收到回响，不管成功与否
			SEProto_RESULT_E_TIMEOUT 表示该命令在iTimeOut内没有收到回响
			SEProto_RESULT_E_FAILED 表示该命令可能在处理过程中，由某个原因导致失败
	 */
	void (*OnEvent)(SEProto_Transaction* pTransaction, SEProto_Result iResult);

	/*库内部数据，外部程序不应该修改*/
	void* pInternal;

	/*用户自定义数据*/
	int iUserData[4];
};

typedef struct _SEProto_DialogParam SEProto_DialogParam;
struct _SEProto_DialogParam
{
	void* pUserData;
	void (*OnDialogClose)(SEProto_Transaction* pTransaction);
};

typedef struct _SEProto_GlobalParam SEProto_GlobalParam;
struct _SEProto_GlobalParam
{
	/*UAS收到Command Request，数据存放在pMsg里*/
	void (*OnCommand)(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);

	/*UAS收到Invite Request*/
	void (*OnInvite)(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);

	/*UAC/UAS收到re-INVITE Request*/
	void (*OnReInvite)(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);
};

/**
	初始化SEProtocol协议库，只能初始化一次
 */
SEProto_Result SEProto_Init(SEProto_GlobalParam* pParam);

/**
	停止使用SEProtocol协议库
 */
void SEProto_Finish();

/**
	创建一个传输层，这是一个逻辑上的传输层，分为可靠或者不可靠的
	比如所有的udp可以共用一个SEProto_HTransport
	所有的tcp也可以共用一个SEProto_HTransport，但是每个tcp连接要自己处理粘包
 */
SEProto_Result SEProto_CreateTransport(TransportConfig* pConfig, SEProto_HTransport* hTransport);

/**
	返回该传输层内置的接收缓冲区，以及该缓冲区的实际大小
	@param[in] hTransport: 具体哪个逻辑传输层
	@param[out] ppBuf: 获取该逻辑传输层的内部缓冲区地址
	@param[out] iLen: 得到内部缓冲区的大小
	@param[out] ppAddrSrc: 此次收到数据的源地址
	@retrun: token
 */
void* SEProto_PrepareParse(SEProto_HTransport hTransport, char** ppBuf, int* iLen, pj_sockaddr_in** ppAddrSrc);

/**
	在调用SEProto_PrepareParse后，调用该函数开始真正的解析
	@param[in] hTransport: 从哪个逻辑传输层收到网络数据
	@param[in] token: 是SEProto_PrepareParse函数的返回值
	@param[in] bytes_read: 从网络中收到数据的大小
	@return: 这次解析处理了多少数据
			在tcp下，可能不等于bytes_read
 */
int SEProto_ParsePacket(SEProto_HTransport hTransport, void *token, int bytes_read);

/**
	该函数必须轮询调用，处理协议库内部的数据重发
	@param[out] tv: 距离下次调用该函数的时间，可以为NULL
 */
void SEProto_HandleEvent(pj_time_val *tv);

/**
	发送命令，命令完成后触发SEProto_Transaction.OnEvent
	@return: 如果成功，则返回SEProto_RESULT_S_OK
 */
SEProto_Result SEProto_SendCmd(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg, void* pUserData);

/**
	创建会话，完成之后触发SEProto_Transaction.OnEvent
 */
SEProto_Result SEProto_OpenDialog(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg, SEProto_DialogParam* pParam);

/**
	更改已经建立的会话
 */
SEProto_Result SEProto_UpdateDialog(SEProto_HDialog hDialog, SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);

/**
	对收到的命令进行相应，可能是建立会话命令，也可能是一条命令
 */
SEProto_Result SEProto_Answer(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);

/**
	精简版的SEProto_Answer，只提供返回码以及数据负载
 */
SEProto_Result SEProto_AnswerLite(SEProto_Transaction* pTransaction, int iStatusCode, SEProto_MsgContent* pContent);

/**
	销毁对象
	hObject可以是SEProto_HCommand SEProto_HDialog SEProto_HTransport
 */
SEProto_Transaction* SEProto_CloseObject(SEProto_HObject hObject, bool bForce);

/**
	取消事务
 */
void SEProto_CloseTransaction(SEProto_Transaction* pTransaction);

/**
	获取给该对象绑定的应用层数据
 */
void* SEProto_GetUserData(SEProto_HObject hObject);

/**
	绑定应用层数据到库内部对象上
 */
void SEProto_SetUserData(SEProto_HObject hObject, void* pUserData);

/**
	设置一次命令的超时值，单位ms
 */
void SEProto_SetTimeout(SEProto_Transaction* pTransaction, int to);

/**
	为此次命令注册OnEvent回调函数
	设置OnEvent，以及iUserData值
 */
void SEProto_SetOnEvent(SEProto_Transaction* pTransaction);

/**
	当UAS事务收到BYE时，回调DialogClose函数
 */
void SEProto_RegisterClose(SEProto_HDialog hDialog, void (*OnDialogClose)(SEProto_Transaction* pTransaction));

/**
	得到一次操作的请求消息
 */
SEProto_Msg* SEProto_GetRequestMsg(SEProto_Transaction* pTransaction);

#endif
};