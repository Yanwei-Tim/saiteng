#include "stdafx.h"

#include "crypt.h"

CCrypt  m_crypt;

AAA_Result AAAC_Init  ()
{
	m_crypt.InitKey();
	return AAA_RESULT_S_OK;
}

AAA_Result AAAC_Cleanup()
{
	return AAA_RESULT_S_OK;
}
	
AAA_Result AAAC_EncryptPasswd(const char* userID, const char* passwd, char* buf, int* len)
{
	return m_crypt.EncryptPasswd(userID, passwd, buf, len);
}

AAA_Result AAAC_CreateCryptKey(const char* name, char* buf, int len)
{
	return m_crypt.CreateCryptKey(name, buf, len);
}

AAA_Result AAAC_Crypt(const AAA_Crypt* crypt, char* buf, int* len)
{
	return m_crypt.Crypt(crypt, buf, len);
}
