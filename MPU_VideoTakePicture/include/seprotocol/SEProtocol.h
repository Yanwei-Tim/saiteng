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
/*��������ֵ*/
typedef enum _SEProto_Result 
{
	SEProto_RESULT_E_FAILED = -0x10000,
	SEProto_RESULT_E_NOMEM,		/** �����ڴ�ʧ�� */
	SEProto_RESULT_E_INVAL,		/** �Ƿ����� */
	SEProto_RESULT_E_INVALIDOP,	/** �Ƿ����� */
	SEProto_RESULT_E_SESSIONTERMINATED,	/** �Ự�Ѿ���ֹ */
	SEProto_RESULT_E_TWOWAYLARGE, /** ˫���Ǵ�� */
	SEProto_RESULT_E_NOTREQUESTMSG,
	SEProto_RESULT_E_INVALIDURI,
	SEProto_RESULT_E_NOTRESPONSEMSG,
	SEProto_RESULT_E_NOTEXIST,
	SEProto_RESULT_E_TIMEOUT,
	SEProto_RESULT_E_BUSYHERE,

	SEProto_RESULT_S_OK = 0,
	SEProto_RESULT_S_PEINDING,	/** �������ڽ����У��Ժ���� */
	SEProto_RESULT_S_PARTIALMSG,	/** ��Ϣ������ */
	SEProto_RESULT_S_PROVISIONAL,	/** ��ʱ�� */
	SEProto_RESULT_S_TRYING,	/** ���ڳ��� */
} SEProto_Result;
#define SEProto_Result_SUCCEEDED(a) ( ((int)(a)) >= 0 )
#define SEProto_Result_FAILED(a) ( ((int)(a)) < 0 )

/************************************************************************/
/* ���䷽ʽ���ɿ��ģ����߲��ɿ��ģ���Ӱ�����������Ӧ�Ƿ�Ҫ�ش�                 */
/************************************************************************/
typedef enum _SEProto_HTransport_Flag
{
	SEProto_HTransport_UNRELIABLE,		/** ���ɿ��Ĵ��䷽ʽ������UDP*/
	SEProto_HTransport_RELIABLE,		/** �ɿ��Ĵ��䷽ʽ������TCP*/
} SEProto_HTransport_Flag;

/************************************************************************/
/* ��������                                                              */
/************************************************************************/
typedef enum _SEProto_Method
{
	SEProto_UNKNOW_Method = 0,
	SEProto_INVITE_Method,
	SEProto_REGISTER_Method,
	SEProto_QUERY_Method,		/** ��ѯ���� */
	SEProto_CONTROL_Method,	/** �������� */
	SEProto_NOTIFY_Method,
	SEProto_KEEPALIVE_Method,
} SEProto_Method;

/************************************************************************/
/* ���ֻ���ڴ�����������                                                */
/* SEProto_Multi_Packet������ֶν��ա����磬��ȡ�豸�б�ʱ�������豸�ܶ࣬�߽��ձ߽�������ʾ */
/* SEProto_Single_Packet�����һ���Խ������	                                            */
/************************************************************************/
enum 
{
	SEProto_Multi_Packet = 1,
	SEProto_Single_Packet = 2,
};

/*��������*/
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
	int iMtu;		/** ����һ������ֵ�������һ�δ��为�س��ȣ�����������ͷ�� */
	/**
		������Ӧ�ò㴦���ͣ���ΪЭ��ⲻ��������㣬��Ӧ�ò㸺���͡�
		����ֵֻ�ܷ���SEProto_RESULT_S_OK����SEProto_RESULT_E_FAILED
	 */
	SEProto_Result (*SendMsg)(const SEProto_Transaction* pTransaction, char* pData, int iDataLen, pj_sockaddr_in* pDstAddr);
} TransportConfig;

typedef struct _SEProto_MsgHeader SEProto_MsgHeader;
struct _SEProto_MsgHeader
{
	/** ��Ϣ���� (����������Ӧ). */
	pjsip_msg_type_e type;

	union
	{
		/** ������. */
		char szTargetID[128];

		/** ״̬��. */
		int iStatusCode;
	} line;

	SEProto_Method iMethod;

	char szSubMethod[64];

	char szUserAgent[64];

	char szFrom[128];

	char szTo[128];

	char szContact[128];

	char szCallID[128];

	/*��ʼ��ֵ������0*/
	int iApplierID;

	int iSeq;

	/*��ʼ��ֵ������-1*/
	int iExpire;
};

typedef struct _SEProto_MsgContent SEProto_MsgContent;
struct _SEProto_MsgContent
{
	/*pData�����ͣ�����protobuf��text*/
	char szContentType[128];

	/*�������ݵ��׵�ַ*/
	char *pData;

	/*�������ݵĴ�С*/
	int iDataLength;

	/*�ڽ��մ��ʱ��������ܳ��ȣ�һ������µ���iDataLength*/
	int iContentLength;
};

typedef struct _SEProto_Msg SEProto_Msg;
struct _SEProto_Msg
{
	/*��Ϣͷ��*/
	SEProto_MsgHeader stMsgHeader;

	/*��Ϣ����*/
	SEProto_MsgContent stMsgContent;

	union
	{
		/*�յ���Ϣ��Դ��ַ*/
		pj_sockaddr_in src;
		/*Ҫ���ĸ���ַ����Ϣ(������ַ)*/
		pj_sockaddr_in via;
	} u;
};

typedef struct _SEProto_Req_Callback SEProto_Req_Callback;
struct _SEProto_Req_Callback
{
	union
	{
		/** OnResponse/OnCompleteֻ����һ�Σ���OnDataRead���ܵ��ö�� */
		struct _multi
		{
			/*
				ֻ����֪ͨӦ�ò㣬�ж�������Ҫ���������û�о�������
			*/
			void (*OnResponse)(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);
			/*
				ÿ�յ�һ�����ݣ��ͻص��˷���
			*/
			void (*OnDataRead)(SEProto_Transaction* pTransaction, SEProto_MsgContent* pMsgContent);
			/*
				ֻ֪ͨӦ�ò㣬��ǰ�����Ѿ�������ɣ�û������
			*/
			void (*OnComplete)(SEProto_Transaction* pTransaction, SEProto_Result iResult, SEProto_Msg* pMsg);
		} multi;
		struct _single
		{
			/*
				���������ж��ֻ�ص�һ�Σ���ʹ��1M���ݡ�
			*/
			void (*OnDataRead)(SEProto_Transaction* pTransaction, SEProto_Result iResult, SEProto_Msg* pMsg);
		} single;
	} func;

	//SEProto_*_Packet
	int iFlag;
};

struct _SEProto_Transaction
{
	/*��������*/
	/*SEProto_Event_CMD_COMPLETED = 1,
		SEProto_Event_DIALOG_OPEN,
		SEProto_Event_DIALOG_UPDATE,
		SEProto_Event_DIALOG_CLOSE,*/
	int iCallType;

	/*�ôβ��������ĸ����󣬿�����SEProto_Command������SEProto_Dialog*/
	union
	{
		SEProto_HCommand hCommand;
		SEProto_HDialog hDialog;
	} u;

	/*�������������ĸ��߼������*/
	SEProto_HTransport hTransport;

	/*���ʱֵ����λ������*/
	int iTimeOut;

	/*�ڷ�������ʱ��Ҫ���øú���*/
	SEProto_Req_Callback cb;

	/*���������Ƿ�ɹ�����ʧ��*/
	/**
		@param iResult: ���������¼��ֿ��ܣ�
			SEProto_RESULT_S_OK ��ʾ������ɹ��յ����죬���ܳɹ����
			SEProto_RESULT_E_TIMEOUT ��ʾ��������iTimeOut��û���յ�����
			SEProto_RESULT_E_FAILED ��ʾ����������ڴ�������У���ĳ��ԭ����ʧ��
	 */
	void (*OnEvent)(SEProto_Transaction* pTransaction, SEProto_Result iResult);

	/*���ڲ����ݣ��ⲿ����Ӧ���޸�*/
	void* pInternal;

	/*�û��Զ�������*/
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
	/*UAS�յ�Command Request�����ݴ����pMsg��*/
	void (*OnCommand)(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);

	/*UAS�յ�Invite Request*/
	void (*OnInvite)(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);

	/*UAC/UAS�յ�re-INVITE Request*/
	void (*OnReInvite)(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);
};

/**
	��ʼ��SEProtocolЭ��⣬ֻ�ܳ�ʼ��һ��
 */
SEProto_Result SEProto_Init(SEProto_GlobalParam* pParam);

/**
	ֹͣʹ��SEProtocolЭ���
 */
void SEProto_Finish();

/**
	����һ������㣬����һ���߼��ϵĴ���㣬��Ϊ�ɿ����߲��ɿ���
	�������е�udp���Թ���һ��SEProto_HTransport
	���е�tcpҲ���Թ���һ��SEProto_HTransport������ÿ��tcp����Ҫ�Լ�����ճ��
 */
SEProto_Result SEProto_CreateTransport(TransportConfig* pConfig, SEProto_HTransport* hTransport);

/**
	���ظô�������õĽ��ջ��������Լ��û�������ʵ�ʴ�С
	@param[in] hTransport: �����ĸ��߼������
	@param[out] ppBuf: ��ȡ���߼��������ڲ���������ַ
	@param[out] iLen: �õ��ڲ��������Ĵ�С
	@param[out] ppAddrSrc: �˴��յ����ݵ�Դ��ַ
	@retrun: token
 */
void* SEProto_PrepareParse(SEProto_HTransport hTransport, char** ppBuf, int* iLen, pj_sockaddr_in** ppAddrSrc);

/**
	�ڵ���SEProto_PrepareParse�󣬵��øú�����ʼ�����Ľ���
	@param[in] hTransport: ���ĸ��߼�������յ���������
	@param[in] token: ��SEProto_PrepareParse�����ķ���ֵ
	@param[in] bytes_read: ���������յ����ݵĴ�С
	@return: ��ν��������˶�������
			��tcp�£����ܲ�����bytes_read
 */
int SEProto_ParsePacket(SEProto_HTransport hTransport, void *token, int bytes_read);

/**
	�ú���������ѯ���ã�����Э����ڲ��������ط�
	@param[out] tv: �����´ε��øú�����ʱ�䣬����ΪNULL
 */
void SEProto_HandleEvent(pj_time_val *tv);

/**
	�������������ɺ󴥷�SEProto_Transaction.OnEvent
	@return: ����ɹ����򷵻�SEProto_RESULT_S_OK
 */
SEProto_Result SEProto_SendCmd(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg, void* pUserData);

/**
	�����Ự�����֮�󴥷�SEProto_Transaction.OnEvent
 */
SEProto_Result SEProto_OpenDialog(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg, SEProto_DialogParam* pParam);

/**
	�����Ѿ������ĻỰ
 */
SEProto_Result SEProto_UpdateDialog(SEProto_HDialog hDialog, SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);

/**
	���յ������������Ӧ�������ǽ����Ự���Ҳ������һ������
 */
SEProto_Result SEProto_Answer(SEProto_Transaction* pTransaction, SEProto_Msg* pMsg);

/**
	������SEProto_Answer��ֻ�ṩ�������Լ����ݸ���
 */
SEProto_Result SEProto_AnswerLite(SEProto_Transaction* pTransaction, int iStatusCode, SEProto_MsgContent* pContent);

/**
	���ٶ���
	hObject������SEProto_HCommand SEProto_HDialog SEProto_HTransport
 */
SEProto_Transaction* SEProto_CloseObject(SEProto_HObject hObject, bool bForce);

/**
	ȡ������
 */
void SEProto_CloseTransaction(SEProto_Transaction* pTransaction);

/**
	��ȡ���ö���󶨵�Ӧ�ò�����
 */
void* SEProto_GetUserData(SEProto_HObject hObject);

/**
	��Ӧ�ò����ݵ����ڲ�������
 */
void SEProto_SetUserData(SEProto_HObject hObject, void* pUserData);

/**
	����һ������ĳ�ʱֵ����λms
 */
void SEProto_SetTimeout(SEProto_Transaction* pTransaction, int to);

/**
	Ϊ�˴�����ע��OnEvent�ص�����
	����OnEvent���Լ�iUserDataֵ
 */
void SEProto_SetOnEvent(SEProto_Transaction* pTransaction);

/**
	��UAS�����յ�BYEʱ���ص�DialogClose����
 */
void SEProto_RegisterClose(SEProto_HDialog hDialog, void (*OnDialogClose)(SEProto_Transaction* pTransaction));

/**
	�õ�һ�β�����������Ϣ
 */
SEProto_Msg* SEProto_GetRequestMsg(SEProto_Transaction* pTransaction);

#endif
};