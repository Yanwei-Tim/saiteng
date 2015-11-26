#ifndef __BVCU_COMMON_H__
#define __BVCU_COMMON_H__

typedef struct _BVCU_ImageSize{
    int iWidth;
    int iHeight;
}BVCU_ImageSize;

typedef struct _BVCU_ImagePos{
    int iLeft;
    int iTop;
}BVCU_ImagePos;

typedef struct _BVCU_ImageRect{
    int iLeft;
    int iTop;
    int iWidth;
    int iHeight;
}BVCU_ImageRect;

//һ���е�һ��ʱ��Ƭ
typedef struct _BVCU_DayTimeSlice{
    char cHourBegin, cMinuteBegin, cSecondBegin;
    char cHourEnd, cMinuteEnd, cSecondEnd;
    char cReserved[2];//����
}BVCU_DayTimeSlice;
//ע�⣺����BVCU_DayTimeSlice stWeek[7][...]�ȱ�ʾһ���е�ʱ��Ƭ��stWeek[0]��ʾ���գ�stWeek[1]��ʾ��һ,...stWeek[6]��ʾ����

//ǽ��ʱ��
typedef struct _BVCU_WallTime{
    short iYear; 
    char  iMonth; 
    char  iDay; 
    char  iHour;
    char  iMinute;
    char  iSecond;
    char  cReserved[1];//����
}BVCU_WallTime;

//��Ƶ��ʽ
typedef struct _BVCU_VideoFormat{
    int iVideoForamt;//BVCU_VIDEOFORMAT_*
    BVCU_ImageSize stImageSize[8];//����ʽ֧�ֵ�ͼ��ֱ��ʡ�
    int iFPSMax[8];//��ͬ�ֱ��ʶ�Ӧ��������֡�ʡ���λ1/1000֡������25fps����Ҫ����Ϊ25*1000��ֻ��
    int iIndex;//��ǰʹ�õķֱ���������
}BVCU_VideoFormat;

//��ʾ����
#define BVCU_HWND void* 

typedef struct _BVCU_Display_Rect
{
    int iLeft;
    int iTop;
    int iRight;
    int iBottom;
}BVCU_Display_Rect;

#endif

