#pragma once

#include <stdlib.h>
#include <time.h>

typedef struct  RSA_PARAM_Tag
{ 
	unsigned __int64    p, q;   //两个素数，不参与加密解密运算
	unsigned __int64    f;      //f=(p-1)*(q-1)，不参与加密解密运算
	unsigned __int64    n, e;   //公匙，n=p*q，gcd(e,f)=1
	unsigned __int64    d;      //私匙，e*d=1 (mod f)，gcd(n,d)=1
	unsigned __int64    s;      //块长，满足2^s<=n的最大的s，即log2(n)
} RSA_PARAM;


class  RandNumber{   
private:
	unsigned __int64    randSeed;
public:
	RandNumber(unsigned __int64 s=0);
	unsigned __int64    Random(unsigned __int64 n);  
};

/* 模乘运算，返回值 x=a*b mod n  */
inline unsigned __int64 MulMod(unsigned __int64 a, unsigned __int64 b, unsigned __int64 n);

/* 模幂运算，返回值 x=base^pow mod n */
unsigned __int64 PowMod(unsigned __int64 base, unsigned __int64 &pow, unsigned __int64 &n);

/*  Rabin-Miller素数测试，通过测试返回1，否则返回0。 n是待测素数。注意：通过测试并不一定就是素数，非素数通过测试的概率是1/4  */
long RabinMillerKnl(unsigned __int64 &n);

/*   Rabin-Miller素数测试，循环调用核心loop次全部通过返回1，否则返回0    */
long RabinMiller(unsigned __int64 &n, long loop);

/* 随机生成一个bits位(二进制位)的素数，最多32位 */
unsigned __int64 RandomPrime(char bits);

/*欧几里得法求最大公约数*/
unsigned __int64 EuclidGcd(unsigned __int64 &p, unsigned __int64 &q);

/*  Stein法求最大公约数  */
unsigned __int64 SteinGcd(unsigned __int64 &p, unsigned __int64 &q);

/*  已知a、b，求x，满足a*x =1 (mod b)相当于求解a*x-b*y=1的最小整数解   */
unsigned __int64 Euclid(unsigned __int64 &a, unsigned __int64 &b);

/* 随机产生一个RSA加密参数 */
RSA_PARAM RsaGetParam(unsigned __int64 * rsa_e,unsigned __int64 * rsa_d,unsigned __int64 * rsa_n);
