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

#define BVCU_MAX_CHANNEL_COUNT 64          //PU最大通道数
#define BVCU_MAX_ID_LEN 31                 //PU/CU ID长度
#define BVCU_MAX_NAME_LEN 63              //显示用的名字长度
#define BVCU_MAX_ID_NAME_LEN (BVCU_MAX_ID_LEN > BVCU_MAX_NAME_LEN ? BVCU_MAX_ID_LEN : BVCU_MAX_NAME_LEN)
#define BVCU_MAX_PASSWORD_LEN 63          //密码长度
#define BVCU_MAX_FILE_NAME_LEN 255         //文件全路径最大长度
#define BVCU_MAX_HOST_NAME_LEN 127      //IP地址/域名最大长度
#define BVCU_MAX_SEDOMAIN_NAME_LEN 1023      //SmartEye域名最大长度
#define BVCU_LAT_LNG_UNIT 10000000.0       //经纬度单位

#define BVCU_MAX_DAYTIMESLICE_COUNT 6      //一天划分的时间片数目
#define BVCU_MAX_LANGGUAGE_COUNT 32        //支持的语言数目
#define BVCU_MAX_MOBILEPHONE_NUM_LEN 15    //手机号码长度
#define BVCU_MAX_ALARMLINKACTION_COUNT 64  //报警联动数目

//PTZ
#define BVCU_PTZ_MAX_PROTOCOL_COUNT 32 //云台支持的协议最大数目
#define BVCU_PTZ_MAX_PRESET_COUNT 256 //预置点数目
#define BVCU_PTZ_MAX_CRUISE_COUNT 32 //巡航路线数目
#define BVCU_PTZ_MAX_CRUISEPOINT_COUNT 32 //每条巡航路线最多允许的预置点数
#define BVCU_PTZ_MAX_NAME_LEN 31 //云台相关（例如预置点、巡航路线）名字
#define BVCU_PTZ_MAX_SPEED 15 //云台运动最大速度
#define BVCU_PTZ_MIN_SPEED 1  //云台运动最小速度
#define BVCU_PTZ_MAX_LOCK_TIMEOUT 60 //云台锁定最长持续时间，单位秒，超过该时间Server会自动解锁。客户端软件也应实现自动解锁。

/*函数返回值及事件通知状态码*/
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
    BVCU_RESULT_E_AUTHORIZE_FAILED,    /*authorize failed。登录/发送命令等的OnEvent回调中使用*/
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
    BVCU_RESULT_E_MAXRETRIES,              /*达到最大重试次数*/
    
    BVCU_RESULT_E_AAA_OBJECTNOTFOUND = -0x0F000,/*AAA 用户/用户组等不存在。SESSION_CLOSE事件的OnEvent回调中使用*/

    BVCU_RESULT_S_OK = 0,            /*succeed*/
    BVCU_RESULT_S_IGNORE,            /*succeed,but something can not handle is ignored.*/
    BVCU_RESULT_S_PENDING,           /*operation is pending.*/
}BVCU_Result;
#define BVCU_Result_SUCCEEDED(a) ( ((int)(a)) >= 0 )
#define BVCU_Result_FAILED(a) ( ((int)(a)) < 0 )

//编码器
#define BVCU_VIDEO_QUALITY_COUNT 6

//DialogTarget.iIndexMajor取值范围与子设备类型
#define BVCU_SUBDEV_INDEXMAJOR_MIN_CHANNEL  0   //音视频通道
#define BVCU_SUBDEV_INDEXMAJOR_MAX_CHANNEL  0x00FFFF
#define BVCU_SUBDEV_INDEXMAJOR_MIN_GPS      0x010000 //GPS设备数据
#define BVCU_SUBDEV_INDEXMAJOR_MAX_GPS      0x0100FF
#define BVCU_SUBDEV_INDEXMAJOR_MIN_TSP      0x010100 //透明串口设备数据
#define BVCU_SUBDEV_INDEXMAJOR_MAX_TSP      0x0101FF
#define BVCU_SUBDEV_INDEXMAJOR_MIN_CUSTOM   0xF00000 //自定义设备数据
#define BVCU_SUBDEV_INDEXMAJOR_MAX_CUSTOM   0xF000FF

//视频码率控制
typedef enum _BVCU_RATECONTROL{
    BVCU_RATECONTROL_CBR = 0,
    BVCU_RATECONTROL_VBR,
    BVCU_RATECONTROL_CVBR
}BVCU_RATECONTROL;

//视频制式
enum{
    BVCU_VIDEOFORMAT_UNKNOWN = 0,
    BVCU_VIDEOFORMAT_PAL,
    BVCU_VIDEOFORMAT_NTSC,
    BVCU_VIDEOFORMAT_CUSTOM,//自定义
};


//存储类型
enum{
    BVCU_STORAGE_RECORDTYPE_NONE =        0,//不存储
    BVCU_STORAGE_RECORDTYPE_MANUAL =  (1<<0),//手动存储
    BVCU_STORAGE_RECORDTYPE_ONTIME =  (1<<1),//定时存储
    BVCU_STORAGE_RECORDTYPE_ONALARM = (1<<2),//报警存储（根据报警联动的配置）
};

/*媒体方向*/
enum{
    BVCU_MEDIADIR_VIDEOSEND = (1<<0),
    BVCU_MEDIADIR_VIDEORECV = (1<<1),
    BVCU_MEDIADIR_AUDIOSEND = (1<<2),
    BVCU_MEDIADIR_AUDIORECV = (1<<3),
    BVCU_MEDIADIR_TALKONLY  = (BVCU_MEDIADIR_AUDIOSEND | BVCU_MEDIADIR_AUDIORECV),
    BVCU_MEDIADIR_DATASEND  = (1<<4),
    BVCU_MEDIADIR_DATARECV  = (1<<5),
};


/*在线状态*/
enum {
    BVCU_ONLINE_STATUS_OFFLINE = 0,
    BVCU_ONLINE_STATUS_ONLINE,
};

/*广播状态*/
enum {
    BVCU_BROADCAST_STATUS_TRYING = 0,
    BVCU_BROADCAST_STATUS_SUCCESS,
    BVCU_BROADCAST_STATUS_FAILED,
};

/*subscribe状态*/
enum {
    BVCU_SUBSCRIBE_OFF = 0,
    BVCU_SUBSCRIBE_ON,
};

/*传输层协议类型*/
enum{
    BVCU_PROTOTYPE_TCP = 0,
    BVCU_PROTOTYPE_UDP,
    BVCU_PROTOTYPE_TLS,

    BVCU_PROTOTYPE_FTP,
    BVCU_PROTOTYPE_HTTP,
    BVCU_PROTOTYPE_SIP,
    BVCU_PROTOTYPE_RTSP,
};


/*CU发出/接收的命令。在BVCU_SendCmd中用到*/
enum{
    BVCU_METHOD_UNKNOWN  = 0,
    BVCU_METHOD_QUERY,
    BVCU_METHOD_CONTROL,    
    BVCU_METHOD_SUBSCRIBE,

    BVCU_METHOD_RESERVED = 0x80000,//保留供内部使用
};

/*CU发出/接收的子命令。 下面的注释中，
输入类型：指BVCU_Command的pData成员的数据类型，由BVCU_Command.iSubMethod决定
输出类型：对query/control等Command,BVCU_Command.OnEvent的pParam指向的((BVCU_Event_SessionCmd*)pParam)->stContent.pData
      对subscribe等Command,接收到的数据通过BVCU_ServerParam.OnNotify通知CU,pData->pData
      对notify，由BVCU_NotifyMsgContent.iSubMethod确定数据类型
触发类型：有些Control命令在CMS执行后，CMS会发送一个通知(notify)或命令(command)给BVCU。
          注释中“同名Notify/Command”表示会产生与原命令相同SUBMETHOD类型的Notify/Command。
          无改注释的命令不会触发Notify/Command。Query命令不会触发。

*/
enum{
    BVCU_SUBMETHOD_UNKNOWN = 0,

    BVCU_SUBMETHOD_CUSTOM = 0x00001, //自定义命令，服务器透明转发该命令给目标。输入类型：自定义；输出类型: 自定义。
                                     //注意，BVCU_CmdMsgContent.pData被认为是字节类型，BVCU_CmdMsgContent.iDataCount表示字节长度
    
    //=============query====================    
    //PU部分---------------------------
    BVCU_SUBMETHOD_PU_LIST = 0x00010,//CU从Server获取设备列表。输入类型：无；输出类型: BVCU_PUChannelInfo数组
    BVCU_SUBMETHOD_PU_BROADCASTSTATUS,//获取广播的设备列表状态(一个Session只能同时有一个广播)。输入类型：无；输出类型：BVCU_BroadcastStatus数组
    BVCU_SUBMETHOD_PU_CHANNELINFO,//获取某个PU的BVCU_PUChannelInfo信息。输入类型：无；输出类型: BVCU_PUChannelInfo
    BVCU_SUBMETHOD_PU_GPSDATA,//获取PU的GPS通道数据。输入类型：无；输出类型: BVCU_PUCFG_GPSData
    BVCU_SUBMETHOD_PU_STORAGE_MEDIA, //存储器信息。输入类型无; 输出类型：BVCU_PUCFG_Storage_Media数组    
    BVCU_SUBMETHOD_PU_GROUPLIST, // 获取设备分组列表。输入类型：无；输出类型：BVCU_PUCFG_GroupItem数组
    BVCU_SUBMETHOD_PU_GROUPINFO, // 获取设备分组信息。输入类型：BVCU_PUCFG_GroupItem；输出类型：BVCU_PUCFG_GroupInfo
    BVCU_SUBMETHOD_PU_UPDATESTATUS, //升级固件的状态。输入类型：无；输出类型：BVCU_PUCFG_UpdateStatus
    
    //User部分---------------------------
    BVCU_SUBMETHOD_USER_GROUPLIST = 0x01000,//获取用户组列表。输入类型：无；输出类型：BVCU_UCFG_UserGroup数组
    BVCU_SUBMETHOD_USER_GROUPINFO,//获取用户组信息。输入类型：BVCU_UCFG_UserGroup；输出类型：BVCU_UCFG_UserGroupInfo
    BVCU_SUBMETHOD_USER_USERLIST, //获取用户列表。输入类型：无；输出类型：BVCU_UCFG_User数组
    BVCU_SUBMETHOD_USER_USERINFO, //获取用户信息。输入类型：BVCU_UCFG_User； 输出类型：BVCU_UCFG_UserInfo
    BVCU_SUBMETHOD_USER_ONLINE, //获取在线用户列表。输入类型：无； 输出类型：BVCU_UCFG_User_Online数组

    //NRU部分---------------------------
    BVCU_SUBMETHOD_NRU_LIST = 0x01400,//获取NRU列表。输入类型：无；输出类型：BVCU_NRUCFG_NRUItem数组
    BVCU_SUBMETHOD_NRU_SCHEDULE_LIST, //某个NRU存储计划列表。输入类型：无；输出类型：BVCU_NRUCFG_Storage_Schedule_ListItem数组
    BVCU_SUBMETHOD_NRU_SCHEDULE_GET,//NRU的一条存储计划。输入类型：BVCU_NRUCFG_Storage_Schedule_ListItem, 输出类型：BVCU_NRUCFG_Storage_Schedule
    
    //CONF部分---------------------------
    BVCU_SUBMETHOD_CONF_LIST = 0x01600,//获取所有会议列表，BVCU_Command.szTargetID设置为CMS ID。输入类型：无；输出类型：BVCU_Conf_BaseInfo数组
    BVCU_SUBMETHOD_CONF_INFO,//获取某个会议详细信息，BVCU_Command.szTargetID设置为CONF_ID@CMS(@CMS可以省略，表示目前登录的CMS）。输入类型：无；输出类型：BVCU_Conf_Info

    //ALARM部分---------------------------
    BVCU_SUBMETHOD_LINKACTION_LIST = 0x01700,//获取报警联动列表。输出类型：BVCU_Event_LinkAction_Base数组
    BVCU_SUBMETHOD_LINKACTION_GET,//获取某个报警联动详细信息。输入类型：BVCU_Event_LinkAction_Base;输出类型：BVCU_Event_LinkAction

    //Task部分---------------------------
    BVCU_SUBMETHOD_TASK_LIST = 0x01800, //获取CMS任务列表。输出类型：BVCU_Task_Base数组
    BVCU_SUBMETHOD_TASK_GET, //获取某个CMS任务详细信息。输入类型：BVCU_Task_Base;输出类型：BVCU_Task_Info
    
    //=============query/control=============    
    /*注意：query/control类型的命令，通常query和control的输入类型和输出类型是对称的，例如
    BVCU_SUBMETHOD_PTZATTR命令做Query时，输入类型无，输出类型是BVCU_PUCFG_PTZAttr，做Control
    时，输入类型BVCU_PUCFG_PTZAttr，输出类型无。以下注释中，只表明Control的参数，如果没有特别说明，     
    Query的参数输入/输出类型就是Control的输出/输入类型
    */
    //PU部分
    BVCU_SUBMETHOD_PU_DEVICEINFO = 0x10000, //设备信息。输入类型：BVCU_PUCFG_DeviceInfo；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_PU_DEVICETIME,//设备时间。输入类型：BVCU_WallTime；输出类型：无
    BVCU_SUBMETHOD_PU_SERVERS, //注册服务器。输入类型：BVCU_PUCFG_Servers；输出类型：无
    BVCU_SUBMETHOD_PU_ETHERNET, //以太网。输入类型：BVCU_PUCFG_Ethernet；输出类型：无
    BVCU_SUBMETHOD_PU_WIFI, //WIFI。输入类型：BVCU_PUCFG_Wifi；输出类型：无
    BVCU_SUBMETHOD_PU_RADIONETWORK, //无线网络。输入类型：BVCU_PUCFG_RadioNetwork数组；输出类型：无                                
    BVCU_SUBMETHOD_PU_PTZATTR,  //云台属性。输入类型：BVCU_PUCFG_PTZAttr；输出类型：无；通知类型：同名Notify
    BVCU_SUBMETHOD_PU_ENCODERCHANNEL,//编码通道属性。输入类型：BVCU_PUCFG_EncoderChannel；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_PU_VIDEOIN,//视频输入属性。输入类型：BVCU_PUCFG_VideoIn；输出类型：无
    BVCU_SUBMETHOD_PU_AUDIOIN,//音频输入属性。输入类型：BVCU_PUCFG_AudioIn；输出类型：无
    BVCU_SUBMETHOD_PU_AUDIOOUT,//音频输出属性。输入类型：BVCU_PUCFG_AudioOut；输出类型：无
    BVCU_SUBMETHOD_PU_ALERTIN,//报警输入属性。输入类型：BVCU_PUCFG_AlertIn；输出类型：无
    BVCU_SUBMETHOD_PU_ALERTOUT,//报警输出属性。输入类型：BVCU_PUCFG_AlertOut；输出类型：无
    BVCU_SUBMETHOD_PU_SERIALPORT,//串口属性。输入类型：BVCU_PUCFG_SerialPort;输出类型：无
    BVCU_SUBMETHOD_PU_GPS,//GPS属性。输入类型：BVCU_PUCFG_GPSParam;输出类型：无
    BVCU_SUBMETHOD_PU_STORAGE_SCHEDULE, //存储计划。输入类型：BVCU_PUCFG_Storage_Schdule;输出类型：无
    BVCU_SUBMETHOD_PU_STORAGE_RULE, //存储属性。输入类型：BVCU_PUCFG_Storage_Rule;输出类型：无    
    BVCU_SUBMETHOD_PU_STORAGE_FORMAT,//格式化存储器。输入类型：BVCU_PUCFG_Storage_Format;输出类型：无    
    BVCU_SUBMETHOD_PU_ONLINECONTROL,//上下线控制。输入类型：BVCU_PUCFG_RegisterControl；输出类型：无
    BVCU_SUBMETHOD_PU_SNAPSHOTPARAM,//配置自动抓拍参数。输入类型：BVCU_PUCFG_SnapshotParam;输出类型：无
    BVCU_SUBMETHOD_PU_POWER,//配置电源参数。输入类型：BVCU_PUCFG_Power;输出类型：无

    //NRU部分
    BVCU_SUBMETHOD_NRU_INFO = 0x11000,//NRU信息。输入类型：BVCU_NRUCFG_NRUInfo；输出类型：无
    
    //CONF部分
    //BVCU_Command.szTargetID设置为CONF_ID@CMS(@CMS可以省略，表示目前登录的CMS）
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_VOLUME = 0x11200,//调整某个参与者的音量。注意只影响本地听到的音量。
                                                                                                         //输入类型：BVCU_Conf_Participator_Info，输出类型：无
    BVCU_SUBMETHOD_CONF_BASEINFO,//获取/设置会议的信息，BVCU_Command.szTargetID设置为CONF_ID@CMS(@CMS可以省略，表示目前登录的CMS）。
                        //输入类型：BVCU_Conf_BaseInfo；输出类型：无；触发类型：同名Notify
                        //查询输出类型：BVCU_Conf_BaseInfo + BVCU_Conf_Participator_Info
                        
    //=============control=============    
    //PU部分---------------------------
    BVCU_SUBMETHOD_PU_REBOOT = 0x20000,   //重启设备。输入类型：无；输出类型：无
    BVCU_SUBMETHOD_PU_DELETE,       //从数据库中删除PU。输入类型：无；输出类型: 无
    BVCU_SUBMETHOD_PU_SHUTDOWN, //关闭设备。输入类型：无；输出类型：无
    BVCU_SUBMETHOD_PU_SAVECONFIG, //通知设备保存配置。输入类型：无；输出类型：无
    BVCU_SUBMETHOD_PU_PTZCONTROL,//操作云台。输入类型：BVCU_PUCFG_PTZControl；输出类型：无    
    BVCU_SUBMETHOD_PU_PUTOFFLINE,//手工使PU下线。输入类型：无；输出类型：无
    BVCU_SUBMETHOD_PU_MANUALRECORD,//手工启动/停止PU录像。输入类型：BVCU_PUCFG_ManualRecord；输出类型：无
    BVCU_SUBMETHOD_PU_UPGRADE, //升级固件。输入类型：BVCU_PUCFG_Upgrade；输出类型：无
    BVCU_SUBMETHOD_PU_ADDGROUP, //添加设备分组。输入类型：BVCU_PUCFG_GroupInfo；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_PU_MODGROUP, //修改设备分组。输入类型：BVCU_PUCFG_GroupInfo；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_PU_DELGROUP, //删除设备分组。输入类型：BVCU_PUCFG_GroupItem；输出类型：无；触发类型：同名Notify
    //BVCU_SUBMETHOD_PU_SNAPSHOT, //手工远程抓拍。输入类型：BVCU_PUCFG_Snapshot；输出类型：无
        
    
    //User部分---------------------------
    BVCU_SUBMETHOD_USER_ADDGROUP = 0x22000, //添加用户组。输入类型：BVCU_UCFG_UserGroupInfo；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_USER_MODGROUP, //修改用户组。输入类型：BVCU_UCFG_UserGroupInfo；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_USER_DELGROUP, //删除用户组。输入类型：BVCU_UCFG_UserGroup；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_USER_ADDUSER,  //添加用户。输入类型：BVCU_UCFG_UserInfo；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_USER_MODUSER,  //修改用户。输入类型：BVCU_UCFG_UserInfo；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_USER_DELUSER,  //删除用户。输入类型：BVCU_UCFG_User；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_USER_MODPASSWD,//修改用户密码。输入类型：BVCU_UCFG_ModPasswd；输出类型：无
    BVCU_SUBMETHOD_USER_KICKOUT,  //踢出在线用户。输入类型：BVCU_UCFG_Kickout；输出类型：无，暂不使用

    //NRU部分---------------------------
    BVCU_SUBMETHOD_NRU_DELETE = 0x22400, //从数据库中删除NRU。输入类型：无；输出类型: 无
    BVCU_SUBMETHOD_NRU_SCHEDULE_SET,//配置NRU的一条存储计划。输入类型：BVCU_NRUCFG_Storage_Schedule, 输出类型:无
    BVCU_SUBMETHOD_NRU_SCHEDULE_DEL,//删除NRU的一条存储计划。输入类型：BVCU_NRUCFG_Storage_Schedule_ListItem, 输出类型:无
	BVCU_SUBMETHOD_NRU_MANUALRECORD,//手工启动/停止某个PU录像到NRU。输入类型：BVCU_NRUCFG_ManualRecord；输出类型：无
	BVCU_SUBMETHOD_NRU_SNAPSHOT,   //手工抓拍到NRU。输入类型：BVCU_NRUCFG_Snapshot；输出类型：无
	
    //CONF部分---------------------------
    //以下由具有BVCU_CONF_PARTICIPATOR_POWER_ADMIN权限者调用
    BVCU_SUBMETHOD_CONF_CREATE = 0x22600,//创建会议，BVCU_Command.szTargetID设置为CMS ID。
                                         // 输入类型：BVCU_Conf_BaseInfo，
                                         // 输出类型：BVCU_Conf_BaseInfo + BVCU_Conf_Participator_Info
                                                                             
    //以下所有BVCU_SUBMETHOD_CONF_*命令除非特别说明，否则BVCU_Command.szTargetID设置为CONF_ID@CMS(@CMS可以省略，表示目前登录的CMS）
    BVCU_SUBMETHOD_CONF_DELETE,//删除会议。输入类型：无;输出类型:无；触发类型：同名Notify
    BVCU_SUBMETHOD_CONF_START,//开始会议。输入类型：无;输出类型:无；触发类型：BVCU_SUBMETHOD_CONF_START_CMD
    BVCU_SUBMETHOD_CONF_STOP,//停止会议。输入类型：无;输出类型:无；触发类型：同名Notify
    
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD, //添加会议参与者。
                                                                                //输入类型：BVCU_Conf_Participator_Info数组，一次调用最多允许BVCU_CONF_MAX_PARTICIPATOR_ONETIME个。
                                                                                //输出类型：BVCU_Conf_Participator_AddResult
                                                                                //触发类型：BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD_CMD（被加入者收到）和BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD_NOTIFY（所有参与者收到）
                                                                                
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_REMOVE,//删除会议参与者。输入类型：BVCU_Conf_Participator_Info数组；输出类型：无；触发类型：同名Notify
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_MODIFY,//修改会议参与者。输入类型：BVCU_Conf_Participator_Info数组；输出类型：无；触发类型：同名Notify                                                                                

    //以下由BVCU_CONF_PARTICIPATOR_POWER_MODETATOR权限者调用
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_INVITE_SPEAK,//点名发言。仅对BVCU_CONF_MODE_SPEAK_CHAIRMAN模式的会议有意义。
                                                                                                //输入类型：BVCU_Conf_Participator_Info；输出类型：无
                                                                                                //触发类型：同名Notify(所有参与者收到)
                                                                                                //          BVCU_SUBMETHOD_CONF_PARTICIPATOR_INVITE_SPEAK_CMD(仅被点名者收到)
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_TERMINATE_SPEAK,//结束发言。输入类型：BVCU_Conf_Participator_Info；输出类型：无;触发类型：同名Notify
    //以下由participator调用
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN,//请求加入会议。输入类型：BVCU_Conf_Participator_Join；输出类型：无；
                                                                                //触发类型：BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN_NOTIFY（所有参与者收到）
                                                                                //            BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN_CMD(仅会议ADMIN收到）
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_EXIT,//请求退出会议。输入类型：无；输出类型：无；触发类型：BVCU_SUBMETHOD_CONF_PARTICIPATOR_EXIT_NOTIFY    
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK,//申请发言，仅对BVCU_CONF_MODE_SPEAK_CHAIRMAN模式的会议有意义。输入类型：无;输出类型：无；
                                                                                                      //触发类型：BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK_NOTIFY    （所有参与者收到）
                                                                                                      //          BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK_CMD(仅会议ADMIN收到）
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_ENDSPEAK,//结束发言，仅对BVCU_CONF_MODE_SPEAK_CHAIRMAN模式的会议有意义。输入类型：无;输出类型：无；
                                                                                                         //触发类型：BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_ENDSPEAK_NOTIFY    
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_LEAVE,//暂时离开会议。输入类型：无；输出类型：无；
                                                                                 //触发类型：BVCU_SUBMETHOD_CONF_PARTICIPATOR_LEAVE_NOTIFY
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_RETURN,//与LEAVE相对，回到会议。输入类型：BVCU_Conf_Participator_Info;输出类型：无：
                                                                                    //触发类型：BVCU_SUBMETHOD_CONF_PARTICIPATOR_RETURN_NOTIFY    
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_INFO,//更改了自己的本地信息(目前仅szAliasName有意义)，通知CMS。输入类型：BVCU_Conf_Participator_Info；输出类型：无;触发类型：同名Notify
    
    //事件联动部分---------------------------
    BVCU_SUBMETHOD_LINKACTION_ADD  = 0x22700,//添加报警联动。输入类型：BVCU_Event_LinkAction;输出类型：无;触发类型：同名Notify
    BVCU_SUBMETHOD_LINKACTION_SET,//修改报警联动。输入类型：BVCU_Event_LinkAction;输出类型：无;触发类型：同名Notify
    BVCU_SUBMETHOD_LINKACTION_DEL,//删除报警联动。输入类型：BVCU_Event_LinkAction_Base;输出类型：无;触发类型：同名Notify
    BVCU_SUBMETHOD_EVENT_PROCESS,//处警。输入类型：BVCU_Event_SourceSaved;输出类型：无

    //任务部分-------------------------------
    BVCU_SUBMETHOD_TASK_ADD = 0x22800, //添加CMS任务。输入类型：BVCU_Task_Info;输出类型：无;触发类型：同名Notify
    BVCU_SUBMETHOD_TASK_SET, //修改CMS任务。输入类型：BVCU_Task_Info;输出类型：无;触发类型：同名Notify
    BVCU_SUBMETHOD_TASK_DEL, //删除CMS任务。输入类型：BVCU_Task_Base;输出类型：无;触发类型：同名Notify

    //=============subscribe/notify=============
    //调用者应检查BVCU_NotifyMsgContent.szSourceID，确定信息的发出者
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD,//负载BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN,//负载BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_EXIT_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_EXIT,//负载BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK,//负载BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_ENDSPEAK_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_ENDSPEAK,//负载BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_LEAVE_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_LEAVE,//负载BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_RETURN_NOTIFY = BVCU_SUBMETHOD_CONF_PARTICIPATOR_RETURN,//负载BVCU_Conf_Participator_Info        
    
    BVCU_SUBMETHOD_LINKACTION_NOTIFY = 0x30000,//负载BVCU_Event_LinkAction_Notify
	BVCU_SUBMETHOD_EVENT_NOTIFY,//负载BVCU_Event_Source
	
    //=============收到的command=======================
    //应用程序在Session的OnCmmand回调中收到命令，处理后配置BVCU_Event_SessionCmd参数并执行命令的OnEvent回调，BVCU_Event_SessionCmd.iResult表示命令执行结果。
    //注意：不可以在OnCommand回调中执行OnEvent回调。
    //注释说明，输入负载表示接收的命令负载，回响负载表示OnEvent的BVCU_Event_SessionCmd.stContent的负载类型
    BVCU_SUBMETHOD_CONF_START_CMD = BVCU_SUBMETHOD_CONF_START, //会议开始，询问是否参加。一个Pariticipator一次只能参加一个Start会议
                                                                                        //输入负载：BVCU_Conf_BaseInfo；回响负载：无
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD_CMD = BVCU_SUBMETHOD_CONF_PARTICIPATOR_ADD,//被邀请加入会议。
                                                                                        //输入负载：BVCU_Conf_BaseInfo+本参与者的BVCU_Conf_Participator_Info
                                                                                        //回响负载：如果同意加入，负载BVCU_Conf_Participator_Info（应用程序应填写szAliasName），不同意为空
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN_CMD = BVCU_SUBMETHOD_CONF_PARTICIPATOR_JOIN,//用户请求加入，仅会议Admin收到。
                                                                                        //输入负载：BVCU_Conf_Participator_Info；回响负载：无
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK_CMD = BVCU_SUBMETHOD_CONF_PARTICIPATOR_APPLYFOR_STARTSPEAK,//用户请求发言，，会议Admin通过返回成功与否决定是否同意。负载BVCU_Conf_Participator_Info
    BVCU_SUBMETHOD_CONF_PARTICIPATOR_INVITE_SPEAK_CMD = BVCU_SUBMETHOD_CONF_PARTICIPATOR_INVITE_SPEAK,//被点名发言。输入负载：BVCU_Conf_Participator_Info；回响负载：无
    
    //=============reserved=============
    BVCU_SUBMETHOD_RESERVED = 0x80000//之后的值内部使用
};


//PTZ协议
enum {
    BVCU_PTZ_PROTO_INVALID = 0,
    BVCU_PTZ_PROTO_USERDEFINED,
    BVCU_PTZ_PROTO_PELCO_D,
    BVCU_PTZ_PROTO_PELCO_P,
    BVCU_PTZ_PROTO_SAMSUNG,
    BVCU_PTZ_PROTO_VISCA,
	BVCU_PTZ_PROTO_YAAN,
};

//PU功能类型
enum{
    BVCU_PUTYPE_ENCODER = 0,//编码器
    BVCU_PUTYPE_DECODER, //解码器
    BVCU_PUTYPE_STORAGE, //存储器
};

//语言
enum{
    BVCU_LANGUAGE_INVALID = 0,
    BVCU_LANGUAGE_ENGLISH,// 英文 
    BVCU_LANGUAGE_CHINESE_SIMPLIFIED,// 简体中文 
    BVCU_LANGUAGE_CHINESE_TRADITIONAL,// 繁体中文
};

//WIFI安全类型
enum{
    BVCU_WIFI_SECURITY_TYPE_OPEN = 0,
    BVCU_WIFI_SECURITY_TYPE_SHARED,
    BVCU_WIFI_SECURITY_TYPE_WPA_PSK,
    BVCU_WIFI_SECURITY_TYPE_WPA2_PSK,
};

//WIFI加密类型
enum{
    BVCU_WIFI_CRYPT_TYPE_NONE = 0,
    BVCU_WIFI_CRYPT_TYPE_WEP_40,//64
    BVCU_WIFI_CRYPT_TYPE_WEP_104,//128
    BVCU_WIFI_CRYPT_TYPE_TKIP,
    BVCU_WIFI_CRYPT_TYPE_AES,//CCMP
};

//无线网络类型
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

//音频输入
enum{
    BVCU_AUDIOIN_INPUT_MIC = 0,
    BVCU_AUDIOIN_INPUT_LINEIN,
};

//存储媒体类型
enum{
    BVCU_STORAGEMEDIATYPE_INVALID = 0,
    BVCU_STORAGEMEDIATYPE_SDCARD, //SD卡
    BVCU_STORAGEMEDIATYPE_HDD,//硬盘
};

//encoder设备支持的流类型
enum{
    BVCU_ENCODERSTREAMTYPE_INVALID = -1,
    BVCU_ENCODERSTREAMTYPE_STORAGE,//存储流
    BVCU_ENCODERSTREAMTYPE_PREVIEW,//传输流
    BVCU_ENCODERSTREAMTYPE_PICTURE,//图片流
};
#endif

