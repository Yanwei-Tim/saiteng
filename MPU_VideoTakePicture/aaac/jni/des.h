#include "time.h"   
#include "stdlib.h"   
#include "memory.h"   

//��string ��'\0'����ַ�����Ϊ 0�����ر��޸��ַ�����
int TrimeString(char * string,int len);

//�������ݳ��ȱ�����8�ı��������򻹻ش��� 2
int DES_Encrypt(char * data,int d_len, char * keyStr);

int DES_Decrypt(char * data,int d_len, char * keyStr);