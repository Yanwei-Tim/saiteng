#pragma once

#include <stdlib.h>
#include <time.h>

typedef struct  RSA_PARAM_Tag
{ 
	unsigned __int64    p, q;   //������������������ܽ�������
	unsigned __int64    f;      //f=(p-1)*(q-1)����������ܽ�������
	unsigned __int64    n, e;   //���ף�n=p*q��gcd(e,f)=1
	unsigned __int64    d;      //˽�ף�e*d=1 (mod f)��gcd(n,d)=1
	unsigned __int64    s;      //�鳤������2^s<=n������s����log2(n)
} RSA_PARAM;


class  RandNumber{   
private:
	unsigned __int64    randSeed;
public:
	RandNumber(unsigned __int64 s=0);
	unsigned __int64    Random(unsigned __int64 n);  
};

/* ģ�����㣬����ֵ x=a*b mod n  */
inline unsigned __int64 MulMod(unsigned __int64 a, unsigned __int64 b, unsigned __int64 n);

/* ģ�����㣬����ֵ x=base^pow mod n */
unsigned __int64 PowMod(unsigned __int64 base, unsigned __int64 &pow, unsigned __int64 &n);

/*  Rabin-Miller�������ԣ�ͨ�����Է���1�����򷵻�0�� n�Ǵ���������ע�⣺ͨ�����Բ���һ������������������ͨ�����Եĸ�����1/4  */
long RabinMillerKnl(unsigned __int64 &n);

/*   Rabin-Miller�������ԣ�ѭ�����ú���loop��ȫ��ͨ������1�����򷵻�0    */
long RabinMiller(unsigned __int64 &n, long loop);

/* �������һ��bitsλ(������λ)�����������32λ */
unsigned __int64 RandomPrime(char bits);

/*ŷ����÷������Լ��*/
unsigned __int64 EuclidGcd(unsigned __int64 &p, unsigned __int64 &q);

/*  Stein�������Լ��  */
unsigned __int64 SteinGcd(unsigned __int64 &p, unsigned __int64 &q);

/*  ��֪a��b����x������a*x =1 (mod b)�൱�����a*x-b*y=1����С������   */
unsigned __int64 Euclid(unsigned __int64 &a, unsigned __int64 &b);

/* �������һ��RSA���ܲ��� */
RSA_PARAM RsaGetParam(unsigned __int64 * rsa_e,unsigned __int64 * rsa_d,unsigned __int64 * rsa_n);
