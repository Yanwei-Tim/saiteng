#ifndef __BVCUCONST_H__
#define __BVCUCONST_H__

#ifdef _MSC_VER
#ifdef LIBBVCU_EXPORTS
#define LIBBVCU_API __declspec(dllexport)
#else
#define LIBBVCU_API __declspec(dllimport)
#endif
#else
#define LIBBVCU_API 
#endif//_MSC_VER

#define BVCU_MAX_CHANNEL_COUNT 64          //PU���ͨ����
#define BVCU_MAX_ID_LEN 31                 //PU/CU ID����
#define BVCU_MAX_NAME_LEN 63              //��ʾ�õ����ֳ���
#define BVCU_MAX_ID_NAME_LEN (BVCU_MAX_ID_LEN > BVCU_MAX_NAME_LEN ? BVCU_MAX_ID_LEN : BVCU_MAX_NAME_LEN)
#define BVCU_MAX_PASSWORD_LEN 63          //���볤��
#define BVCU_MAX_FILE_NAME_LEN 255         //�ļ�ȫ·����󳤶�
#define BVCU_MAX_HOST_NAME_LEN 127      //IP��ַ/������󳤶�
#define BVCU_MAX_SEDOMAIN_NAME_LEN 1023      //SmartEye������󳤶�
#define BVCU_LAT_LNG_UNIT 10000000.0       //��γ�ȵ�λ

#define BVCU_MAX_DAYTIMESLICE_COUNT 6      //һ�컮�ֵ�ʱ��Ƭ��Ŀ
#define BVCU_MAX_LANGGUAGE_COUNT 32        //֧�ֵ�������Ŀ
#define BVCU_MAX_MOBILEPHONE_NUM_LEN 15    //�ֻ����볤��
#define BVCU_MAX_ALARMLINKACTION_COUNT 64  //����������Ŀ

//PTZ
#define BVCU_PTZ_MAX_PROTOCOL_COUNT 32 //��̨֧�ֵ�Э�������Ŀ
#define BVCU_PTZ_MAX_PRESET_COUNT 256 //Ԥ�õ���Ŀ
#define BVCU_PTZ_MAX_CRUISE_COUNT 32 //Ѳ��·����Ŀ
#define BVCU_PTZ_MAX_CRUISEPOINT_COUNT 32 //ÿ��Ѳ��·����������Ԥ�õ���
#define BVCU_PTZ_MAX_NAME_LEN 31 //��̨��أ�����Ԥ�õ㡢Ѳ��·�ߣ�����
#define BVCU_PTZ_MAX_SPEED 15 //��̨�˶�����ٶ�
#define BVCU_PTZ_MIN_SPEED 1  //��̨�˶���С�ٶ�
#define BVCU_PTZ_MAX_LOCK_TIMEOUT 60 //��̨���������ʱ�䣬��λ�룬������ʱ��Server���Զ��������ͻ������ҲӦʵ���Զ�������

/*��������ֵ���¼�֪ͨ״̬��*/
typedef enum _BVCU_Result {
    BVCU_RESULT_E_FAILED  = -0x10000, /*general error*/
    BVCU_RESULT_E_INVALIDARG,         /*invalid argument*/
    BVCU_RESULT_E_UNSUPPORTED,        /*unsupported functions*/
    BVCU_RESULT_E_ALLOCMEMFAILED,    /*allocate memory failed*/ 
    BVCU_RESULT_E_MEMALIGNMENT,      /*memory alignment is not satisfied*/
    BVCU_RESULT_E_NOTFOUND,          /*not found*/
    BVCU_RESULT_E_NOTALLOWED,        /*the requested access is not allowed*/
    BVCU_RESULT_E_IO,                /*I/O error*/
    BVCU_RESULT_E_EOF,               /*End of file*/
    BVCU_RESULT_E_INVALIDDATA,       /*Invalid data found when processing input*/
    BVCU_RESULT_E_NOTIMPL,           /*not implemented*/
    BVCU_RESULT_E_BUSY,              /*busy.deny service now*/
    BVCU_RESULT_E_INUSE,              /*device in use*/
    BVCU_RESULT_E_BADREQUEST,        /*bad request*/
    BVCU_RESULT_E_AUTHORIZE_FAILED,    /*authorize failed����¼/��������ȵ�OnEvent�ص���ʹ��*/
    BVCU_RESULT_E_BADSTATE,          /*bad internal state*/ 
    BVCU_RESULT_E_NOTINITILIZED,      /*not initialized*/   
    BVCU_RESULT_E_FATALERROR,        /*fatal error. BVCU should be closed*/
    BVCU_RESULT_E_OUTOFSPACE,        /*out of space*/
    BVCU_RESULT_E_DISCONNECTED,      /*disconnected*/
    BVCU_RESULT_E_TIMEOUT,            /*time out*/
    BVCU_RESULT_E_CONNECTFAILED,      /*connect failed*/
    BVCU_RESULT_E_ABORTED,            /*request aborted*/
    BVCU_RESULT_E_THRAEDCONTEXT,      /*can not execute in the specified thread context*/
    BVCU_RESULT_E_UNAVAILABLE,        /*unavailable, eg: initialize a dialog with an offlined PU*/  
    BVCU_RESULT_E_ALREADYEXIST,       /*already exist*/  
    BVCU_RESULT_E_SEVERINTERNAL,      /*Server internal error*/
    BVCU_RESULT_E_MAXRETRIES,              /*�ﵽ������Դ���*/
    
    BVCU_RESULT_E_AAA_OBJECTNOTFOUND = -0x0F000,/*AAA �û�/�û���Ȳ����ڡ�SESSION_CLOSE�¼���OnEvent�ص���ʹ��*/

    BVCU_RESULT_S_OK = 0,            /*succeed*/
    BVCU_RESULT_S_IGNORE,            /*succeed,but something can not handle is ignored.*/
    BVCU_RESULT_S_PENDING,           /*operation is pending.*/
}BVCU_Result;
#define BVCU_Result_SUCCEEDED(a) ( ((int)(a)) >= 0 )
#define BVCU_Result_FAILED(a) ( ((int)(a)) < 0 )

//������
#define BVCU_VIDEO_QUALITY_COUNT 6

//DialogTarget.iIndexMajorȡֵ��Χ�����豸����
#define BVCU_SUBDEV_INDEXMAJOR_MIN_CHANNEL  0   //����Ƶͨ��
#define BVCU_SUBDEV_INDEXMAJOR_MAX_CHANNEL  0x00FFFF
#define BVCU_SUBDEV_INDEXMAJOR_MIN_GPS      0x010000 //GPS�豸����
#define BVCU_SUBDEV_INDEXMAJOR_MAX_GPS      0x0100FF
#define BVCU_SUBDEV_INDEXMAJOR_MIN_TSP      0x010100 //͸�������豸����
#define BVCU_SUBDEV_INDEXMAJOR_MAX_TSP      0x0101FF
#define BVCU_SUBDEV_INDEXMAJOR_MIN_CUSTOM   0xF00000 //�Զ����豸����
#define BVCU_SUBDEV_INDEXMAJOR_MAX_CUSTOM   0xF000FF

//��Ƶ���ʿ���
typedef enum _BVCU_RATECONTROL{
    BVCU_RATECONTROL_CBR = 0,
    BVCU_RATECONTROL_VBR,
    BVCU_RATECONTROL_CVBR
}BVCU_RATECONTROL;

//��Ƶ��ʽ
enum{
    BVCU_VIDEOFORMAT_UNKNOWN = 0,
    BVCU_VIDEOFORMAT_PAL,
    BVCU_VIDEOFORMAT_NTSC,
    BVCU_VIDEOFORMAT_CUSTOM,//�Զ���
};


//�洢����
enum{
    BVCU_STORAGE_RECORDTYPE_NONE =        0,//���洢
    BVCU_STORAGE_RECORDTYPE_MANUAL =  (1<<0),//�ֶ��洢
    BVCU_STORAGE_RECORDTYPE_ONTIME =  (1<<1),//��ʱ�洢
    BVCU_STORAGE_RECORDTYPE_ONALARM = (1<<2),//�����洢�����ݱ������������ã�
};

/*ý�巽��*/
enum{
    BVCU_MEDIADIR_VIDEOSEND = (1<<0),
    BVCU_MEDIADIR_VIDEORECV = (1<<1),
    BVCU_MEDIADIR_AUDIOSEND = (1<<2),
    BVCU_MEDIADIR_AUDIORECV = (1<<3),
    BVCU_MEDIADIR_TALKONLY  = (BVCU_MEDIADIR_AUDIOSEND | BVCU_MEDIADIR_AUDIORECV),
    BVCU_MEDIADIR_DATASEND  = (1<<4),
    BVCU_MEDIADIR_DATARECV  = (1<<5),
};


/*����״̬*/
enum {
    BVCU_ONLINE_STATUS_OFFLINE = 0,
    BVCU_ONLINE_STATUS_ONLINE,
};

/*�㲥״̬*/
enum {
    BVCU_BROADCAST_STATUS_TRYING = 0,
    BVCU_BROADCAST_STATUS_SUCCESS,
    BVCU_BROADCAST_STATUS_FAILED,
};

/*subscribe״̬*/
enum {
    BVCU_SUBSCRIBE_OFF = 0,
    BVCU_SUBSCRIBE_ON,
};

/*�����Э������*/
enum{
    BVCU_PROTOTYPE_TCP = 0,
    BVCU_PROTOTYPE_UDP,
    BVCU_PROTOTYPE_TLS,

    BVCU_PROTOTYPE_FTP,
    BVCU_PROTOTYPE_HTTP,
    BVCU_PROTOTYPE_SIP,
    BVCU_PROTOTYPE_RTSP,
};


/*CU����/���յ������BVCU_SendCmd���õ�*/
enum{
    BVCU_METHOD_UNKNOWN  = 0,
    BVCU_METHOD_QUERY,
    BVCU_METHOD_CONTROL,    
    BVCU_METHOD_SUBSCRIBE,

    BVCU_METHOD_RESERVED = 0x80000,//�������ڲ�ʹ��
};

/*CU����/���յ������ �����ע���У�
�������ͣ�ָBVCU_Command��pData��Ա���������ͣ���BVCU_Command.iSubMethod����
������ͣ���query/control��Command,BVCU_Command.OnEvent��pParamָ���((BVCU_Event_SessionCmd*)pParam)->stContent.pData
      ��subscribe��Command,���յ�������ͨ��BVCU_ServerParam.OnNotify֪ͨCU,pData->pData
      ��notify����BVCU_NotifyMsgContent.iSubMethodȷ����������
�������ͣ���ЩControl������CMSִ�к�CMS�ᷢ��һ��֪ͨ(notify)������(command)��BVCU��
          ע���С�ͬ��Notify/Command����ʾ�������ԭ������ͬSUBMETHOD���͵�Notify/Command��
          �޸�ע�͵�����ᴥ��Notify/Command��Query����ᴥ����

*/
enum{
    BVCU_SUBMETHOD_UNKNOWN = 0,

    BVCU_SUBMETHOD_CUSTOM = 0x00001, //�Զ������������͸��ת���������Ŀ�ꡣ�������ͣ��Զ��壻�������: �Զ��塣
                                     //ע�⣬BVCU_CmdMsgContent.pData����Ϊ���ֽ����ͣ�BVCU_CmdMsgContent.iDataCount��ʾ�ֽڳ���
    
    //=============query====================    
    //PU����---------------------------
    BVCU_SUBMETHOD_PU_LIST = 0x00010,//CU��Server��ȡ�豸�б��������ͣ��ޣ��������: BVCU_PUChannelInfo����
    BVCU_SUBMETHOD_PU_BROADCASTSTATUS,//��ȡ�㲥���豸�б�״̬(һ��Sessionֻ��ͬʱ��һ���㲥)���������ͣ��ޣ�������ͣ�BVCU_BroadcastStatus����
    BVCU_SUBMETHOD_PU_CHANNELINFO,//��ȡĳ��PU��BVCU_PUChannelInfo��Ϣ���������ͣ��ޣ��������: BVCU_PUChannelInfo
    BVCU_SUBMETHOD_PU_GPSDATA,//��ȡPU��GPSͨ�����ݡ��������ͣ��ޣ��������: BVCU_PUCFG_GPSData
    BVCU_SUBMETHOD_PU_STORAGE_MEDIA, //�洢����Ϣ������������; ������ͣ�BVCU_PUCFG_Storage_Media����    
    BVCU_SUBMETHOD_PU_GROUPLIST, // ��ȡ�豸�����б��������ͣ��ޣ�������ͣ�BVCU_PUCFG_GroupItem����
    BVCU_SUBMETHOD_PU_GROUPINFO, // ��ȡ�豸������Ϣ���������ͣ�BVCU_PUCFG_GroupItem��������ͣ�BVCU_PUCFG_GroupInfo
    BVCU_SUBMETHOD_PU_UPDATESTATUS, //�����̼���״̬���������ͣ��ޣ�������ͣ�BVCU_PUCFG_UpdateStatus
    
    //User����---------------------------
    BVCU_SUBMETHOD_USER_GROUPLIST = 0x01000,//��ȡ�û����б��������ͣ��ޣ�������ͣ�BVCU_UCFG_UserGroup����
    BVCU_SUBMETHOD_USER_GROUPINFO,//��ȡ�û�����Ϣ���������ͣ�BVCU_UCFG_UserGroup��������ͣ�BVCU_UCFG_UserGroupInfo
    BVCU_SUBMETHOD_USER_USERLIST, //��ȡ�û��б��������ͣ��ޣ�������ͣ�BVCU_UCFG_User����
    BVCU_SUBMETHOD_USER_USERINFO, //��ȡ�û���Ϣ���������ͣ�BVCU_UCFG_User�� ������ͣ�BVCU_UCFG_UserInfo
    BVCU_SUBMETHOD_USER_ONLINE, //��ȡ�����û��б��������ͣ��ޣ� ������ͣ�BVCU_UCFG_User_Online����

    //NRU����---------------------------
    BVCU_SUBMETHOD_NRU_LIST = 0x01400,//��ȡNRU�б��������ͣ��ޣ�������ͣ�BVCU_NRUCFG_NRUItem����
    BVCU_SUBMETHOD_NRU_SCHEDULE_LIST, //ĳ��NRU�洢�ƻ��б��������ͣ��ޣ�������ͣ�BVCU_NRUCFG_Storage_Schedule_ListItem����
    BVCU_SUBMETHOD_NRU_SCHEDULE_GET,//NRU��һ���洢�ƻ����������ͣ�BVCU_NRUCFG_Storage_Schedule_ListItem, ������ͣ�BVCU_NRUCFG_Storage_Schedule
    
    //CONF����---------------------------
    BVCU_SUBMETHOD_CONF_LIST = 0x01600,//��ȡ���л����б�BVCU_Command.szTargetID����ΪCMS ID���������ͣ��ޣ�������ͣ�BVCU_Conf_BaseInfo����
    BVCU_SUBMETHOD_CONF_INFO,//��ȡĳ��������ϸ��Ϣ��BVCU_Command.szTargetID����ΪCONF_ID@CMS(@CMS����ʡ�ԣ���ʾĿǰ��¼��CMS�����������ͣ��ޣ�������ͣ�BVCU_Conf_Info

    //ALARM����---------------------------
    BVCU_SUBMETHOD_LINKACTION_LIST = 0x01700,//��ȡ���������б�������ͣ�BVCU_Event_LinkAction_Base����
    BVCU_SUBMETHOD_LINKACTION_GET,//��ȡĳ������������ϸ��Ϣ���������ͣ�BVCU_Event_LinkAction_Base;������ͣ�BVCU_Event_LinkAction

    //Task����---------------------------
    BVCU_SUBMETHOD_TASK_LIST = 0x01800, //��ȡCMS�����б�������ͣ�BVCU_Task_Base����
    BVCU_SUBMETHOD_TASK_GET, //��ȡĳ��CMS������ϸ��Ϣ���������ͣ�BVCU_Task_Base;������ͣ�BVCU_Task_Info
    
    //=============query/control=============    
    /*ע�⣺query/control���͵����ͨ��query��control���������ͺ���������ǶԳƵģ�����
    BVCU_SUBMETHOD_PTZATTR������Queryʱ�����������ޣ����������BVCU_PUCFG_PTZAttr����Control
    ʱ����������BVCU_PUCFG_PTZAttr����������ޡ�����ע���У�ֻ����Control�Ĳ��������û���ر�˵����     
    Query�Ĳ�������/������;���Control�����/��������
    */
    //PU����
    BVCU_SUBMETHOD_PU_DEVICEINFO = 0x10000, //�豸��Ϣ���������ͣ�BVCU_PUCFG_DeviceInfo��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_PU_DEVICETIME,//�豸ʱ�䡣�������ͣ�BVCU_WallTime��������ͣ���
    BVCU_SUBMETHOD_PU_SERVERS, //ע����������������ͣ�BVCU_PUCFG_Servers��������ͣ���
    BVCU_SUBMETHOD_PU_ETHERNET, //��̫�����������ͣ�BVCU_PUCFG_Ethernet��������ͣ���
    BVCU_SUBMETHOD_PU_WIFI, //WIFI���������ͣ�BVCU_PUCFG_Wifi��������ͣ���
    BVCU_SUBMETHOD_PU_RADIONETWORK, //�������硣�������ͣ�BVCU_PUCFG_RadioNetwork���飻������ͣ���                                
    BVCU_SUBMETHOD_PU_PTZATTR,  //��̨���ԡ��������ͣ�BVCU_PUCFG_PTZAttr��������ͣ��ޣ�֪ͨ���ͣ�ͬ��Notify
    BVCU_SUBMETHOD_PU_ENCODERCHANNEL,//����ͨ�����ԡ��������ͣ�BVCU_PUCFG_EncoderChannel��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_PU_VIDEOIN,//��Ƶ�������ԡ��������ͣ�BVCU_PUCFG_VideoIn��������ͣ���
    BVCU_SUBMETHOD_PU_AUDIOIN,//��Ƶ�������ԡ��������ͣ�BVCU_PUCFG_AudioIn��������ͣ���
    BVCU_SUBMETHOD_PU_AUDIOOUT,//��Ƶ������ԡ��������ͣ�BVCU_PUCFG_AudioOut��������ͣ���
    BVCU_SUBMETHOD_PU_ALERTIN,//�����������ԡ��������ͣ�BVCU_PUCFG_AlertIn��������ͣ���
    BVCU_SUBMETHOD_PU_ALERTOUT,//����������ԡ��������ͣ�BVCU_PUCFG_AlertOut��������ͣ���
    BVCU_SUBMETHOD_PU_SERIALPORT,//�������ԡ��������ͣ�BVCU_PUCFG_SerialPort;������ͣ���
    BVCU_SUBMETHOD_PU_GPS,//GPS���ԡ��������ͣ�BVCU_PUCFG_GPSParam;������ͣ���
    BVCU_SUBMETHOD_PU_STORAGE_SCHEDULE, //�洢�ƻ����������ͣ�BVCU_PUCFG_Storage_Schdule;������ͣ���
    BVCU_SUBMETHOD_PU_STORAGE_RULE, //�洢���ԡ��������ͣ�BVCU_PUCFG_Storage_Rule;������ͣ���    
    BVCU_SUBMETHOD_PU_STORAGE_FORMAT,//��ʽ���洢�����������ͣ�BVCU_PUCFG_Storage_Format;������ͣ���    
    BVCU_SUBMETHOD_PU_ONLINECONTROL,//�����߿��ơ��������ͣ�BVCU_PUCFG_RegisterControl��������ͣ���
    BVCU_SUBMETHOD_PU_SNAPSHOTPARAM,//�����Զ�ץ�Ĳ������������ͣ�BVCU_PUCFG_SnapshotParam;������ͣ���
    BVCU_SUBMETHOD_PU_POWER,//���õ�Դ�������������ͣ�BVCU_PUCFG_Power;������ͣ���

    //NRU����
    BVCU_SUBMETHOD_NRU_INFO = 0x11000,//NRU��Ϣ���������ͣ�BVCU_NRUCFG_NRUInfo��������ͣ���
    
    //CONF����
    //BVCU_Command.szTargetID����ΪCONF_ID@CMS(@CMS����ʡ�ԣ���ʾĿǰ��¼��CMS��
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_VOLUME = 0x11200,//����ĳ�������ߵ�������ע��ֻӰ�챾��������������
                                                                                                         //�������ͣ�BVCU_Conf_Participator_Info��������ͣ���
    BVCU_SUBMETHOD_CONF_BASEINFO,//��ȡ/���û������Ϣ��BVCU_Command.szTargetID����ΪCONF_ID@CMS(@CMS����ʡ�ԣ���ʾĿǰ��¼��CMS����
                        //�������ͣ�BVCU_Conf_BaseInfo��������ͣ��ޣ��������ͣ�ͬ��Notify
                        //��ѯ������ͣ�BVCU_Conf_BaseInfo + BVCU_Conf_Participator_Info
                        
    //=============control=============    
    //PU����---------------------------
    BVCU_SUBMETHOD_PU_REBOOT = 0x20000,   //�����豸���������ͣ��ޣ�������ͣ���
    BVCU_SUBMETHOD_PU_DELETE,       //�����ݿ���ɾ��PU���������ͣ��ޣ��������: ��
    BVCU_SUBMETHOD_PU_SHUTDOWN, //�ر��豸���������ͣ��ޣ�������ͣ���
    BVCU_SUBMETHOD_PU_SAVECONFIG, //֪ͨ�豸�������á��������ͣ��ޣ�������ͣ���
    BVCU_SUBMETHOD_PU_PTZCONTROL,//������̨���������ͣ�BVCU_PUCFG_PTZControl��������ͣ���    
    BVCU_SUBMETHOD_PU_PUTOFFLINE,//�ֹ�ʹPU���ߡ��������ͣ��ޣ�������ͣ���
    BVCU_SUBMETHOD_PU_MANUALRECORD,//�ֹ�����/ֹͣPU¼���������ͣ�BVCU_PUCFG_ManualRecord��������ͣ���
    BVCU_SUBMETHOD_PU_UPGRADE, //�����̼����������ͣ�BVCU_PUCFG_Upgrade��������ͣ���
    BVCU_SUBMETHOD_PU_ADDGROUP, //����豸���顣�������ͣ�BVCU_PUCFG_GroupInfo��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_PU_MODGROUP, //�޸��豸���顣�������ͣ�BVCU_PUCFG_GroupInfo��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_PU_DELGROUP, //ɾ���豸���顣�������ͣ�BVCU_PUCFG_GroupItem��������ͣ��ޣ��������ͣ�ͬ��Notify
    //BVCU_SUBMETHOD_PU_SNAPSHOT, //�ֹ�Զ��ץ�ġ��������ͣ�BVCU_PUCFG_Snapshot��������ͣ���
        
    
    //User����---------------------------
    BVCU_SUBMETHOD_USER_ADDGROUP = 0x22000, //����û��顣�������ͣ�BVCU_UCFG_UserGroupInfo��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_USER_MODGROUP, //�޸��û��顣�������ͣ�BVCU_UCFG_UserGroupInfo��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_USER_DELGROUP, //ɾ���û��顣�������ͣ�BVCU_UCFG_UserGroup��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_USER_ADDUSER,  //����û����������ͣ�BVCU_UCFG_UserInfo��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_USER_MODUSER,  //�޸��û����������ͣ�BVCU_UCFG_UserInfo��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_USER_DELUSER,  //ɾ���û����������ͣ�BVCU_UCFG_User��������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_USER_MODPASSWD,//�޸��û����롣�������ͣ�BVCU_UCFG_ModPasswd��������ͣ���
    BVCU_SUBMETHOD_USER_KICKOUT,  //�߳������û����������ͣ�BVCU_UCFG_Kickout��������ͣ��ޣ��ݲ�ʹ��

    //NRU����---------------------------
    BVCU_SUBMETHOD_NRU_DELETE = 0x22400, //�����ݿ���ɾ��NRU���������ͣ��ޣ��������: ��
    BVCU_SUBMETHOD_NRU_SCHEDULE_SET,//����NRU��һ���洢�ƻ����������ͣ�BVCU_NRUCFG_Storage_Schedule, �������:��
    BVCU_SUBMETHOD_NRU_SCHEDULE_DEL,//ɾ��NRU��һ���洢�ƻ����������ͣ�BVCU_NRUCFG_Storage_Schedule_ListItem, �������:��
	BVCU_SUBMETHOD_NRU_MANUALRECORD,//�ֹ�����/ֹͣĳ��PU¼��NRU���������ͣ�BVCU_NRUCFG_ManualRecord��������ͣ���
	BVCU_SUBMETHOD_NRU_SNAPSHOT,   //�ֹ�ץ�ĵ�NRU���������ͣ�BVCU_NRUCFG_Snapshot��������ͣ���
	
    //CONF����---------------------------
    //�����ɾ���BVCU_CONF_PARTICIPATOR_POWER_ADMINȨ���ߵ���
    BVCU_SUBMETHOD_CONF_CREATE = 0x22600,//�������飬BVCU_Command.szTargetID����ΪCMS ID��
                                         // �������ͣ�BVCU_Conf_BaseInfo��
                                         // ������ͣ�BVCU_Conf_BaseInfo + BVCU_Conf_Participator_Info
                                                                             
    //��������BVCU_SUBMETHOD_CONF_*��������ر�˵��������BVCU_Command.szTargetID����ΪCONF_ID@CMS(@CMS����ʡ�ԣ���ʾĿǰ��¼��CMS��
    BVCU_SUBMETHOD_CONF_DELETE,//ɾ�����顣�������ͣ���;�������:�ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_CONF_START,//��ʼ���顣�������ͣ���;�������:�ޣ��������ͣ�BVCU_SUBMETHOD_CONF_START_CMD
    BVCU_SUBMETHOD_CONF_STOP,//ֹͣ���顣�������ͣ���;�������:�ޣ��������ͣ�ͬ��Notify
    
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD, //��ӻ�������ߡ�
                                                                                //�������ͣ�BVCU_Conf_Participator_Info���飬һ�ε����������BVCU_CONF_MAX_PARTICIPATOR_ONETIME����
                                                                                //������ͣ�BVCU_Conf_Participator_AddResult
                                                                                //�������ͣ�BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD_CMD�����������յ�����BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD_NOTIFY�����в������յ���
                                                                                
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_REMOVE,//ɾ����������ߡ��������ͣ�BVCU_Conf_Participator_Info���飻������ͣ��ޣ��������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_MODIFY,//�޸Ļ�������ߡ��������ͣ�BVCU_Conf_Participator_Info���飻������ͣ��ޣ��������ͣ�ͬ��Notify                                                                                

    //������BVCU_CONF_PARTICIPATOR_POWER_MODETATORȨ���ߵ���
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_INVITE_SPEAK,//�������ԡ�����BVCU_CONF_MODE_SPEAK_CHAIRMANģʽ�Ļ��������塣
                                                                                                //�������ͣ�BVCU_Conf_Participator_Info��������ͣ���
                                                                                                //�������ͣ�ͬ��Notify(���в������յ�)
                                                                                                //          BVCU_SUBMETHOD_CONF_PARTICIPATOR_INVITE_SPEAK_CMD(�����������յ�)
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_TERMINATE_SPEAK,//�������ԡ��������ͣ�BVCU_Conf_Participator_Info��������ͣ���;�������ͣ�ͬ��Notify
    //������participator����
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN,//���������顣�������ͣ�BVCU_Conf_Participator_Join��������ͣ��ޣ�
                                                                                //�������ͣ�BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN_NOTIFY�����в������յ���
                                                                                //            BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN_CMD(������ADMIN�յ���
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_EXIT,//�����˳����顣�������ͣ��ޣ�������ͣ��ޣ��������ͣ�BVCU_SUBMETHOD_CONF_PARTICIPATOR_EXIT_NOTIFY    
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK,//���뷢�ԣ�����BVCU_CONF_MODE_SPEAK_CHAIRMANģʽ�Ļ��������塣�������ͣ���;������ͣ��ޣ�
                                                                                                      //�������ͣ�BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK_NOTIFY    �����в������յ���
                                                                                                      //          BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK_CMD(������ADMIN�յ���
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_ENDSPEAK,//�������ԣ�����BVCU_CONF_MODE_SPEAK_CHAIRMANģʽ�Ļ��������塣�������ͣ���;������ͣ��ޣ�
                                                                                                         //�������ͣ�BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_ENDSPEAK_NOTIFY    
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_LEAVE,//��ʱ�뿪���顣�������ͣ��ޣ�������ͣ��ޣ�
                                                                                 //�������ͣ�BVCU_SUBMETHOD_CONF_PARTICIPATOR_LEAVE_NOTIFY
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_RETURN,//��LEAVE��ԣ��ص����顣�������ͣ�BVCU_Conf_Participator_Info;������ͣ��ޣ�
                                                                                    //�������ͣ�BVCU_SUBMETHOD_CONF_PARTICIPATOR_RETURN_NOTIFY    
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_INFO,//�������Լ��ı�����Ϣ(Ŀǰ��szAliasName������)��֪ͨCMS���������ͣ�BVCU_Conf_Participator_Info��������ͣ���;�������ͣ�ͬ��Notify
    
    //�¼���������---------------------------
    BVCU_SUBMETHOD_LINKACTION_ADD  = 0x22700,//��ӱ����������������ͣ�BVCU_Event_LinkAction;������ͣ���;�������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_LINKACTION_SET,//�޸ı����������������ͣ�BVCU_Event_LinkAction;������ͣ���;�������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_LINKACTION_DEL,//ɾ�������������������ͣ�BVCU_Event_LinkAction_Base;������ͣ���;�������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_EVENT_PROCESS,//�������������ͣ�BVCU_Event_SourceSaved;������ͣ���

    //���񲿷�-------------------------------
    BVCU_SUBMETHOD_TASK_ADD = 0x22800, //���CMS�����������ͣ�BVCU_Task_Info;������ͣ���;�������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_TASK_SET, //�޸�CMS�����������ͣ�BVCU_Task_Info;������ͣ���;�������ͣ�ͬ��Notify
    BVCU_SUBMETHOD_TASK_DEL, //ɾ��CMS�����������ͣ�BVCU_Task_Base;������ͣ���;�������ͣ�ͬ��Notify

    //=============subscribe/notify=============
    //������Ӧ���BVCU_NotifyMsgContent.szSourceID��ȷ����Ϣ�ķ�����
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD,//����BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN,//����BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_EXIT_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_EXIT,//����BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK,//����BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_ENDSPEAK_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_ENDSPEAK,//����BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_LEAVE_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_LEAVE,//����BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_RETURN_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_RETURN,//����BVCU_Conf_Participator_Info        
    
    BVCU_SUBMETHOD_LINKACTION_NOTIFY = 0x30000,//����BVCU_Event_LinkAction_Notify
	BVCU_SUBMETHOD_EVENT_NOTIFY,//����BVCU_Event_Source
	
    //=============�յ���command=======================
    //Ӧ�ó�����Session��OnCmmand�ص����յ�������������BVCU_Event_SessionCmd������ִ�������OnEvent�ص���BVCU_Event_SessionCmd.iResult��ʾ����ִ�н����
    //ע�⣺��������OnCommand�ص���ִ��OnEvent�ص���
    //ע��˵�������븺�ر�ʾ���յ�����أ����츺�ر�ʾOnEvent��BVCU_Event_SessionCmd.stContent�ĸ�������
    BVCU_SUBMETHOD_CONF_START_CMD = BVCU_SUBMETHOD_CONF_START, //���鿪ʼ��ѯ���Ƿ�μӡ�һ��Pariticipatorһ��ֻ�ܲμ�һ��Start����
                                                                                        //���븺�أ�BVCU_Conf_BaseInfo�����츺�أ���
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD_CMD = BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD,//�����������顣
                                                                                        //���븺�أ�BVCU_Conf_BaseInfo+�������ߵ�BVCU_Conf_Participator_Info
                                                                                        //���츺�أ����ͬ����룬����BVCU_Conf_Participator_Info��Ӧ�ó���Ӧ��дszAliasName������ͬ��Ϊ��
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN_CMD = BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN,//�û�������룬������Admin�յ���
                                                                                        //���븺�أ�BVCU_Conf_Participator_Info�����츺�أ���
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK_CMD = BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK,//�û������ԣ�������Adminͨ�����سɹ��������Ƿ�ͬ�⡣����BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_INVITE_SPEAK_CMD = BVCU_SUBMETHOD_CONF_PARTICIPATOR_INVITE_SPEAK,//���������ԡ����븺�أ�BVCU_Conf_Participator_Info�����츺�أ���
    
    //=============reserved=============
    BVCU_SUBMETHOD_RESERVED = 0x80000//֮���ֵ�ڲ�ʹ��
};


//PTZЭ��
enum {
    BVCU_PTZ_PROTO_INVALID = 0,
    BVCU_PTZ_PROTO_USERDEFINED,
    BVCU_PTZ_PROTO_PELCO_D,
    BVCU_PTZ_PROTO_PELCO_P,
    BVCU_PTZ_PROTO_SAMSUNG,
    BVCU_PTZ_PROTO_VISCA,
	BVCU_PTZ_PROTO_YAAN,
};

//PU��������
enum{
    BVCU_PUTYPE_ENCODER = 0,//������
    BVCU_PUTYPE_DECODER, //������
    BVCU_PUTYPE_STORAGE, //�洢��
};

//����
enum{
    BVCU_LANGUAGE_INVALID = 0,
    BVCU_LANGUAGE_ENGLISH,// Ӣ�� 
    BVCU_LANGUAGE_CHINESE_SIMPLIFIED,// �������� 
    BVCU_LANGUAGE_CHINESE_TRADITIONAL,// ��������
};

//WIFI��ȫ����
enum{
    BVCU_WIFI_SECURITY_TYPE_OPEN = 0,
    BVCU_WIFI_SECURITY_TYPE_SHARED,
    BVCU_WIFI_SECURITY_TYPE_WPA_PSK,
    BVCU_WIFI_SECURITY_TYPE_WPA2_PSK,
};

//WIFI��������
enum{
    BVCU_WIFI_CRYPT_TYPE_NONE = 0,
    BVCU_WIFI_CRYPT_TYPE_WEP_40,//64
    BVCU_WIFI_CRYPT_TYPE_WEP_104,//128
    BVCU_WIFI_CRYPT_TYPE_TKIP,
    BVCU_WIFI_CRYPT_TYPE_AES,//CCMP
};

//������������
enum{
    BVCU_RADIONETWORK_TYPE_INVALID = 0,
    BVCU_RADIONETWORK_TYPE_AUTO,
    BVCU_RADIONETWORK_TYPE_GPRS,
    BVCU_RADIONETWORK_TYPE_CDMA,
    BVCU_RADIONETWORK_TYPE_EDGE,
    BVCU_RADIONETWORK_TYPE_3GWCDMA,
    BVCU_RADIONETWORK_TYPE_3GTDSCDMA,
    BVCU_RADIONETWORK_TYPE_3GCDMA2K,
};

//��Ƶ����
enum{
    BVCU_AUDIOIN_INPUT_MIC = 0,
    BVCU_AUDIOIN_INPUT_LINEIN,
};

//�洢ý������
enum{
    BVCU_STORAGEMEDIATYPE_INVALID = 0,
    BVCU_STORAGEMEDIATYPE_SDCARD, //SD��
    BVCU_STORAGEMEDIATYPE_HDD,//Ӳ��
};

//encoder�豸֧�ֵ�������
enum{
    BVCU_ENCODERSTREAMTYPE_INVALID = -1,
    BVCU_ENCODERSTREAMTYPE_STORAGE,//�洢��
    BVCU_ENCODERSTREAMTYPE_PREVIEW,//������
    BVCU_ENCODERSTREAMTYPE_PICTURE,//ͼƬ��
};
#endif

