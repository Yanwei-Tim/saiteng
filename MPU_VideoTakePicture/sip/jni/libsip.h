#ifndef __SIP_H__
#define __SIP_H__
#include <yatengine.h>
#include <yatephone.h>
#include <framework.h>
#include <map>
#include <vector>
#include "SEProtocol.h"
#include "BVCU.h"
#include "PUConfig.h"
#include "SmartEye.Register.pb.h"
#include "SmartEye.InviteSDP.pb.h"
#include "SEConst_Internal.h"
#include "PUConfig.h"
#include "PUConfig.pb.h"
#include "NRUConfig.pb.h"
#include "BVEvent.pb.h"
#include "UserConfig.pb.h"
#include "BVCUConfig.pb.h"
#include "UserConfig.h"
#include "NRUConfig.h"
#include "BVEvent.h"
using namespace std;
using namespace TelEngine;
using namespace BVCU::PUConfig;
using namespace SmartEye;
using namespace SmartEye::InviteSDP;

class Call {

};

class MPUSIPFactory {
public:
	MPUSIPFactory();
	~MPUSIPFactory();
	void restart();
	static SEProto_Result sip_send_msg_udp(
			const SEProto_Transaction* pOperation, char* pData, int iDataLen,
			pj_sockaddr_in *rem_addr);

	void setSIPSocketId(int id);
	int getSIPSocketId();
	void setSIPIOBuf(IOBuf*);
	IOBuf* getSIPIOBuf();
	void login();
	char* getChannelName();
	void setChannelName(char* name);
	void setServerAddr(char*);
	char* getServerAddr();
	int getServerPort();
	void setServerPort(int port);
	void setDeviceId(int id);
	int getDeviceId();
	void setDeviceName(char*);
	char* getDeviceName();
	void setLoginCallback(OnEventCallback* cb);
	OnEventCallback* getLoginCallback();
	SEProto_HTransport getTransport();
	void setApplierId(int id);
	int getApplierId();
	void processInvite(sdp_session*);
	void processInviteGPS(sdp_session*);
	int getMediaDir(sdp_session*);
	void sendKeepalive();
	void setSPSAndPPS(char* sps, char* pps);
	void getSPSAndPPS(char* sps, char* pps);
	static void Login_OnEvent(SEProto_Transaction* pOperation,
			SEProto_Result iResult);
	static void Login_OnResponse(SEProto_Transaction* pOperation,
			SEProto_Result iResult, SEProto_Msg* pMsg);
	static void OnAVDialogClose(SEProto_Transaction* pTransaction);
	static void OnGPSDialogClose(SEProto_Transaction* pTransaction);
	void setDialogStatus(int status);
	int getDialogStatus();
	int getCurrentMediaDir();
	void setCurrentMediaDir(int);
	int getGPSMediaDir();
	void setGPSMediaDir(int);
	void getDefaultWGS(int* longitude, int *latitude);
private:
	void initRegisterInfo(BVCU_PUCFG_DeviceInfo* pDeviceInfo,
			BVCU_PUChannelInfo* pChannelInfo);
	int property_get(const char *key, char *value, const char *default_value);
private:
	int m_sipSocketId;
	IOBuf *m_sipIOBuf;
	SEProto_HTransport m_sipTransport;
	BVCU_PUCFG_DeviceInfo m_deviceInfo;
	BVCU_PUChannelInfo m_channelInfo;
	android::Mutex m_sendMsgMutex;
	int m_serverPort;
	char m_serverAddr[64];
	int m_deviceId;
	char m_deviceName[64];
	OnEventCallback *m_loginCallback;
	int m_applierId;
	Condition m_waitSPSAndPPS;
	char m_sps[128];
	char m_pps[128];
	bool m_readlyForSPSAndPPS;
	int m_dialogStatus;
	int m_currentMediaDir;
	int m_gpsMediaDir;
	char m_channelName[128];
};

class SIPThread: public SimpleThread {
public:
	SIPThread();
	virtual ~SIPThread();
	virtual void run();
	virtual void cleanup();
	virtual void exit();
private:
	volatile int m_exit;
};

class SIPModule: public Module, public OnRecvFrom {
public:
	SIPModule();
	~SIPModule();

	MPUSIPFactory* getMPUSIPFactory();

	static void sip_on_recv_command(SEProto_Transaction* pOperation,
			SEProto_Msg* pMsg);
	static void sip_on_recv_invite(SEProto_Transaction* pOperation,
			SEProto_Msg* pMsg);
	static void sip_on_recv_reinvite(SEProto_Transaction* pOperation,
			SEProto_Msg* pMsg);
	void onRecvFrom(char* data, int size, sockaddr *src_addr, int addr_len,
			status_t status);
	virtual void* getObject(const String& name) const;
protected:
	virtual void initialize();
	virtual bool received(Message& msg, int id);
private:
	MPUSIPFactory *m_mpuSIPFactory;
	SIPThread m_sipThread;
};

class MPURegisterHandler: public MessageHandler {
public:
	MPURegisterHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class MPUBroadcastCodecHandler: public MessageHandler {
public:
	MPUBroadcastCodecHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

#endif
