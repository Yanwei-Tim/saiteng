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
	  * @brief  ��ʼ��AAAC
	  * @return �����Ƿ��ʼ���ɹ����� AAA_RESULT_*
	  */
	extern_in_out AAA_Result AAAC_Init  ();

	/**
	  * @brief  �ͷ�AAACռ�õ���Դ
	  * @return �ͷ���Դ�Ƿ�ɹ����� AAA_RESULT_*
	*/
	extern_in_out AAA_Result AAAC_Cleanup();

	/**
	  * @brief �����û����룬����ĳ��ȱ���ʼ�ձ���һ�£�����8byte��������
	  *
	  * @param userID [in] �û�ID��
	  * @param passwd [in] �û�����
	  * @param buf    [out]��ż��ܺ��û�����Ļ�����
	  * @param len    [in,out] buf��������С(byte)�����벻С���û����볤�ȼ�10�����ؼ��ܺ����볤��
	  *
	  * @return �� AAA_RESULT_*,����AAA_RESULT_E_CRYPT��AAA_RESULT_E_BUFF,AAA_RESULT_NULL
	  */
	extern_in_out AAA_Result AAAC_EncryptPasswd(const char* userID, const char* passwd, char* buf, int* len);
	
	/**
	  * @brief �����ӽ�����Կ
	  *
	  * @param name  [in]  �㷨���ƣ�����Ϊ "RSA"("rsa") �� "RC4"("rc4")�����ɵ���ԿΪASCII�ַ���
	  * @param buf   [out] ���������Կ�Ļ�������
	          �����ڴ洢RSA��Կʱ�������СΪ64byte,RC4ʱ����Ϊ256byte(AAA_RC4_KEY_LEN)
	  * @param len   [in]  pBufָ�򻺳�����С
	  *
	  * @return �� AAA_RESULT_*������AAA_RESULT_E_CRYPT��AAA_RESULT_E_BUFF,AAA_RESULT_NULL
	  */
	extern_in_out AAA_Result AAAC_CreateCryptKey (const char* name, char* buf, int len);
	
	/**
	  * @brief �ӽ�������
	  *
	  * @param crypt  [in]  �ӽ������ݽṹ��
	  * @param buf    [out] ��żӽ��ܺ�����ݣ�
	          ��ʹ��RSA����ʱ���û�������С�費С�ڼӽ������ݴ�С��4��,
			  RSA����ʱ���û�������С�費С�ڼӽ������ݴ�С��4��֮һ��
			  RC4�ӽ���ʱ�費С�ڼӽ������ݴ�С��
	  * @param len    [in,out]  pBufָ�򻺳�����С,�ӽ��ܺ����ݴ�С
	  *
	  * @return �� AAA_RESULT_*������AAA_RESULT_E_CRYPT��AAA_RESULT_E_BUFF,AAA_RESULT_NULL
	  */
	extern_in_out AAA_Result AAAC_Crypt(const AAA_Crypt* crypt, char* buf, int* len);

#ifdef __cplusplus
};
#endif

#endif