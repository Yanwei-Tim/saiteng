#ifndef __BVTASK_H__
#define __BVTASK_H__

#include "BVCU.h"
#include "PUConfig.h"

typedef enum _BVCU_TASK_TYPE{
    BVCU_TASK_TYPE_PU_UPGRADE = 0, // PU�����������񣬸��أ�BVCU_TaskUpgrade
}BVCU_TASK_TYPE;

typedef struct _BVCU_TASK_Base_
{
    char szID[BVCU_MAX_ID_LEN +1]; // ����ID��
    char szName[BVCU_MAX_NAME_LEN+1]; // ��������

    int  iTaskType; // �������ͣ��� BVCU_TASK_TYPE_* 
}BVCU_Task_Base;

typedef struct _BVCU_TASK_INFO_ {
    
    BVCU_Task_Base stBase;  // ������Ϣ

    int  bAutoDelete; // �Ƿ��Զ�ɾ�������񣨵�������ɻ���Чʱ����0���� 1����

    //ʱ������
    BVCU_WallTime stBegin;//��ʼʱ��
    BVCU_WallTime stEnd;//����ʱ�䡣�ڿ�ʼ/����ʱ����η�Χ�ڣ�������Ч��������ʱ������  

    BVCU_CmdMsgContent stData; // �������ͣ�����ӦBVCU_TASK_TYPE_*˵����
}BVCU_Task_Info;

typedef struct _BVCU_TASK_UPGRADE_PU_
{
    char szPUID[BVCU_MAX_ID_LEN +1]; // PU ID ��
    int  iStatus; // ����״̬�� -1��û��׼���ã�������PU�����ߣ���0������ɣ�1������������2���´�������Ч
                  // Ĭ��ֵ�� -1
    int  iPercent; // ��iStatusΪ��1��ʱ��iPercent��ʾ�������Ȱٷֱȡ���0~100����
}BVCU_Task_UpgradePU;

typedef struct _BVCU_TASK_UPGRADE_{
    BVCU_PUCFG_Upgrade stUpgrade; // �������á�
    int   iPUCount; // ������PU����
    BVCU_Task_UpgradePU* pPUList; // PU��ID���б�
}BVCU_Task_Upgrade;

#endif