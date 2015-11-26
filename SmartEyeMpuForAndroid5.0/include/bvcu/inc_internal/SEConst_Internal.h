#ifndef __SECONST_INTERNAL_H__
#define __SECONST_INTERNAL_H__
typedef long long __int64;
#include "BVCUConst.h"
//�ڲ�������BVCU_METHOD/BVCU_SUBMETHOD������ͳһ������;
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
    /* ��ȡVTDU/NRU/CMS��ַ Ŀ��ΪPU/NRU/CMS��ID 
      �������ͣ�Payload_FTP_SESSION_OPEN���������:Payload_FTP_SESSION_OPEN_Stage2 */
    BVCU_SUBMETHOD_FTP_SESSION_OPEN = BVCU_SUBMETHOD_RESERVED+1,

    /* ֪ͨPU��Ҫ���ӵ�VTDU��ַ Ŀ��ΪPU��ID�� CMS����PU��PU����
      �������ͣ�Payload_FTP_SESSION_OPEN_Stage2�� �������: �� */
    BVCU_SUBMETHOD_FTP_SESSION_OPEN_STAGE2 , 

    //=============�յ���command=======================
    /* ����FTP�û���֤Key��NRU�Ƕ�����
      ���븺�أ�Payload_FTP_SESSION_OPEN�����츺��:Payload_FTP_SESSION_OPEN_Stage1 */
    BVCU_SUBMETHOD_FTP_SESSION_OPEN_CMD = BVCU_SUBMETHOD_FTP_SESSION_OPEN,
};

// �ڲ�ʹ�õĽṹ��
// FTP---------
typedef	struct _Playload_FTP_SESSION_OPEN{
    int iVersion;//FTPЭ��汾��
    int iReserved;
    __int64 iRandomClient;  //Client���ɵ������
}Payload_FTP_SESSION_OPEN;

typedef	struct _Playload_FTP_SESSION_OPEN_Stage1{
    int iVersion;//FTPЭ��汾��
    int iFTPSessionID; //FTP Session ID����VTDU/NRU�ڲ����䣬ÿ��FTP_SESSION_OPEN�����Ӧһ��Ψһ��ID����0��ʼ����
    __int64 iKeyLow;  //Key��64λ
    __int64 iKeyHigh;  //Key��64λ
    int  iCmdPort; //FTP ����˿�
    int  iDataPort; //FTP���ݶ˿�
}Payload_FTP_SESSION_OPEN_Stage1;

typedef	struct _Playload_FTP_SESSION_OPEN_Stage2{
    int iVersion;//FTPЭ��汾��
    int iFTPSessionID; //FTP Session ID����0��ʼ����
    __int64 iKeyLow;  //Key��64λ
    __int64 iKeyHigh;  //Key��64λ
    int  iCmdPort;  //FTP����˿�
    int  iDataPort;  //FTP���ݶ˿�
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1]; //FTP ������IP������
}Payload_FTP_SESSION_OPEN_Stage2;

#endif

