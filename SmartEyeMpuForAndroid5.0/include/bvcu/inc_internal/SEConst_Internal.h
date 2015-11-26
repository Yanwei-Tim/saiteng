#ifndef __SECONST_INTERNAL_H__
#define __SECONST_INTERNAL_H__
typedef long long __int64;
#include "BVCUConst.h"
//内部保留的BVCU_METHOD/BVCU_SUBMETHOD在这里统一定义用途
enum
{
	BVCU_METHOD_REGISTER = BVCU_METHOD_RESERVED+1,
	BVCU_METHOD_NOTIFY,
	BVCU_METHOD_KEEPALIVE,
	BVCU_METHOD_INVITE,
	BVCU_METHOD_HOLEPUNCH,
	BVCU_METHOD_BYE,
	BVCU_METHOD_ACK,
};
enum
{
    //=============query====================
    // FTP----------
    /* 获取VTDU/NRU/CMS地址 目标为PU/NRU/CMS的ID 
      输入类型：Payload_FTP_SESSION_OPEN；输出类型:Payload_FTP_SESSION_OPEN_Stage2 */
    BVCU_SUBMETHOD_FTP_SESSION_OPEN = BVCU_SUBMETHOD_RESERVED+1,

    /* 通知PU需要连接的VTDU地址 目标为PU的ID， CMS发给PU，PU处理
      输入类型：Payload_FTP_SESSION_OPEN_Stage2； 输出类型: 无 */
    BVCU_SUBMETHOD_FTP_SESSION_OPEN_STAGE2 , 

    //=============收到的command=======================
    /* 请求FTP用户认证Key，NRU角度命令
      输入负载：Payload_FTP_SESSION_OPEN；回响负载:Payload_FTP_SESSION_OPEN_Stage1 */
    BVCU_SUBMETHOD_FTP_SESSION_OPEN_CMD = BVCU_SUBMETHOD_FTP_SESSION_OPEN,
};

// 内部使用的结构体
// FTP---------
typedef	struct _Playload_FTP_SESSION_OPEN{
    int iVersion;//FTP协议版本号
    int iReserved;
    __int64 iRandomClient;  //Client生成的随机数
}Payload_FTP_SESSION_OPEN;

typedef	struct _Playload_FTP_SESSION_OPEN_Stage1{
    int iVersion;//FTP协议版本号
    int iFTPSessionID; //FTP Session ID，由VTDU/NRU内部分配，每个FTP_SESSION_OPEN命令对应一个唯一的ID，从0开始递增
    __int64 iKeyLow;  //Key低64位
    __int64 iKeyHigh;  //Key高64位
    int  iCmdPort; //FTP 命令端口
    int  iDataPort; //FTP数据端口
}Payload_FTP_SESSION_OPEN_Stage1;

typedef	struct _Playload_FTP_SESSION_OPEN_Stage2{
    int iVersion;//FTP协议版本号
    int iFTPSessionID; //FTP Session ID，从0开始递增
    __int64 iKeyLow;  //Key低64位
    __int64 iKeyHigh;  //Key高64位
    int  iCmdPort;  //FTP命令端口
    int  iDataPort;  //FTP数据端口
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1]; //FTP 服务器IP或域名
}Payload_FTP_SESSION_OPEN_Stage2;

#endif

