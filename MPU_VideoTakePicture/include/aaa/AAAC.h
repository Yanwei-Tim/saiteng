/*======================================================
	SmartEye AAA Client Library.
	Copyright(c) BesoVideo Ltd. 2011-2012
	houji.fang@besovideo.com
========================================================*/

#ifndef __AAAC_H__
#define __AAAC_H__
#include "AAABase.h"
#include "AAA.h"
#ifdef _MSC_VER
	#ifdef   AAAC
		#define  extern_in_out __declspec( dllexport )
	#else
		#define  extern_in_out __declspec( dllimport )
	#endif
#else
	#define extern_in_out
#endif

#ifdef __cplusplus
extern "C"
{
#endif

	/**
	  * @brief  初始化AAAC
	  * @return 返回是否初始化成功，见 AAA_RESULT_*
	  */
	extern_in_out AAA_Result AAAC_Init  ();

	/**
	  * @brief  释放AAAC占用的资源
	  * @return 释放资源是否成功，见 AAA_RESULT_*
	*/
	extern_in_out AAA_Result AAAC_Cleanup();

	/**
	  * @brief 加密用户密码，密码的长度必须始终保持一致，并是8byte的整数倍
	  *
	  * @param userID [in] 用户ID号
	  * @param passwd [in] 用户密码
	  * @param buf    [out]存放加密后用户密码的缓冲区
	  * @param len    [in,out] buf缓冲区大小(byte)，必须不小于用户密码长度加10。带回加密后密码长度
	  *
	  * @return 见 AAA_RESULT_*,常见AAA_RESULT_E_CRYPT，AAA_RESULT_E_BUFF,AAA_RESULT_NULL
	  */
	extern_in_out AAA_Result AAAC_EncryptPasswd(const char* userID, const char* passwd, char* buf, int* len);
	
	/**
	  * @brief 产生加解密密钥
	  *
	  * @param name  [in]  算法名称，可以为 "RSA"("rsa") 或 "RC4"("rc4")，生成的密钥为ASCII字符串
	  * @param buf   [out] 存放生成密钥的缓冲区，
	          当用于存储RSA密钥时，建议大小为64byte,RC4时建议为256byte(AAA_RC4_KEY_LEN)
	  * @param len   [in]  pBuf指向缓冲区大小
	  *
	  * @return 见 AAA_RESULT_*，常见AAA_RESULT_E_CRYPT，AAA_RESULT_E_BUFF,AAA_RESULT_NULL
	  */
	extern_in_out AAA_Result AAAC_CreateCryptKey (const char* name, char* buf, int len);
	
	/**
	  * @brief 加解密数据
	  *
	  * @param crypt  [in]  加解密数据结构体
	  * @param buf    [out] 存放加解密后的数据，
	          当使用RSA加密时，该缓冲区大小需不小于加解密数据大小的4倍,
			  RSA解密时，该缓冲区大小需不小于加解密数据大小的4份之一，
			  RC4加解密时需不小于加解密数据大小。
	  * @param len    [in,out]  pBuf指向缓冲区大小,加解密后数据大小
	  *
	  * @return 见 AAA_RESULT_*，常见AAA_RESULT_E_CRYPT，AAA_RESULT_E_BUFF,AAA_RESULT_NULL
	  */
	extern_in_out AAA_Result AAAC_Crypt(const AAA_Crypt* crypt, char* buf, int* len);

#ifdef __cplusplus
};
#endif

#endif