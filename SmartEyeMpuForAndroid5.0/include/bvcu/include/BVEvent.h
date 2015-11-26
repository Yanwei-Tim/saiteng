#ifndef __BVEVENT_H__
#define __BVEVENT_H__

#include "BVCUConst.h"
#include "BVCUCommon.h"

//�¼�����
enum {
    BVCU_EVENT_TYPE_NONE = 0,        //��Чֵ
    
    //ͨ��
	BVCU_EVENT_TYPE_DISKERROR = 0x0001,       //���̴���Դ��PU/NRU��Storage
	
    //PU���
    BVCU_EVENT_TYPE_VIDEOLOST = 0x1000,    //��Ƶ��ʧ��Դ��PU �� VideoIn
    BVCU_EVENT_TYPE_VIDEOMD,               //�˶���⡣Դ��PU �� VideoIn
    BVCU_EVENT_TYPE_VIDEOOCCLUSION,        //��Ƶ�ڵ���Դ��PU �� VideoIn
    BVCU_EVENT_TYPE_ALERTIN,               //�������롣Դ��PU��AlertIn
    BVCU_EVENT_TYPE_PERIOD,                //���ڱ�����Դ��PU�����ڱ���    
    BVCU_EVENT_TYPE_PUONLINE,              //PU���ߡ�Դ��PU
    BVCU_EVENT_TYPE_PUOFFLINE,             //PU���ߡ�Դ��PU
    
    //NRU���
    BVCU_EVENT_TYPE_NRUONLINE = 0x2000,   //NRU����
    BVCU_EVENT_TYPE_NRUOFFLINE,           //NRU����
    
    //VTDU���
    BVCU_EVENT_TYPE_VTDUONLINE = 0x3000,
    BVCU_EVENT_TYPE_VTDUOFFLINE,
    
    //CMS���
    BVCU_EVENT_TYPE_CMSONLINE = 0x4000,
    BVCU_EVENT_TYPE_CMSOFFLINE,
    
    //�û�(User)���
    BVCU_EVENT_TYPE_USERLOGIN = 0x5000,   //�û���¼
    BVCU_EVENT_TYPE_USERLOGOUT,           //�û�ע��
        
	//��ֵ�������ֵΪ�Զ�������
    BVCU_EVENT_TYPE_CUSTOM = 0x10000000,
};

//�¼�����
enum {
    BVCU_EVENT_ACTION_NONE = 0,
    BVCU_EVENT_ACTION_PURECORD,     //ִ����:PU�� PU¼�񡣶�Ӧ�ṹ�壺BVCU_Event_Action_PURecord
    BVCU_EVENT_ACTION_ALERTOUT,     //ִ����:PU�� �����������Ӧ�ṹ�壺BVCU_Event_Action_AlertOut
    BVCU_EVENT_ACTION_SNAPSHOT,     //ִ����:PU�� ץ�ġ���Ӧ�ṹ�壺BVCU_Event_Action_Snapshot
    BVCU_EVENT_ACTION_PTZ,          //ִ����:PU�� �ƶ���̨����Ӧ�ṹ�壺BVCU_Event_Action_PTZ
    BVCU_EVENT_ACTION_AUDIOOUT,     //ִ����:PU�� ���ű�����������Ӧ�ṹ�壺BVCU_Event_Action_AudioOut
    BVCU_EVENT_ACTION_SMS,          //ִ����:PU�� �����ֻ����š�ֻ��BVCU_PUCFG_DeviceInfo.bSupportSMS=1ʱ�ſ��á���Ӧ�ṹ�壺BVCU_Event_Action_SMS
    BVCU_EVENT_ACTION_EMAIL,        //ִ����:CMS������Email����Ӧ�ṹ�壺BVCU_Event_Action_Email
    BVCU_EVENT_ACTION_SHOWMSG,      //ִ����:PU/CU�� ��PU/CU����������Ϣ����Ӧ�ṹ�壺BVCU_Event_Action_ShowMsg
	BVCU_EVENT_ACTION_EventRECORD,  //ִ����:NRU��ƽ̨¼�񡣶�Ӧ�ṹ�壺BVCU_Event_Action_EventRecord

    BVCU_EVENT_ACTION_CUSTOM = 0x10000000,//��ֵ�������ֵΪ�Զ��嶯������Ӧ�ṹ�壺BVCU_Event_Action_Custom
};

//�¼������� �����¼�ʹ�õĽṹ��
typedef struct _BVCU_Event_Source{
	int  iEventType;//�¼����ͣ�BVCU_EVENT_TYPE_*
	BVCU_WallTime stTime;//�¼�����ʱ��
	char szID[BVCU_MAX_ID_NAME_LEN+1];//ԴID������PU ID/�û�����
	int  iSubDevIdx;//���豸��������PU����Ƶ����ȡ�BVCU_ALARM_TYPE_PERIOD����ʱ�������������λ��
	int  iReserved[2];//����������Ϊ0
}BVCU_Event_Source;

//�ͻ��˲�ѯ���صĴ洢���¼���CU�����޸����еĴ�������
typedef struct _BVCU_Event_SourceSaved{
	BVCU_Event_Source stEvent;//Event
	int  iEventIndex;//���ص����ݿ��е�Ψһ��ʶ���ͻ��˱��뱣�沢�Ҳ����κθĶ�, -1��ʾ�������������з���stEvent�������¼�������ǰ��Ϣ����
	int  bProcessed;//�Ƿ񴦾���0-δ������1-�Ѵ���
	char szProcesserID[BVCU_MAX_ID_LEN+1];//������ID
	char szProcesserDesc[128];//��������
}BVCU_Event_SourceSaved;

//�¼������Ķ���
typedef struct _BVCU_Event_Action{
	int iAction; //������BVCU_EVENT_ACTION_*
	int iCount; //��������
	int iDelay;//��һ��ִ�ж�������ʱʱ�䡣��λ���롣
	int iInterval;//ÿ�ζ���ִ�����ȴ���ʱ��������λ����
	void* pAction;//������BVCU_Event_Action_*
}BVCU_Event_Action;

typedef struct _BVCU_Event_Action_PURecord{
	char  szID[BVCU_MAX_ID_LEN+1];//PU ID
    int   iIndex;//Channel ��
    short iPreRecord; //����Ԥ¼ʱ�䳤��,��λ�룬ͨ��������30��(��PU�����ڴ�����).
    short iPostRecord; //������ʧ����¼ʱ�䳤��,��λ��
}BVCU_Event_Action_PURecord;

typedef struct _BVCU_Event_Action_EventRecord{
	char  szPUID[BVCU_MAX_ID_LEN+1];//PU ID
	int   iIndex;//Channel
	char  szNRUID[BVCU_MAX_ID_LEN+1];//�洢����NRU ID
	int   iFileLen;//¼���ļ�ʱ�䳤�ȣ���λ��
}BVCU_Event_Action_EventRecord;

typedef struct _BVCU_Event_Action_AlertOut{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    int  iIndex;//AlertOut�豸
    int  bAction; //0-ͨ��1-�ϣ�
}BVCU_Event_Action_AlertOut;

typedef struct _BVCU_Event_Action_Email{
    char szReceiverAddr[BVCU_MAX_HOST_NAME_LEN+1];//�������ʼ���ַ
    char sCcAddr[BVCU_MAX_HOST_NAME_LEN+1];        // ���͵�ַ
    char sBccAddr[BVCU_MAX_HOST_NAME_LEN+1];    // ������ַ
    char szTitle[256];//�ʼ�����    
}BVCU_Event_Action_Email;

typedef struct _BVCU_Event_Action_Snapshot{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    int iIndex;//VideoIn�豸

    int iDuration;//ץ�ĳ���ʱ�䣬��λ�롣    

    int bLocal;//�Ƿ��ڱ��ش洢ץ�ĵ�ͼƬ��0-���洢��1-�洢��
    int bUpload;//�Ƿ��ϴ���
    int bEmail;//�Ƿ���Email��

    //�ϴ���������NRU����ֻ����bUpload=1ʱ��������
    char szNRUID[BVCU_MAX_ID_LEN+1];//NRU IDָ��

    //Email��ֻ����bEmail=1ʱ��������    
    BVCU_Event_Action_Email stEmail;    
    int bAttach;//�Ƿ��ͼƬ��Ϊ�ʼ��ĸ�����0-����Ӹ�����1-��Ӹ���
}BVCU_Event_Action_Snapshot;

typedef struct _BVCU_Event_Action_SMS{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    char szCardNum[BVCU_MAX_MOBILEPHONE_NUM_LEN+1];//�ֻ�����
}BVCU_Event_Action_SMS;

typedef struct _BVCU_Event_Action_PTZ{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    int  iIndex;//PTZ�豸
    int  iCommand;//PTZ����ο�PUConfig.h�е�BVCU_PTZ_COMMAND_*
    int  iParam;//Ԥ�õ��/Ѳ��·���ŵ�
    int  iReserved[4];
}BVCU_Event_Action_PTZ;

typedef struct _BVCU_Event_Action_AudioOut{
	char szID[BVCU_MAX_ID_LEN+1];//PU ID
    int  iIndex;//AudioOut�豸
}BVCU_Event_Action_AudioOut;

typedef struct _BVCU_Event_Action_ShowMsg{
	char szID[BVCU_MAX_ID_NAME_LEN+1];//���͵���CU�û�������PU ID
	char szText[128];//��������
	int  bSoundAlert;//0-��ʹ��������ʾ��1-Ҫ��CUʹ��������ʾ�û�
}BVCU_Event_Action_ShowMsg;

typedef struct _BVCU_Event_Action_Custom{
	char  szID[BVCU_MAX_ID_NAME_LEN+1];//ID���û���
	int   iIndex;//���豸��
	int   iContentLength;//�������ݳ��ȣ���λBytes
	void* pContentData;//��������
	int   iReserved[2];
}BVCU_Event_Action_Custom;

//����������Ϣ��������������
typedef struct _BVCU_Event_LinkAction_Base{
    char szName[BVCU_MAX_NAME_LEN+1];//��������
    int  bEnable;//�Ƿ�ʹ�ܡ�0-δʹ�ܣ�1-ʹ��
    int  bInAction;//�Ƿ����ڴ�����0-δ������1-���ڴ���

    //ʱ������
    BVCU_WallTime stBegin;//��ʼʱ��
    BVCU_WallTime stEnd;//����ʱ�䡣�ڿ�ʼ/����ʱ����η�Χ�ڣ�����ʱ��Ƭ������Ч��������ʱ������   
}BVCU_Event_LinkAction_Base;

//������ϸ��Ϣ��������������
typedef struct _BVCU_Event_LinkAction{
    //������Ϣ
    BVCU_Event_LinkAction_Base stBase;

    //ʱ������    
    BVCU_DayTimeSlice stWeek[7][BVCU_MAX_DAYTIMESLICE_COUNT];//Event��Ч��ʱ��Ƭ�Σ��������á���������BVCU_Event_EventLinkAction_Base�е��¼������ǡ�And����ϵ

    //�¼�Դ����
    int iSourceCount;//�¼�Դ����
    BVCU_Event_Source* pSource;//���EventԴ֮���ǡ�And���Ĺ�ϵ
    int iSourceInterval;//��λ���롣��Eventͬʱ�������������ʱ����С��iSourceInterval���򴥷�����    

    //��������
    int iActionCount;//��������
    BVCU_Event_Action* pAction;//Event��������
}BVCU_Event_LinkAction;

//����֪ͨ������֪ͨCU/PU/NRU��
typedef struct _BVCU_Event_LinkAction_Notify{
	//�¼�Դ
	int iSourceCount;//�¼�Դ����
	BVCU_Event_Source* pSource;//���EventԴ֮���ǡ�And���Ĺ�ϵ
	
	//��������
	int iActionCount;//��������������Ϊ0
	BVCU_Event_Action* pAction;//Event��������
}BVCU_Event_LinkAction_Notify;

#if 0
//��������
typedef struct _BVCU_AlarmContent{    
	BVCU_CFG_AlarmSource stSource;//����Դ
	BVCU_WallTime stTime;//��������ʱ��
	int iContentLength;//�������ݳ��ȣ���λBytes
	void* pContentData;//��������
	int iReserved[2];
}BVCU_AlarmContent;
#endif
#endif