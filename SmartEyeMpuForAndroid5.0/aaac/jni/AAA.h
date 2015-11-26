#ifndef __AAA_H__
#define __AAA_H__
//This Hand is AAAS and AAAC have together


#define AAA_RSA_KEY_LEN  64         //rsa��Կ����
#define AAA_RC4_KEY_LEN  256        //rc4��Կ����

#define AAAResultSuccess(p) (((int)(p) >= 0))
#define AAAResultFailed(p)  (((int)(p) <  0))

//  �ӽ��ܷ�ʽö��
enum
{
	AAA_CRYPT_UNKNOWN,    //δ֪�ӽ��ܷ�ʽ

	AAA_ENCRYPT_RSA = 1,  //RSA����
	AAA_DECRYPT_RSA = 2,  //RSA����
	AAA_CRYPT_RSA   = 3,  //RSA��Կ

	AAA_ENCRYPT_RC4 = 4,  //RC4����
	AAA_DECRYPT_RC4 = 8,  //RC4����
	AAA_CRYPT_RC4   = 12, //RC4��Կ
};
//  ���ö��
enum _aaa_result
{
	AAA_RESULT_S_OK = 0,             //�ɹ�
	AAA_RESULT_S_OK2,

	AAA_RESULT_E_ERROR = -0x100,     //�������
	AAA_RESULT_E_PASSWORD,           //�������
	AAA_RESULT_E_VERSION,            //�㷨�汾����(����ȶ��У��������벻��ͬһ�㷨����ʱ�����ظô���)
	AAA_RESULT_E_PERMISSIONS,        //Ȩ�޲���
	AAA_RESULT_E_ALREADEXISTS,       //��Դ�Ѿ�����(һ���ڴ�������Դʱ,���ܻ���ָô���)
	AAA_RESULT_E_NULL,               //��ֵ����
	AAA_RESULT_E_NOFIND,             //������Դ������
	AAA_RESULT_E_CRYPT,              //�ӽ��ܳ���
	AAA_RESULT_E_BUFF,               //������̫С�����ڴ����
	AAA_RESULT_E_FILE,               //�ļ���д����
};
//  ϵͳ����Ȩֵö��
//  ˵�������û�ӵ��ϵͳ����Ȩ��������������鼰���¼�����û����飩ӵ�в���Ȩ��
//  ע�⣺AAA_GROUP_DEFAULT_GOD ���ڵ��û�����ӵ��ϵͳ����Ȩ�����ȫ�����û����飩ӵ�в���Ȩ��
enum
{
	AAA_SYSADMIN_NONE = 0,  // û��ϵͳ����Ȩ
	AAA_SYSADMIN_GROUP = 1, // ���û���ӵ�й���Ȩ
	AAA_SYSADMIN_USER  = 1<<2, // ���û�ӵ�й���Ȩ
	AAA_SYSADMIN_DEV   = 1<<4, // ���豸ӵ�й���Ȩ
	AAA_SYSADMIN_DEVASS= 1<<6, // ���豸ӵ�з���Ȩ
	AAA_SYSADMIN_ALL = AAA_SYSADMIN_GROUP | AAA_SYSADMIN_USER | AAA_SYSADMIN_DEV | AAA_SYSADMIN_DEVASS,
};

//  �������
typedef _aaa_result   AAA_Result;

//  RSA��Կ����
typedef unsigned long long  AAA_RsaKey;

//  �ӽ�������ʱ�����ṹ��
typedef struct _aaa_crypt_
{
	/* �ӽ��ܷ�ʽ , �� AAA_ENCRYPT_* */
	int      iMethod;

	/* �ӽ�����Կ ,(RSA����ʱ�����Բ�����)*/
	char*    pKey;

	/* ��Կ���� */
	int      iKeyLen;

	/* ��Ҫ���ܵ����� */
	char*    pData;

	/* ��Ҫ�������ݵĳ��� */
	int      iDataLen;

}AAA_Crypt;


#endif
