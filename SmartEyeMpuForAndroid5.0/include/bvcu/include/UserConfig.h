#ifndef _BVCU_AAA_H_
#define _BVCU_AAA_H_

#include "AAABase.h"
#include "BVCUConst.h"

#define BVCU_MAX_SESSION_INVALID -1

// Ȩ����Դ��Ϣ
typedef struct _BVCU_UCFG_Resource_
{
    /*  �豸ID�� */
    char sPuID[AAA_ID_LEN];

    /*  ��Ը��豸��Ȩ�� */
    AAA_PUPermissions szPermissions;

}BVCU_UCFG_Resource;

// �û���Ļ�����Ϣ
typedef struct _BVCU_UCFG_UserGroup_ 
{
    /*  ���id��ʶ�� */
    char sId[AAA_ID_LEN];

    /*  ������� */
    char sName[AAA_NAME_LEN];

    /*  ����ϼ���ID, ������Ĵ�ֵΪ�� */
    char sParentId[AAA_ID_LEN];

}BVCU_UCFG_UserGroup;

// �û�����ϸ��Ϣ
typedef struct _BVCU_UCFG_UserGroupInfo_
{
    /*  ���id��ʶ�� */
    char sId[AAA_ID_LEN];

    /*  ������� */
    char sName[AAA_NAME_LEN];

    /*  ����ϼ��� */
    char sParentId[AAA_ID_LEN];

    /*  ���������һЩ��Ϣ */
    char sDescription[AAA_DESCRIPTION_LEN];

    /*  �û���ӵ����Դ��  */
    unsigned int   iResource;

    /*  ��Դ����  */
    BVCU_UCFG_Resource* pResource;

} BVCU_UCFG_UserGroupInfo;


// �û��Ļ�����Ϣ
typedef struct _BVCU_UCFG_User_
{
    /*  �û����˺���,��¼ʱʹ�õ����� */
    char sId[AAA_ID_LEN];

    /*  �û�����id */
    char sGroupId[AAA_ID_LEN];

    /*  �û����������ɲ����� */
    char sName[AAA_NAME_LEN];

}BVCU_UCFG_User;


// �����û��Ļ�����Ϣ
typedef struct _BVCU_UCFG_User_Online_
{
    /*  �û����˺���,��¼ʱʹ�õ����� */
    char sUserId[AAA_ID_LEN];

    /*  ��¼�����豸ID��ͨ����CU ID */
    char szDevID[BVCU_MAX_ID_LEN+1];

    /* ��ַ��Ϣ */
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1];

    /* ����״̬ */
    int iStatus;//BVCU_ONLINE_STATUS_*
    
    /*CMS����ĵ�¼��ʶ*/
    int iApplierID;
    
    /*�����������ʼ��Ϊ0*/
    int iReserved[2];
}BVCU_UCFG_User_Online;

// �û�����ϸ��Ϣ
typedef struct _BVCU_UCFG_UserInfo_
{
    /*  �û����˺���,��¼ʱʹ�õ����� */
    char sId[AAA_ID_LEN];

    /*  �û����� */
    char sPasswd[AAA_PASSWD_LEN];

    /*  �����Ƿ���Ч��0:��Ч����ʾsPasswd���޸�;����ֵΪ��Ч�� */
    int  bSetPasswd;

    /*  ϵͳ����Ȩ�� */
    AAA_UserPermissions szSysadmin;

    /*  ָ�������ʻ���Server����һ�汾��ʹ�ã��ɲ����� */
    char sServerId[AAA_ID_LEN];

    /*  �û�����id */
    char sGroupId[AAA_ID_LEN];

    /*  �û������Ự�����ɲ����� */
    int  iMaxSession;

    /*  ������û���id */
    char sAllocateId[AAA_ID_LEN];

    /*  �û����������ɲ����� */
    char sName[AAA_NAME_LEN];

    /*  �û�����ϵ�绰���ɲ����� */
    char sPhone[AAA_PHONE_LEN];

    /*  �û���email���ɲ����� */
    char sEmail[AAA_EMAIL_LEN];

    /*  �Դ��û����������ɲ����� */
    char sDescription[AAA_DESCRIPTION_LEN];

    /*  �û�ӵ����Դ��  */
    unsigned int   iResource;

    /*  ��Դ����  */
    BVCU_UCFG_Resource* pResource;

}BVCU_UCFG_UserInfo;

// �޸��û�����ṹ��
typedef struct _BVCU_UCFG_Mod_passwd_
{
    /*  �û����˺���, ��¼ʱʹ�õ����� */
    char sId[AAA_ID_LEN];

    /*  �û���ԭ����,�޸���������ʱ��������ֵ������Ҫ����Ȩ�� */
    char sPassword[AAA_PASSWD_LEN];

    /*  �û��������� */
    char sNewPassword[AAA_PASSWD_LEN];

}BVCU_UCFG_ModPasswd;

#endif