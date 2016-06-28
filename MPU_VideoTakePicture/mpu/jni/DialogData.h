#ifndef __DIALOG_DATA_H__
#define __DIALOG_DATA_H__
#include "framework.h"

enum {
	DIALOGDATA_LIST_AUDIORENDER, DIALOGDATA_LIST_COUNT
};

class DialogData {
public:
	DialogData();
	~DialogData();

	IOBuf* getIOBuf(int index);
	void setIOBuf(int index, IOBuf* pIOBuf);
private:
	IOBuf* m_ioBuf[DIALOGDATA_LIST_COUNT];
};

#endif
