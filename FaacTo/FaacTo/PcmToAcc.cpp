/**


*/


#include <stdio.h>
#include "iostream"
#include "faac.h"
int main()
{
	// 定义别名
	typedef unsigned char   BYTE;

	unsigned long	nSampleRate = 8000;
	unsigned int	nChannels = 1;
	unsigned int	nPCMBitSize = 16;
	unsigned long	nInputSamples = 0;
	unsigned long	nMaxOutputBytes = 0;
	faacEncHandle	hEncoder = {0};
	// 设置输入输出文件
	FILE* fpIn = fopen("output.pcm", "rb");
	FILE* fpOut = fopen("Beyond.aac", "wb");

	if(fpIn==NULL)
	{
		printf("打开文件失败!\n");
		system("pause");
		return -1;
	}
	// 打开faac编码器引擎
	hEncoder = faacEncOpen(nSampleRate, nChannels, &nInputSamples, &nMaxOutputBytes);
	if(hEncoder == NULL)
	{
		printf("打开faac编码器引擎失败!\n");
		system("pause");
		return -1;
	}
	// 分配内存信息
	int		nPCMBufferSize = nInputSamples*nPCMBitSize/8;
	BYTE*	pbPCMBuffer = new BYTE[nPCMBufferSize];
	BYTE*	pbAACBuffer = new BYTE[nMaxOutputBytes];

	// 获取当前编码器信息
	faacEncConfigurationPtr pConfiguration = {0};
	pConfiguration = faacEncGetCurrentConfiguration(hEncoder);

	// 设置编码配置信息
	/*
		PCM Sample Input Format
		0	FAAC_INPUT_NULL			invalid, signifies a misconfigured config
		1	FAAC_INPUT_16BIT		native endian 16bit
		2	FAAC_INPUT_24BIT		native endian 24bit in 24 bits		(not implemented)
		3	FAAC_INPUT_32BIT		native endian 24bit in 32 bits		(DEFAULT)
		4	FAAC_INPUT_FLOAT		32bit floating point
    */
	pConfiguration->inputFormat = FAAC_INPUT_16BIT;

	// 0 = Raw; 1 = ADTS
	pConfiguration->outputFormat = 1;

	// AAC object types 
	//#define MAIN 1
	//#define LOW  2
	//#define SSR  3
	//#define LTP  4
	pConfiguration->aacObjectType = LOW;
	pConfiguration->allowMidside = 0;
	pConfiguration->useLfe = 0;
	pConfiguration->bitRate = 48000;
	pConfiguration->bandWidth = 32000;

	// 其他的参数不知道怎么配置，毕竟对音频不熟
	// 不过当前的设置可以实现转换，不过声音好像有一丢丢怪异
	// 这一块的配置信息很重要，错了会导致转码失败，然后你以为代码其他地方错了

	// 重置编码器的配置信息
	faacEncSetConfiguration(hEncoder, pConfiguration);

	size_t nRet = 0;

	printf("Data Translate....:");
	int i = 0;
	while( (nRet = fread(pbPCMBuffer, 1, nPCMBufferSize, fpIn)) > 0)
	{
		printf("\b\b\b\b\b\b\b\b%-8d", ++i);
		nInputSamples = nRet / (nPCMBitSize/8);

		// 编码
		nRet = faacEncEncode(hEncoder, (int*) pbPCMBuffer, nInputSamples, pbAACBuffer, nMaxOutputBytes);

		// 写入转码后的数据
		fwrite(pbAACBuffer, 1, nRet, fpOut);
	}

	// 扫尾工作
	faacEncClose(hEncoder);
	fclose(fpOut);
	fclose(fpIn);

	delete[] pbAACBuffer;
	delete[] pbPCMBuffer;
	system("pause");
	return 0;
}