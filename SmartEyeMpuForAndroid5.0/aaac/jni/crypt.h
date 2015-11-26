#ifndef _ENCRYPT_H_
#define _ENCRYPT_H_

#include "rsa.h"

class CCrypt
{
private:
	AAA_RsaKey  m_rsaE;
	AAA_RsaKey  m_rsaD;
	AAA_RsaKey  m_rsaN;

	AAA_Result Encrypt_RSA(const unsigned char * _src,int s_len,AAA_RsaKey d,AAA_RsaKey n,char* buf,int b_len);
	AAA_Result Decrypt_RSA(const char * _src,int s_len,unsigned char * buf,int b_len);

	AAA_Result Encrypt_RC4(unsigned char* buf,int len,const unsigned char* s_box,int s_len);

	AAA_Result Encrypt_passwd(const char* pUserName,const char* pPasswd,char* buf,int b_len);
	AAA_Result Decrypt_passwd(const char* pUserName,const char* pPasswd,int p_len,char* buf,int b_len);
public:
	CCrypt(void);
	~CCrypt(void);
	void InitKey();

	AAA_Result EncryptPasswd(const char* pUserName,const char* pPasswd,char* pBuf,int* iLen);
	AAA_Result DecryptPasswd(const char* pUserName,const char* pPasswd,int wLen,char* pBuf,int iLen);

	/* pName £º "RSA"("rsa") or "RC4"("rc4") */
	AAA_Result CreateCryptKey(const char* pName, char* pBuf, int iLen);

	AAA_Result Crypt(const AAA_Crypt* lpCrypt, char* pBuf, int* iLen);

};

#endif