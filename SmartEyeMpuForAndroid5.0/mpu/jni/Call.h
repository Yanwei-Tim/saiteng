#ifndef __CALL_H__
#define __CALL_H__
#include "framework.h"
#include "DialogData.h"

enum {
	CALL_TYPE_CALL, CALL_TYPE_DIALOG,
};

class Call {
public:
	Call(int type);
	~Call();

	int getCallType();
	DialogData* getDialogData();
private:
	int m_type;
	DialogData m_dialogData;
};

#endif
