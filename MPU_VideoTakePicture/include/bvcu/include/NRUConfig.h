#ifndef __NRU_CONFIG_H__
#define __NRU_CONFIG_H__

#include "BVCUConst.h"
#include "BVCUCommon.h"
//##!!!!!!!ע�⣺���нṹ���Reserved��Ա�����ʼ��Ϊ0!!!!!!!!!!!!!
//============NRU��Ϣ=======================
//�洢����
typedef struct _BVCU_NRUCFG_StorageMedia{
    char szName[BVCU_MAX_NAME_LEN+1];//���֡�ֻ��
    unsigned int  iTotalSpace;//�ܿռ䣬��λMB��ֻ��
    unsigned int  iFreeSpace;//ʣ��ռ䣬��λMB��ֻ��
    int  bInUse;//�Ƿ�ʹ�øô洢������д
}BVCU_NRUCFG_StorageMedia;

typedef struct _BVCU_NRUCFG_NRUItem{
    char szID[BVCU_MAX_ID_LEN+1];      //ID��ֻ��
    char szName[BVCU_MAX_NAME_LEN+1];  //���֡���д
    int  iStorageMediaCount;//�洢��������ֻ��
    int  iOnlineStatus; //����״̬���μ�BVCU_ONLINE_STATUS_*����������֪ͨ�У�����iOnlineStatus�ж�������֪ͨ��������֪ͨ
    int  iReserved[2];//����������Ϊ0
}BVCU_NRUCFG_NRUItem;

typedef struct _BVCU_NRUCFG_NRUInfo{
    char szID[BVCU_MAX_ID_LEN+1];             //ID��ֻ��
    char szManufacturer[BVCU_MAX_NAME_LEN+1]; //���������֡�ֻ��
    char szSoftwareVersion[BVCU_MAX_NAME_LEN+1]; //����汾��ֻ��
    char szHardwareVersion[BVCU_MAX_NAME_LEN+1]; //Ӳ���汾��ֻ��
    char szName[BVCU_MAX_NAME_LEN+1];//���֡���д
    int  iNoSpaceRule; //���̿ռ䲻�㴦�����0-ֹͣ¼��1-���Ǿ�¼�񡣿�д
    int  iReserveRecordDays;//¼���ļ�������������д
    int  iRecordFileLength;//¼���ļ�ʱ�䳤�ȡ���λ�롣����д
    int  iReservePicDays;//ץͼ�ļ�������������д
    int  iReserveGPSDays; // GPS���ݱ�����������д
    int  iStorageMediaCount;//�洢��������ֻ��
    BVCU_NRUCFG_StorageMedia* pStorageMedia;//�洢������
    int  iOnlineStatus; //����״̬���μ�BVCU_ONLINE_STATUS_*����������֪ͨ�У�����iOnlineStatus�ж�������֪ͨ��������֪ͨ
    int  iReserved[2];//����������Ϊ0
}BVCU_NRUCFG_NRUInfo;

//============�洢�ƻ�=====================
typedef struct _BVCU_NRUCFG_Storage_Schedule_ListItem{
    char szName[BVCU_MAX_NAME_LEN+1];//�洢�ƻ�������
    BVCU_WallTime stBegin;//��ʼʱ�䡣ȫ0��ʾ������Ч��
    BVCU_WallTime stEnd;//����ʱ�䡣ȫFF��ʾ��Զ���������ڿ�ʼ/����ʱ����η�Χ�ڣ��ƻ���Ч��
}BVCU_NRUCFG_Storage_Schedule_ListItem;

typedef struct _BVCU_NRUCFG_Storage_Channel{
    int  iChannelIndex; //ͨ����, BVCU_SUBDEV_INDEXMAJOR_*
    int  bRecordAudio;  //¼��ʱ�Ƿ�¼����Ƶ 0-��¼ 1-¼
}BVCU_NRUCFG_Storage_Channel;

typedef struct _BVCU_NRUCFG_Storage_PU{
    char szID[BVCU_MAX_ID_LEN+1];  //�豸ID
    int  iChannelCount; //pChannel�����Ա����
    BVCU_NRUCFG_Storage_Channel* pChannel; //ͨ������
}BVCU_NRUCFG_Storage_PU;

typedef struct _BVCU_NRUCFG_Storage_Schedule{
    char szName[BVCU_MAX_NAME_LEN+1];//����
    BVCU_DayTimeSlice stWeek[7][BVCU_MAX_DAYTIMESLICE_COUNT];//һ�ܵ�ʱ��Ƭ���֣�ÿ��BVCU_MAX_DAYTIMESLICE_COUNT��ʱ��Ƭ
    BVCU_WallTime stBegin;//��ʼʱ�䡣ȫ0��ʾ������Ч��
    BVCU_WallTime stEnd;//����ʱ�䡣ȫFF��ʾ��Զ���������ڿ�ʼ/����ʱ����η�Χ�ڣ��ƻ���Ч��
    int  iPUCount; //pStoragePU�����Ա����
    BVCU_NRUCFG_Storage_PU* pStoragePU; //�üƻ��漰����PU�б�
}BVCU_NRUCFG_Storage_Schedule;

//�ֹ�Զ��¼��NRU
typedef struct _BVCU_NRUCFG_ManualRecord{
    char szID[BVCU_MAX_ID_LEN+1];  //�豸ID
    int iChannelIndex;//ͨ����, BVCU_SUBDEV_INDEXMAJOR_*
    int bStart;//1-��ʼ¼��0-ֹͣ¼��
    int iLength;//�洢ʱ�䳤�ȣ���λ��
}BVCU_NRUCFG_ManualRecord;

//�ֹ�Զ��ץ�ĵ�NRU
typedef struct _BVCU_NRUCFG_Snapshot{
    char szID[BVCU_MAX_ID_LEN+1];  //�豸ID
    int iChannelIndex;//ͨ����, BVCU_SUBDEV_INDEXMAJOR_*
    int iCount;//ץ���������������ֵΪ15
    int iInterval;//ץ�ļ������λ�롣�������ֵΪ60��	
}BVCU_NRUCFG_Snapshot;
#endif
