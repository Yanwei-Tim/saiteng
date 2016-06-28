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

/*NOTE: �����ַ���������UTF-8����*/
/*�¼���*/
/*ȫ���¼�*/
enum{
    
    BVCU_EVENT_AUDIOINPUT_DISCONNECTED = 1, /*û�в�����Ƶ�����豸���¼���������*/
    BVCU_EVENT_AUDIOINPUT_CONNECTED, /*������Ƶ�����豸���¼���������*/
};
/*Session�¼�*/
enum{
    BVCU_EVENT_SESSION_OPEN = 1,  /* ����Session���¼�������BVCU_Event_Common*/
    BVCU_EVENT_SESSION_CLOSE,     /*�ر�Session���¼�������BVCU_Event_Common*/
    BVCU_EVENT_SESSION_CMD_COMPLETE, /*������ɡ��¼�������BVCU_Event_SessionCmd*/
};
/*Dialog�¼�*/
enum{
    BVCU_EVENT_DIALOG_OPEN = 1,    /* ����Dialog���¼�������BVCU_Event_Common*/
    BVCU_EVENT_DIALOG_UPDATE,    /* ����Dialog���¼�������BVCU_Event_Common*/
    BVCU_EVENT_DIALOG_CLOSE,       /* �ر�Dialog���¼�������BVCU_Event_Common*/
    BVCU_EVENT_STORAGE_FILE_REQUIRENAME,  /*��ȡ�ļ��������¼��ṩ���Զ����ļ������ơ��¼�����: BVCU_Event_Storage*/
    BVCU_EVENT_STORAGE_FILE_OPEN,  /*�����ļ����¼�����: BVCU_Event_Storage*/
    BVCU_EVENT_STORAGE_FILE_CLOSE, /*�ر��ļ����¼�������BVCU_Event_Storage*/
    BVCU_EVENT_STORAGE_ERROR,      /*�洢�����¼�������BVCU_Event_Storage*/
};

//Subscribe
typedef struct _BVCU_Subscribe_Status{
    int iSubscribeStatus;//����״̬��BVCU_SUBSCRIBE_*
    int iReserved[4];
}BVCU_Subscribe_Status;

/*
CU���͵�֪ͨ�����յ��������������
*/
typedef struct _BVCU_CmdMsgContent BVCU_CmdMsgContent;
struct _BVCU_CmdMsgContent{
    /*һ��֪ͨ/������ܰ���������Ϣ��pNextָ����һ����Ϣ�����һ����Ϣ��pNextӦָ��NULL 
        ÿ��֪ͨ/�������Ϣ���ͺ�˳���ǹ̶��ġ������֪ͨ/����ֻ֧��һ����������(pNext��NULL)*/
    BVCU_CmdMsgContent* pNext;
    
    /*��Ϣ��Ŀ*/
    int iDataCount;

    /*��Ϣ���飬����Ԫ�ظ�������iDataCount��pData[0]��ʾ��һ����Ա��pData[1]��ʾ��2����Ա��
    �����ɾ����������*/
    void* pData;
};

typedef struct _BVCU_NotifyMsgContent BVCU_NotifyMsgContent;
struct _BVCU_NotifyMsgContent{
    /*һ��֪ͨ���ܰ���������Ϣ��pNextָ����һ����Ϣ�����һ����Ϣ��pNextӦָ��NULL��*/
    BVCU_NotifyMsgContent* pNext;

    /*֪ͨ���ݵ����ͣ�BVCU_SUBMETHOD_*/
    int iSubMethod;

    /*��ϢԴ��ϵͳ�е�����ʵ��)ID��Ϊ�ձ�ʾ�ǵ�ǰ��¼��Server*/
    char szSourceID[BVCU_MAX_ID_LEN+1];

     /*��ϢԴ�ĸ����豸����������0��ʼ������PU����̨/ͨ��/����ƵIO�ȡ���Ϊ-1��ʾ������*/
    int iSourceIndex;
    
    /*Ŀ��ID��Ϊ�ձ�ʾ����Ŀ���ǵ�ǰ��¼��Server*/
    char szTargetID[BVCU_MAX_ID_LEN+1];

     /*��0��ʼ��Ŀ�긽���豸������������PU����̨/ͨ��/����ƵIO�ȡ���Ϊ-1��ʾ������*/
    int iTargetIndex;
    
    /*��Ϣ��Ŀ*/
    int iDataCount;

    /*��Ϣ���飬����Ԫ�ظ�������iDataCount��pData[0]��ʾ��һ����Ա��pData[1]��ʾ��2����Ա�� 
        ������iSubMethod����*/
    void* pData;
};

/*�����¼������У��漰��ָ��Ĳ��֣������߶�Ӧ��ʶ�����������ջ�Ϸ����ڴ档OnEvent�������غ�ָ�����ʧЧ*/
typedef struct _BVCU_Event_Common
{
    BVCU_Result iResult;/*������*/
}BVCU_Event_Common;

typedef struct _BVCU_Event_SessionCmd
{
    BVCU_Result iResult;/*������*/
    int iPercent;//������ɰٷֱȣ�ȡֵ��Χ0~100��һ������ķ��ؿ��ܺܳ���BVCUͨ����ε���OnEvent��֪ͨӦ�ó���
    //ÿ��OnEvent��iPercent��Ա�������100��ʾ������ɣ�ֻ�����һ��OnEvent��������Ϊ100��
    //�������iResult�ᱻ���óɴ����룬iPercent���óɳ���ʱ��ɵİٷֱȡ�
    BVCU_CmdMsgContent stContent; /*������������*/
}BVCU_Event_SessionCmd;

typedef struct _BVCU_Event_Storage
{
    BVCU_Result iResult;/*�����롣
        BVCU_EVENT_STORAGE_FILE_REQUIRENAME��ֻ��BVCU_RESULT_S_OKһ��
        BVCU_EVENT_STORAGE_ERROR��
        ��1�����iResult==BVCU_RESULT_E_OUTOFSPACE,���ڲ��������رյ�ǰ�ļ�������BVCU_EVENT_STORAGE_FILE_CLOSE��Ȼ��������Ӳ�̿ռ䣬
        ���Ӳ�̿ռ��㹻�����Զ��ٴο�ʼ¼��
        ��2�����iResult==BVCU_RESULT_E_FAILED����ʾδ֪ԭ����󣬿Ᵽ֤��һ���ļ��ر�֮ǰ�������������δ��󣬶�ֻ����һ�θ��¼���
        �����¼��󣬿���Ȼ����¼��*/

    char* szFileName;/*�ļ�����Ҫ��1�������Ǿ���·����2����'\0'��β�����ַ���ָ����ڲ��ĳ���Ϊ(BVCU_MAX_FILE_NAME_LEN+1)�ֽڵ��ַ�����
    Ӧ�ó��������FILE_REQUIRENAME�¼����޸ĳ��κκϷ����ļ�����
    ��ʵ���ļ�������ԡ������¼��н�ֹ�޸�*/
    SAV_TYPE_INT64 iTimestamp;/*REQUIRENAME/FILE_OPEN:�ļ���ʼʱ�����FILE_CLOSE:�ļ�����ʱ�������1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
}BVCU_Event_Storage;

typedef  void* BVCU_HSession;
typedef  void* BVCU_HDialog;

typedef struct _BVCU_GlobalParam{
    /*���ṹ��Ĵ�С��������Ӧ��ʼ��Ϊsizeof(BVCU_GlobalParam)*/
    int iSize;

    /*��ѿ��ڲ���⵽��ȫ���¼�֪ͨӦ�ó���
     iEventCode:�¼��룬�μ�BVCU_EVENT_*
     pParam: ÿ���¼���Ӧ�Ĳ������������Ͳο������¼����˵�������pParam��NULL����ʾ�޲�����
    */
    void (*OnEvent)(int iEventCode, void* pParam);

    /*����*/
    int iReserved[4];
}BVCU_GlobalParam;


/*
CU����������
*/
typedef struct _BVCU_Command BVCU_Command;
struct _BVCU_Command{
    /*���ṹ��Ĵ�С��������Ӧ��ʼ��Ϊsizeof(BVCU_Command)*/
    int iSize;
    
    /*�û��Զ������ݡ�ͨ�����ڻص�֪ͨ��Ӧ�ó���/������øó�Ա�����ֲ�ͬ������*/
    void* pUserData;

    /*�������ͣ�BVCU_METHOD_* */
    int iMethod;
    
    /*���������ͣ�BVCU_SUBMETHOD_*��������BVCU_CmdMsgContent.pData����*/
    int iSubMethod;

    /*ϵͳ�е�����ʵ��Ŀ��ID������Ϊ�ձ�ʾ����Ŀ���ǵ�ǰ��¼��Server*/
    char szTargetID[BVCU_MAX_ID_LEN+1];

    /*��0��ʼ��Ŀ�긽���豸������������PU����̨/ͨ��/����ƵIO�ȡ���Ϊ-1��ʾ������*/
    int iTargetIndex;
    
    /*�����iTimeOutδ�յ���������Ϊʧ�ܣ���λ���롣�������Ϊ0�������BVCU_ServerParam.iTimeout*/
    int iTimeOut;

    /*�����*/
    BVCU_CmdMsgContent stMsgContent;

    /*�¼���
    pCommand:������ָ�롣ע���ָ��ָ�����SDK�ڲ�ά����һ��BVCU_Commandǳ����
    iEventCode:�¼��룬�μ�BVCU_EVENT_SESSION_CMD_*��ĿǰiEventCode���ǵ���BVCU_EVENT_SESSION_CMD_COMPLETE
     pParam: ÿ���¼���Ӧ�Ĳ������������Ͳο������¼����˵��*/
    void (*OnEvent)(BVCU_HSession hSession, BVCU_Command* pCommand, int iEventCode, void* pParam);

    /*����*/
    int iReserved[2];
};

/*Server��Ϣ*/
typedef struct _BVCU_ServerParam{
    /*���ṹ��Ĵ�С��������Ӧ��ʼ��Ϊsizeof(BVCU_ServerParam)*/
    int iSize;
    
    /*�û��Զ������ݡ�ͨ�����ڻص�֪ͨ*/
    void* pUserData;

    /*Server��ַ����������IP*/
    char szServerAddr[BVCU_MAX_HOST_NAME_LEN+1];
    
    /*Server�˿ں�*/
    int  iServerPort;
    
    /*Client ID��������Ӧ�ó������ͣ�"CU_"/"NRU_"/"PU_"�ȣ���ʼ��֮��Ĳ��ֽ�ֹ����'_')��������Ӧѡ��һ��ID���䷽ʽ������ʹÿ̨������ϵ�ID��ͬ�����Ϊ�գ����ɿ��ڲ�����ID*/
    char szClientID[BVCU_MAX_ID_LEN+1];

    /*Ӧ�ó������ơ������Ʊ�Server�˼�¼��Log��*/
    char szUserAgent[BVCU_MAX_NAME_LEN+1];

    /*��¼�û���*/
    char szUserName[BVCU_MAX_NAME_LEN+1];
    
    /*��¼����*/
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];
    
    /*CU��Server֮������ͨ��ʹ�õĴ����Э�����ͣ��μ�BVCU_PROTOTYPE_*��Ŀǰ��֧��TCP*/
    int iCmdProtoType;

    /*�����iTimeOutδ�յ���������Ϊʧ�ܣ���λ���롣����>0*/
    int iTimeOut;

    /*CU/NRU�յ�Server��Notify֪ͨ��Ļص�����
    ���أ������ݷ���ֵ�����Server�Ļ����
    */
    BVCU_Result (*OnNotify)(BVCU_HSession hSession, BVCU_NotifyMsgContent* pData);

    /*��Session��ص��¼�������BVCU_GetSessionInfo�����������BVCU_ServerParam������
    iEventCode:�¼��룬�μ�Session�¼�
     pParam: ÿ���¼���Ӧ�Ĳ������������Ͳο������¼����˵��*/
    void (*OnEvent)(BVCU_HSession hSession, int iEventCode, void* pParam);

    /*
    CU/NRU�յ���Control/Query���� 
    pCommand�����ڲ���һ��BVCU_Command����ָ�롣Ӧ�ó������������Ӧ����pCommand->OnEvent 
    ���أ�BVCU_RESULT_S_OK��ʾӦ�ó���Ҫ�����������ֵ��ʾӦ�ó�����Ը�����ɿ������δ���
    */
    BVCU_Result (*OnCommand)(BVCU_HSession hSession, BVCU_Command* pCommand);

    /*�����������ʼ��Ϊ0*/
    int iReserved[4];
}BVCU_ServerParam;


/*
Session��Ϣ��һ�ε�¼����һ��Session��
*/
typedef struct _BVCU_SessionInfo{
    /*������Session��Param*/
    BVCU_ServerParam stParam;

    /*������ID*/
    char szServerID[BVCU_MAX_ID_LEN+1];

    /*SmartEye����*/
    char szDomain[BVCU_MAX_SEDOMAIN_NAME_LEN+1];

    /*��������*/
    char szServerName[BVCU_MAX_NAME_LEN+1];

    /*Server ��������*/
    char szServerCodeName[BVCU_MAX_NAME_LEN+1];

    /*Server�汾��*/
    char szServerVersion[BVCU_MAX_NAME_LEN+1];

    /*CU����IP*/
    char szLocalIP[BVCU_MAX_HOST_NAME_LEN+1];
    
    /*CU��������˿�*/
    int iLocalPort;
    
    /*CU�Ƿ�����*/
    int iOnlineStatus;
    
    /*��¼ʱ�̣���1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
    SAV_TYPE_INT64 iLoginTime;
    
    /*���ε�¼����ʱ�䣬��λ΢��*/
    SAV_TYPE_INT64 iOnlineTime;
    
    /*CMS������û���ʶ*/
    int iApplierID; 
    
    /*�����������ʼ��Ϊ0*/
    int iReserved[3];
}BVCU_SessionInfo;


/*
����PU��ͨ��ý����Ϣ
*/
typedef struct _BVCU_PUOneChannelInfo{
    /*Channel Name*/
    char   szName[BVCU_MAX_NAME_LEN + 1];

    /*PUͨ����*/
    int    iChannelIndex;

    /*PUÿ��ͨ�������������򡣲μ�BVCU_MEDIADIR_* */
    char   iMediaDir;
    
    /*PUÿ��ͨ������̨������-1��ʾ����̨*/
    char   iPTZIndex;

    char   cReserved[2];//����
}BVCU_PUOneChannelInfo;

//ͨ����Ϣ���� BVCU_MSGCONTENT_TYPE_PU_CHANNELINFO��������֪ͨ��/BVCU_SUBMETHOD_GETPULIST����ȡPU�б�ʹ��
typedef struct _BVCU_PUChannelInfo{
    /*PU ID*/
    char  szPUID[BVCU_MAX_ID_LEN+1];
    
    /*PU Name*/
    char  szPUName[BVCU_MAX_NAME_LEN+1];
    
    /*pChannel�����Ա����(PUͨ����)��������֪ͨ��BVCU_SUBMETHOD_GETPULIST����Ч��������֪ͨ��������*/
    int   iChannelCount;
    
    /*PUͨ����Ϣ���顣������֪ͨ��BVCU_SUBMETHOD_GETPULIST����Ч��������֪ͨ��������*/
    BVCU_PUOneChannelInfo* pChannel;

    /*����״̬���μ�BVCU_ONLINE_STATUS_*����������֪ͨ�У�����iOnlineStatus�ж�������֪ͨ��������֪ͨ*/
    int   iOnlineStatus;
    
    //�����GPS��Ϣ������PUConfig.h��BVCU_PUCFG_DeviceInfo
    /*GPS�豸��Ŀ*/
    int iGPSCount;

    /*PUλ�ã�GPS����*/
    int  iLongitude; //���ȣ���������ֵ��������ֵ����λ1/10000000�ȡ�����180�Ȼ�С��-180�ȱ�ʾ��Чֵ
    int  iLatitude; //γ�ȣ���γ����ֵ����γ�Ǹ�ֵ����λ1/10000000�ȡ�����180�Ȼ�С��-180�ȱ�ʾ��Чֵ
}BVCU_PUChannelInfo;

/*
����PU�㲥��״̬
*/
typedef struct _BVCU_BroadcastStatus
{
    /*Ŀ��ID������PU ID*/
    char szID[BVCU_MAX_ID_LEN+1];

    /*PUͨ����*/
    int iChannelIndex;

    /*�㲥״̬���μ�BVCU_BROADCAST_STATUS_* */
    int   iBroadcastStatus;
}BVCU_BroadcastStatus;


/*�Ự��ء�һ���Ự��ָ���һ���豸��һ��ͨ������Ƶ���ݻ��߶Խ�*/

typedef struct _BVCU_DialogTarget{
    /*Ŀ��ID������PU ID,NRU ID*/
    char szID[BVCU_MAX_ID_LEN+1];
    
    /*Ŀ�����������豸����Ҫ�ţ�BVCU_SUBDEV_INDEXMAJOR_* */
    int iIndexMajor;

    /*Ŀ�����������豸�Ĵ�Ҫ�ţ�����PUͨ�����������š�����Ϊ-1��ʾ��Server���������ĸ�����
      bit 0��5��BVCU_ENCODERSTREAMTYPE_* 
      bit 6~31����BVCU_ENCODERSTREAMTYPE_�����Ĳ�����Ĭ��Ϊ0
      	��BVCU_ENCODERSTREAMTYPE_STORAGE/BVCU_ENCODERSTREAMTYPE_PREVIEW������Ϊ0����ʾδʹ��
      	��BVCU_ENCODERSTREAMTYPE_PICTURE��bit 6~9����������-1��������Ϊ0��ʾ����1�ţ���bit 10~15: ץ�ļ������λ�룬�������ֵΪ60��
    */
    int iIndexMinor;
    
}BVCU_DialogTarget;

typedef struct _BVCU_DialogParam
{
    /*���ṹ��Ĵ�С��������Ӧ��ʼ��Ϊsizeof(BVCU_DialogParam)*/
    int iSize;

    /*�û��Զ������ݡ�ͨ�����ڻص�֪ͨ*/
    void* pUserData;

    /*��¼Session*/
    BVCU_HSession hSession;
    
    /*�ỰĿ�����*/
    int iTargetCount;

    /*�ỰĿ�����顣pTarget�ڴ��ɵ����߷���/�ͷš�����BVCU_Dialog_Open/BVCU_Dialog_Updateʱ�� 
        SDK�ᱣ��pTarget�Ŀ���������BVCU_GetDialogInfoʱ��pTarget��ָ��SDK�ڲ��Ŀ�����������
        �����ԶԿ������κ��޸�*/
    const BVCU_DialogTarget* pTarget;
    
    /*�Ự������������*/
    int iAVStreamDir;

    /*CodecThread��������Ƶ���ݣ�������ɺ���á������߿��ԶԽ��������ݽ��и��ִ���������Ƶ���ݣ����������ݺ��޸ı��ݣ�
                                 ����pFrame��ppData����ָ��ָ���޸ĺ�ı��ݡ�
            �Դ�����(SAVCodec_Context.eMediaType==SAV_MEDIATYPE_DATA)���ӻ������õ����ݺ������ص�
    pCodec: Codec��Ϣ
    pFrame������Ƶ���ݣ�����õ���ԭʼý�����ݣ������ݣ����֡�������
        ���أ��Դ����������塣������Ƶ���ݣ�
                BVCU_RESULT_S_OK��pFrame����ʾ/���š�
        BVCU_RESULT_E_FAILED��pFrame������ʾ/���š�
    */
    BVCU_Result (*afterDecode)(BVCU_HDialog hDialog, SAVCodec_Context* pCodec, SAV_Frame* pFrame);
    
    /*VideoRenderThread/AudioRenderThread����ʾ/������һ����Ƶ����Ƶ֡����á��û������ڴ˴���������Ч�������ֵȡ�
    pCodec: Codec��Ϣ
    pFrame������õ���ԭʼý������
    ���أ�Ŀǰ�����Է���ֵ*/
    BVCU_Result (*afterRender)(BVCU_HDialog hDialog, SAVCodec_Context* pCodec,SAV_Frame* pFrame);

    /* 
    �¼��ص�������BVCU_GetDialogInfo�����������BVCU_DialogParam
    iEventCode:�¼��룬�μ�Dialog�¼�
     pParam: ÿ���¼���Ӧ�Ĳ������������Ͳο������¼����˵�������pParam��NULL����ʾ�޲�����
     */
    void (*OnEvent)(BVCU_HDialog hDialog, int iEventCode, void* pParam);

    /*����*/
    int iReserved[4];
}BVCU_DialogParam;


/*
���ƻỰ���������
*/
#define BVCU_NETWORK_DVSS_MIN 1
#define BVCU_NETWORK_DVSS_MAX 7
#define BVCU_NETWORK_DELAY_MAX 10000
typedef struct _BVCU_DialogControl_Network{
    /*�Ự�����iTimeOutδ�յ���������Ϊʧ�ܣ���λ���롣�������Ϊ0�������BVCU_ServerParam.iTimeout*/
    int iTimeOut;

    /*A/V���ݴӽ��յ���������������ӳ٣����ܳ���BVCU_NETWORK_DELAY_MAX����λ�����루�ο�ֵ��5000��*/
    int iDelayMax;
    
    /*A/V���ݴӽ��յ��������С������ӳ٣����ܳ���BVCU_NETWORK_DELAY_MAX����λ�����루�ο�ֵ��1000��*/
    int iDelayMin;
    
    /*�����ӳ���ƽ��ѡ��ȡֵ��ΧBVCU_NETWORK_DVSS_MIN��BVCU_NETWORK_DVSS_MAX��ԽС�򲥷��ӳ�ԽС����ƽ����Խ���򲥷�Խƽ�������ӳٱ�󣨲ο�ֵ��3����*/
    int iDelayVsSmooth;
}BVCU_DialogControlParam_Network;

/*
���ƻỰ����Ƶ��ʾ/��Ƶ�ط�
*/
#define BVCU_RENDER_NO_VIDEO NULL
#define BVCU_RENDER_NO_AUDIO -1

typedef struct _BVCU_DialogControl_Render{
    /*��ʾ���ھ��, BVCU_RENDER_NO_VIDEO��ʾ����ʾ�����Ҳ�Ҫִ����Ƶ���롣*/
    BVCU_HWND hWnd;
    
    /*��ʾ���Σ����ó�(0,0,0,0)��ʾ����ʾ����ִ����Ƶ����*/
    BVCU_Display_Rect rcDisplay;
    
    /*����������������Χ0��100.�����ΪBVCU_RENDER_NO_AUDIO ��ʾ��������Ƶ�����Ҳ�ִ����Ƶ����*/
    int  iPlackbackVolume;
    
    /*�ɼ�������������Χ0��100.�����ΪBVCU_RENDER_NO_AUDIO ��ʾ���ɼ���Ƶ*/
    int  iCaptureVolume;
    
    /*ʹ�ܻ��ֹ����Ƶͬ����0��ʹ�ܣ�1����ֹ*/
    int bDisableAVSync;
}BVCU_DialogControlParam_Render;

/*
���ƻỰ��¼��
*/
#define BVCU_STORAGE_MAX_FILELENINSEC 7200
typedef struct _BVCU_DialogControl_Storage{
    /*¼��·��*/
    char szFilePath[BVCU_MAX_FILE_NAME_LEN+1];
    
    /*ÿ��¼���ļ���ʱ�䳤�ȣ���λ�롣����Ϊ<=0��ʾֹͣ�洢�����ܳ���BVCU_STORAGE_MAX_FILELENINSEC*/
    int   iFileLenInSeconds;

    /*����*/
    int iReserved[2];
}BVCU_DialogControlParam_Storage;

/*
���ƻỰ
*/
typedef struct _BVCU_DialogControlParam{
    /*���ṹ��Ĵ�С��������Ӧ��ʼ��Ϊsizeof(BVCU_DialogControlParam)*/
    int iSize;

    BVCU_DialogControlParam_Network stNetwork;
    BVCU_DialogControlParam_Render  stRender;
    BVCU_DialogControlParam_Storage stStorage;
}BVCU_DialogControlParam;

/*
�Ự��Ϣ
*/
typedef struct _BVCU_DialogInfo{
    /*Dialog����*/
    BVCU_DialogParam stParam;
    BVCU_DialogControlParam stControlParam;
    
    /*ý����Ϣ*/
    SAVCodec_Context stVideoCodecRemote;
    SAVCodec_Context stAudioCodecRemote;
    SAVCodec_Context stAudioCodecLocal;
    
    /*�Ự��ʼʱ�̣���1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
    SAV_TYPE_INT64 iCreateTime;
    
    /*�Ự����ʱ�䣬��λ΢��*/
    SAV_TYPE_INT64 iOnlineTime;
    
    /*�Ự�Ƿ�����*/
    int iOnlineStatus;

    /*ͳ����Ϣ*/
    
    /*�յ����ܰ���*/
    SAV_TYPE_INT64 iVideoTotalPackets;
    SAV_TYPE_INT64 iAudioTotalPackets;
    
    /*�յ�����֡��*/
    SAV_TYPE_INT64 iVideoTotalFrames;
    SAV_TYPE_INT64 iAudioTotalFrames;
    
    /*���粿�ֳ�ʱ��ͳ������*/
    int iVideoLostRateLongTerm;/*����(��֡)�ʣ���λ1/10000*/
    int iAudioLostRateLongTerm;/*����(��֡)�ʣ���λ1/10000*/
    int iVideoRecvFPSLongTerm;/*�������֡�ʣ���λ1/10000֡ÿ��*/
    int iVideoKbpsLongTerm;/*��Ƶ�������ʣ���λ Kbits/second*/
    int iAudioKbpsLongTerm;/*��Ƶ�������ʣ���λ Kbits/second*/
    

    /*���粿�ֶ�ʱ��ʱ��ͳ������*/
    int iVideoLostRateShortTerm;
    int iAudioLostRateShortTerm;
    int iVideoRecvFPSShortTerm;
    int iVideoKbpsShortTerm;
    int iAudioKbpsShortTerm;

    /*VideoRender��ʾ֡��*/
    int iVideoRenderFPS;/*֡�ʣ���λ1/10000֡ÿ��*/
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

/*=====================��������===================================*/
//��������
enum {
    //���鷢������,ռ��4bit
    BVCU_CONF_MODE_SPEAK_DISCUSSIONGROUP = (0<<0),//������ģʽ�����ɷ��ԣ��������롣���Զ����ͬʱ���ԡ�
    BVCU_CONF_MODE_SPEAK_CHAIRMAN = (1<<0),//�ݽ���ѵģʽ�������������룬������chairman�������ԡ�ͬһʱ��ֻ����һ���˷���

    //�����������,ռ��3bit
    BVCU_CONF_MODE_JOIN_INVITE = (0<<4),//�����������롣Opener���˽����������߿��Խ��ܻ�ܾ���
    BVCU_CONF_MODE_JOIN_PASSWORD = (1<<4),//�������룬����Ҫ��������
    BVCU_CONF_MODE_JOIN_FREE = (2<<4),//�������룬����ʾ����
};

#define BVCU_CONF_GetModeSpeak(ConfMode) (((unsigned int)(ConfMode)) & (0x0F))
#define BVCU_CONF_GetModeJoin(ConfMode) (((unsigned int)(ConfMode)) & (0x070))
#define BVCU_CONF_SetModeSpeak(ConfMode,newMode) ((((unsigned int)(ConfMode)) & (~0x0F)) | newMode)
#define BVCU_CONF_SetModeJoin(ConfMode,newMode) ((((unsigned int)(ConfMode)) & (~0x070)) | newMode)


//����״̬
enum{
    BVCU_CONF_STATUS_STOPPED = 0,
    BVCU_CONF_STATUS_STARTED,
};

//participator�Ļ���Ȩ�ޡ����鴴����creatorӵ������Ȩ��
enum{
    BVCU_CONF_PARTICIPATOR_POWER_ADMIN = (1<<0),//������������ɴ�/�رջ��顢���/ɾ�������Ա�ȡ�һ���������������һ����������Ȩ��ADMIN��
    //������һ��ADMIN�˳����飬���鱻�Զ��رա�
    BVCU_CONF_PARTICIPATOR_POWER_MODETATOR = (1<<1),//���Թ���������/��ֹĳ����Ա����
};

//participator�Ļ���״̬
enum {
    BVCU_CONF_PARTICIPATOR_STATUS_UNKNOWN = -1,
    BVCU_CONF_PARTICIPATOR_STATUS_OFFLINE = BVCU_ONLINE_STATUS_OFFLINE,//����
    BVCU_CONF_PARTICIPATOR_STATUS_ONLINE_LEAVED,//���ߣ�����ʱ�뿪���顣���������participator���ɷ��ԣ�Ҳ�����������˵ķ���
    BVCU_CONF_PARTICIPATOR_STATUS_ONLINE_INSEAT,//���ߣ��������ڲ������
    BVCU_CONF_PARTICIPATOR_STATUS_ONLINE_SPEAKING,//���ߣ��������ڷ��ԡ�ֻ��BVCU_CONF_MODE_SPEAK_CHAIRMAN��Ч
};

//���������Ϣ
typedef struct _BVCU_Conf_BaseInfo{
    char szName[BVCU_MAX_NAME_LEN+1];//���֡���������Ϊ�ǿա�
    char szID[BVCU_MAX_ID_LEN+1];//����ID��Createʱ����Ϊ�գ�����CMS���õ�ID�����������������
    int  iMode;//BVCU_CONF_MODE_*����������
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//���룬����BVCU_CONF_NODE_JOIN_PASSWORD��BVCU_SUBMETHOD_CONF_CREATE���������壬�����������Ϊ��
    int  iConfStatus;//����״̬,BVCU_CONF_STATUS_*
}BVCU_Conf_BaseInfo;


//���������
typedef struct _BVCU_Conf_Participator_Info{
    char szID[BVCU_MAX_ID_LEN+1];//participator ID��ͨ����PU/CU ID
    char szUserName[BVCU_MAX_NAME_LEN+1];//��¼�û�����Ŀǰ����CU�����塣PU����Ϊ��
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1];//��ַ��Ϣ��CU�������á�PU��ѡ
    char szAliasName[BVCU_MAX_NAME_LEN+1];//������ʹ�õı���������Ϊ�ա�����BVCU_SUBMETHOD_CONF_PARTICIPATOR_INFO��CMS���������
    int  iApplierID;//CMS������û���ʶ�����CU������ʱ(BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD),�������ø�ֵ���������������Ϊ��Чֵ��Ӧ����Ϊ0
    int  iPID;//�û�����򱻼������ʱ��CMS�������participator��participator ID�����Ըû�����Ч��0��ʾ��Чֵ��
                //CU participatorӦ�����ֵ���ڶ����������쳣����£���������ʹ��RETURN����ʱ��CMS���ݵ�¼�û�����iPID�������ȷ�����û��������顣
    int  iAllowedMediaDir;//BVCU_MEDIADIR_*������CONF_PARTICIPATOR_ADD/MODIFY���������壬��������������
    int  iStatus;//��ǰ״̬��BVCU_CONF_PARTICIPATOR_STATUS_*����CMS���Ϊ�������ֵ��CU������дΪBVCU_CONF_PARTICIPATOR_STATUS_UNKNOWN
    int  iPower;//Ȩ�ޣ�BVCU_CONF_PARTICIPATOR_POWER_*����Ϊcontrol��������ʱ��ֻ��BVCU_CONF_PARTICIPATOR_POWER_ADMINȨ���ߵ�����ֵ�����á�
    int  iVolume;//��ǰparticipator������szID participator������ȡֵ��Χ0~128��128��ʾԭʼ������Ĭ��ֵӦ����Ϊ128��
    //����BVCU_SUBMETHOD_CONF_PARTICIPATOR_VOLUME/BVCU_SUBMETHOD_CONF_INFO����������
    int  iReserved[2];//��������������Ϊ0
}BVCU_Conf_Participator_Info;

#define BVCU_CONF_MAX_PARTICIPATOR_ONETIME 1024 //һ��CONF_PARTICIPATOR_ADD/REMOVE����������ӵ�participator�����Ŀ
//CMS��"��ӻ�������ߡ��������
typedef struct _BVCU_Conf_Participator_AddResult{
    int iResultBits[BVCU_CONF_MAX_PARTICIPATOR_ONETIME/32];//����BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD����������˳��
                                                             //�ɹ���ӵ�λ����Ϊ1��ʧ�ܵ�λ����Ϊ0������˳��i=>bitλӳ���ϵ����[i/32]��int��[31-(i&31)]λ
}BVCU_Conf_Participator_AddResult;

//������Ϣ
typedef struct _BVCU_Conf_Info{
    BVCU_Conf_BaseInfo baseInfo;//���������Ϣ
    int iParticipatorCount;//��������߸���
    BVCU_Conf_Participator_Info* pParticipators;//����������б�
    BVCU_Conf_Participator_Info* pCreator;//���鴴���ߣ���������ڲ������б���
    int  iReserved[2];//��������������Ϊ0
}BVCU_Conf_Info;

//����������
typedef struct _BVCU_Conf_Participator_Join{
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//���룬����BVCU_CONF_NODE_JOIN_PASSWORD��BVCU_SUBMETHOD_CONF_CREATE���������壬�����������Ϊ��
    char szAliasName[BVCU_MAX_NAME_LEN+1];//������ʹ�õı���������Ϊ�ա�
}BVCU_Conf_Participator_Join;
/*================================================================*/
/**
*��ʼ��BVCU�⣬ֻ����Ӧ�ó�������ʱ����һ�Ρ��κ�����BVCU�⺯��ֻ���� 
 BVCU_Initialize�ɹ���ſ��Ե���
*/
LIBBVCU_API BVCU_Result BVCU_Initialize(const BVCU_GlobalParam* pParam);

/**
*ֹͣʹ��BVCU��
*/
LIBBVCU_API BVCU_Result BVCU_Finish();


/**
 * 
 * ������־�������
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
ע�⣺��¼��ɺ�Ӧ�ó�����ʱ�����յ�BVCU_EVENT_SESSION_CLOSE�¼��ص���
�ص�֮��Session��SDK�ݻ٣�BVCU_HSession�����Чֵ
*/

/**
*��¼Server���ú������첽�ġ������¼�ɹ����ڷ���ǰ���߷��غ�����OnEvent�ص���
* @param[out] phSession: ���ص�¼Session
* @param[in] pParam: Server��Ϣ
* @return: ��������ֵ
*        BVCU_RESULT_S_OK: ��ȷ��ɡ����ͨ��OnEvent֪ͨ�����ߡ�
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_Login(BVCU_HSession* phSession, BVCU_ServerParam* pParam);

/**
*��õ�¼Session�����Ϣ
*@param[in] hSession: BVCU_Login���صĵ�¼Session.
*@param[out] pInfo: BVCU_SessionInfo 
*@return: BVCU_Result
*/
LIBBVCU_API BVCU_Result BVCU_GetSessionInfo(BVCU_HSession hSession, BVCU_SessionInfo* pInfo);

/**
* �˳���¼���ú������첽�ģ��ڷ���ǰ���߷��غ�����OnEvent�ص���
* ע�⣺(1)�ú���������BVCU_Login��¼�ɹ���BVCU_Login��OnEvent�ص�����������֮��ſ��Ե��� 
*  (2)�������κ�OnEvent/OnNotify�е���BVCU_Logout
* @param[in] hSession: BVCU_Login���صĵ�¼Session.
* @return: ��������ֵ
*    BVCU_RESULT_S_OK: ��ȷ��ɡ����ͨ��OnEvent֪ͨ�����ߡ�
*    BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_Logout(BVCU_HSession hSession);

/*=======================command=========================================*/
/**
* CU��������ú������첽�ģ�������ɺ󴥷�BVCU_Command.OnEvent�ص�֪ͨ��
* @param[in] hSession: BVCU_Login���صĵ�¼Session.
* @return: ��������ֵ
*        BVCU_RESULT_S_OK: ������ȷ��ɡ����ͨ��OnEvent֪ͨ�����ߡ�
*        BVCU_RESULT_E_NOTEXIST: ��¼Session�����ڣ���δ��¼
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_SendCmd(BVCU_HSession hSession, BVCU_Command* pCommand);

/*=======================dialog=========================================*/
/*
ע�⣺�Ự�����У���ʱ�����յ�BVCU_EVENT_DIALOG_CLOSE�¼��ص���
�ص�֮�󣬻Ự��SDK�ݻ٣�BVCU_HDialog�����Чֵ
*/

/**
* �����Ự���ú������첽�ġ���������Ự�ɹ����ڷ���ǰ���߷��غ�����OnEvent�ص�������
* �¼�����BVCU_EVENT_DIALOG_OPEN������¼�������iResult��ʧ�ܴ��룬��Ự����ʧ�ܣ������߲��ص���BVCU_Dialog_Close
* @param[out] phDialog: ���ػỰ���.
* @param[in] pParam: �Ự������
* @param[in] pControl: ���Ʋ�����
* @return: ��������ֵ 
*        BVCU_RESULT_S_OK: ������ȷ��ɡ����ͨ��OnEvent֪ͨ�����ߡ�
*        BVCU_RESULT_E_UNSUPPORTED: ��֧�ֵĲ����������ڲ�֧�ֶԽ���ͨ����Ҫ��Խ�
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Open(BVCU_HDialog* phDialog, BVCU_DialogParam* pParam, BVCU_DialogControlParam* pControl);

/**
*��ûỰ�����Ϣ
*@param[in] hDialog: BVCU_Dialog_Open���ص�hDialog. 
*@param[out] pInfo: BVCU_DialogInfo 
*@return: BVCU_Result
*/
LIBBVCU_API BVCU_Result BVCU_GetDialogInfo(BVCU_HDialog hDialog, BVCU_DialogInfo* pInfo);

/**
* �����ѽ����ĻỰ����Ҫ��ServerͨѶ�����ѽ����ĻỰ�������޸�pParam->iAVStreamDir�� ��ֻ��һ��Target��Dialog��
* �������޸�Target��iMajorIndex������ʵ��ͨ���л��� 
* �ú������첽�ġ� 
* �ú������ܷ����첽BVCU_EVENT_DIALOG_UPDATE�¼���Я�����״̬�롣������״̬��ʧ�ܣ����ܻ������������� 
* (1)Dialog��Ȼ����Update֮ǰ�Ĵ�״̬������ֻ����/��Ƶ=>����Ƶͬ��ʧ��
* (2)Dialog�رգ����Żᷢ��BVCU_EVENT_DIALOG_CLOSE�¼�������ֻ����Ƶ=>ֻ����Ƶ���෴������iMajorIndex��
* @param[in] hDialog: BVCU_Dialog_Open���ص�hDialog.
* @param[in] pParam: �Ự������
* @return: ��������ֵ 
*        BVCU_RESULT_S_OK:������ȷ��ɡ����ͨ��OnEvent֪ͨ�����ߣ��¼�����BVCU_EVENT_DIALOG_UPDATE/BVCU_EVENT_DIALOG_CLOSE��
*        BVCU_RESULT_E_NOTEXIST: �Ự������ 
*        BVCU_RESULT_E_BUSY:��һ�εĻỰ������δ��� 
*        BVCU_RESULT_E_UNSUPPORTED:��֧�ֵĲ����������ڲ�֧�ֶԽ���ͨ����Ҫ��Խ� BVCU_RESULT_E_FAILED�� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Update(BVCU_HDialog hDialog, BVCU_DialogParam* pParam);

/**
*����ץȡ�Ự�н��յ���һ֡��Ƶ����Ϊͼ���ļ���Ŀǰ��֧��JPG��ʽ
* @param[in] hDialog: BVCU_Dialog_Open���ص�hDialog.
* @param[in] szFileName: ץȡ�ļ�����
* @param[in] iQuality:
*       JPGѹ��������ȡֵ��Χ1��100����ֵԽ��ѹ�����ͼ������Խ�ã����ļ���Խ�󡣽���������С��80
* @return: ��������ֵ
*        BVCU_RESULT_S_OK: �ɹ�
*        BVCU_RESULT_E_FAILED�� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Snapshot(BVCU_HDialog hDialog, const char* szFileName, int iQuality);

/**
* ���ĻỰ�ı������ã���������/�洢/�طŵȡ��˺�������Ҫ��ServerͨѶ��
* @param[in] hDialog: BVCU_Dialog_Open���ص�Dialog���.
* @param[in] pParam: ���Ʋ�����
* @return: ��������ֵ
*        BVCU_RESULT_S_OK: �ɹ�
*        BVCU_RESULT_E_NOTEXIST: �Ự������
*        BVCU_RESULT_E_UNSUPPORTED: ��֧�ֵĲ���
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Control(BVCU_HDialog hDialog, BVCU_DialogControlParam* pParam);

/**
* �رջỰ���ú������첽�ģ��ڷ���ǰ���߷��غ�����OnEvent�ص�������
* ע�⣺(1)�ú���������BVCU_Dialog_Open�ɹ���BVCU_Dialog_Open��OnEvent�ص�����������֮��ſ��Ե��� 
* (2)�������κ�OnEvent/OnNotify�е���BVCU_Dialog_Close 
* @param[in] hDialog: BVCU_Dialog_Open���ص�Dialog���.
* @return: ��������ֵ
*        BVCU_RESULT_S_OK: ��ȷ��ɡ����ͨ��OnEvent֪ͨ�����ߡ� 
*        BVCU_RESULT_S_IGNORE:  �Ự������
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_Dialog_Close(BVCU_HDialog hDialog);

/*================================================================*/

//==========================FTP Client��ؽӿ�===============================
typedef void* BVCU_FTP_HSession;
typedef void* BVCU_FTP_HTransfer;

/*
    ��¼��������Ϣ������ʹ��FTP Server ID����IP/Port��¼�����BVCU_FTP_ServerParam��������ID�������IP/Port
*/
typedef struct _BVCU_FTP_ServerParam{        
    int iSize;/*���ṹ��Ĵ�С��������Ӧ��ʼ��Ϊsizeof(BVCU_FTP_ServerParam)*/
    BVCU_HSession hSession;/*��¼CMS��Session������������Ϊ0����ʾʹ��szServerAddr/iServerPortֱ������FTP Server��
                             �����0����ʾʹ��szID����FTP Server*/
    char szID[BVCU_MAX_ID_LEN+1];//FTP Server ID
    
    char szServerAddr[BVCU_MAX_HOST_NAME_LEN+1];//FTP Server��ַ����������IP
    int  iServerPort;    //FTP Server�˿ں�        
    char szUserName[BVCU_MAX_NAME_LEN+1];//��¼�û�����Ŀǰû��ʹ�ã���������Ϊ��
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//��¼���룬Ŀǰû��ʹ�ã���������Ϊ��
    
    int iTimeOut;//����iTimeOutδ�յ���������Ϊʧ�ܣ���λ���롣����>0
    int iKeepAliveInterval;//����KeepAlive���ļ������λ���롣����<=0��ʾ������
    /*�¼���
     iEventCode:�¼��룬�μ�Session�¼�BVCU_EVENT_SESSION_*
     pParam: ÿ���¼���Ӧ�Ĳ������������Ͳο������¼����˵��*/
    void (*OnEvent)(BVCU_FTP_HSession hSession, int iEventCode, void* pParam);    
    
    int iReserved[2];//����������Ϊ0
}BVCU_FTP_ServerParam;


/*
FTP_Session��Ϣ��һ�ε�¼����һ��FTP Session
*/
enum{
    BVCU_FTP_SERVER_CAP_RESUMEBREAKTRANSFER = (1<<0),//�ϵ�����
    BVCU_FTP_SERVER_CAP_UPLOAD              = (1<<1),//�����ϴ�
    BVCU_FTP_SERVER_CAP_OVERWRITE           = (1<<2),//�ϴ������Ǿ��ļ�
};
typedef struct _BVCU_FTP_SessionInfo{    
    BVCU_FTP_ServerParam stParam;//������Session��Param
    unsigned int bCapability;//֧�ֵĹ��ܡ���BVCU_FTP_SERVER_CAP_*�����
    unsigned int iMaxSession;//��Server֧�ֵĲ���Session�����Ŀ��0��ʾ������
    unsigned int iMaxTransfer;//��Server֧�ֵĲ������������Ŀ��0��ʾ������
    unsigned int iMaxTransferPerSession;//ÿ��Session֧�ֵĲ������������Ŀ��0��ʾ������
    unsigned int iBandwidthLimit;//�������ơ���λKbytes��0��ʾ������
    int iTimeIdle;//�����Idleʱ�䳤�ȣ������ó�����û���յ�Client���κ�������server�����Ͽ����ӡ���λ�롣-1��ʾ������
    
    /*�����������ʼ��Ϊ0*/
    int iReserved[4];
}BVCU_FTP_SessionInfo;

//�������
enum {
    BVCU_FTP_RULE_SAMEFILENAME_OVERWRITE = 0,
    BVCU_FTP_RULE_SAMEFILENAME_SKIP,
    BVCU_FTP_RULE_SAMEFILENAME_RESUME,//������е�ͬ���ļ����Ƚ�С������
};

enum {
    BVCU_FTP_TRANSFER_EVENT_CLOSE = 0,  /* Transfer �رա��¼�������BVCU_Event_Common*/
    BVCU_FTP_TRANSFER_EVENT_PROGRESS = 1, /* Transfer ���ȡ��¼��������������� int[2]���ֱ�Ϊ�Ѵ���KB����KB*/
};

typedef struct _BVCU_FTP_TransferParam{
    int iSize;/*���ṹ��Ĵ�С��������Ӧ��ʼ��Ϊsizeof(BVCU_FTP_TransferParam)*/
    BVCU_FTP_HSession hSession;//BVCU_FTP_Login���صľ��
    char szRemoteFileName[BVCU_MAX_FILE_NAME_LEN+1];//Զ���ļ���
    char* szLocalFileName;//�����ļ������OnData����!=NULL������Ը�ֵ��������ڲ������ݱ��浽szLocalFileName�����ı����ļ���
    int iRuleSameFileName;//����ͬ���ļ��Ĵ������BVCU_FTP_RULE_SAMEFILENAME_*
    int iTimeout;//���ӳ�ʱ
    int bUpload;//0-���أ�1-�ϴ�
    void* pUserData;//�Զ�������
    
    /*�ص�������
    hTransfer:��������
    pUserData�����ṹ���е�pUserData
    pBuffer�����ڲ���������
            upload��Ӧ�ó���Ӧ��pBufferд�벻����iSizeBytes�ֽڵ����ݡ�
            download��Ӧ�ó���Ӧ��pBuffer�е����ݱ��棬����iSizeBytes
    iSizeBytes: 
        iSizeBytes==0,���ʾ�������
        iSizeBytes > 0����upload��ʾӦ�ó������ṩ�����ݳ��ȣ���download��ʾӦ�ó��������ѵ����ݳ���
    
    ����ֵ��upload��ʵ��д��pBuffer�����ݳ��ȣ��������ֵ<iSizeBytes������Ϊ�����һ���ϴ����ݰ���ֹͣ����
            download��ʵ�ʶ�ȡpBuffer�����ݳ��ȣ��������ֵ<iSizeBytes������Ϊ����ֹͣ����
    */
    int (*OnData)(BVCU_FTP_HTransfer hTransfer, void* pUserData, void* pBuffer, int iSizeBytes);
    /* iEventCode: �¼��룬�μ�BVCU_FTP_TRANSFER_EVENT_*
     * pParam: ÿ���¼���Ӧ�Ĳ������������Ͳο������¼����˵��
     */
    void (*OnEvent)(BVCU_FTP_HTransfer hTransfer, int iEventCode, void* pParam, void* pUserData);
    int iReserved[2];
}BVCU_FTP_TransferParam;

//������Ʋ���
typedef struct _BVCU_FTP_TransferControlParam{    
    int iSize;/*���ṹ��Ĵ�С��������Ӧ��ʼ��Ϊsizeof(BVCU_FTP_TransferControlParam)*/
    int iBufferSize;//���ڲ�ʹ�õĴ��仺������С����λbyte�����鲻С��16K
    int iBufferCount;//���ڲ�ʹ�õĴ��仺��������������>=2
    unsigned int iBandwidthLimit;//�������ơ���λKbytes��0��ʾ������
    int iResumeLimit;//���������������ƣ������趨����ֹͣ���䡣-1��ʾ�����ơ�
    int iReserved[2];
}BVCU_FTP_TransferControlParam;

/*
������Ϣ
*/
typedef struct _BVCU_FTP_TransferInfo{
    /*�������*/
    BVCU_FTP_TransferParam stParam;
    BVCU_FTP_TransferControlParam stControlParam;
        
    /*Transfer��ʼʱ�̣���1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
    SAV_TYPE_INT64 iCreateTime;
    
    /*Transfer����ʱ�䣬��λ΢��*/
    SAV_TYPE_INT64 iOnlineTime;     
    
    /*�Ѿ��յ����ֽ���*/
    int iReceivedKb;
    /*���ֽ���*/
    int iTotalKb;
    
    int iSpeedKBpsLongTerm;/*��ʱ�䴫�����ʣ���λ KBytes/second*/
    int iSpeedKBpsShortTerm;/*��ʱ�䴫�����ʣ���λ KBytes/second*/
    
    int iResumeCount;//�ѽ��еĶ�����������
    int iReseved;
}BVCU_FTP_TransferInfo;

//FTP����
enum {
    BVCU_FTP_METHOD_SEARCH_RECORDFILE = 1,//����¼���ļ�����������:BVCU_FTP_RecordFileFilter; �������:BVCU_FTP_RecordFileInfo
    BVCU_FTP_METHOD_SEARCH_FILE,//������ͨ�ļ�����������:BVCU_FTP_FileFilter; �������:BVCU_FTP_FileInfo
    BVCU_FTP_METHOD_DEL_RECORDFILE,//ɾ��¼���ļ�����������:BVCU_FTP_RecordFileFilter; �������:��
    BVCU_FTP_METHOD_DEL_FILE,//ɾ����ͨ�ļ���Ŀ¼����������:BVCU_FTP_FileFilter; �������:��
    BVCU_FTP_METHOD_CD,//�л�Ŀ¼����������:�䳤�ַ�����; �������:��
    BVCU_FTP_METHOD_PWD,//��ȡ��ǰĿ¼����������:��; �������:�䳤�ַ�����
    BVCU_FTP_METHOD_MKD,//����Ŀ¼����������:�䳤�ַ�����; �������:��
};
typedef struct _BVCU_FTP_Command BVCU_FTP_Command;
struct _BVCU_FTP_Command{    
    int iSize;/*���ṹ��Ĵ�С��������Ӧ��ʼ��Ϊsizeof(BVCU_Command)*/
    
    /*�û��Զ������ݡ�ͨ�����ڻص�֪ͨ��Ӧ�ó���/������øó�Ա�����ֲ�ͬ������*/
    void* pUserData;

    /*�������ͣ�BVCU_FTP_METHOD_*��������BVCU_CmdMsgContent.pData���� */
    int iMethod;    
    
    /*�����iTimeOutδ�յ���������Ϊʧ�ܣ���λ���롣�������Ϊ0�������BVCU_ServerParam.iTimeout*/
    int iTimeOut;

    /*�����*/
    BVCU_CmdMsgContent stMsgContent;

    /*�¼���
    pCommand:������ָ�롣ע���ָ��ָ�����SDK�ڲ�ά����һ��BVCU_FTP_Commandǳ����
    iEventCode:�¼��룬�μ�BVCU_EVENT_SESSION_CMD_*��ĿǰiEventCode���ǵ���BVCU_EVENT_SESSION_CMD_COMPLETE
    pParam: ÿ���¼���Ӧ�Ĳ������������Ͳο������¼����˵��*/
    void (*OnEvent)(BVCU_FTP_HSession hSession, BVCU_FTP_Command* pCommand, int iEventCode, void* pParam);

    /*����*/
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
//¼���ļ�����
typedef struct _BVCU_FTP_RecordFileFilter{
    int iValidIndex;//BVCU_FTP_FILTER_INDEX_����ϡ���ʾ��Ӧ����Ч��Ա
    char szPUID[BVCU_MAX_ID_LEN+1];//PU ID
    char szPUName[BVCU_MAX_NAME_LEN+1];//PU Name
    int  iChannelIndex;//Channel��
    char szChannelName[BVCU_MAX_NAME_LEN+1];//Channel Name    
    SAV_TYPE_INT64 iTimeBegin; /*¼���ļ���ʼʱ�̣���1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
    SAV_TYPE_INT64 iTimeEnd;   /*¼���ļ�����ʱ�̣���1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
    SAV_TYPE_INT64 iFileSizeMin; //�ļ���С������
    SAV_TYPE_INT64 iFileSizeMax; //�ļ���С������
    int  iRecordType;//¼��ԭ��BVCU_STORAGE_RECORDTYPE_*
    int iReserved[1];//����������Ϊ0
}BVCU_FTP_RecordFileFilter;

//¼���ļ���Ϣ
typedef struct _BVCU_FTP_RecordFileInfo{
    char szFilePath[BVCU_MAX_FILE_NAME_LEN+1];//�ļ�·��
    char szFileName[BVCU_MAX_FILE_NAME_LEN+1];//�ļ���
    char szPUID[BVCU_MAX_ID_LEN+1];//PU ID
    char szPUName[BVCU_MAX_NAME_LEN+1];//PU Name
    int  iChannelIndex;//Channel��
    char szChannelName[BVCU_MAX_NAME_LEN+1];//Channel Name
    int  iRecordType;//¼��ԭ��BVCU_STORAGE_RECORDTYPE_*    
    SAV_TYPE_INT64 iTimeBegin; /*¼���ļ���ʼʱ�̣���1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
    SAV_TYPE_INT64 iTimeEnd;   /*¼���ļ�����ʱ�̣���1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
    SAV_TYPE_INT64 iFileSize; //�ļ���С����λ�ֽ�
    int iReserved[2];//����������Ϊ0
}BVCU_FTP_RecordFileInfo;

//��ͨ�ļ�����
enum{
    BVCU_FTP_PATTERNTYPE_FILE = (1<<0),//�ļ�
    BVCU_FTP_PATTERNTYPE_DIRECTORY = (1<<1),//Ŀ¼
};
/*
 * �����ļ�ʱͨ��iPatternTypeָ�����ͣ�����ͬʱ���ҷ����������ļ���Ŀ¼�����������
 * �����������򷵻ص�ǰĿ¼�������ļ���Ŀ¼�б�
 */
typedef struct _BVCU_FTP_FileFilter{
    int iValidIndex;//BVCU_FTP_FILTER_INDEX_����ϡ���ʾ��Ӧ����Ч��Ա
    char* szPattern;//�ļ���ģ��
    int iPatternType;//�ļ�����
    SAV_TYPE_INT64 iTimeBegin; /*¼���ļ���ʼʱ�̣���1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
    SAV_TYPE_INT64 iTimeEnd;   /*¼���ļ�����ʱ�̣���1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/
    SAV_TYPE_INT64 iFileSizeMin; //�ļ���С������
    SAV_TYPE_INT64 iFileSizeMax; //�ļ���С������
    int iReserved[2];//����������Ϊ0
}BVCU_FTP_FileFilter;

//��ͨ�ļ���Ϣ
typedef struct _BVCU_FTP_FileInfo{
    char szFilePath[BVCU_MAX_FILE_NAME_LEN+1];//�ļ�·��
    char szFileName[BVCU_MAX_FILE_NAME_LEN+1];//�ļ���    
    SAV_TYPE_INT64 iTime; /*�ļ��޸�ʱ�䣬��1970-01-01 00:00:00 +0000 (UTC)��ʼ��΢����*/    
    SAV_TYPE_INT64 iFileSize; //�ļ���С����λ�ֽ�
    int iReserved[2];//����������Ϊ0
}BVCU_FTP_FileInfo;

/*
ע�⣺��¼��ɺ�Ӧ�ó�����ʱ�����յ�BVCU_EVENT_SESSION_CLOSE�¼��ص����ص�֮��Session��SDK�ݻ٣�BVCU_FTP_HSession�����Чֵ
*/

/**
*��¼Server���ú������첽�ġ������¼�ɹ����ڷ���ǰ���߷��غ�����OnEvent�ص���
* @param[out] phSession: ���ص�¼Session
* @param[in] pParam: Server��Ϣ
* @return: ��������ֵ
*        BVCU_RESULT_S_OK: ��ȷ��ɡ����ͨ��OnEvent֪ͨ�����ߡ�
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Login(BVCU_FTP_HSession* phSession, BVCU_FTP_ServerParam* pParam);

/**
*��õ�¼Session�����Ϣ
*@param[in] hSession: BVCU_FTP_Login���صĵ�¼Session.
*@param[out] pInfo: BVCU_FTP_SessionInfo 
*@return: BVCU_Result
*/
LIBBVCU_API BVCU_Result BVCU_FTP_GetSessionInfo(BVCU_FTP_HSession hSession, BVCU_FTP_SessionInfo* pInfo);

/**
* �˳���¼���ú������첽�ģ��ڷ���ǰ���߷��غ�����OnEvent�ص���
* ע�⣺(1)�ú���������BVCU_FTP_Login��¼�ɹ���BVCU_FTP_Login��OnEvent�ص�����������֮��ſ��Ե��� 
*  (2)�������κ�OnEvent/OnNotify�е���BVCU_FTP_Logout
* @param[in] hSession: BVCU_FTP_Login���صĵ�¼Session.
* @return: ��������ֵ
*        BVCU_RESULT_S_OK: ��ȷ��ɡ����ͨ��OnEvent֪ͨ�����ߡ�
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Logout(BVCU_FTP_HSession hSession);

/**
* CU��������ú������첽�ģ�������ɺ󴥷�BVCU_FTP_Command.OnEvent�ص�֪ͨ��
* @param[in] hSession: BVCU_FTP_Login���صĵ�¼Session.
* @return: ��������ֵ
*       BVCU_RESULT_S_OK: ������ȷ��ɡ����ͨ��OnEvent֪ͨ�����ߡ�
*      BVCU_RESULT_E_NOTEXIST: ��¼Session�����ڣ���δ��¼
*      BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_FTP_SendCommand(BVCU_FTP_HSession hSession, BVCU_FTP_Command* pCommand);

/**
* �������䡣�ú������첽�ġ���������ɹ����ڷ���ǰ���߷��غ�����OnData�ص�������
* @param[out] phTransfer: ���ؾ��.
* @param[in] pParam: ���������
* @param[in] pControlParam: ���Ʋ�����
* @return: ��������ֵ 
*     BVCU_RESULT_S_OK: ������ȷ��ɡ�
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
/*
ע�⣺�������/�ϴ�������ɻ��߷����쳣����������һ��OnData�ص����Զ�����Transfer�������������Ӧ�ó����������BVCU_FTP_Transer_Close
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Transer_Open(BVCU_FTP_HTransfer* phTransfer, BVCU_FTP_TransferParam* pParam, BVCU_FTP_TransferControlParam* pControlParam);

/**
*��ûỰ�����Ϣ��������Transfer�������ڵ���
* @param[in] hTransfer: BVCU_FTP_Transer_Open���ص�hTransfer. 
* @param[out] pInfo: BVCU_FTP_TransferInfo 
* @return: BVCU_Result
*/
LIBBVCU_API BVCU_Result BVCU_FTP_GetTransferInfo(BVCU_FTP_HTransfer hTransfer, BVCU_FTP_TransferInfo* pInfo);

/**
* ����Transfer�ı������ã��˺�������Ҫ��ServerͨѶ��
* @param[in] hTransfer: BVCU_FTP_Transer_Open���ص�hTransfer. 
* @param[in] pParam: ���Ʋ�����
* @return: ��������ֵ
*        BVCU_RESULT_S_OK: �ɹ�
*        BVCU_RESULT_E_NOTEXIST: Transfer������
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Transer_Control(BVCU_FTP_HTransfer hTransfer, BVCU_FTP_TransferControlParam* pControlParam);

/**
* �رմ��䡣�ú������첽�ģ��ڷ���ǰ���߷��غ�����OnData�ص�������
* ע�⣺ (1)������OnData�ص��е���BVCU_FTP_Transer_Close 
* @param[in] hTransfer: BVCU_FTP_Login���صĵ�¼Session.
* @return: ��������ֵ
*        BVCU_RESULT_S_OK: ��ȷ��ɡ����ͨ��OnData֪ͨ�����ߡ� 
*        BVCU_RESULT_S_IGNORE:  �Ự������
*        BVCU_RESULT_E_FAILED�������� ����������ʧ��
*/
LIBBVCU_API BVCU_Result BVCU_FTP_Transfer_Close(BVCU_FTP_HTransfer hTransfer);

/*================================================================*/


/*================================================================*/
#endif
};