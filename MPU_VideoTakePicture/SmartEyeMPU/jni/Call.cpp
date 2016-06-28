#include "Call.h"

Call::Call(int type) {
	m_type = type;
}

Call::~Call() {
}

int Call::getCallType() {
	return m_type;
}

DialogData* Call::getDialogData() {
	return &m_dialogData;
}
