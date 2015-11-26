#ifndef __PU_CONFIG_H__
#define __PU_CONFIG_H__

#include "BVCUConst.h"
#include "BVCUCommon.h"
//##!!!!!!!注意：所有结构体的Reserved成员必须初始化为0!!!!!!!!!!!!!
//
//设备信息，除非注明可写，否则其成员只读
typedef struct _BVCU_PUCFG_DeviceInfo{
    char szID[BVCU_MAX_ID_LEN+1];             //设备ID
    char szManufacturer[BVCU_MAX_NAME_LEN+1]; //制造商名字
    char szProductName[BVCU_MAX_NAME_LEN+1];    //产品名
    char szSoftwareVersion[BVCU_MAX_NAME_LEN+1]; //软件版本
    char szHardwareVersion[BVCU_MAX_NAME_LEN+1]; //硬件版本    
    int  iPUType;          //BVCU_PUTYPE_*
    int  iLanguage[BVCU_MAX_LANGGUAGE_COUNT];    //支持的语言列表。BVCU_LANGUAGE_*
    int  iLanguageIndex;  //当前使用的语言索引。可写
    char szName[BVCU_MAX_NAME_LEN+1];//名字。可写
    int  iWIFICount;      //WIFI数目
    int  iRadioCount;     //无线模块数目
    int  iChannelCount;   //音视频通道数
    int  iVideoInCount;   //视频输入数
    int  iAudioInCount;   //音频输入数
    int  iAudioOutCount;  //音频输出数
    int  iPTZCount;       //PTZ数
    int  iSerialPortCount;//串口数 
    int  iAlertInCount;   //报警输入数
    int  iAlertOutCount;  //报警输出数
    int  iStorageCount;   //存储设备数
    int  iGPSCount;       //GPS设备数
    int  bSupportSMS;     //是否支持手机短信功能。0-不支持，1-支持

    int  iPresetCount; //支持的PTZ预置点数目
    int  iCruiseCount; //支持的PTZ巡航点数目
    int  iAlarmLinkActionCount; //支持的报警联动数目

    //PU位置
    int  iLongitude; //经度，东经是正值，西经负值，单位1/10000000度。大于180度或小于-180度表示无效值
    int  iLatitude; //纬度，北纬是正值，南纬是负值，单位1/10000000度。大于180度或小于-180度表示无效值

    int  iReserved[8];
}BVCU_PUCFG_DeviceInfo;

//升级固件状态
typedef struct _BVCU_PUCFG_UpdateStatus{
	int  iDownloadPercent;//下载百分比，0～100
	int  iSpeed;//下载速度，单位KB/s
	char szSoftwareVersion[BVCU_MAX_NAME_LEN+1]; //当前软件版本
	BVCU_WallTime stUpdateFinishTime;//当前软件版本升级完成时刻	
}BVCU_PUCFG_UpdateStatus;

typedef struct _BVCU_PUCFG_UPGRADE_{
    char szFTPID[BVCU_MAX_ID_LEN+1]; // 指定的升级ftp服务器
    char szFilePath[BVCU_MAX_FILE_NAME_LEN+1]; // 指定的文件路径
    char szFileName[BVCU_MAX_NAME_LEN+1]; // 指定的文件名称
    int  bPromptly; // 是否立即升级，0-下次启动时再升级  1-立即升级
}BVCU_PUCFG_Upgrade;

//设备组下的PU描述信息
typedef struct _BVCU_PUCFG_GroupPU{
    char  szPUID[BVCU_MAX_ID_LEN+1]; // PU ID
}BVCU_PUCFG_GroupPU;

//设备组列表
typedef struct _BVCU_PUCFG_GroupItem{
    char szID[BVCU_MAX_ID_LEN+1]; // 组的id标识符

    char szName[BVCU_MAX_NAME_LEN+1]; // 组的名称 

    char szParentID[BVCU_MAX_ID_LEN+1]; // 组的上级组, 顶层组的此值为空
}BVCU_PUCFG_GroupItem;

//设备组信息
typedef struct _BVCU_PUCFG_GroupInfo{
    char szID[BVCU_MAX_ID_LEN+1]; // 组的id标识符

    char szName[BVCU_MAX_NAME_LEN+1]; // 组的名称 

    char szParentID[BVCU_MAX_ID_LEN+1]; // 组的上级组, 顶层组的此值为空
    
    char szDescription[BVCU_MAX_SEDOMAIN_NAME_LEN+1]; //  描述该组的一些信息

    int  iPUCount; // PU组下PU数目

    BVCU_PUCFG_GroupPU* pPU; // PU组下PU数组
}BVCU_PUCFG_GroupInfo;

//电源参数
typedef struct _BVCU_PUCFG_Power{
    int iTurnOffDelay;//定时关机延时。单位秒
    int bEnableTimer;//是否允许定时开关机。0-不允许，1-允许
    BVCU_DayTimeSlice stTurnOn[7][2];//每周7天中的每天开机时刻（允许2个片段），该时刻之外的时间认为是关机。
}BVCU_PUCFG_Power;

//摄像头采集参数
typedef struct  _BVCU_PUCFG_VideoColorCtl{
    BVCU_DayTimeSlice stTime; //时间片
    char cBrightness;  //亮度,取值范围[0,255]
    char cContrast;    //对比度,取值范围[0,255]
    char cSaturation;  //饱和度,取值范围[0,255]
    char cHue;         //色调,取值范围[0,255]
}BVCU_PUCFG_VideoColorCtl;

//运动检测
#define BVCU_PUCFG_MAX_MD_COUNT 16
typedef struct _BVCU_PUCFG_MotionDetect{
    int bSupport;//0-不支持，1-支持。只读
    BVCU_ImageRect rcROI[BVCU_PUCFG_MAX_MD_COUNT];//区域，以16x16为单位，
    int iSensitivity;//灵敏度，范围0~10,0表示不检测，越大的值越灵敏
    int iInterval;//每次检测时间间隔，单位毫秒。大的间隔可以节省计算时间，对使用电池供电的设备，还可以延长电池使用寿命
    //但有可能漏检运动事件。设为0则每帧视频都做检测。
}BVCU_PUCFG_MotionDetect;

//视频区域遮盖
#define BVCU_PUCFG_MAX_SHELTER_COUNT 4
typedef struct _BVCU_PUCFG_VideoShelter{ //
    int bSupport;//0-不支持，1-支持。只读
    BVCU_ImageRect rcROI[BVCU_PUCFG_MAX_SHELTER_COUNT];//区域，以16x16为单位，
}BVCU_PUCFG_VideoShelter;

//镜头遮挡检测
#define BVCU_PUCFG_MAX_VIDEOOCCLUSION_COUNT 4
typedef struct _BVCU_PUCFG_VideoOcclusionDetect{ //
    int bSupport;//0-不支持，1-支持。只读
    BVCU_ImageRect rcROI[BVCU_PUCFG_MAX_VIDEOOCCLUSION_COUNT];//区域，以16x16为单位，
}BVCU_PUCFG_VideoOcclusionDetect;

//视频输入
enum{
    //注意：格式1/2/9/10/11/12年月日之间的分隔符由BVCU_PUCFG_VideoIn.cOSDTimeSplitChar决定
    BVCU_OSD_TIMEFORMAT_INVALID = 0,//不叠加时间
    BVCU_OSD_TIMEFORMAT_1,//YYYY-MM-DD hh:mm:ss
    BVCU_OSD_TIMEFORMAT_2,//YYYY-MM-DD 星期W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_3,//DD日MM月YYYY年 hh:mm:ss
    BVCU_OSD_TIMEFORMAT_4,//DD日MM月YYYY年 星期W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_5,//MM月DD日YYYY年 hh:mm:ss
    BVCU_OSD_TIMEFORMAT_6,//MM月DD日YYYY年 星期W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_7,//YYYY年MM月DD日 hh:mm:ss
    BVCU_OSD_TIMEFORMAT_8,//YYYY年MM月DD日 星期W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_9,//DD-MM-YYYY hh:mm:ss
    BVCU_OSD_TIMEFORMAT_10,//DD-MM-YYYY 星期W hh:mm:ss
    BVCU_OSD_TIMEFORMAT_11,//MM-DD-YYYY hh:mm:ss
    BVCU_OSD_TIMEFORMAT_12,//MM-DD-YYYY 星期W hh:mm:ss
};


typedef struct  _BVCU_PUCFG_VideoIn{
    BVCU_PUCFG_VideoColorCtl stVCC[2];//色彩控制。0表示白天的配置，1表示晚上的配置

    BVCU_PUCFG_MotionDetect stMD;//运动检测
    BVCU_PUCFG_VideoShelter stShelter;//区域遮盖
    BVCU_PUCFG_VideoOcclusionDetect stOcclusion; //镜头遮挡检测

    BVCU_VideoFormat stVideoFormat[4];    
    int iVideoFormatIndex;//当前视频制式索引。
    
    //叠加图标。
    int iOSDIcon;//-1表示设备不支持叠加图标，==0表示支持但目前没有叠加，==1表示支持并且已叠加
    char szOSDIcon[BVCU_MAX_FILE_NAME_LEN+1];//图片的本地路径,查询时无意义。为空表示不更新图片，否则把本地图片上传到PU
    BVCU_ImagePos stOSDIconPos; //位置。

    char szOSDTitle[BVCU_MAX_NAME_LEN+1];//叠加文字
    BVCU_ImagePos stOSDTitlePos; //位置
    char  iOSDTitleFontSize; //字体大小

    char  cOSDTime;//叠加时间格式，BVCU_OSD_TIMEFORMAT_*
    char  cOSDTimeSplitChar;//YYYY-MM-DD 之间的分隔符。常见的是'.' '-'和'/'。
    char  iOSDTimeFontSize; //字体大小
    BVCU_ImagePos stOSDTimePos; //位置
    int iReserved[4];
}BVCU_PUCFG_VideoIn;

typedef struct  _BVCU_PUCFG_AudioIn{
    int iInput;//BVCU_AUDIOIN_INPUT_*
    int iChannelCount;//声道数
    int iSamplesPerSec;//采样率
    int iBitsPerSample;//采样精度
    int iVolume;//音量
}BVCU_PUCFG_AudioIn;

typedef struct _BVCU_PUCFG_AudioDecoderParam{
    int bEnable;//0-禁止，1-使能
    SAVCodec_ID iAudioCodecAll[4];//支持的音频解码器ID。只读
    int iAudioCodecIndex;//当前使用的音频解码器
}BVCU_PUCFG_AudioDecoderParam;

//音频输出
typedef struct  _BVCU_PUCFG_AudioOut{
    BVCU_PUCFG_AudioDecoderParam stADParam;//音频解码参数。
    int iChannelCount;//声道数
    int iSamplesPerSec;//采样率
    int iBitsPerSample;//采样精度
    int iVolume;//音量
    int iReserved[4];
}BVCU_PUCFG_AudioOut;

//报警输入
typedef struct  _BVCU_PUCFG_AlertIn{
    int bType;//0-常开报警 1-常闭报警
    int iInterval;//检测间隔，单位秒。检测到报警输入后，经过iInterval秒再次检测
}BVCU_PUCFG_AlertIn;

//报警输出
typedef struct  _BVCU_PUCFG_AlertOut{
    int bAction; //0-通，1-断，
    int iDuration; //输出持续时间，单位秒
}BVCU_PUCFG_AlertOut;

//GPS
typedef struct  _BVCU_PUCFG_GPSParam{
    int bEnable;        //是否使能
    int iReportInterval;//采样时间间隔，单位秒
    int iReserved[2];//保留，设置为0
}BVCU_PUCFG_GPSParam;

typedef struct  _BVCU_PUCFG_GPSData{
    BVCU_WallTime stTime;//数据对应的时间
    int  iLongitude; //经度，东经是正值，西经负值，单位1/10000000度
    int  iLatitude; //纬度，北纬是正值，南纬是负值，单位1/10000000度
    int  iHeight; //高度，单位1/100米
    int  iAngle; //方向角(正北方向为原点，顺时针为正),单位1/1000度
    int  iSpeed; //速度(米/小时)         
    int  iStarCount;  //定位星数      
    int  bAntennaState; //天线状态(1-好，0-坏) 
    int  bOrientationState;//定位状态(1-定位，0-不定位) 
    int  iReserved[4];
}BVCU_PUCFG_GPSData;

//自动抓拍参数
enum {
    BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_TIME = (1<<0),
    BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_TEXT = (1<<1),
    BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_GPS =  (1<<2),
    BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_ALARM = (1<<3),    
};
typedef struct  _BVCU_PUCFG_SnapshotParam{
    BVCU_ImageSize iImageSize;//图像分辨率
    int iQuality;//抓拍JPG压缩质量，取值范围1～100    
    int iSequencePicCount; //单次连拍图片数。
    int iSequenceInterval;//连拍的每张图片时间间隔。单位毫秒
    int iSequenceDelay;//一次连拍周期结束后延时时间。单位毫秒。
    int iOverlay;//叠加信息标志。BVCU_PUCFG_SNAPSHOTPARAM_OVERLAY_*的组合
    int iReserved[2];//保留，必须设置为0
}BVCU_PUCFG_SnapshotParam;

//音频编码器参数
//
typedef struct _BVCU_PUCFG_AudioEncoderParam{
    SAVCodec_ID iCodecID;//编码器ID
    char iChannelCount[4];//可选声道数，0表示无效值。只读
    char iBitsPerSample[4];//可选采样精度，0表示无效值。只读
    int iSamplesPerSec[8];//可选采样率，0表示无效值。只读
    int iBitRate[8];//可选的码率，0表示无效的码率。单位bits/s。只读
    char iChannelCountIndex;//当前使用的声道索引
    char iBitsPerSampleIndex;//当前使用的采样精度索引
    char iSamplesPerSecIndex;//当前使用的采样率索引
    char iBitRateIndex;//当前使用的码率索引
}BVCU_PUCFG_AudioEncoderParam;

//编码器压缩参数
typedef struct _BVCU_PUCFG_EncoderParam{
    BVCU_DayTimeSlice stTime; //时间片。不同的时间可以采用不同的编码参数

    //视频编码属性
    int bVideoEnable;//0-禁止，1-使能
    SAVCodec_ID iVideoCodecAll[4];//支持的视频编码器ID。只读
    int iVideoCodecIndex;//当前使用的视频编码器
    BVCU_RATECONTROL iRateControl;//码率控制类型
    BVCU_ImageRect iImageRectAll[8];//可选的视频编码区域，全0表示无效的编码区域
    int iImageRectIndex;//当前使用的视频编码区域
    int iFramesPerSec;//单位1/1000帧。例如25fps，需要设置为25*1000。该帧率不能超过BVCU_PUCFG_VideoIn.iFPSMax
    int iKeyFrameInterval;//关键帧间隔
    int iImageQuality;//视频编码质量，取值范围1~BVCU_VIDEO_QUALITY_COUNT
    int iKbpsLimitMin[BVCU_VIDEO_QUALITY_COUNT];//每个质量等级对应的码率限制最小值。只读
    int iKbpsLimitMax;//码率限制最大值，由视频分辨率/帧率决定。只读
    int iKbpsLimitCurrent;//码率限制当前值。
    int iReserved1[4];

    //音频编码属性
    int bAudioEnable;//0-禁止，1-使能
    BVCU_PUCFG_AudioEncoderParam iAudioCodecAll[4];//支持的音频编码器ID。只读
    int iAudioCodecIndex;//当前使用的音频编码器
    int iReserved2[4];
}BVCU_PUCFG_EncoderParam;

typedef struct _BVCU_PUCFG_EncoderStreamParam{
    int iCount;//编码器配置的时间片个数
    BVCU_PUCFG_EncoderParam* pstParams;//编码器配置，每个成员对应一个时间片段的设置
    int iStreamType;//流类型。BVCU_ENCODERSTREAMTYPE_*
    int bEnableTransfer;//是否允许传输。0-不允许，1-允许
    int iReserved[4];
}BVCU_PUCFG_EncoderStreamParam;

//编码器通道
typedef struct  _BVCU_PUCFG_EncoderChannel{
    char szName[BVCU_MAX_NAME_LEN+1];
    int iCount;//支持的码流个数。只读
    BVCU_PUCFG_EncoderStreamParam* pParams;
    char iVideoInIndex;//摄像头索引，-1表示不支持。只读
    char iAudioInIndex;//音频输入索引，-1表示不支持。只读
    char iAudioOutIndex;//音频输出索引，-1表示不支持。只读
    char iPTZIndex; //云台索引。-1表示不支持。只读
    char cReserved[16];
}BVCU_PUCFG_EncoderChannel;

//解码器通道
/*
typedef struct  _BVCU_PUCFG_DecoderChannel{

}BVCU_PUCFG_DecoderChannel;
*/

//RS232串口
typedef struct _BVCU_PUCFG_RS232{
    int   iDataBit;    //数据位。5/6/7/8
    int   iStopBit;    //停止位。0:1位，1：1.5位，2：2位
    int   iParity;     //奇偶校验位。0:无，1：奇校验，2：偶校验
    int   iBaudRate;   //波特率.常见的包括1200，2400，4800，9600，19200，38400，57600，115200等
    int   iFlowControl; //流控。0:无，1：软流控，2：硬流控
}BVCU_PUCFG_RS232;

//串口
typedef struct _BVCU_PUCFG_SerialPort{
    BVCU_PUCFG_RS232 stRS232;
    int iAddress;//RS485地址，如果为-1，表明不是RS485串口
    int iType;//0-数据传输（例如PPP拨号）;1-控制台;2-透明串口
}BVCU_PUCFG_SerialPort;

//=======================云台相关============================
//预置点
typedef struct _BVCU_PUCFG_Preset{
    int  iID;//预置点号。-1表示无效，有效值从0开始
    char szPreset[BVCU_PTZ_MAX_NAME_LEN+1]; //预置点名
}BVCU_PUCFG_Preset;
//巡航点
typedef struct _BVCU_PUCFG_CruisePoint{
    short iPreset;//预置点号。-1表示无效值
    short iSpeed;//转到下一巡航点的云台速度
    int   iDuration;//在本预置点停留时间，单位秒
}BVCU_PUCFG_CruisePoint;

//巡航路线
typedef struct _BVCU_PUCFG_Cruise{
    int  iID;//巡航路线号。-1表示无效，有效值从0开始
    char szName[BVCU_PTZ_MAX_NAME_LEN+1];//巡航路线名字。未设置的巡航路线名字为空

    //巡航路线的巡航点。约定：有效的巡航点放在数组前面，数组中第一个无效巡航点之后的点都被认为是无效点
    BVCU_PUCFG_CruisePoint stPoints[BVCU_PTZ_MAX_CRUISEPOINT_COUNT];
}BVCU_PUCFG_Cruise;

//云台属性
typedef struct _BVCU_PUCFG_PTZAttr{
    int iPTZProtocolAll[BVCU_PTZ_MAX_PROTOCOL_COUNT];  //支持的所有协议列表。BVCU_PTZ_PROTO_*。只读
    int iPTZProtocolIndex;   //当前使用的PTZ协议索引

    int iAddress;      //485地址，范围0～255。可写。
    BVCU_PUCFG_RS232 stRS232;  //232串口属性。可写

    //是否批量更改预置点。0-不更改，1-更改。不更改时szPreset的内容被忽略。 仅在配置命令时有意义，查询时无意义
    int bChangePreset;

    //是否批量更改巡航路线。0-不更改，1-更改。不更改时stCruise的内容被忽略。仅在配置命令时有意义，查询时无意义
    int bChangeCruise;

    //预置列表。
    //查询时返回所有预置点，设置时的作用是批量改预置点的名字和删除预置点
    //注意：预置点的位置只能用BVCU_PTZ_COMMAND_PRESET_SET命令设置
    //约定：所有有效的预置点放在数组最前面，如果总数目不到BVCU_PTZ_MAX_PRESET_COUNT，则第一个无效的Preset的iIndex为-1
    BVCU_PUCFG_Preset stPreset[BVCU_PTZ_MAX_PRESET_COUNT]; 

    //巡航路线。
    //查询时返回巡航路线。设置时批量更改巡航路线
    //约定：所有有效的巡航路线放在数组最前面，如果总数目不到BVCU_PTZ_MAX_CRUISE_COUNT，第一个无效的Cruise的iIndex为-1
    BVCU_PUCFG_Cruise stCruise[BVCU_PTZ_MAX_CRUISE_COUNT];
    
    //当前正在使用的巡航路线ID。-1表示没有活跃的巡航路线。
    int iActiveCruiseID;
}BVCU_PUCFG_PTZAttr;

//云台操作

//PTZ操作命令

//注意：所有unused参数必须设置为0。
//阈值点/巡航路线的序号从0开始，-1表示无效序号
enum {
    //方向操作
    BVCU_PTZ_COMMAND_UP,     //向上。iParam1：unused;iParam2: 速度;iParam3:unused
    BVCU_PTZ_COMMAND_DOWN,   //向下。iParam1：unused;iParam2: 速度;iParam3:unused
    BVCU_PTZ_COMMAND_LEFT,  //向左。iParam1：unused;iParam2: 速度;iParam3:unused
    BVCU_PTZ_COMMAND_RIGHT, //向右。iParam1：unused;iParam2: 速度;iParam3:unused
    BVCU_PTZ_COMMAND_LEFTTOP,  //左上。iParam1：垂直速度;iParam2: 水平速度;iParam3:unused
    BVCU_PTZ_COMMAND_RIGHTTOP,  //右上。iParam1：垂直速度;iParam2: 水平速度;iParam3:unused
    BVCU_PTZ_COMMAND_LEFTDOWN,  //左下。iParam1：垂直速度;iParam2: 水平速度;iParam3:unused
    BVCU_PTZ_COMMAND_RIGHTDOWN,  //右下。iParam1：垂直速度;iParam2: 水平速度;iParam3:unused

    //镜头操作
    BVCU_PTZ_COMMAND_ZOOM_INC,  //增加放大倍数。iParam1：unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_ZOOM_DEC,  //减小放大倍数。iParam1：unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_FOCUS_INC, //焦距调远。iParam1：unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_FOCUS_DEC, //焦距调近。iParam1：unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_APERTURE_INC, //光圈放大。iParam1：unused;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_APERTURE_DEC, //光圈缩小。iParam1：unused;iParam2: unused;iParam3:unused

    //预置点操作
    BVCU_PTZ_COMMAND_PRESET_GO,  //转到预置点。iParam1：预置点号;iParam2: 垂直速度;iParam3:水平速度
    BVCU_PTZ_COMMAND_PRESET_SET, //把当前位置设置为预置点。iParam1：预置点号;iParam2: 预置点名;iParam3:unused
    BVCU_PTZ_COMMAND_PRESET_SETNAME, //更改预置点名字。iParam1：预置点号;iParam2: 预置点名;iParam3:unused
    BVCU_PTZ_COMMAND_PRESET_DEL, //删除预置点。iParam1：预置点号;iParam2: unused;iParam3:unused

    //巡航路线操作
    BVCU_PTZ_COMMAND_CRUISE_GO,//启动巡航。iParam1：巡航路线号;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_CRUISE_STOP,//停止巡航。iParam1：巡航路线号;iParam2: unused;iParam3:unused
    BVCU_PTZ_COMMAND_CRUISE_SET,//设置整个巡航路线。iParam1：巡航路线号;iParam2: BVCU_PUCFG_CRUISE指针;iParam3:unused
    BVCU_PTZ_COMMAND_CRUISE_DEL,//删除巡航路线。iParam1：巡航路线号;iParam2: unused;iParam3:unused

    //辅助功能操作
    BVCU_PTZ_COMMAND_AUX,//打开/关闭辅助功能开关，Param1：辅助号;iParam2: 0-关闭,1-开启;iParam3:unused

    //锁操作
    //如果锁定超过60秒后，用户没有手工解除锁定，Server会自动解除锁定。
    BVCU_PTZ_COMMAND_LOCK,//锁定/解锁云台。iParam1：unused;iParam2: unused;iParam3:unused
};

typedef struct _BVCU_PUCFG_PTZControl{
    int iPTZCommand;    //BVCU_PTZ_COMMAND_*
    int bStop;//0-动作开始，1-动作停止。仅对方向操作/镜头操作/锁操作有效，其他操作应该设置为0。锁操作：0-开始锁定，1-停止锁定
    int iParam1,iParam2,iParam3;//参考BVCU_PTZ_COMMAND_*说明
    //注意：BVCU_PTZ_COMMAND_CRUISE_SET的iParam2是个指针，网络发送/接收时应发送/接收整个BVCU_PTZ_COMMAND_CRUISE_SET结构体
}BVCU_PUCFG_PTZControl;

//=======================网络相关============================

typedef struct _BVCU_PUCFG_Ethernet{
    int bDHCP;//是否使用DHCP。0-不使用；1-使用;-1-设备不支持
    int bPPPoE;//是否使用PPPoE。0-不使用；1-使用；-1-设备不支持。
    int bAutoDNS;//自动获取DNS。0-不使用；1-使用。只有bDHCP=1或者bPPPoE=1才有意义

    char szIP[16];//ip地址。只有bDHCP !=1 才有意义
    char szNetMask[16];//子网掩码。只有bDHCP !=1 才有意义
    char szGateway[16];//默认网关。只有bDHCP !=1 才有意义
    char szDNS[2][BVCU_MAX_HOST_NAME_LEN+1];//域名服务器。只有bAutoDNS=0才有意义

    char szPPPoEUserName[BVCU_MAX_NAME_LEN+1];//PPPoE用户名，只有bPPPoE=1才有意义
    char szPPPoEPassword[BVCU_MAX_PASSWORD_LEN+1];//PPPoE密码，只有bPPPoE=1才有意义

    int iReserved[4];
}BVCU_PUCFG_Ethernet;

typedef struct _BVCU_PUCFG_WifiHotSpot{
    char szProviderAll[8][BVCU_MAX_NAME_LEN+1];//支持的提供商列表。ChinaNet/ChinaMobile/ChinaUniccom
    int  iProviderIndex;//当前使用的提供商
    char szAreaAll[64][16];//开户地列表.通常用代码表示，例如bj-北京,ah-安徽,...
    int  iAreaIndex;//开户地
    char szUserName[BVCU_MAX_NAME_LEN+1];//用户名
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//密码
}BVCU_PUCFG_WifiHotSpot;

typedef struct _BVCU_PUCFG_WifiGeneral{
    char szSSID[32];//SSID 
    int iSecurityType;//安全类型.BVCU_WIFI_SECURITY_TYPE_*
    int iCryptType;//加密类型.BVCU_WIFI_CRYPT_TYPE_*
    char szWEPKey[4][16];//WEP密钥
    char szWPAKey[64];//WPA密钥

    int bDHCP;//是否使用DHCP。0-不使用；1-使用;-1-设备不支持
    int bPPPoE;//是否使用PPPoE。0-不使用；1-使用；-1-设备不支持。PPPoE的用户名/密码使用BVCU_PUCFG_Ethernet中的
    int bAutoDNS;//自动获取DNS。0-不使用；1-使用。只有bDHCP=1或者bPPPoE=1才有意义

    char szIP[16];//ip地址。只有bDHCP=0才有意义
    char szNetMask[16];//子网掩码。只有bDHCP !=1 才有意义
    char szGateway[16];//默认网关。只有bDHCP !=1 才有意义
    char szDNS[2][BVCU_MAX_HOST_NAME_LEN+1];//域名服务器。只有bAutoDNS=0才有意义
    int  iReserved[4];
}BVCU_PUCFG_WifiGeneral;

typedef struct _BVCU_PUCFG_Wifi{
    int bEnable;//0-不使用；1-使用
    int iMode;//0-普通方式，1-热点方式
    int  iSignalLevel;//信号强度。0~100，0最差，100最好

    BVCU_PUCFG_WifiGeneral stGeneral;//普通方式。只有iMode=0才有意义
    BVCU_PUCFG_WifiHotSpot stHostSpot;//热点方式。只有iMode=1才有意义
    
}BVCU_PUCFG_Wifi;

typedef struct _BVCU_PUCFG_RadioNetwork{
    int bEnable;//是否使用该模块。0-不使用；1-使用
    int iTypeAll[4];//模块支持的所有网络类型。BVCU_RADIONETWORK_TYPE_*
    int iTypeIndex;//当前使用的网络类型
    
    char szModuleName[BVCU_MAX_NAME_LEN+1];//模块名
    char szUserName[BVCU_MAX_NAME_LEN+1];//用户名。设为空表示采用默认
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//密码

    char szAPN[BVCU_MAX_NAME_LEN+1];//APN名。设为空表示采用默认
    char szAccessNum[BVCU_MAX_NAME_LEN+1];//接入号

    char szCardNum[BVCU_MAX_MOBILEPHONE_NUM_LEN+1];//卡号

    int bOnline;//是否在线。0-不在线，1-在线
    int iSignalLevel[4];//每种网络类型对应的信号强度。0~100，0最差，100最好
    int iOnlineTime;//上线时间，单位秒.设置为-1表示重置上线时间
    int iTrafficDownload;//下载的网络流量总计，单位MB字节.设置为-1表示重置网络流量
    int iTrafficUpload;//上传的网络流量总计，单位MB字节.设置为-1表示重置网络流量
    int iSpeedDownload;//当前下载速度，单位KB/s
    int iSpeedUpload;//当前上载速度，单位KB/s
    int iReserved[4];
}BVCU_PUCFG_RadioNetwork;

//注册服务器
typedef struct  _BVCU_PUCFG_RegisterServer{
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1];     //服务器IP或域名
    int  iPort;           //服务器端口
    int  iProto;         //使用的协议类型，TCP/UDP
    char cReserved[16];
}BVCU_PUCFG_RegisterServer;

//设备升级服务器
typedef struct _BVCU_PUCFG_UpdateServer{
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1];    //服务器IP或域名
    int  iPort;                               //端口    
    char szUserName[BVCU_MAX_NAME_LEN+1];     //用户名
    char szPassword[BVCU_MAX_PASSWORD_LEN+1]; //密码
    int  iProto;                              //协议
    char szPath[BVCU_MAX_FILE_NAME_LEN+1];    //设备固件存放路径
}BVCU_PUCFG_UpdateServer;

//Email服务器
typedef struct _BVCU_PUCFG_EmailServer{
    char szServerAddr[BVCU_MAX_HOST_NAME_LEN+1];//Email SMTP服务器地址。
    int  iServerPort;//Email服务器端口
    char szUserName[BVCU_MAX_NAME_LEN+1];//帐号名
    char szPassword[BVCU_MAX_PASSWORD_LEN+1];//密码
    char szSenderAddr[BVCU_MAX_HOST_NAME_LEN+1];//发送者邮件地址
    int  bSSLEnable; //是否使能SSL。0-不使能，1-使能    
}BVCU_PUCFG_EmailServer;

// NTP配置
#define BVCU_MAX_NTP_SERVER_COUNT 3
typedef struct  _BVCU_PUCFG_NTPServer{
    char szAddr[BVCU_MAX_NTP_SERVER_COUNT][BVCU_MAX_HOST_NAME_LEN+1];     //NTP服务器IP或域名
    int  iPort[BVCU_MAX_NTP_SERVER_COUNT];           //NTP服务器默认端口为123
    int  iUpdateInterval; //更新时间间隔，单位：分钟
    int  bUpdateImmediately;//立即同步
    char cReserved[16];
}BVCU_PUCFG_NTPServer;

//较时设备。
typedef struct _BVCU_PUCFG_TimeSource{    
    int     iTimeZone; //本地时区
    int  bDST;//是否使用夏令时。0-不使用，1-使用
    BVCU_PUCFG_NTPServer stNTPServer;
    
    //优先级大的设备会被优先使用
    char iNTP; //NTP优先级1-100, <=0表示不使用
    char iGPS; //GPS优先级1-100, <=0表示不使用。只有支持GPS的设备才有意义
    char iReserved[10];
}BVCU_PUCFG_TimeSource;

//DDNS
typedef struct _BVCU_PUCFG_DDNS{
    int bDDNS;//动态域名。0-不使用；1-使用；-1-设备不支持
    char szDDNSProvider[BVCU_MAX_NAME_LEN+1];//DDNS提供商。只有bDDNS=1才有意义
    char szDDNSAddr[BVCU_MAX_HOST_NAME_LEN+1];//DDNS服务器地址。只有bDDNS=1才有意义
    char szDDNSUserName[BVCU_MAX_NAME_LEN+1];//DDNS用户名。只有bDDNS=1才有意义
    char szDDNSPassword[BVCU_MAX_PASSWORD_LEN+1];//DDNS密码。只有bDDNS=1才有意义
    char szDynamicName[BVCU_MAX_HOST_NAME_LEN+1];//申请的动态域名。只有bDDNS=1才有意义
}BVCU_PUCFG_DDNS;

//各种Server相关配置
typedef struct _BVCU_PUCFG_Servers{
    BVCU_PUCFG_RegisterServer stRegisterServer;
    BVCU_PUCFG_UpdateServer stUpdateServer;
    BVCU_PUCFG_TimeSource stTimeSource;
    BVCU_PUCFG_DDNS stDDNS;
    BVCU_PUCFG_EmailServer stEmailServer;
}BVCU_PUCFG_Servers;

//=======================存储============================
//存储计划
typedef struct _BVCU_PUCFG_Storage_Schedule{
    int iChannelIndex;
    BVCU_DayTimeSlice stWeekSnapshot[7][BVCU_MAX_DAYTIMESLICE_COUNT];//一周的抓拍时间片划分，每天BVCU_MAX_DAYTIMESLICE_COUNT个时间片
    BVCU_DayTimeSlice stWeekRecord[7][BVCU_MAX_DAYTIMESLICE_COUNT];//一周的录像时间片划分，每天BVCU_MAX_DAYTIMESLICE_COUNT个时间片
    int   bRecordAudio;//是否录音频。0-不存储，1-存储。
    BVCU_WallTime stBegin;//开始时间
    BVCU_WallTime stEnd;//结束时间。在开始/结束时间这段范围内，计划有效。
}BVCU_PUCFG_Storage_Schedule;

//磁盘信息
typedef struct _BVCU_PUCFG_Storage_Media{
    char szMediaName[BVCU_MAX_FILE_NAME_LEN+1];//唯一标识该存储器的名字或者路径
    int iStorageMediaType;//存储媒体类型。BVCU_STORAGEMEDIATYPE_*
    unsigned int iTotalSpace;//总空间。单位MB 
    unsigned int iFreeSpace;//剩余空间。单位MB    
    int bFormated;//是否已格式化，0-未格式化，1-格式化
    int iReserved[2];
}BVCU_PUCFG_Storage_Media;

//存储规则
typedef struct _BVCU_PUCFG_Storage_Rule{
    int iNoSpaceRule;//磁盘满时处理规则。0-停止录像，1-覆盖旧录像
    int iAlarmSpace;//空间不足报警门限。单位MB。设为0表示不报警
    int iReserveDays;//录像文件保留天数
    int iRecordFileLength;//录像文件时间长度。单位秒。
    int bRecordGPS;//是否存储GPS信息。0-不存储，1-存储。
    int iReserved[2];
}BVCU_PUCFG_Storage_Rule;

//格式化存储器。管理器应定时发送BVCU_PUCFG_Storage_Format消息查询格式化进度
typedef struct _BVCU_PUCFG_Storage_Format{
    char szMediaName[BVCU_MAX_FILE_NAME_LEN+1];//唯一标识该存储器的名字或者路径
    int iAction;//0-查询格式化进度，1-开始格式化，
    int iPercent;//查询返回的格式化进度。正常值0～100。100表示格式化完毕，特殊值-1表示格式化失败
}BVCU_PUCFG_Storage_Format;

//手工远程录像
typedef struct _BVCU_PUCFG_ManualRecord{
    int bStart;//1-开始录像，0-停止录像
    int iLength;//存储时间长度，单位秒
}BVCU_PUCFG_ManualRecord;

//手工抓拍，保存到PU
typedef struct _BVCU_PUCFG_Snapshot{
    int iCount;//抓拍张数，最大允许值为15
    int iInterval;//抓拍间隔，单位秒。最大允许值为60秒
}BVCU_PUCFG_Snapshot;
//==========================上下线（注册）控制===========================
//上线触发方式
enum{
    BVCU_PU_ONLINE_TRIGGER_INVALID = 0,
    BVCU_PU_ONLINE_TRIGGER_MANUAL, //手动
    BVCU_PU_ONLINE_TRIGGER_ONTIME, //定时
    BVCU_PU_ONLINE_TRIGGER_ONEVENT, //事件
};

//上线事件。各种事件可以组合
enum{
    BVCU_PU_ONLINE_EVENT_ALERTIN = 1<<0,//报警输入
    BVCU_PU_ONLINE_EVENT_VIDEOMD = 1<<1,//视频运动检测
    BVCU_PU_ONLINE_EVENT_SMS     = 1<<2, //短信输入
};

//上线途径
enum{
    BVCU_PU_ONLINE_THROUGH_INVALID  = 1<<0,    
    BVCU_PU_ONLINE_THROUGH_ETHERNET = 1<<1, //以太网
    BVCU_PU_ONLINE_THROUGH_WIFI     = 1<<2 , //WIFI
    BVCU_PU_ONLINE_THROUGH_RADIO    = 1<<3, //无线
};

//触发上线的短信配置
typedef struct _BVCU_PUCFG_OnlineEventSMS{
    char szCardNum[16][BVCU_MAX_MOBILEPHONE_NUM_LEN+1];//允许的卡号。最多允许16个卡号。只有在列表中的卡号才会允许触发上线
    char szContent[128];//触发内容。只有szCardNum中匹配szContent的短信才会触发
    int  bReply;//PU是否回复短信
}BVCU_PUCFG_OnlineEventSMS;

typedef struct _BVCU_PUCFG_OnlineControlOne{
    int iTrigger;//触发方式
    int iEvent;//事件。仅在iEvent == BVCU_PU_ONLINE_TRIGGER_ONEVENT时有意义
    int iOnlineTime;//事件触发后，至少保持iOnlineTime秒在线,-1表示一直保持在线。仅在iEvent == BVCU_PU_ONLINE_TRIGGER_ONEVENT时有意义
    int iThrough;//上线途径,BVCU_PU_ONLINE_THROUGH_*的组合
}BVCU_PUCFG_OnlineControlOne;

typedef struct _BVCU_PUCFG_OnlineControl{
    BVCU_DayTimeSlice stWeek[7][BVCU_MAX_DAYTIMESLICE_COUNT];//一周中的时间片段
    BVCU_PUCFG_OnlineControlOne stRCO[7][BVCU_MAX_DAYTIMESLICE_COUNT];//每个时间片端对应的上线方式
    BVCU_PUCFG_OnlineEventSMS stRESMS; //短信触发的配置
}BVCU_PUCFG_OnlineControl;

/*TODO: 
   1、设备手动升级
   2、上传报警声音/OSD图片
   3、远程抓拍
   4、下载录像
 
   1~4涉及到上传/下载协议，需要统一考虑
*/

#endif

