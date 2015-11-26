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

//一天中的一个时间片
typedef struct _BVCU_DayTimeSlice{
    char cHourBegin, cMinuteBegin, cSecondBegin;
    char cHourEnd, cMinuteEnd, cSecondEnd;
    char cReserved[2];//对齐
}BVCU_DayTimeSlice;
//注意：所有BVCU_DayTimeSlice stWeek[7][...]等表示一周中的时间片，stWeek[0]表示周日，stWeek[1]表示周一,...stWeek[6]表示周六

//墙上时间
typedef struct _BVCU_WallTime{
    short iYear; 
    char  iMonth; 
    char  iDay; 
    char  iHour;
    char  iMinute;
    char  iSecond;
    char  cReserved[1];//对齐
}BVCU_WallTime;

//视频制式
typedef struct _BVCU_VideoFormat{
    int iVideoForamt;//BVCU_VIDEOFORMAT_*
    BVCU_ImageSize stImageSize[8];//该制式支持的图像分辨率。
    int iFPSMax[8];//不同分辨率对应的最大采样帧率。单位1/1000帧。例如25fps，需要设置为25*1000。只读
    int iIndex;//当前使用的分辨率索引。
}BVCU_VideoFormat;

//显示参数
#define BVCU_HWND void* 

typedef struct _BVCU_Display_Rect
{
    int iLeft;
    int iTop;
    int iRight;
    int iBottom;
}BVCU_Display_Rect;

#endif

