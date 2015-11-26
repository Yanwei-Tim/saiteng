#ifndef __FRAMEWORK_H__
#define __FRAMEWORK_H__
#include <types.h>
#include <yatengine.h>
#include <yatephone.h>
#include <map>
#include <list>
#include <utils/threads.h>
extern "C" {
#include "SAVCodec.h"
}
;
using namespace std;
using namespace TelEngine;
using namespace android;

#ifdef WIN32
#ifdef LIBFRAMEWORK_EXPORTS
#define FRAMEWORK_API __declspec(dllexport)
#else
#define FRAMEWORK_API __declspec(dllimport)
#endif
#else
#define FRAMEWORK_API
#endif

class FRAMEWORK_API DeathRecipient {
public:
	virtual ~DeathRecipient();
	virtual void died(RefObject* who, void* cookie, int flags) = 0;
};

class FRAMEWORK_API Obituary: public RefObject {
public:
	Obituary();
	virtual ~Obituary();
	virtual status_t linkToDeath(DeathRecipient* recipient, void* cookie,
			int flags);
	virtual status_t unlinkToDeath(DeathRecipient* recipient, void* cookie,
			int flags);
	void sendObituary();
	virtual void* getObject(const String& name) const;
private:
	struct Obituary_t {
		DeathRecipient *recipient;
		void* cookie;
		int flags;
	};
	std::list<Obituary_t> m_qbituaries;
};

class FRAMEWORK_API OnConnectComplete: public Obituary {
public:
	virtual void onConnectComplete(status_t status) = 0;
	virtual void* getObject(const String& name) const;
};

class FRAMEWORK_API OnRecvFrom: public Obituary {
public:
	virtual void onRecvFrom(char* data, int size, sockaddr *src_addr,
			int addr_len, status_t status) = 0;
	virtual void* getObject(const String& name) const;
};

class FRAMEWORK_API OnRecv: public Obituary {
public:
	virtual void onRecv(char *data, int size, status_t status,
			int *remainder) = 0;
	virtual void* getObject(const String& name) const;
};

class FRAMEWORK_API DataSendTo: public Obituary {
public:
	virtual status_t sendTo(char *data, int *size, int flags, sockaddr *addr,
			int addrlen) = 0;
	virtual void* getObject(const String& name) const;
};

class FRAMEWORK_API DataSend: public Obituary {
public:
	virtual status_t send(char *data, int *size, int flags) = 0;
	virtual void* getObject(const String& name) const;
};

class FRAMEWORK_API DataSendWapper: public DataSendTo, public DataSend {
public:
	virtual status_t sendTo(char *data, int *size, int flags, sockaddr *addr,
			int addrlen);
	virtual status_t send(char *data, int *size, int flags) = 0;
	virtual void* getObject(const String& name) const;
};

typedef struct _IONet {
	char* buf;
	int len;
	int flags;
	sockaddr_in addr;
} IONet;

typedef union _BigData {
	SAV_Packet packet;
	SAV_Frame frame;
	IONet net;
} BigData;

class FRAMEWORK_API Transmit: public Obituary {
public:
	virtual void transmit(BigData*) = 0;
	virtual void* getObject(const String& name) const;
};

class FRAMEWORK_API SimpleThread {
public:
	SimpleThread();
	virtual ~SimpleThread();
	virtual void startup();
	virtual void run() = 0;
	virtual void cleanup() = 0;
	virtual void exit();
private:
	static void* __run(void*);
private:
	pthread_t pt;
	static int count;
};

class FRAMEWORK_API IOBuf: public Obituary {
public:
	struct IOUnit {
		BigData data;
		void* pBuf;
		int iLength;
	};
private:
	enum {
		MAX_UNIT_COUNT = 1024
	};
	IOUnit m_dUnit[MAX_UNIT_COUNT];
	int m_iReadOffset, m_iWriteOffset;
	int m_iReadIndex, m_iWriteIndex;
	int m_iMaxUnitCount;
	int m_iTotalSize;
	int m_iAlignment;
	char* m_pMemBase;
	void* m_pMemAlign;
	Condition m_cond;
	bool m_condBool;
	bool m_wakeup;
public:
	IOBuf(int iTotalSize, int iAlignment, bool cond = false);
	virtual ~IOBuf();
	IOUnit* consumeBegin();
	void consumeEnd(IOUnit* pUnit);
	IOUnit* produceBegin(int iRequireSize);
	void produceEnd(IOUnit* pUnit);
	int getAvailCount();
	IOUnit* query(int iIndex);
	int getUsedPercent();
	void reset();
	void wakeup();
	virtual void* getObject(const String& name) const;
};

class FRAMEWORK_API OnEventCallback: public Obituary {
public:
	virtual void* getParam() = 0;
	virtual void onEvent(int iId, int iEventCode, void* pParam) = 0;
	virtual void* getObject(const String& name) const;
};

#endif
