#ifndef _BVCU_AAA_H_
#define _BVCU_AAA_H_

#include "AAABase.h"
#include "BVCUConst.h"

#define BVCU_MAX_SESSION_INVALID -1

// 权限资源信息
typedef struct _BVCU_UCFG_Resource_
{
    /*  设备ID号 */
    char sPuID[AAA_ID_LEN];

    /*  针对该设备的权限 */
    AAA_PUPermissions szPermissions;

}BVCU_UCFG_Resource;

// 用户组的基本信息
typedef struct _BVCU_UCFG_UserGroup_ 
{
    /*  组的id标识符 */
    char sId[AAA_ID_LEN];

    /*  组的名称 */
    char sName[AAA_NAME_LEN];

    /*  组的上级组ID, 顶层组的此值为空 */
    char sParentId[AAA_ID_LEN];

}BVCU_UCFG_UserGroup;

// 用户组详细信息
typedef struct _BVCU_UCFG_UserGroupInfo_
{
    /*  组的id标识符 */
    char sId[AAA_ID_LEN];

    /*  组的名称 */
    char sName[AAA_NAME_LEN];

    /*  组的上级组 */
    char sParentId[AAA_ID_LEN];

    /*  描述该组的一些信息 */
    char sDescription[AAA_DESCRIPTION_LEN];

    /*  用户组拥有资源数  */
    unsigned int   iResource;

    /*  资源数组  */
    BVCU_UCFG_Resource* pResource;

} BVCU_UCFG_UserGroupInfo;


// 用户的基本信息
typedef struct _BVCU_UCFG_User_
{
    /*  用户的账号名,登录时使用的名称 */
    char sId[AAA_ID_LEN];

    /*  用户的组id */
    char sGroupId[AAA_ID_LEN];

    /*  用户的姓名，可不设置 */
    char sName[AAA_NAME_LEN];

}BVCU_UCFG_User;


// 在线用户的基本信息
typedef struct _BVCU_UCFG_User_Online_
{
    /*  用户的账号名,登录时使用的名称 */
    char sUserId[AAA_ID_LEN];

    /*  登录所用设备ID。通常是CU ID */
    char szDevID[BVCU_MAX_ID_LEN+1];

    /* 地址信息 */
    char szAddr[BVCU_MAX_HOST_NAME_LEN+1];

    /* 在线状态 */
    int iStatus;//BVCU_ONLINE_STATUS_*
    
    /*CMS分配的登录标识*/
    int iApplierID;
    
    /*保留，必须初始化为0*/
    int iReserved[2];
}BVCU_UCFG_User_Online;

// 用户的详细信息
typedef struct _BVCU_UCFG_UserInfo_
{
    /*  用户的账号名,登录时使用的名称 */
    char sId[AAA_ID_LEN];

    /*  用户密码 */
    char sPasswd[AAA_PASSWD_LEN];

    /*  密码是否有效（0:无效，表示sPasswd不修改;其它值为有效） */
    int  bSetPasswd;

    /*  系统管理权限 */
    AAA_UserPermissions szSysadmin;

    /*  指向分配此帐户的Server，第一版本不使用，可不设置 */
    char sServerId[AAA_ID_LEN];

    /*  用户的组id */
    char sGroupId[AAA_ID_LEN];

    /*  用户的最大会话数，可不设置 */
    int  iMaxSession;

    /*  分配此用户的id */
    char sAllocateId[AAA_ID_LEN];

    /*  用户的姓名，可不设置 */
    char sName[AAA_NAME_LEN];

    /*  用户的联系电话，可不设置 */
    char sPhone[AAA_PHONE_LEN];

    /*  用户的email，可不设置 */
    char sEmail[AAA_EMAIL_LEN];

    /*  对此用户的描述，可不设置 */
    char sDescription[AAA_DESCRIPTION_LEN];

    /*  用户拥有资源数  */
    unsigned int   iResource;

    /*  资源数组  */
    BVCU_UCFG_Resource* pResource;

}BVCU_UCFG_UserInfo;

// 修改用户密码结构体
typedef struct _BVCU_UCFG_Mod_passwd_
{
    /*  用户的账号名, 登录时使用的名称 */
    char sId[AAA_ID_LEN];

    /*  用户的原密码,修改他人密码时不用填充此值，但需要管理权限 */
    char sPassword[AAA_PASSWD_LEN];

    /*  用户的新密码 */
    char sNewPassword[AAA_PASSWD_LEN];

}BVCU_UCFG_ModPasswd;

#endif