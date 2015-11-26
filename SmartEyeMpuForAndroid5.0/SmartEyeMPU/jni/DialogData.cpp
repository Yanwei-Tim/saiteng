#include "DialogData.h"

DialogData::DialogData() {
	memset(m_ioBuf, 0, sizeof(m_ioBuf));
}

DialogData::~DialogData() {
	for (int i = 0; i < DIALOGDATA_LIST_COUNT; i++) {
		if (m_ioBuf[i]) {
			delete m_ioBuf[i];
			m_ioBuf[i] = NULL;
		}
	}
}

IOBuf* DialogData::getIOBuf(int index) {
	return m_ioBuf[index];
}

void DialogData::setIOBuf(int index, IOBuf* pIOBuf) {
	m_ioBuf[index] = pIOBuf;
}
