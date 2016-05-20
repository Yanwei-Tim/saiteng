/**


*/


#include <stdio.h>
#include "iostream"
#include "faac.h"
int main()
{
	// �������
	typedef unsigned char   BYTE;

	unsigned long	nSampleRate = 8000;
	unsigned int	nChannels = 1;
	unsigned int	nPCMBitSize = 16;
	unsigned long	nInputSamples = 0;
	unsigned long	nMaxOutputBytes = 0;
	faacEncHandle	hEncoder = {0};
	// ������������ļ�
	FILE* fpIn = fopen("output.pcm", "rb");
	FILE* fpOut = fopen("Beyond.aac", "wb");

	if(fpIn==NULL)
	{
		printf("���ļ�ʧ��!\n");
		system("pause");
		return -1;
	}
	// ��faac����������
	hEncoder = faacEncOpen(nSampleRate, nChannels, &nInputSamples, &nMaxOutputBytes);
	if(hEncoder == NULL)
	{
		printf("��faac����������ʧ��!\n");
		system("pause");
		return -1;
	}
	// �����ڴ���Ϣ
	int		nPCMBufferSize = nInputSamples*nPCMBitSize/8;
	BYTE*	pbPCMBuffer = new BYTE[nPCMBufferSize];
	BYTE*	pbAACBuffer = new BYTE[nMaxOutputBytes];

	// ��ȡ��ǰ��������Ϣ
	faacEncConfigurationPtr pConfiguration = {0};
	pConfiguration = faacEncGetCurrentConfiguration(hEncoder);

	// ���ñ���������Ϣ
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

	// �����Ĳ�����֪����ô���ã��Ͼ�����Ƶ����
	// ������ǰ�����ÿ���ʵ��ת������������������һ��������
	// ��һ���������Ϣ����Ҫ�����˻ᵼ��ת��ʧ�ܣ�Ȼ������Ϊ���������ط�����

	// ���ñ�������������Ϣ
	faacEncSetConfiguration(hEncoder, pConfiguration);

	size_t nRet = 0;

	printf("Data Translate....:");
	int i = 0;
	while( (nRet = fread(pbPCMBuffer, 1, nPCMBufferSize, fpIn)) > 0)
	{
		printf("\b\b\b\b\b\b\b\b%-8d", ++i);
		nInputSamples = nRet / (nPCMBitSize/8);

		// ����
		nRet = faacEncEncode(hEncoder, (int*) pbPCMBuffer, nInputSamples, pbAACBuffer, nMaxOutputBytes);

		// д��ת��������
		fwrite(pbAACBuffer, 1, nRet, fpOut);
	}

	// ɨβ����
	faacEncClose(hEncoder);
	fclose(fpOut);
	fclose(fpIn);

	delete[] pbAACBuffer;
	delete[] pbPCMBuffer;
	system("pause");
	return 0;
}