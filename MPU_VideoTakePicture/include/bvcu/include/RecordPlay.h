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

//==========================¼���ļ��ط���ؽӿ�===============================

/*NOTE: �����ַ���������UTF-8����*/
typedef struct _BVCU_RecordPlay_FileParam{
    char* szMediaFileName;
    char* szGPSFileName;
    char* szEventFileName;  
}BVCU_RecordPlay_FileParam;


typedef struct _BVCU_RecordPlay_FileInfo{
    //��ʼʱ�䣬��λ 1/SAV_TIME_BASE���� Epoch, 1970-01-01 00:00:00 +0000 (UTC)��ʼ������ʱ��
    SAV_TYPE_INT64 iCreationTime;   
    int iDuration;//����ʱ�䣬��λ����      

    //��Ƶ��Ⱥ͸߶�
    int iVideoWidth;
    int iVideoHeight;

    //¼��ԭ��,BVCU_STORAGE_RECORDTYPE_*
    int iReason;

    //��Ƶͨ����,0-û����Ƶ��1-����������2-˫����,...
    int iAudioChannel;  

    //�Ƿ���GPS,0-û�У�1-��
    int bGPS;   

    //�Ƿ����¼�,0-û�У�1-��
    int bEvent;
}BVCU_RecordPlay_FileInfo;

//����״̬
enum
{
    BVCU_RECORDPLAY_STATE_IDLE = 0,//δ���κ��ļ�
    BVCU_RECORDPLAY_STATE_OPENED,//�����ļ�
    BVCU_RECORDPLAY_STATE_PLAYING,//���ڲ���
    BVCU_RECORDPLAY_STATE_PAUSE,//��ͣ
	BVCU_RECORDPLAY_STATE_STEP,//��֡����
	BVCU_RECORDPLAY_STATE_CLOSEING,//���ڹر�
    BVCU_RECORDPLAY_STATE_CLOSEED,//�ر����ļ����������ֶ��رջ򲥷���Ϻ��Զ��ر�
};

typedef struct _BVCU_RecordPlay_SizeCtrl{     
    BVCU_HWND hWnd;//��ʾ���ھ��
    BVCU_Display_Rect rcSource;//��ƵԴ���Σ���С���ܳ���BVCU_RecordPlay_FileInfo�е���Ƶ���
    BVCU_Display_Rect rcDisplay;//��ʾ����
}BVCU_RecordPlay_SizeCtrl;

typedef struct _BVCU_RecordPlay_ColorCtrl{
    int iBrightness;//���ȣ�ȡֵ��Χ0-100
    int iContrast;//�Աȶȣ�ȡֵ��Χ0-100
    int iSaturation;//���Ͷȣ�ȡֵ��Χ0-100
}BVCU_RecordPlay_ColorCtrl;

#define SPEEDBASE 64
//������Ϣ
typedef struct _BVCU_RecordPlay_PlayInfo{   
    int iTimeOffset;//��ǰ����ʱ�̣���0��ʼ����λ����
    BVCU_RecordPlay_SizeCtrl stSizeCtrl;//��Ƶ��ʾ    
    BVCU_RecordPlay_ColorCtrl stColorCtrl;//ɫ�ʿ���

    //��Ƶ�ط��豸��״̬��0-��Ƶ�豸δ�򿪣�1-����Ƶ�ط��豸�ɹ���2-���Թ�����Ƶ�豸��ʧ��
    int iAudioDevice;

    //���ڲ��ŵ���Ƶ��������bitλ��ʾÿ�������Ĳ���״̬��0-�����ţ�1-���š�bit 0-������������������bit 1-������
    int iPlaybackChannel;

    //����������ȡֵ��Χ0-100
    int  iPlaybackVolume;

    //�����ٶȣ���λ1/64   
    int iSpeed;

    //����״̬��BVCU_RECORDPLAY_STATE_*
    int iPlayState;

	//ͬ����ID��>=1��ʾͬ����ID��<=0��ʾ������ͬ���顣Ĭ��ֵΪ0������ͬһ����ļ�����ʱͬ��
	int  iSyncGroupID;

	//�߷űߴ���ļ���������Զ�̲���
	char* szSaveFileName;

	//�����ٶȣ���λKB/s������Զ�̲���
	int iKBps;

	//���Ż����������Ȱٷֱȡ�ȡֵ��Χ0-100������Զ�̲���ʱ�����Ƿ񿨶�
	int  iFullness;
}BVCU_RecordPlay_PlayInfo;


typedef void* BVCU_RecordPlay_Handler;

typedef struct _BVCU_RecordPlay_GPSData{
    SAV_TYPE_INT64 iTimeOffset;//���ʱ�䣬��BVCU_RecordPlay_FileInfo.iCreationTimeΪ��ʼ
    BVCU_PUCFG_GPSData stData;//GPS����
}BVCU_RecordPlay_GPSData;

//¼��������¼�
typedef struct _BVCU_RecordPlay_EventData{  
    SAV_TYPE_INT64 iTimeOffset;//���ʱ�䣬��FileInfo.iCreationTimeΪ��ʼ
    BVCU_WallTime stTime;//�¼�����ʱ��
    char szID[BVCU_MAX_ID_LEN+1];//�¼�ԴID
//    BVCU_AlarmContent stAlarm; //�¼�����
}BVCU_RecordPlay_EventData;

typedef struct _BVCU_RecordPlay_CallbackParam{
    int bStateChanged; //״̬�Ƿ����ı�
    BVCU_Result iErrorCode; //״̬��
    BVCU_RecordPlay_GPSData* pGPSData; //GPS����
    BVCU_RecordPlay_EventData* pEventData; //Event����
    SAV_Packet* pVideoData; //��Ƶ����
    SAV_Packet* pAudioData; //��Ƶ����
}BVCU_RecordPlay_CallbackParam;

//֪ͨӦ�ò�Ļص���ע�ⲻ���ڸûص���ִ���������ʱ�Ĳ�����
typedef void (*BVCU_RecordPlay_Callback)(BVCU_RecordPlay_Handler hHandler, BVCU_RecordPlay_CallbackParam* pParam);

//���ſ���
enum {
    BVCU_RECORDPLAY_CTRL_RESIZE = 1,//���ţ���ȫ�������洰�ڴ�С�����ֿ�߱ȡ���Ƶ�ֲ��Ŵ�ȣ�Ӧ�ò���ݴ��ڴ�С�仯��Ϣ���ô˹��ܣ�������BVCU_RecordPlay_SizeCtrlָ��
    BVCU_RECORDPLAY_CTRL_VOLUME,//����������: int iVolume
    BVCU_RECORDPLAY_CTRL_AUDIOCHANNEL,//����������:int iPlaybackChannel
    BVCU_RECORDPLAY_CTRL_SPEED,//�ٶ�,����: int iSpeed
    BVCU_RECORDPLAY_CTRL_COLOR,//ɫ��,������BVCU_RECORDPLAY_ColorCtrlָ��
    BVCU_RECORDPLAY_CTRL_JUMP,//��ת��������int iTimeOffset
    BVCU_RECORDPLAY_CTRL_PAUSE,//��ͣ������: ��
    BVCU_RECORDPLAY_CTRL_PLAY,//���ţ���������
    BVCU_RECORDPLAY_CTRL_STEP,//��֡���ţ�ÿ������һ֡����PAUSE״̬����������
    BVCU_RECORDPLAY_CTRL_CALLBACK,//GPS/Event�ص�������BVCU_RecordPlay_Callback����ָ��
    BVCU_RECORDPLAY_CTRL_SYNCGROUPID,//ͬ����ID,������int  iSyncGroupID
    BVCU_RECORDPLAY_CTRL_SAVEFILE,//�߷űߴ��ļ������ڲ���Զ��¼���ļ�������: const char* �ļ���ָ�룬��ΪNULL��ʾ��ʹ�ñ߷űߴ档
};

//���ļ�
LIBBVCU_API BVCU_Result BVCU_RecordPlay_Open(BVCU_RecordPlay_Handler* pHandler, BVCU_RecordPlay_FileParam* pParam, BVCU_RecordPlay_FileInfo* pInfo, BVCU_HSession hSession = NULL);

//ִ�в��ſ���
LIBBVCU_API BVCU_Result BVCU_RecordPlay_Control(BVCU_RecordPlay_Handler hHandler, int iCtrlCode, void* pParam);

//��ѯ������Ϣ
LIBBVCU_API BVCU_Result BVCU_RecordPlay_Query(BVCU_RecordPlay_Handler hHandler, BVCU_RecordPlay_PlayInfo* pInfo);

//�ر��ļ�
LIBBVCU_API BVCU_Result BVCU_RecordPlay_Close(BVCU_RecordPlay_Handler hHandler);
#endif
};