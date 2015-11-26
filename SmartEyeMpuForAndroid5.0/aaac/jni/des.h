#include "time.h"   
#include "stdlib.h"   
#include "memory.h"   

//将string 中'\0'后的字符设置为 0，返回被修改字符个数
int TrimeString(char * string,int len);

//加密数据长度必须是8的倍数，否则还回错误 2
int DES_Encrypt(char * data,int d_len, char * keyStr);

int DES_Decrypt(char * data,int d_len, char * keyStr);