#ifndef __BVTASK_H__
#define __BVTASK_H__

#include "BVCU.h"
#include "PUConfig.h"

typedef enum _BVCU_TASK_TYPE{
    BVCU_TASK_TYPE_PU_UPGRADE = 0, // PU批量升级任务，负载：BVCU_TaskUpgrade
}BVCU_TASK_TYPE;

typedef struct _BVCU_TASK_Base_
{
    char szID[BVCU_MAX_ID_LEN +1]; // 任务ID号
    char szName[BVCU_MAX_NAME_LEN+1]; // 任务名称

    int  iTaskType; // 任务类型，见 BVCU_TASK_TYPE_* 
}BVCU_Task_Base;

typedef struct _BVCU_TASK_INFO_ {
    
    BVCU_Task_Base stBase;  // 基本信息

    int  bAutoDelete; // 是否自动删除本任务（当任务完成或无效时），0：否 1：是

    //时间设置
    BVCU_WallTime stBegin;//开始时间
    BVCU_WallTime stEnd;//结束时间。在开始/结束时间这段范围内，任务有效。按绝对时间设置  

    BVCU_CmdMsgContent stData; // 负载类型，见相应BVCU_TASK_TYPE_*说明。
}BVCU_Task_Info;

typedef struct _BVCU_TASK_UPGRADE_PU_
{
    char szPUID[BVCU_MAX_ID_LEN +1]; // PU ID 号
    int  iStatus; // 升级状态： -1：没有准备好（可能是PU不在线）；0：已完成；1：正在升级；2：下次启动生效
                  // 默认值： -1
    int  iPercent; // 当iStatus为“1”时，iPercent表示升级进度百分比。（0~100）。
}BVCU_Task_UpgradePU;

typedef struct _BVCU_TASK_UPGRADE_{
    BVCU_PUCFG_Upgrade stUpgrade; // 升级配置。
    int   iPUCount; // 升级的PU个数
    BVCU_Task_UpgradePU* pPUList; // PU　ID　列表
}BVCU_Task_Upgrade;

#endif