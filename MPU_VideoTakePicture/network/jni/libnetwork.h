#ifndef __LIB_NETWORK_H__
#define __LIB_NETWORK_H__
#include <yatengine.h>
#include <yatephone.h>
#include <framework.h>
#include <map>
#include <vector>
using namespace std;

using namespace TelEngine;
namespace {
class NetworkModule;
class WorkThread;
class SetupHandler;
class ConnectHandler;
class DataReadHandler;
class DataRecvFromHandler;
class SendHandler;
class CloseHandler;
class SocketOpUtil;
class TCPTransport;
class UDPTransport;
class Transport;

class ProtocolHolder {
public:
	enum Protocol {
		Unknown = 0, Udp, Tcp, Tls
	};
	static inline const char* lookupProtoName(int proto,
			bool upperCase = true) {
		return lookup(proto, upperCase ? s_protoUC : s_protoLC);
	}
	static const TokenDict s_protoLC[]; // Lower case proto name
	static const TokenDict s_protoUC[]; // Upper case proto name
};

typedef struct _SocketOp {
	Socket *sock;

	int protocolType;

	enum {
		SOCKET_OP_NONE = 0, SOCKET_OP_CONNECT = 1 << 1, SOCKET_OP_RECVFROM = 1
				<< 2, SOCKET_OP_READ = 1 << 3,
	};

	int sockOp;

	vector<OnConnectComplete*> connect;

	vector<OnRecv*> dataRead;

	vector<OnRecvFrom*> recvFrom;

	vector<Transport*> transport;
} SocketOp;

class WorkThread: public SimpleThread {
public:
	WorkThread();
	~WorkThread();
	virtual void run();
	virtual void cleanup();
	virtual void stop();
private:
	volatile int m_exit;
};

class NetworkModule: public Module {
public:
	NetworkModule();
	~NetworkModule();
	inline void startInitThread();
	int add(Socket* sock);
	SocketOp* find(int id);
	void remove(int id);
	std::map<int, SocketOp*>* getSocketMap();
protected:
	virtual void initialize();
	virtual bool received(Message& msg, int id);
private:
	WorkThread *m_workThread;
	std::map<int, SocketOp*> m_mapSock;
	static int s_socketId;
};

class SendHandler: public MessageHandler {
public:
	SendHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class SetupHandler: public MessageHandler {
public:
	SetupHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
private:
	Socket* initSocket(int proto, SocketAddr& lAddr, bool forceBind,
			String& reason);
};

class ConnectHandler: public MessageHandler {
public:
	ConnectHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class DataReadHandler: public MessageHandler {
public:
	DataReadHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class DataRecvFromHandler: public MessageHandler {
public:
	DataRecvFromHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class SocketOpUtil {
public:
	static void dataRead(SocketOp *pOp);
	static void recvfrom(SocketOp *pOp);
	static void connect(SocketOp *pOp);
};

class CloseHandler: public MessageHandler {
public:
	CloseHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class Transport {
public:
	virtual status_t sendPacket(char *data, int *size, int flags,
			sockaddr *addr, int addrlen) = 0;
	IOBuf* getIOBuf() {
		return m_buf;
	}
	virtual ~Transport();
protected:
	Socket* m_socket;
	IOBuf *m_buf;
};

class TCPTransport: public DataSendWapper, public Transport {
public:
	TCPTransport(Socket *socket);
	virtual ~TCPTransport();
	virtual status_t send(char *data, int *size, int flags);
	virtual status_t sendPacket(char *data, int *size, int flags,
			sockaddr *addr, int addrlen);
};

class UDPTransport: public DataSendTo, public Transport {
public:
	UDPTransport(Socket *socket);
	virtual ~UDPTransport();
	virtual status_t sendTo(char *data, int *size, int flags, sockaddr *addr,
			int addrlen);
	virtual status_t sendPacket(char *data, int *size, int flags,
			sockaddr *addr, int addrlen);
};

}
;

#endif
