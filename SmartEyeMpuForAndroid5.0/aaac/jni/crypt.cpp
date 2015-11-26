#include "StdAfx.h"
#include "crypt.h"
#include <stdio.h>

#ifdef AAAC
#include "des.h"
#include "md5sum.h"
#endif


CCrypt::CCrypt(void)
{
	RsaGetParam(&m_rsaE,&m_rsaD,&m_rsaN);
}

CCrypt::~CCrypt(void)
{
}

void CCrypt::InitKey()
{
	RsaGetParam(&m_rsaE,&m_rsaD,&m_rsaN);
}

AAA_Result CCrypt::Encrypt_RSA(const unsigned char * _src,int s_len,AAA_RsaKey d,AAA_RsaKey n,char* buf,int b_len)
{
	if(buf == NULL || _src == NULL || d >= n)
		return AAA_RESULT_E_NULL;
	unsigned int * _dst = (unsigned int*)buf;
	b_len = b_len/4;
	for(int i = 0;i < b_len && i < s_len;i ++)
		_dst[i] = (unsigned int)PowMod(_src[i],d,n);
	return AAA_RESULT_S_OK;
}
AAA_Result CCrypt::Decrypt_RSA(const char * _src,int s_len,unsigned char * buf,int b_len)
{
	if(buf == NULL || _src == NULL)
		return AAA_RESULT_E_NULL;
	unsigned int * src = (unsigned int *)_src;
	s_len = s_len/4;
	for(int i = 0;i < b_len && i < s_len; i++)
		buf[i] = (unsigned int)PowMod(src[i],m_rsaE,m_rsaN);
	return AAA_RESULT_S_OK;
}

AAA_Result CCrypt::Encrypt_RC4(unsigned char* buf,int len,const unsigned char* s_box,int s_len)
{
	unsigned char box[AAA_RC4_KEY_LEN];
	int x = 0,y = 0;
	for(;x < AAA_RC4_KEY_LEN;x++){
		if(x < s_len)
			box[x] = s_box[x];
		else
			box[x] = 0;
	}

	for(int k = 0,x = 0; k < len; k++){
		x = (x + 1) % AAA_RC4_KEY_LEN;
		y = (box[x] + y) % AAA_RC4_KEY_LEN;
		box[x] ^= box[y];
		box[y] ^= box[x];
		box[x] ^= box[y];
		buf[k] ^= box[(box[x] + box[y]) % AAA_RC4_KEY_LEN];
	}
	return AAA_RESULT_S_OK;
}

AAA_Result CCrypt::Encrypt_passwd(const char* pUserName,const char* pPasswd,char* buf,int b_len)
{
#ifdef AAAC
	if(pUserName==NULL || pPasswd == NULL || buf == NULL)
		return AAA_RESULT_E_NULL;
	MD5Sum md5;
	char   key[8];
	int n_len = strlen(pUserName)+1;
	md5.put(pUserName,n_len);
	md5.getString(key,0,8);

	int p_len = strlen(pPasswd)+1;
	strcpy_s(buf,p_len,pPasswd);
	p_len = (b_len/8)*8;
	DES_Encrypt(buf,p_len,key);
	for (int i=0; i < b_len; i++)
	{
		buf[i] = ~buf[i];
	}
//	buf[b_len -1] = b_len - 1;
#endif
	return AAA_RESULT_S_OK;
}
AAA_Result CCrypt::Decrypt_passwd(const char* pUserName,const char* pPasswd,int p_len,char* buf,int b_len)
{
#ifdef AAAC
	if(pUserName==NULL || pPasswd == NULL || buf == NULL)
		return AAA_RESULT_E_NULL;
	MD5Sum md5;
	char   key[8];
	int n_len = strlen(pUserName)+1;
	md5.put(pUserName,n_len);
	md5.getString(key,0,8);

	memcpy(buf,pPasswd,p_len);
	n_len = (p_len/8)*8;
	for (int i=0; i < p_len; i++)
	{
		buf[i] = ~buf[i];
	}
	DES_Decrypt(buf,n_len,key);
	buf[p_len] = '\0';
#endif
	return AAA_RESULT_S_OK;
}

AAA_Result CCrypt::EncryptPasswd(const char* pUserName,const char* pPasswd, char* pBuf, int* iLen)
{
#ifdef AAAC
	if (pUserName == NULL || pPasswd == NULL || pBuf == NULL || iLen == NULL)
		return AAA_RESULT_E_NULL;
	int iPasswd = strlen(pPasswd);
	int iBuf;
	if (iPasswd <= 8)
		iBuf = 10;
	else if(iPasswd%8 < 7)
		iBuf = iPasswd + 2;
	else
		iBuf = iPasswd + 3;
	if (*iLen < iBuf)
		return AAA_RESULT_E_BUFF;
	memset(pBuf,0x00,*iLen);
	if (AAAResultSuccess(Encrypt_passwd(pUserName,pPasswd,pBuf+1,iBuf-1)))
	{
		// 0x01 : 加密算法版本号
		pBuf[0] = 0x01;
		pBuf[iBuf -1] = iBuf - 1;
		*iLen = iBuf;
		return AAA_RESULT_S_OK;
	}
#endif
	return AAA_RESULT_E_CRYPT;
}
AAA_Result CCrypt::DecryptPasswd(const char* pUserName,const char* pPasswd,int wLen,char* pBuf,int iLen)
{
#ifdef AAAC
	if (pUserName == NULL || pPasswd == NULL || wLen <= 0 || pBuf == NULL || iLen <= 0)
		return AAA_RESULT_E_NULL;
	if (pPasswd[0] != 0x01) // 加解密版本不对
		return AAA_RESULT_E_VERSION;

	register int end = wLen;
	while (--end)
		if(*(pPasswd+end) == (char)end)
			break;
	if(end < 8)
		return AAA_RESULT_E_ERROR;
	if (iLen <= end)
		return AAA_RESULT_E_BUFF;
	return Decrypt_passwd(pUserName,pPasswd+1,end-1,pBuf,iLen);
#endif
	return AAA_RESULT_E_CRYPT;
}

/* pName ： "RSA"("rsa") or "RC4"("rc4") */
AAA_Result CCrypt::CreateCryptKey(const char* pName, char* pBuf, int iLen)
{
	if (pName == NULL || pBuf == NULL)
		return AAA_RESULT_E_NULL;
	if (strcmp("rsa",pName) == 0)
	{// RSA public key
		if (iLen < 64)
			return AAA_RESULT_E_BUFF;
		sprintf_s(pBuf,iLen,"<RSAKey d=\"%I64u\" n=\"%I64u\">",m_rsaD,m_rsaN);
		return AAA_RESULT_S_OK;
	}
	else if (strcmp("rc4",pName) == 0)
	{// RC4 key
		if (iLen < AAA_RC4_KEY_LEN)
			return AAA_RESULT_E_BUFF;
		for(int i = 0;i < AAA_RC4_KEY_LEN-1;i++)
			pBuf[i] = rand()%94 + 32;       //去掉特殊字符
		pBuf[AAA_RC4_KEY_LEN-1] = '\0';
		return AAA_RESULT_S_OK;
	}
	return AAA_RESULT_E_ERROR;
}

AAA_Result CCrypt::Crypt(const AAA_Crypt* lpCrypt, char* pBuf, int* iLen)
{
	if (lpCrypt && pBuf && iLen)
	{
		switch (lpCrypt->iMethod)
		{
		case AAA_ENCRYPT_RSA://RSA encrypt
			{
				if (lpCrypt->pKey && lpCrypt->pData && lpCrypt->iDataLen > 0)
				{
					AAA_RsaKey d,n;
					unsigned int dd, nn;
					char *pd = strstr(lpCrypt->pKey, "d=\"");
					char *pn = strstr(lpCrypt->pKey, "n=\"");
					sscanf(pd+3, "%u", &dd);
					sscanf(pn+3, "%u", &nn);
					/*if(sscanf_s(lpCrypt->pKey,"<RSAKey d=\"%I64u\" n=\"%I64u\">",&d,&n) < 2)
						return AAA_RESULT_E_CRYPT;*/
					int  bLen = lpCrypt->iDataLen*4;
					if (*iLen < bLen)
						return AAA_RESULT_E_BUFF;
					if(AAAResultSuccess(Encrypt_RSA((unsigned char*)lpCrypt->pData,lpCrypt->iDataLen,dd,nn,pBuf,bLen)))
					{
						*iLen = bLen;
						return AAA_RESULT_S_OK;
					}
				}
			}
			break;
		case AAA_DECRYPT_RSA://RSA decrypt
			{
				if (lpCrypt->pData && lpCrypt->iDataLen > 0)
				{
					int  bLen = lpCrypt->iDataLen/4;
					if (*iLen < bLen)
						return AAA_RESULT_E_BUFF;
					if(AAAResultSuccess(Decrypt_RSA(lpCrypt->pData,lpCrypt->iDataLen,(unsigned char*)pBuf,bLen)))
					{
						*iLen  = bLen;
						return AAA_RESULT_S_OK;
					}
				}
			}
			break;
		case AAA_ENCRYPT_RC4://RC4 encrypt
		case AAA_DECRYPT_RC4://RC4 decrypt
			{
				if (lpCrypt->pKey && lpCrypt->pData)
				{
					int  bLen = lpCrypt->iDataLen;
					if (*iLen < bLen)
						return AAA_RESULT_E_BUFF;
					if (lpCrypt->pData != pBuf)
						memcpy(pBuf,lpCrypt->pData,bLen);
					if(AAAResultSuccess(Encrypt_RC4((unsigned char*)pBuf,bLen,(unsigned char*)lpCrypt->pKey,lpCrypt->iKeyLen)))
					{
						*iLen = bLen;
						return AAA_RESULT_S_OK;
					}
				}
			}
			break;
		}
		return AAA_RESULT_E_CRYPT;
	}
	return AAA_RESULT_E_NULL;
}
