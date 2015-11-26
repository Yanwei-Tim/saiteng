#ifndef __AAA_H__
#define __AAA_H__
//This Hand is AAAS and AAAC have together


#define AAA_RSA_KEY_LEN  64         //rsa密钥长度
#define AAA_RC4_KEY_LEN  256        //rc4密钥长度

#define AAAResultSuccess(p) (((int)(p) >= 0))
#define AAAResultFailed(p)  (((int)(p) <  0))

//  加解密方式枚举
enum
{
	AAA_CRYPT_UNKNOWN,    //未知加解密方式

	AAA_ENCRYPT_RSA = 1,  //RSA加密
	AAA_DECRYPT_RSA = 2,  //RSA解密
	AAA_CRYPT_RSA   = 3,  //RSA密钥

	AAA_ENCRYPT_RC4 = 4,  //RC4加密
	AAA_DECRYPT_RC4 = 8,  //RC4解密
	AAA_CRYPT_RC4   = 12, //RC4密钥
};
//  结果枚举
enum _aaa_result
{
	AAA_RESULT_S_OK = 0,             //成功
	AAA_RESULT_S_OK2,

	AAA_RESULT_E_ERROR = -0x100,     //请求错误
	AAA_RESULT_E_PASSWORD,           //密码错误
	AAA_RESULT_E_VERSION,            //算法版本错误(密码比对中，两个密码不是同一算法加密时，返回该错误)
	AAA_RESULT_E_PERMISSIONS,        //权限不足
	AAA_RESULT_E_ALREADEXISTS,       //资源已经存在(一般在创建新资源时,可能会出现该错误)
	AAA_RESULT_E_NULL,               //空值错误
	AAA_RESULT_E_NOFIND,             //请求资源不存在
	AAA_RESULT_E_CRYPT,              //加解密出错
	AAA_RESULT_E_BUFF,               //缓冲区太小，或内存溢出
	AAA_RESULT_E_FILE,               //文件读写错误
};
//  系统管理权值枚举
//  说明：若用户拥有系统管理权，则其对其所在组及其下级组的用户（组）拥有操作权力
//  注意：AAA_GROUP_DEFAULT_GOD 组内的用户，若拥有系统管理权，则对全部的用户（组）拥有操作权力
enum
{
	AAA_SYSADMIN_NONE = 0,  // 没有系统管理权
	AAA_SYSADMIN_GROUP = 1, // 对用户组拥有管理权
	AAA_SYSADMIN_USER  = 1<<2, // 对用户拥有管理权
	AAA_SYSADMIN_DEV   = 1<<4, // 对设备拥有管理权
	AAA_SYSADMIN_DEVASS= 1<<6, // 对设备拥有分配权
	AAA_SYSADMIN_ALL = AAA_SYSADMIN_GROUP | AAA_SYSADMIN_USER | AAA_SYSADMIN_DEV | AAA_SYSADMIN_DEVASS,
};

//  结果类型
typedef _aaa_result   AAA_Result;

//  RSA密钥类型
typedef unsigned long long  AAA_RsaKey;

//  加解密数据时参数结构体
typedef struct _aaa_crypt_
{
	/* 加解密方式 , 见 AAA_ENCRYPT_* */
	int      iMethod;

	/* 加解密密钥 ,(RSA解密时，可以不设置)*/
	char*    pKey;

	/* 密钥长度 */
	int      iKeyLen;

	/* 需要加密的数据 */
	char*    pData;

	/* 需要加密数据的长度 */
	int      iDataLen;

}AAA_Crypt;


#endif
