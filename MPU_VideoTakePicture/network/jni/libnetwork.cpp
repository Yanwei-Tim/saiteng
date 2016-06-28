// libnetwork.cpp : Defines the exported functions for the DLL application.
//
#include "libnetwork.h"
#include <android/log.h>
#undef LOG_TAG
#define  LOG_TAG    "Network"
#define  LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

TelEngine::Mutex s_sockMutex(false, "Network::socks");

// Lower case proto name
const TokenDict ProtocolHolder::s_protoLC[] = { { "udp", Udp }, { "tcp", Tcp },
		{ "tls", Tls }, { 0, 0 }, };

// Upper case proto name
const TokenDict ProtocolHolder::s_protoUC[] = { { "UDP", Udp }, { "TCP", Tcp },
		{ "TLS", Tls }, { 0, 0 }, };

WorkThread::WorkThread() {
	m_exit = 0;
}

WorkThread::~WorkThread() {

}

int NetworkModule::s_socketId = 10001000;

NetworkModule::NetworkModule() :
		Module("Network", "misc") {

}

NetworkModule::~NetworkModule() {
	Output("Unload module Network");
}

void NetworkModule::startInitThread() {
	lock();
	if (!m_workThread)
		(m_workThread = new WorkThread())->startup();
	unlock();
}

int NetworkModule::add(Socket* sock) {
	SocketOp *op = (SocketOp*) malloc(sizeof(SocketOp));
	memset(op, 0, sizeof(SocketOp));
	op->sock = sock;

	m_mapSock.insert(pair<int, SocketOp*>(s_socketId, op));
	int sockId = s_socketId;
	s_socketId++;
	return sockId;
}

SocketOp* NetworkModule::find(int id) {
	map<int, SocketOp*>::iterator iter = m_mapSock.find(id);
	if (iter != m_mapSock.end()) {
		return iter->second;
	}
	return NULL;
}

void NetworkModule::remove(int id) {
	m_mapSock.erase(id);
}

std::map<int, SocketOp*>* NetworkModule::getSocketMap() {
	return &m_mapSock;
}

INIT_PLUGIN(NetworkModule);

SendHandler::SendHandler(unsigned int prio) :
		MessageHandler("net.send", prio, __plugin.name()) {

}

/**
 msg: net.send
 param: socketid [in]
 userdata: IOBuf [return]
 */
bool SendHandler::received(Message& msg) {
	int id = msg.getIntValue("socketid", 0);
	SocketOp *op = __plugin.find(id);
	if (op) {
		Transport *pTransport = NULL;
		if (op->protocolType == ProtocolHolder::Udp) {
			pTransport = new UDPTransport(op->sock);
		} else if (op->protocolType == ProtocolHolder::Tcp) {
			pTransport = new TCPTransport(op->sock);
		}
		op->transport.push_back(pTransport);
		RefObject* pObj = (RefObject*) pTransport->getIOBuf()->getObject(
				"RefObject");
		msg.userData(pObj);
	}
	return true;
}

void NetworkModule::initialize() {
	Output("Initializing module Network");

	Engine::install(new SendHandler());
	Engine::install(new SetupHandler());
	Engine::install(new ConnectHandler());
	Engine::install(new CloseHandler());
	Engine::install(new DataRecvFromHandler());
	Engine::install(new DataReadHandler());
	installRelay(Halt);
	installRelay(Private + 1, "engine.stop");
	startInitThread();

	Module::initialize();
}

bool NetworkModule::received(Message& msg, int id) {
	if (id == Halt) {
	} else if (msg == "engine.stop") {
		m_workThread->stop();
		LOGI("stop");
	}
	return Module::received(msg, id);
}

SetupHandler::SetupHandler(unsigned int prio) :
		MessageHandler("net.socket", prio, __plugin.name()) {

}

/**
 msg: net.socket
 param: type [in]
 param: port [in]
 param: address [in]
 param: socketid [return]
 */
bool SetupHandler::received(Message& msg) {
	const String& type = msg["type"];
	int protocol = ProtocolHolder::Udp;

	if (type) {
		int port = msg.getIntValue("port", 0);
		const String& ip = msg.getValue("address", "0.0.0.0");

		SocketAddr lAddr(AF_INET);
		lAddr.host(ip);
		lAddr.port(port);

		String reason;
		Socket *sock = 0;

		if (type == "udp") {
			protocol = ProtocolHolder::Udp;
			sock = initSocket(ProtocolHolder::Udp, lAddr, true, reason);
		} else if (type == "tcp") {
			protocol = ProtocolHolder::Tcp;
			sock = initSocket(ProtocolHolder::Tcp, lAddr, false, reason);
		}
		if (sock != NULL) {
			int sockId = __plugin.add(sock);
			SocketOp *pOp = __plugin.find(sockId);
			pOp->protocolType = protocol;
			msg.addParam("socketid", String(sockId));
		}

		return true;
	}

	return false;
}

Socket* SetupHandler::initSocket(int proto, SocketAddr& lAddr, bool forceBind,
		String& reason) {
	bool udp = (proto == ProtocolHolder::Udp);
	Socket* sock = 0;
	const char* type = ProtocolHolder::lookupProtoName(proto);
	if (udp) {
		sock = new Socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
		int size = 512 * 1024;
		sock->setOption(SOL_SOCKET, SO_RCVBUF, (char*) &size, sizeof(size));
	} else
		sock = new Socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

	while (true) {
		if (!sock->valid()) {
			reason = "Create socket failed";
			break;
		}
		if (!udp)
			sock->setReuse();
		if (forceBind) {
			bool ok = sock->bind(lAddr);
			if (!ok) {
				String error;
				TelEngine::Thread::errorString(error, sock->error());
				Debug(&__plugin, DebugWarn,
						"Listener(%s) unable to bind on '%s:%d' - trying a random port. %d '%s'",
						type, lAddr.host().c_str(), lAddr.port(), sock->error(),
						error.c_str());

				reason = "Bind failed";
				break;
			}
		}
		if (!sock->setBlocking(false)) {
			reason = "Set non blocking mode failed";
			break;
		}
		break;
	}

	if (!reason) {
		Debug(&__plugin, DebugInfo, "Listener(%s) started on '%s:%d'", type,
				lAddr.host().safe(), lAddr.port());
		return sock;
	}
	delete sock;
	return NULL;
}

ConnectHandler::ConnectHandler(unsigned int prio /* = 100 */) :
		MessageHandler("net.connect", prio, __plugin.name()) {

}

/**
 msg: net.connect
 param: socketid [in]
 param: ip [in]
 param: port [in]
 userdata: OnConnectComplete [in]
 */
bool ConnectHandler::received(Message& msg) {
	int sockId = msg.getIntValue("socketid", 0);
	SocketOp *pOp = __plugin.find(sockId);

	SocketAddr lAddr(AF_INET);
	String ip = msg["address"];
	int port = msg.getIntValue("port");
	lAddr.host(ip);
	lAddr.port(port);

	if (pOp) {
		RefObject *obj = msg.userData();
		OnConnectComplete *pConnect = (OnConnectComplete *) obj->getObject(
				"OnConnectComplete");

		if (pConnect == NULL)
			return false;

		bool succ = pOp->sock->connect(lAddr);
		if (succ) {
			pConnect->onConnectComplete(STATUS_SUCCESS);
		} else {
			pOp->connect.push_back(pConnect);
			pOp->sockOp = pOp->sockOp = SocketOp::SOCKET_OP_CONNECT;
		}
		return true;
	}
	return false;
}

DataReadHandler::DataReadHandler(unsigned int prio) :
		MessageHandler("net.read", prio, __plugin.name()) {

}

/**
 msg: net.read
 param: socketid [in]
 userdata: OnRecv [in]
 */
bool DataReadHandler::received(Message& msg) {
	int sockId = msg.getIntValue("socketid", 0);
	SocketOp *pOp = __plugin.find(sockId);

	if (pOp) {
		RefObject *pObject = msg.userData();
		OnRecv *pRead = (OnRecv*) pObject->getObject("OnRecv");

		pOp->dataRead.push_back(pRead);
		pOp->sockOp = SocketOp::SOCKET_OP_READ;

		return true;
	}
	return false;
}

DataRecvFromHandler::DataRecvFromHandler(unsigned int prio) :
		MessageHandler("net.recvfrom", prio, __plugin.name()) {

}

/**
 msg: net.recvfrom
 param: socketid [in]
 userdata: OnRecvFrom [in]
 */
bool DataRecvFromHandler::received(Message& msg) {
	int sockId = msg.getIntValue("socketid", 0);
	SocketOp *pOp = __plugin.find(sockId);

	if (pOp) {
		RefObject *pObject = msg.userData();
		OnRecvFrom *pRead = (OnRecvFrom*) pObject->getObject("OnRecvFrom");
		pOp->recvFrom.push_back(pRead);
		pOp->sockOp = SocketOp::SOCKET_OP_RECVFROM;

		return true;
	}
	return false;
}

void WorkThread::run() {
	while (!m_exit) {
		fd_set read_set;
		fd_set write_set;
		fd_set except_set;

		FD_ZERO(&read_set);
		FD_ZERO(&write_set);
		FD_ZERO(&except_set);

		int fd = 0;

		Lock lock(s_sockMutex);

		map<int, SocketOp*>::iterator iter = __plugin.getSocketMap()->begin();
		while (iter != __plugin.getSocketMap()->end()) {
			SocketOp *op = iter->second;
			if (fd < op->sock->handle()) {
				fd = op->sock->handle();
			}
			if (op->sockOp & SocketOp::SOCKET_OP_READ) {
				FD_SET(op->sock->handle(), &read_set);
			}
			if (op->sockOp & SocketOp::SOCKET_OP_RECVFROM) {
				FD_SET(op->sock->handle(), &read_set);
			}
			if (op->sockOp & SocketOp::SOCKET_OP_CONNECT) {
				FD_SET(op->sock->handle(), &write_set);
			}

			++iter;
		}

		iter = __plugin.getSocketMap()->begin();
		while (iter != __plugin.getSocketMap()->end()) {
			SocketOp *op = iter->second;
			for (int i = 0; i < op->transport.size(); i++) {
				Transport *pTransport = op->transport[i];
				IOBuf::IOUnit* pUnit = pTransport->getIOBuf()->consumeBegin();
				if (pUnit != NULL) {
					int buf_len = pUnit->data.net.len;

					pTransport->sendPacket(pUnit->data.net.buf, &buf_len, 0,
							(sockaddr*) &pUnit->data.net.addr,
							sizeof(pUnit->data.net.addr));
					pTransport->getIOBuf()->consumeEnd(pUnit);
				}
			}

			++iter;
		}

		timeval tval;
		tval.tv_sec = 0;
		tval.tv_usec = 10;
		int err = select(fd + 1, &read_set, &write_set, NULL, &tval);
		if (err > 0) {
			iter = __plugin.getSocketMap()->begin();
			while (iter != __plugin.getSocketMap()->end()) {
				SocketOp *op = iter->second;
				SOCKET sock = op->sock->handle();
				if (FD_ISSET(sock, &read_set)) {
					if (op->protocolType == ProtocolHolder::Udp) {
						SocketOpUtil::recvfrom(op);
					} else if (op->protocolType == ProtocolHolder::Tcp) {
						SocketOpUtil::dataRead(op);
					}
				}
				if (FD_ISSET(sock, &write_set)) {
					if (op->protocolType == ProtocolHolder::Tcp
							&& op->sockOp == SocketOp::SOCKET_OP_CONNECT) {
						op->sockOp = SocketOp::SOCKET_OP_NONE;
						SocketOpUtil::connect(op);
					}
				}

				++iter;
			}
		}
	}
}

void WorkThread::cleanup() {

}

void WorkThread::stop() {
	m_exit = 1;
}

void SocketOpUtil::dataRead(SocketOp *pOp) {
	for (int i = 0; i < pOp->dataRead.size(); i++) {
		OnRecv *pRead = pOp->dataRead[i];
		char buf[1600];
		int buf_len = 1600;
		buf_len = pOp->sock->readData(buf, buf_len);
		int remainder = 0;
		int status = STATUS_SUCCESS;
		if (buf_len <= 0) {
			status = STATUS_ERROR;
		}
		pRead->onRecv(buf, buf_len, status, &remainder);
	}
}

void SocketOpUtil::recvfrom(SocketOp *pOp) {
	for (int i = 0; i < pOp->recvFrom.size(); i++) {
		OnRecvFrom *pRead = pOp->recvFrom[i];
		char buf[1600];
		int buf_len = 1600;
		SocketAddr lAddr;

		buf_len = pOp->sock->recvFrom(buf, buf_len, lAddr);
		pRead->onRecvFrom(buf, buf_len, lAddr.address(), lAddr.length(),
				STATUS_SUCCESS);
	}
}

void SocketOpUtil::connect(SocketOp *pOp) {
	int error = 0;
	int length = sizeof(error);
	bool err = pOp->sock->getOption(SOL_SOCKET, SO_ERROR, &error, &length);
	for (int i = 0; i < pOp->connect.size(); i++) {
		OnConnectComplete *pRead = pOp->connect[i];
		pRead->onConnectComplete(err ? STATUS_SUCCESS : STATUS_ERROR);
	}
}

CloseHandler::CloseHandler(unsigned int prio /* = 100 */) :
		MessageHandler("net.close", prio, __plugin.name()) {

}

/**
 msg: net.close
 param: socketid [in]
 */
bool CloseHandler::received(Message& msg) {
	int sockId = msg.getIntValue("socketid", 0);
	SocketOp *pOp = __plugin.find(sockId);
	if (pOp) {
		s_sockMutex.lock();
		__plugin.remove(sockId);
		s_sockMutex.unlock();
		delete pOp->sock;
		for (int i = 0; i < pOp->transport.size(); i++) {
			delete pOp->transport[i];
		}
		free(pOp);
	}

	return false;
}

TCPTransport::TCPTransport(Socket *socket) {
	m_buf = new IOBuf(1600 * 512, 4, NULL);
	m_socket = socket;
}

status_t TCPTransport::send(char *data, int *size, int flags) {
	m_socket->send(data, *size, flags);
	return STATUS_SUCCESS;
}

status_t TCPTransport::sendPacket(char *data, int *size, int flags,
		sockaddr *addr, int addrlen) {
	return send(data, size, flags);
}

UDPTransport::UDPTransport(Socket *socket) {
	m_buf = new IOBuf(1600 * 128, 4, NULL);
	m_socket = socket;
}

status_t UDPTransport::sendTo(char *data, int *size, int flags, sockaddr *addr,
		int addrlen) {
	SocketAddr lAddr(addr, addrlen);
	m_socket->sendTo(data, *size, lAddr, flags);
	return STATUS_SUCCESS;
}

status_t UDPTransport::sendPacket(char *data, int *size, int flags,
		sockaddr *addr, int addrlen) {
	return sendTo(data, size, flags, addr, addrlen);
}

Transport::~Transport() {
	delete m_buf;
}

TCPTransport::~TCPTransport() {

}

UDPTransport::~UDPTransport() {

}
