#include "framework.h"
#include <string.h>
#include <android/log.h>
#undef	LOG_TAG
#define LOG_TAG "FRAMEWORK"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

typedef long long __int64;

void* OnConnectComplete::getObject(const String& name) const {
	if (name == YATOM("OnConnectComplete"))
		return const_cast<OnConnectComplete*>(this);
	return Obituary::getObject(name);
}

void* OnRecvFrom::getObject(const String& name) const {
	if (name == YATOM("OnRecvFrom"))
		return const_cast<OnRecvFrom*>(this);
	return Obituary::getObject(name);
}

void* OnRecv::getObject(const String& name) const {
	if (name == YATOM("OnRecv"))
		return const_cast<OnRecv*>(this);
	return Obituary::getObject(name);
}

void* DataSendTo::getObject(const String& name) const {
	if (name == YATOM("DataSendTo"))
		return const_cast<DataSendTo*>(this);
	return Obituary::getObject(name);
}

void* DataSend::getObject(const String& name) const {
	if (name == YATOM("DataSend"))
		return const_cast<DataSend*>(this);
	return Obituary::getObject(name);
}

void* Transmit::getObject(const String& name) const {
	if (name == YATOM("Transmit"))
		return const_cast<Transmit*>(this);
	return Obituary::getObject(name);
}

#define ALIGNUP(a)  (((long)(a) + m_iAlignment - 1)/m_iAlignment * m_iAlignment)

IOBuf::IOBuf(int iTotalSize, int iAlignment, bool cond) {
	m_iMaxUnitCount = MAX_UNIT_COUNT;
	m_iAlignment = iAlignment;
	m_iTotalSize = ALIGNUP(iTotalSize);
	m_pMemBase = new char[m_iTotalSize + m_iAlignment];
	if (!m_pMemBase) {
		return;
	}

	m_pMemAlign = (void*) ALIGNUP(m_pMemBase);

	memset(m_dUnit, 0, sizeof(m_dUnit));
	m_iReadIndex = m_iWriteIndex = 0;
	m_iReadOffset = m_iWriteOffset = 0;
	m_condBool = cond;
	m_wakeup = false;
}

IOBuf::~IOBuf() {
	if (m_pMemBase) {
		delete m_pMemBase;
		m_pMemBase = NULL;
	}
}

void IOBuf::reset() {
	m_iReadIndex = m_iWriteIndex = 0;
	m_iReadOffset = m_iWriteOffset = 0;
}

void IOBuf::wakeup() {
	if (m_condBool) {
		m_wakeup = true;
		m_cond.signal();
	}
}

IOBuf::IOUnit* IOBuf::query(int iIndex) {
	int iWI = m_iWriteIndex;
	int iRI = m_iReadIndex;
	int iCount = iWI - iRI;
	if (iCount < 0) {
		iCount += m_iMaxUnitCount;
	}

	if (iIndex < 0 || iIndex >= iCount) {
		return NULL;
	}
	iRI += iIndex;
	if (iRI >= m_iMaxUnitCount) {
		iRI -= m_iMaxUnitCount;
	}
	return &m_dUnit[iRI];
}

IOBuf::IOUnit* IOBuf::consumeBegin() {
	if (!m_condBool) {
		if (m_iReadIndex == m_iWriteIndex) {
			return NULL;
		}
	} else {
		m_wakeup = false;
		while ((m_iReadIndex == m_iWriteIndex) && !m_wakeup) {
			android::Mutex mutex;
			m_cond.wait(mutex);
		}
		if (m_iReadIndex == m_iWriteIndex) {
			return NULL;
		}
	}
	return &m_dUnit[m_iReadIndex];
}

void IOBuf::consumeEnd(IOUnit* pUnit) {
	m_iReadIndex = (m_iReadIndex + 1) % m_iMaxUnitCount;
	m_iReadOffset = (long) pUnit->pBuf + pUnit->iLength - (long) m_pMemAlign;
}

IOBuf::IOUnit* IOBuf::produceBegin(int iRequireSize) {
	int iAvailSize, iOffset;
	IOUnit* pUnit = NULL;
	int iNextIndex = (m_iWriteIndex + 1) % m_iMaxUnitCount;
	if (iNextIndex == m_iReadIndex) {
		return NULL;
	}
	iRequireSize = ALIGNUP(iRequireSize);
	if (m_iWriteOffset >= m_iReadOffset) {
		iAvailSize = m_iTotalSize - m_iWriteOffset - 1;
		if (iAvailSize >= iRequireSize) {
			iOffset = m_iWriteOffset;
		} else {
			iAvailSize = m_iReadOffset - 1;
			iOffset = 0;
		}
	} else {
		iAvailSize = m_iReadOffset - m_iWriteOffset - 1;
		iOffset = m_iWriteOffset;
	}
	if (iAvailSize >= iRequireSize) {
		m_dUnit[m_iWriteIndex].pBuf = (void*) ((long) m_pMemAlign + iOffset);
		m_dUnit[m_iWriteIndex].iLength = iRequireSize;
		pUnit = &m_dUnit[m_iWriteIndex];
	}
	return pUnit;
}

void IOBuf::produceEnd(IOUnit* pUnit) {
	m_iWriteIndex = (m_iWriteIndex + 1) % m_iMaxUnitCount;
	m_iWriteOffset = (long) pUnit->pBuf + pUnit->iLength - (long) m_pMemAlign;
	if (m_condBool) {
		m_cond.signal();
	}
}

int IOBuf::getAvailCount() {
	int iCount = m_iWriteIndex - m_iReadIndex;
	if (iCount < 0) {
		iCount += m_iMaxUnitCount;
	}
	return iCount;
}

int IOBuf::getUsedPercent() {
	int iDiff = m_iWriteOffset - m_iReadOffset;
	int iPercent;
	if (iDiff < 0) {
		iDiff += m_iTotalSize;
	}
	if (iDiff >= 1024 * 1024 * 10) {
		iPercent = int(
				((__int64 ) iDiff * 100 + (m_iTotalSize >> 1))
						/ (__int64 ) m_iTotalSize);
	} else {
		iPercent = (iDiff * 100 + (m_iTotalSize >> 1)) / m_iTotalSize;
	}
	return iPercent;
}

void* IOBuf::getObject(const String& name) const {
	if (name == YATOM("IOBuf"))
		return const_cast<IOBuf*>(this);
	return Obituary::getObject(name);
}

status_t DataSendWapper::sendTo(char *data, int *size, int flags,
		sockaddr *addr, int addrlen) {
	return send(data, size, flags);
}

void* DataSendWapper::getObject(const String& name) const {
	if (name == YATOM("DataSendWapper"))
		return const_cast<DataSendWapper*>(this);
	else if (name == YATOM("DataSendTo"))
		return DataSendTo::getObject("DataSendTo");
	return DataSend::getObject("DataSend");
}

void* OnEventCallback::getObject(const String& name) const {
	if (name == YATOM("OnEventCallback"))
		return const_cast<OnEventCallback*>(this);
	return Obituary::getObject(name);
}

DeathRecipient::~DeathRecipient() {

}

Obituary::Obituary() {

}

Obituary::~Obituary() {

}

status_t Obituary::linkToDeath(DeathRecipient* recipient, void* cookie,
		int flags) {
	Obituary_t ob;
	ob.recipient = recipient;
	ob.cookie = cookie;
	ob.flags = flags;
	m_qbituaries.push_back(ob);
	return STATUS_SUCCESS;
}

status_t Obituary::unlinkToDeath(DeathRecipient* recipient, void* cookie,
		int flags) {
	std::list<Obituary_t>::iterator iter = m_qbituaries.begin();
	while (iter != m_qbituaries.end()) {
		Obituary_t& obit = *iter;
		if (obit.cookie == cookie && obit.flags == flags
				&& obit.recipient == recipient) {
			m_qbituaries.erase(iter);
			return STATUS_SUCCESS;
		}
		++iter;
	}
	return STATUS_ERROR;
}

void Obituary::sendObituary() {
	std::list<Obituary_t>::iterator iter = m_qbituaries.begin();
	while (iter != m_qbituaries.end()) {
		Obituary_t& obit = *iter;
		obit.recipient->died((RefObject*) getObject("RefObject"), obit.cookie,
				obit.flags);
		m_qbituaries.erase(iter++);
	}
}

void* Obituary::getObject(const String& name) const {
	if (name == YATOM("Obituary"))
		return const_cast<Obituary*>(this);
	return RefObject::getObject(name);
}

int SimpleThread::count = 0;

SimpleThread::SimpleThread() {

}

SimpleThread::~SimpleThread() {

}

void SimpleThread::startup() {
	pthread_create(&pt, NULL, __run, this);
}

void* SimpleThread::__run(void* that) {
	SimpleThread *thread = (SimpleThread*) that;
	count++;
	thread->run();
	count--;
	thread->cleanup();
	LOGI("SimpleThread count=%d", count);
	return NULL;
}

void SimpleThread::exit() {

}
