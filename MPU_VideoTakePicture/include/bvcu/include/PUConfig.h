#ifndef __PU_CONFIG_H__
#define __PU_CONFIG_H__

#include "BVCUConst.h"
#include "BVCUCommon.h"
//##!!!!!!!ע�⣺���нṹ���Reserved��Ա�����ʼ��Ϊ0!!!!!!!!!!!!!
//
//�豸��Ϣ������ע����д���������Աֻ��
typedef struct _BVCU_PUCFG_DeviceInfo{
    char szID[BVCU_MAX_ID_LEN+1];             //�豸ID
    char szManufacturer[BVCU_MAX_NAME_LEN+1]; //����������
    char szProductName[BVCU_MAX_NAME_LEN+1];    //��Ʒ��
    char szSoftwareVersion[BVCU_MAX_NAME_LEN+1]; //����汾
    char szHardwareVersion[BVCU_MAX_NAME_LEN+1]; //Ӳ���汾    
    int  iPUType;          //BVCU_PUTYPE_*
    int  iLanguage[BVCU_MAX_LANGGUAGE_COUNT];    //֧�ֵ������б�BVCU_LANGUAGE_*
    int  iLanguageIndex;  //��ǰʹ�õ�������������д
    char szName[BVCU_MAX_NAME_LEN+1];//���֡���д
    int  iWIFICount;      //WIFI��Ŀ
    int  iRadioCount;     //����ģ����Ŀ
    int  iChannelCount;   //����Ƶͨ����
    int  iVideoInCount;   //��Ƶ������
    int  iAudioInCount;   //��Ƶ������
    int  iAudioOutCount;  //��Ƶ�����
    int  iPTZCount;       //PTZ��
    int  iSerialPortCount;//������ 
    int  iAlertInCount;   //����������
    int  iAlertOutCount;  //���������
    int  iStorageCount;   //�洢�豸��
    int  iGPSCount;       //GPS�豸��
    int  bSupportSMS;     //�Ƿ�֧���ֻ����Ź��ܡ�0-��֧�֣�1-֧��

    int  iPresetCount; //֧�ֵ�PTZԤ�õ���Ŀ
    int  iCruiseCount; //֧�ֵ�PTZѲ������Ŀ
    int  iAlarmLinkActionCount; //֧�ֵı���������Ŀ

    //PUλ��
    int  iLongitude; //���ȣ���������ֵ��������ֵ����λ1/10000000�ȡ�����180�Ȼ�С��-180�ȱ�ʾ��Чֵ
    int  iLatitude; //γ�ȣ���γ����ֵ����γ�Ǹ�ֵ����λ1/10000000�ȡ�����180�Ȼ�С��-180�ȱ�ʾ��Чֵ

    int  iReserved[8];
}BVCU_PUCFG_DeviceInfo;

//�����̼�״̬
typedef struct _BVCU_PUCFG_UpdateStatus{
	int  iDownloadPercent;//���ذٷֱȣ�0��100
	int  iSpeed;//�����ٶȣ���λKB/s
	char szSoftwareVersion[BVCU_MAX_NAME_LEN+1]; //��ǰ����汾
	BVCU_WallTime stUpdateFinishTime;//��ǰ����汾�������ʱ��	
}BVCU_PUCFG_UpdateStatus;

typedef struct _BVCU_PUCFG_UPGRADE_{
    char szFTPID[BVCU_MAX_ID_LEN+1]; // ָ��������ftp������
    char szFilePath[BVCU_MAX_FILE_NAME_LEN+1]; // ָ�����ļ�·��
    char szFileName[BVCU_MAX_NAME_LEN+1]; // ָ�����ļ�����
    int  bPromptly; // �Ƿ�����������0-�´�����ʱ������  1-��������
}BVCU_PUCFG_Upgrade;

//�豸���µ�PU������Ϣ
typedef struct _BVCU_PUCFG_GroupPU{
    char  szPUID[BVCU_MAX_ID_LEN+1]; // PU ID
}BVCU_PUCFG_GroupPU;

//�豸���б�
typedef struct _BVCU_PUCFG_GroupItem{
    char szID[BVCU_MAX_ID_LEN+1]; // ���id��ʶ��

    char szName[BVCU_MAX_NAME_LEN+1]; // ������� 

    char szParentID[BVCU_MAX_ID_LEN+1]; // ����ϼ���, ������Ĵ�ֵΪ��
}BVCU_PUCFG_GroupItem;

//�豸����Ϣ
typedef struct _BVCU_PUCFG_GroupInfo{
    char szID[BVCU_MAX_ID_LEN+1]; // ���id��ʶ��

    char szName[BVCU_MAX_NAME_LEN+1]; // ������� 

    char szParentID[BVCU_MAX_ID_LEN+1]; // ����ϼ���, ������Ĵ�ֵΪ��
    
    char szDescription[BVCU_MAX_SEDOMAIN_NAME_LEN+1]; //  ���������һЩ��Ϣ

    int  iPUCount; // PU����PU��Ŀ

    BVCU_PUCFG_GroupPU* pPU; // PU����PU����
}BVCU_PUCFG_GroupInfo;

//��Դ����
typedef struct _BVCU_PUCFG_Power{
    int iTurnOffDelay;//��ʱ�ػ���ʱ����λ��
    int bEnableTimer;//�Ƿ�����ʱ���ػ���0-������1-����
    BVCU_DayTimeSlice stTurnOn[7][2];//ÿ��7���е�ÿ�쿪��ʱ�̣�����2��Ƭ�Σ�����ʱ��֮���ʱ����Ϊ�ǹػ���
}BVCU_PUCFG_Power;

//����ͷ�ɼ�����
typedef struct  _BVCU_PUCFG_VideoColorCtl{
    BVCU_DayTimeSlice stTime; //ʱ��Ƭ
    char cBrightness;  //����,ȡֵ��Χ[0,255]
    char cContrast;    //�Աȶ�,ȡֵ��Χ[0,255]
    char cSaturation;  //���Ͷ�,ȡֵ��Χ[0,255]
    char cHue;         //ɫ��,ȡֵ��Χ[0,255]
}BVCU_PUCFG_VideoColorCtl;

//�˶����
#define BVCU_PUCFG_MAX_MD_COUNT 16
typedef struct _BVCU_PUCFG_MotionDetect{
    int bSupport;//0-��֧�֣�1-֧�֡�ֻ��
    BVCU_ImageRect rcROI[BVCU_PUCFG_MAX_MD_COUNT];//������16x16Ϊ��λ��
    int iSensitivity;//�����ȣ���Χ0~10,0��ʾ����⣬Խ���ֵԽ����
    int iInterval;//ÿ�μ��ʱ��������λ���롣��ļ�����Խ�ʡ����ʱ�䣬��ʹ�õ�ع�����豸���������ӳ����ʹ������
    //���п���©���˶��¼�����Ϊ0��ÿ֡��Ƶ������⡣
}BVCU_PUCFG_MotionDetect;

//��Ƶ�����ڸ�
#define BVCU_PUCFG_MAX_SHELTER_COUNT 4
typedef struct _BVCU_PUCFG_VideoShelter{ //
    int bSupport;//0-��֧�֣�1-֧�֡�ֻ��
    BVCU_ImageRect rcROI[BVCU_PUCFG_MAX_SHELTER_COUNT];//������16x16Ϊ��λ��
}BVCU_PUCFG_VideoShelter;

//��ͷ�ڵ����
#define BVCU_PUCFG_MAX_VIDEOOCCLUSION_COUNT 4
typedef struct _BVCU_PUCFG_VideoOcclusionDetect{ //
    int bSupport;//0-��֧�֣�1-֧�֡�ֻ��
    BVCU_ImageRect rcROI[BVCU_PUCFG_MAX_VIDEOOCCLUSION_COUNT];//������16x16Ϊ��λ��
}BVCU_PUCFG_VideoOcclusionDetect;

//��Ƶ����
enum{
    //ע�⣺��ʽ1/2/9/10/11/12������֮��ķָ�����BVCU_PUCFG_VideoIn.cOSDTimeSplitChar����
    BVCU_OSD_TIMEFORMAT_INVALID = 0,//������ʱ��
    BVCU_OSD_TIMEFORMAT_1,//YYYY-MM-DD hh:mm:ss
    BVCU_OSD_TIMEFORMAT_2,//YYYY-MM-DD ����W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_3,//DD��MM��YYYY�� hh:mm:ss
    BVCU_OSD_TIMEFORMAT_4,//DD��MM��YYYY�� ����W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_5,//MM��DD��YYYY�� hh:mm:ss
    BVCU_OSD_TIMEFORMAT_6,//MM��DD��YYYY�� ����W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_7,//YYYY��MM��DD�� hh:mm:ss
    BVCU_OSD_TIMEFORMAT_8,//YYYY��MM��DD�� ����W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_9,//DD-MM-YYYY hh:mm:ss
    BVCU_OSD_TIMEFORMAT_10,//DD-MM-YYYY ����W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_11,//MM-DD-YYYY hh:mm:ss
    BVCU_OSD_TIMEFORMAT_12,//MM-DD-YYYY ����W hh:mm:ss
};


typedef struct  _BVCU_PUCFG_VideoIn{
    BVCU_PUCFG_VideoColorCtl stVCC[2];//ɫ�ʿ��ơ�0��ʾ��������ã�1��ʾ���ϵ�����

    BVCU_PUCFG_MotionDetect stMD;//�˶����
    BVCU_PUCFG_VideoShelter stShelter;//�����ڸ�
    BVCU_PUCFG_VideoOcclusionDetect stOcclusion; //��ͷ�ڵ����

    BVCU_VideoFormat stVideoFormat[4];    
    int iVideoFormatIndex;//��ǰ��Ƶ��ʽ������
    
    //����ͼ�ꡣ
    int iOSDIcon;//-1��ʾ�豸��֧�ֵ���ͼ�꣬==0��ʾ֧�ֵ�Ŀǰû�е��ӣ�==1��ʾ֧�ֲ����ѵ���
    char szOSDIcon[BVCU_MAX_FILE_NAME_LEN+1];//ͼƬ�ı���·��,��ѯʱ�����塣Ϊ�ձ�ʾ������ͼƬ������ѱ���ͼƬ�ϴ���PU
    BVCU_ImagePos stOSDIconPos; //λ�á�

    char szOSDTitle[BVCU_MAX_NAME_LEN+1];//��������
    BVCU_ImagePos stOSDTitlePos; //λ��
    char  iOSDTitleFontSize; //�����С

    char  cOSDTime;//����ʱ���ʽ��BVCU_OSD_TIMEFORMAT_*
    char  cOSDTimeSplitChar;//YYYY-MM-DD ֮��ķָ�������������'.' '-'��'/'��
    char  iOSDTimeFontSize; //�����С
    BVCU_ImagePos stOSDTimePos; //λ��
    int iReserved[4];
}BVCU_PUCFG_VideoIn;

typedef struct  _BVCU_PUCFG_AudioIn{
    int iInput;//BVCU_AUDIOIN_INPUT_*
    int iChannelCount;//������
    int iSamplesPerSec;//������
    int iBitsPerSample;//��������
    int iVolume;//����
}BVCU_PUCFG_AudioIn;

typedef struct _BVCU_PUCFG_AudioDecoderParam{
    int bEnable;//0-��ֹ��1-ʹ��
    SAVCodec_ID iAudioCodecAll[4];//֧�ֵ���Ƶ������ID��ֻ��
    int iAudioCodecIndex;//��ǰʹ�õ���Ƶ������
}BVCU_PUCFG_AudioDecoderParam;

//��Ƶ���
typedef struct  _BVCU_PUCFG_AudioOut{
    BVCU_PUCFG_AudioDecoderParam stADParam;//��Ƶ���������
    int iChannelCount;//������
    int iSamplesPerSec;//������
    int iBitsPerSample;//��������
    int iVolume;//����
    int iReserved[4];
}BVCU_PUCFG_AudioOut;

//��������
typedef struct  _BVCU_PUCFG_AlertIn{
    int bType;//0-�������� 1-���ձ���
    int iInterval;//���������λ�롣��⵽��������󣬾���iInterval���ٴμ��
}BVCU_PUCFG_AlertIn;

//�������
typedef struct  _BVCU_PUCFG_AlertOut{
    int bAction; //0-ͨ��1-�ϣ�
    int iDuration; //�������ʱ�䣬��λ��
}BVCU_PUCFG_AlertOut;

//GPS
typedef struct  _BVCU_PUCFG_GPSParam{
    int bEnable;        //�Ƿ�ʹ��
    int iReportInterval;//����ʱ��������λ��
    int iReserved[2];//����������Ϊ0
}BVCU_PUCFG_GPSParam;

typedef struct  _BVCU_PUCFG_GPSData{
    BVCU_WallTime stTime;//���ݶ�Ӧ��ʱ��
    int  iLongitude; //���ȣ���������ֵ��������ֵ����λ1/10000000��
    int  iLatitude; //γ�ȣ���γ����ֵ����γ�Ǹ�ֵ����λ1/10000000��
    int  iHeight; //�߶ȣ���λ1/100��
    int  iAngle; //�����(��������Ϊԭ�㣬˳ʱ��Ϊ��),��λ1/1000��
    int  iSpeed; //�ٶ�(��/Сʱ)         
    int  iStarCount;  //��λ����      
    int  bAntennaState; //����״̬(1-�ã�0-��) 
    int  bOrientationState;//��λ״̬(1-��λ��0-����λ) 
    int  iReserved[4];
}BVCU_PUCFG_GPSData;

//�Զ�ץ�Ĳ���
enum {
    BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_TIME = (1<<0),
    BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_TEXT = (1<<1),
    BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_GPS =  (1<<2),
    BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_ALARM = (1<<3),    
};
typedef struct  _BVCU_PUCFG_SnapshotParam{
    BVCU_ImageSize iImageSize;//ͼ��ֱ���
    int iQuality;//ץ��JPGѹ��������ȡֵ��Χ1��100    
    int iSequencePicCount; //��������ͼƬ����
    int iSequenceInterval;//���ĵ�ÿ��ͼƬʱ��������λ����
    int iSequenceDelay;//һ���������ڽ�������ʱʱ�䡣��λ���롣
    int iOverlay;//������Ϣ��־��BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_*�����
    int iReserved[2];//��������������Ϊ0
}BVCU_PUCFG_SnapshotParam;

//��Ƶ����������
//
typedef struct _BVCU_PUCFG_AudioEncoderParam{
    SAVCodec_ID iCodecID;//������ID
    char iChannelCount[4];//��ѡ��������0��ʾ��Чֵ��ֻ��
    char iBitsPerSample[4];//��ѡ�������ȣ�0��ʾ��Чֵ��ֻ��
    int iSamplesPerSec[8];//��ѡ�����ʣ�0��ʾ��Чֵ��ֻ��
    int iBitRate[8];//��ѡ�����ʣ�0��ʾ��Ч�����ʡ���λbits/s��ֻ��
    char iChannelCountIndex;//��ǰʹ�õ���������
    char iBitsPerSampleIndex;//��ǰʹ�õĲ�����������
    char iSamplesPerSecIndex;//��ǰʹ�õĲ���������
    char iBitRateIndex;//��ǰʹ�õ���������
}BVCU_PUCFG_AudioEncoderParam;

//������ѹ������
typedef struct _BVCU_PUCFG_EncoderParam{
    BVCU_DayTimeSlice stTime; //ʱ��Ƭ����ͬ��ʱ����Բ��ò�ͬ�ı������

    //��Ƶ��������
    int bVideoEnable;//0-��ֹ��1-ʹ��
    SAVCodec_ID iVideoCodecAll[4];//֧�ֵ���Ƶ������ID��ֻ��
    int iVideoCodecIndex;//��ǰʹ�õ���Ƶ������
    BVCU_RATECONTROL iRateControl;//���ʿ�������
    BVCU_ImageRect iImageRectAll[8];//��ѡ����Ƶ��������ȫ0��ʾ��Ч�ı�������
    int iImageRectIndex;//��ǰʹ�õ���Ƶ��������
    int iFramesPerSec;//��λ1/1000֡������25fps����Ҫ����Ϊ25*1000����֡�ʲ��ܳ���BVCU_PUCFG_VideoIn.iFPSMax
    int iKeyFrameInterval;//�ؼ�֡���
    int iImageQuality;//��Ƶ����������ȡֵ��Χ1~BVCU_VIDEO_QUALITY_COUNT
    int iKbpsLimitMin[BVCU_VIDEO_QUALITY_COUNT];//ÿ�������ȼ���Ӧ������������Сֵ��ֻ��
    int iKbpsLimitMax;//�����������ֵ������Ƶ�ֱ���/֡�ʾ�����ֻ��
    int iKbpsLimitCurrent;//�������Ƶ�ǰֵ��
    int iReserved1[4];

    //��Ƶ��������
    int bAudioEnable;//0-��ֹ��1-ʹ��
    BVCU_PUCFG_AudioEncoderParam iAudioCodecAll[4];//֧�ֵ���Ƶ������ID��ֻ��
    int iAudioCodecIndex;//��ǰʹ�õ���Ƶ������
    int iReserved2[4];
}BVCU_PUCFG_EncoderParam;

typedef struct _BVCU_PUCFG_EncoderStreamParam{
    int iCount;//���������õ�ʱ��Ƭ����
    BVCU_PUCFG_EncoderParam* pstParams;//���������ã�ÿ����Ա��Ӧһ��ʱ��Ƭ�ε�����
    int iStreamType;//�����͡�BVCU_ENCODERSTREAMTYPE_*
    int bEnableTransfer;//�Ƿ������䡣0-������1-����
    int iReserved[4];
}BVCU_PUCFG_EncoderStreamParam;

//������ͨ��
typedef struct  _BVCU_PUCFG_EncoderChannel{
    char szName[BVCU_MAX_NAME_LEN+1];
    int iCount;//֧�ֵ�����������ֻ��
    BVCU_PUCFG_EncoderStreamParam* pParams;
    char iVideoInIndex;//����ͷ������-1��ʾ��֧�֡�ֻ��
    char iAudioInIndex;//��Ƶ����������-1��ʾ��֧�֡�ֻ��
    char iAudioOutIndex;//��Ƶ���������-1��ʾ��֧�֡�ֻ��
    char iPTZIndex; //��̨������-1��ʾ��֧�֡�ֻ��
    char cReserved[16];
}BVCU_PUCFG_EncoderChannel;

//������ͨ��
/*
typedef struct  _BVCU_PUCFG_DecoderChannel{

}BVCU_PUCFG_DecoderChannel;
*/

//RS232����
typedef struct _BVCU_PUCFG_RS232{
    int   iDataBit;    //����λ��5/6/7/8
    int   iStopBit;    //ֹͣλ��0:1λ��1��1.5λ��2��2λ
    int   iParity;     //��żУ��λ��0:�ޣ�1����У�飬2��żУ��
    int   iBaudRate;   //������.�����İ���1200��2400��4800��9600��19200��38400��57600��115200��
    int   iFlowControl; //���ء�0:�ޣ�1�������أ�2��Ӳ����
}BVCU_PUCFG_RS232;

//����
typedef struct _BVCU_PUCFG_SerialPort{
    BVCU_PUCFG_RS232 stRS232;
    int iAddress;//RS485��ַ�����Ϊ-1����������RS485����
    int iType;//0-���ݴ��䣨����PPP���ţ�;1-����̨;2-͸������
}BVCU_PUCFG_SerialPort;

//=======================��̨���============================
//Ԥ�õ�
typedef struct _BVCU_PUCFG_Preset{
    int  iID;//Ԥ�õ�š�-1��ʾ��Ч����Чֵ��0��ʼ
    char szPreset[BVCU_PTZ_MAX_NAME_LEN+1]; //Ԥ�õ���
}BVCU_PUCFG_Preset;
//Ѳ����
typedef struct _BVCU_PUCFG_CruisePoint{
    short iPreset;//Ԥ�õ�š�-1��ʾ��Чֵ
    short iSpeed;//ת����һѲ�������̨�ٶ�
    int   iDuration;//�ڱ�Ԥ�õ�ͣ��ʱ�䣬��λ��
}BVCU_PUCFG_CruisePoint;

//Ѳ��·��
typedef struct _BVCU_PUCFG_Cruise{
    int  iID;//Ѳ��·�ߺš�-1��ʾ��Ч����Чֵ��0��ʼ
    char szName[BVCU_PTZ_MAX_NAME_LEN+1];//Ѳ��·�����֡�δ���õ�Ѳ��·������Ϊ��

    //Ѳ��·�ߵ�Ѳ���㡣Լ������Ч��Ѳ�����������ǰ�棬�����е�һ����ЧѲ����֮��ĵ㶼����Ϊ����Ч��
    BVCU_PUCFG_CruisePoint stPoints[BVCU_PTZ_MAX_CRUISEPOINT_COUNT];
}BVCU_PUCFG_Cruise;

//��̨����
typedef struct _BVCU_PUCFG_PTZAttr{
    int iPTZProtocolAll[BVCU_PTZ_MAX_PROTOCOL_COUNT];  //֧�ֵ�����Э���б�BVCU_PTZ_PROTO_*��ֻ��
    int iPTZProtocolIndex;   //��ǰʹ�õ�PTZЭ������

    int iAddress;      //485��ַ����Χ0��255����д��
    BVCU_PUCFG_RS232 stRS232;  //232�������ԡ���д

    //�Ƿ���������Ԥ�õ㡣0-�����ģ�1-���ġ�������ʱszPreset�����ݱ����ԡ� ������������ʱ�����壬��ѯʱ������
    int bChangePreset;

    //�Ƿ���������Ѳ��·�ߡ�0-�����ģ�1-���ġ�������ʱstCruise�����ݱ����ԡ�������������ʱ�����壬��ѯʱ������
    int bChangeCruise;

    //Ԥ���б�
    //��ѯʱ��������Ԥ�õ㣬����ʱ��������������Ԥ�õ�����ֺ�ɾ��Ԥ�õ�
    //ע�⣺Ԥ�õ��λ��ֻ����BVCU_PTZ_COMMAND_PRESET_SET��������
    //Լ����������Ч��Ԥ�õ����������ǰ�棬�������Ŀ����BVCU_PTZ_MAX_PRESET_COUNT�����һ����Ч��Preset��iIndexΪ-1
    BVCU_PUCFG_Preset stPreset[BVCU_PTZ_MAX_PRESET_COUNT]; 

    //Ѳ��·�ߡ�
    //��ѯʱ����Ѳ��·�ߡ�����ʱ��������Ѳ��·��
    //Լ����������Ч��Ѳ��·�߷���������ǰ�棬�������Ŀ����BVCU_PTZ_MAX_CRUISE_COUNT����һ����Ч��Cruise��iIndexΪ-1
    BVCU_PUCFG_Cruise stCruise[BVCU_PTZ_MAX_CRUISE_COUNT];
    
    //��ǰ����ʹ�õ�Ѳ��·��ID��-1��ʾû�л�Ծ��Ѳ��·�ߡ�
    int iActiveCruiseID;
}BVCU_PUCFG_PTZAttr;

//��̨����

//PTZ��������

//ע�⣺����unused������������Ϊ0��
//��ֵ��/Ѳ��·�ߵ���Ŵ�0��ʼ��-1��ʾ��Ч���
enum {
    //�������
    BVCU_PTZ_COMMAND_UP,     //���ϡ�iParam1��unused;iParam2: �ٶ�;iParam3:unused
    BVCU_PTZ_COMMAND_DOWN,   //���¡�iParam1��unused;iParam2: �ٶ�;iParam3:unused
    BVCU_PTZ_COMMAND_LEFT,  //����iParam1��unused;iParam2: �ٶ�;iParam3:unused
    BVCU_PTZ_COMMAND_RIGHT, //���ҡ�iParam1��unused;iParam2: �ٶ�;iParam3:unused
    BVCU_PTZ_COMMAND_LEFTTOP,  //���ϡ�iParam1����ֱ�ٶ�;iParam2: ˮƽ�ٶ�;iParam3:unused
    BVCU_PTZ_COMMAND_RIGHTTOP,  //���ϡ�iParam1����ֱ�ٶ�;iParam2: ˮƽ�ٶ�;iParam3:unused
    BVCU_PTZ_COMMAND_LEFTDOWN,  //���¡�iParam1����ֱ�ٶ�;iParam2: ˮƽ�ٶ�;iParam3:unused
    BVCU_PTZ_COMMAND_RIGHTDOWN,  //���¡�iParam1����ֱ�ٶ�;iParam2: ˮƽ�ٶ�;iParam3:unused

    //��ͷ����
    BVCU_PTZ_COMMAND_ZOOM_INC,  //���ӷŴ�����iParam1��unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_ZOOM_DEC,  //��С�Ŵ�����iParam1��unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_FOCUS_INC, //�����Զ��iParam1��unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_FOCUS_DEC, //���������iParam1��unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_APERTURE_INC, //��Ȧ�Ŵ�iParam1��unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_APERTURE_DEC, //��Ȧ��С��iParam1��unused;iParam2: unused;iParam3:unused

    //Ԥ�õ����
    BVCU_PTZ_COMMAND_PRESET_GO,  //ת��Ԥ�õ㡣iParam1��Ԥ�õ��;iParam2: ��ֱ�ٶ�;iParam3:ˮƽ�ٶ�
    BVCU_PTZ_COMMAND_PRESET_SET, //�ѵ�ǰλ������ΪԤ�õ㡣iParam1��Ԥ�õ��;iParam2: Ԥ�õ���;iParam3:unused
    BVCU_PTZ_COMMAND_PRESET_SETNAME, //����Ԥ�õ����֡�iParam1��Ԥ�õ��;iParam2: Ԥ�õ���;iParam3:unused
    BVCU_PTZ_COMMAND_PRESET_DEL, //ɾ��Ԥ�õ㡣iParam1��Ԥ�õ��;iParam2: unused;iParam3:unused

    //Ѳ��·�߲���
    BVCU_PTZ_COMMAND_CRUISE_GO,//����Ѳ����iParam1��Ѳ��·�ߺ�;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_CRUISE_STOP,//ֹͣѲ����iParam1��Ѳ��·�ߺ�;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_CRUISE_SET,//��������Ѳ��·�ߡ�iParam1��Ѳ��·�ߺ�;iParam2: BVCU_PUCFG_CRUISEָ��;iParam3:unused
    BVCU_PTZ_COMMAND_CRUISE_DEL,//ɾ��Ѳ��·�ߡ�iParam1��Ѳ��·�ߺ�;iParam2: unused;iParam3:unused

    //�������ܲ���
    BVCU_PTZ_COMMAND_AUX,//��/�رո������ܿ��أ�Param1��������;iParam2: 0-�ر�,1-����;iParam3:unused

    //������
    //�����������60����û�û���ֹ����������Server���Զ����������
    BVCU_PTZ_COMMAND_LOCK,//����/������̨��iParam1��unused;iParam2: unused;iParam3:unused
};

typedef struct _BVCU_PUCFG_PTZControl{
    int iPTZCommand;    //BVCU_PTZ_COMMAND_*
    int bStop;//0-������ʼ��1-����ֹͣ�����Է������/��ͷ����/��������Ч����������Ӧ������Ϊ0����������0-��ʼ������1-ֹͣ����
    int iParam1,iParam2,iParam3;//�ο�BVCU_PTZ_COMMAND_*˵��
    //ע�⣺BVCU_PTZ_COMMAND_CRUISE_SET��iParam2�Ǹ�ָ�룬���緢��/����ʱӦ����/��������BVCU_PTZ_COMMAND_CRUISE_SET�ṹ��
}BVCU_PUCFG_PTZControl;

//=======================�������============================

typedef struct _BVCU_PUCFG_Ethernet{
    int bDHCP;//�Ƿ�ʹ��DHCP��0-��ʹ�ã�1-ʹ��;-1-�豸��֧��
    int bPPPoE;//�Ƿ�ʹ��PPPoE��0-��ʹ�ã�1-ʹ�ã�-1-�豸��֧�֡�
    int bAutoDNS;//�Զ���ȡDNS��0-��ʹ�ã�1-ʹ�á�ֻ��bDHCP=1����bPPPoE=1��������

    char szIP[16];//ip��ַ��ֻ��bDHCP !=1 ��������
    char szNetMask[16];//�������롣ֻ��bDHCP !=1 ��������
    char szGateway[16];//Ĭ�����ء�ֻ��bDHCP !=1 ��������
    char szDNS[2][BVCU_MAX_HOST_NAME_LEN+1];//������������ֻ��bAutoDNS=0��������

    char szPPPoEUserName[BVCU_MAX_NAME_LEN+1];//PPPoE�û�����ֻ��bPPPoE=1��������
    char szPPPoEPassword[BVCU_MAX_PASSWORD_LEN+1];//PPPoE���룬ֻ��bPPPoE=1��������

    int iReserved[4];
}BVCU_PUCFG_Ethernet;

typedef struct _BVCU_PUCFG_WifiHotSpot{
    char szProviderAll[8][BVCU_MAX_NAME_LEN+1];//֧�ֵ��ṩ���б�ChinaNet/ChinaMobile/ChinaUniccom
    int  iProviderIndex;//��ǰʹ�õ��ṩ��
    char szAreaAll[64][16];//�������б�.ͨ���ô����ʾ������bj-����,ah-����,...
    int  iAreaIndex;//������
    char szUserName[BVCU_MAX_NAME_LEN+1];//�û���
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//����
}BVCU_PUCFG_WifiHotSpot;

typedef struct _BVCU_PUCFG_WifiGeneral{
    char szSSID[32];//SSID 
    int iSecurityType;//��ȫ����.BVCU_WIFI_SECURITY_TYPE_*
    int iCryptType;//��������.BVCU_WIFI_CRYPT_TYPE_*
    char szWEPKey[4][16];//WEP��Կ
    char szWPAKey[64];//WPA��Կ

    int bDHCP;//�Ƿ�ʹ��DHCP��0-��ʹ�ã�1-ʹ��;-1-�豸��֧��
    int bPPPoE;//�Ƿ�ʹ��PPPoE��0-��ʹ�ã�1-ʹ�ã�-1-�豸��֧�֡�PPPoE���û���/����ʹ��BVCU_PUCFG_Ethernet�е�
    int bAutoDNS;//�Զ���ȡDNS��0-��ʹ�ã�1-ʹ�á�ֻ��bDHCP=1����bPPPoE=1��������

    char szIP[16];//ip��ַ��ֻ��bDHCP=0��������
    char szNetMask[16];//�������롣ֻ��bDHCP !=1 ��������
    char szGateway[16];//Ĭ�����ء�ֻ��bDHCP !=1 ��������
    char szDNS[2][BVCU_MAX_HOST_NAME_LEN+1];//������������ֻ��bAutoDNS=0��������
    int  iReserved[4];
}BVCU_PUCFG_WifiGeneral;

typedef struct _BVCU_PUCFG_Wifi{
    int bEnable;//0-��ʹ�ã�1-ʹ��
    int iMode;//0-��ͨ��ʽ��1-�ȵ㷽ʽ
    int  iSignalLevel;//�ź�ǿ�ȡ�0~100��0��100���

    BVCU_PUCFG_WifiGeneral stGeneral;//��ͨ��ʽ��ֻ��iMode=0��������
    BVCU_PUCFG_WifiHotSpot stHostSpot;//�ȵ㷽ʽ��ֻ��iMode=1��������
    
}BVCU_PUCFG_Wifi;

typedef struct _BVCU_PUCFG_RadioNetwork{
    int bEnable;//�Ƿ�ʹ�ø�ģ�顣0-��ʹ�ã�1-ʹ��
    int iTypeAll[4];//ģ��֧�ֵ������������͡�BVCU_RADIONETWORK_TYPE_*
    int iTypeIndex;//��ǰʹ�õ���������
    
    char szModuleName[BVCU_MAX_NAME_LEN+1];//ģ����
    char szUserName[BVCU_MAX_NAME_LEN+1];//�û�������Ϊ�ձ�ʾ����Ĭ��
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//����

    char szAPN[BVCU_MAX_NAME_LEN+1];//APN������Ϊ�ձ�ʾ����Ĭ��
    char szAccessNum[BVCU_MAX_NAME_LEN+1];//�����

    char szCardNum[BVCU_MAX_MOBILEPHONE_NUM_LEN+1];//����

    int bOnline;//�Ƿ����ߡ�0-�����ߣ�1-����
    int iSignalLevel[4];//ÿ���������Ͷ�Ӧ���ź�ǿ�ȡ�0~100��0��100���
    int iOnlineTime;//����ʱ�䣬��λ��.����Ϊ-1��ʾ��������ʱ��
    int iTrafficDownload;//���ص����������ܼƣ���λMB�ֽ�.����Ϊ-1��ʾ������������
    int iTrafficUpload;//�ϴ������������ܼƣ���λMB�ֽ�.����Ϊ-1��ʾ������������
    int iSpeedDownload;//��ǰ�����ٶȣ���λKB/s
    int iSpeedUpload;//��ǰ�����ٶȣ���λKB/s
    int iReserved[4];
}BVCU_PUCFG_RadioNetwork;

//ע�������
typedef struct  _BVCU_PUCFG_RegisterServer{
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1];     //������IP������
    int  iPort;           //�������˿�
    int  iProto;         //ʹ�õ�Э�����ͣ�TCP/UDP
    char cReserved[16];
}BVCU_PUCFG_RegisterServer;

//�豸����������
typedef struct _BVCU_PUCFG_UpdateServer{
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1];    //������IP������
    int  iPort;                               //�˿�    
    char szUserName[BVCU_MAX_NAME_LEN+1];     //�û���
    char szPassword[BVCU_MAX_PASSWORD_LEN+1]; //����
    int  iProto;                              //Э��
    char szPath[BVCU_MAX_FILE_NAME_LEN+1];    //�豸�̼����·��
}BVCU_PUCFG_UpdateServer;

//Email������
typedef struct _BVCU_PUCFG_EmailServer{
    char szServerAddr[BVCU_MAX_HOST_NAME_LEN+1];//Email SMTP��������ַ��
    int  iServerPort;//Email�������˿�
    char szUserName[BVCU_MAX_NAME_LEN+1];//�ʺ���
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//����
    char szSenderAddr[BVCU_MAX_HOST_NAME_LEN+1];//�������ʼ���ַ
    int  bSSLEnable; //�Ƿ�ʹ��SSL��0-��ʹ�ܣ�1-ʹ��    
}BVCU_PUCFG_EmailServer;

// NTP����
#define BVCU_MAX_NTP_SERVER_COUNT 3
typedef struct  _BVCU_PUCFG_NTPServer{
    char szAddr[BVCU_MAX_NTP_SERVER_COUNT][BVCU_MAX_HOST_NAME_LEN+1];     //NTP������IP������
    int  iPort[BVCU_MAX_NTP_SERVER_COUNT];           //NTP������Ĭ�϶˿�Ϊ123
    int  iUpdateInterval; //����ʱ��������λ������
    int  bUpdateImmediately;//����ͬ��
    char cReserved[16];
}BVCU_PUCFG_NTPServer;

//��ʱ�豸��
typedef struct _BVCU_PUCFG_TimeSource{    
    int     iTimeZone; //����ʱ��
    int  bDST;//�Ƿ�ʹ������ʱ��0-��ʹ�ã�1-ʹ��
    BVCU_PUCFG_NTPServer stNTPServer;
    
    //���ȼ�����豸�ᱻ����ʹ��
    char iNTP; //NTP���ȼ�1-100, <=0��ʾ��ʹ��
    char iGPS; //GPS���ȼ�1-100, <=0��ʾ��ʹ�á�ֻ��֧��GPS���豸��������
    char iReserved[10];
}BVCU_PUCFG_TimeSource;

//DDNS
typedef struct _BVCU_PUCFG_DDNS{
    int bDDNS;//��̬������0-��ʹ�ã�1-ʹ�ã�-1-�豸��֧��
    char szDDNSProvider[BVCU_MAX_NAME_LEN+1];//DDNS�ṩ�̡�ֻ��bDDNS=1��������
    char szDDNSAddr[BVCU_MAX_HOST_NAME_LEN+1];//DDNS��������ַ��ֻ��bDDNS=1��������
    char szDDNSUserName[BVCU_MAX_NAME_LEN+1];//DDNS�û�����ֻ��bDDNS=1��������
    char szDDNSPassword[BVCU_MAX_PASSWORD_LEN+1];//DDNS���롣ֻ��bDDNS=1��������
    char szDynamicName[BVCU_MAX_HOST_NAME_LEN+1];//����Ķ�̬������ֻ��bDDNS=1��������
}BVCU_PUCFG_DDNS;

//����Server�������
typedef struct _BVCU_PUCFG_Servers{
    BVCU_PUCFG_RegisterServer stRegisterServer;
    BVCU_PUCFG_UpdateServer stUpdateServer;
    BVCU_PUCFG_TimeSource stTimeSource;
    BVCU_PUCFG_DDNS stDDNS;
    BVCU_PUCFG_EmailServer stEmailServer;
}BVCU_PUCFG_Servers;

//=======================�洢============================
//�洢�ƻ�
typedef struct _BVCU_PUCFG_Storage_Schedule{
    int iChannelIndex;
    BVCU_DayTimeSlice stWeekSnapshot[7][BVCU_MAX_DAYTIMESLICE_COUNT];//һ�ܵ�ץ��ʱ��Ƭ���֣�ÿ��BVCU_MAX_DAYTIMESLICE_COUNT��ʱ��Ƭ
    BVCU_DayTimeSlice stWeekRecord[7][BVCU_MAX_DAYTIMESLICE_COUNT];//һ�ܵ�¼��ʱ��Ƭ���֣�ÿ��BVCU_MAX_DAYTIMESLICE_COUNT��ʱ��Ƭ
    int   bRecordAudio;//�Ƿ�¼��Ƶ��0-���洢��1-�洢��
    BVCU_WallTime stBegin;//��ʼʱ��
    BVCU_WallTime stEnd;//����ʱ�䡣�ڿ�ʼ/����ʱ����η�Χ�ڣ��ƻ���Ч��
}BVCU_PUCFG_Storage_Schedule;

//������Ϣ
typedef struct _BVCU_PUCFG_Storage_Media{
    char szMediaName[BVCU_MAX_FILE_NAME_LEN+1];//Ψһ��ʶ�ô洢�������ֻ���·��
    int iStorageMediaType;//�洢ý�����͡�BVCU_STORAGEMEDIATYPE_*
    unsigned int iTotalSpace;//�ܿռ䡣��λMB 
    unsigned int iFreeSpace;//ʣ��ռ䡣��λMB    
    int bFormated;//�Ƿ��Ѹ�ʽ����0-δ��ʽ����1-��ʽ��
    int iReserved[2];
}BVCU_PUCFG_Storage_Media;

//�洢����
typedef struct _BVCU_PUCFG_Storage_Rule{
    int iNoSpaceRule;//������ʱ�������0-ֹͣ¼��1-���Ǿ�¼��
    int iAlarmSpace;//�ռ䲻�㱨�����ޡ���λMB����Ϊ0��ʾ������
    int iReserveDays;//¼���ļ���������
    int iRecordFileLength;//¼���ļ�ʱ�䳤�ȡ���λ�롣
    int bRecordGPS;//�Ƿ�洢GPS��Ϣ��0-���洢��1-�洢��
    int iReserved[2];
}BVCU_PUCFG_Storage_Rule;

//��ʽ���洢����������Ӧ��ʱ����BVCU_PUCFG_Storage_Format��Ϣ��ѯ��ʽ������
typedef struct _BVCU_PUCFG_Storage_Format{
    char szMediaName[BVCU_MAX_FILE_NAME_LEN+1];//Ψһ��ʶ�ô洢�������ֻ���·��
    int iAction;//0-��ѯ��ʽ�����ȣ�1-��ʼ��ʽ����
    int iPercent;//��ѯ���صĸ�ʽ�����ȡ�����ֵ0��100��100��ʾ��ʽ����ϣ�����ֵ-1��ʾ��ʽ��ʧ��
}BVCU_PUCFG_Storage_Format;

//�ֹ�Զ��¼��
typedef struct _BVCU_PUCFG_ManualRecord{
    int bStart;//1-��ʼ¼��0-ֹͣ¼��
    int iLength;//�洢ʱ�䳤�ȣ���λ��
}BVCU_PUCFG_ManualRecord;

//�ֹ�ץ�ģ����浽PU
typedef struct _BVCU_PUCFG_Snapshot{
    int iCount;//ץ���������������ֵΪ15
    int iInterval;//ץ�ļ������λ�롣�������ֵΪ60��
}BVCU_PUCFG_Snapshot;
//==========================�����ߣ�ע�ᣩ����===========================
//���ߴ�����ʽ
enum{
    BVCU_PU_ONLINE_TRIGGER_INVALID = 0,
    BVCU_PU_ONLINE_TRIGGER_MANUAL, //�ֶ�
    BVCU_PU_ONLINE_TRIGGER_ONTIME, //��ʱ
    BVCU_PU_ONLINE_TRIGGER_ONEVENT, //�¼�
};

//�����¼��������¼��������
enum{
    BVCU_PU_ONLINE_EVENT_ALERTIN = 1<<0,//��������
    BVCU_PU_ONLINE_EVENT_VIDEOMD = 1<<1,//��Ƶ�˶����
    BVCU_PU_ONLINE_EVENT_SMS     = 1<<2, //��������
};

//����;��
enum{
    BVCU_PU_ONLINE_THROUGH_INVALID  = 1<<0,    
    BVCU_PU_ONLINE_THROUGH_ETHERNET = 1<<1, //��̫��
    BVCU_PU_ONLINE_THROUGH_WIFI     = 1<<2 , //WIFI
    BVCU_PU_ONLINE_THROUGH_RADIO    = 1<<3, //����
};

//�������ߵĶ�������
typedef struct _BVCU_PUCFG_OnlineEventSMS{
    char szCardNum[16][BVCU_MAX_MOBILEPHONE_NUM_LEN+1];//����Ŀ��š��������16�����š�ֻ�����б��еĿ��ŲŻ�����������
    char szContent[128];//�������ݡ�ֻ��szCardNum��ƥ��szContent�Ķ��ŲŻᴥ��
    int  bReply;//PU�Ƿ�ظ�����
}BVCU_PUCFG_OnlineEventSMS;

typedef struct _BVCU_PUCFG_OnlineControlOne{
    int iTrigger;//������ʽ
    int iEvent;//�¼�������iEvent == BVCU_PU_ONLINE_TRIGGER_ONEVENTʱ������
    int iOnlineTime;//�¼����������ٱ���iOnlineTime������,-1��ʾһֱ�������ߡ�����iEvent == BVCU_PU_ONLINE_TRIGGER_ONEVENTʱ������
    int iThrough;//����;��,BVCU_PU_ONLINE_THROUGH_*�����
}BVCU_PUCFG_OnlineControlOne;

typedef struct _BVCU_PUCFG_OnlineControl{
    BVCU_DayTimeSlice stWeek[7][BVCU_MAX_DAYTIMESLICE_COUNT];//һ���е�ʱ��Ƭ��
    BVCU_PUCFG_OnlineControlOne stRCO[7][BVCU_MAX_DAYTIMESLICE_COUNT];//ÿ��ʱ��Ƭ�˶�Ӧ�����߷�ʽ
    BVCU_PUCFG_OnlineEventSMS stRESMS; //���Ŵ���������
}BVCU_PUCFG_OnlineControl;

/*TODO: 
   1���豸�ֶ�����
   2���ϴ���������/OSDͼƬ
   3��Զ��ץ��
   4������¼��
 
   1~4�漰���ϴ�/����Э�飬��Ҫͳһ����
*/

#endif

