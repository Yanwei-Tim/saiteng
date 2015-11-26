#ifndef __RTP_H__
#define __RTP_H__
#include <yatengine.h>
#include <yatephone.h>
#include <framework.h>
#include <map>
#include <vector>
using namespace std;
using namespace TelEngine;

typedef struct _StreamIdMap {
	int recv;
	int send;
} StreamIdMap;

enum {
	STREAM_DIR_VIDEO_SEND,
	STREAM_DIR_AUDIO_SEND,
	STREAM_DIR_AUDIO_RECV,
	STREAM_DIR_GPS_SEND,
	STREAM_COUNT,
};

class VideoTransmit: public Transmit {
public:
	virtual void transmit(BigData*);
};

class AudioTransmit: public Transmit {
public:
	virtual void transmit(BigData*);
};

class GPSTransmit: public Transmit {
public:
	virtual void transmit(BigData*);
};

class SendThread: public SimpleThread {
public:
	SendThread();
	virtual void run();
	virtual void cleanup();
	virtual void exit();
	void sendVideo(char* payload, int len, int ts, int m, int64_t pts);
	void sendRTCP();
private:
	volatile int m_exit;
	volatile int m_videoTS;
	volatile int m_audioTS;
	volatile int64_t m_videoNTP;
	volatile int64_t m_audioNTP;
};

typedef struct _AudioBuffer {
	int seq;
	int length;
	char buffer[128];
} AudioBuffer;

typedef struct _VideoBuffer {
	int seq;
	int length;
	char buffer[1024];
} VideoBuffer;

class RTPFactory: public OnRecvFrom {
public:
	RTPFactory();
	~RTPFactory();
	virtual void onRecvFrom(char* data, int size, sockaddr *src_addr,
			int addr_len, status_t status);
	void inputAudioData(char* data, int size);
	VideoTransmit* getVideoTransmit();
	AudioTransmit* getAudioTransmit();
	GPSTransmit* getGPSTransmit();
	void setRTPIOBuf(IOBuf* pIOBuf);
	IOBuf* getRTPIOBuf();
	void setRTPSocketId(int id);
	int getRTPSocketId();
	int getRTPDataPort();
	void setRTPDataPort(int port);
	char* getServerAddr();
	void setServerAddr(char* addr);
	StreamIdMap* getStreamIdMap(int index);
	void addAudioPacketTransmit(Transmit*);
	IOBuf* getVideoSendIOBuf();
	IOBuf* getAudioSendIOBuf();
	IOBuf* getGPSSendIOBuf();
	void setVideoSendIOBuf(IOBuf*);
	void setAudioSendIOBuf(IOBuf*);
	void setGPSSendIOBuf(IOBuf*);
	int getCurrentMediaDir();
	void setCurrentMediaDir(int);
	android::Mutex* getVideoSendIOBufMutex();
	android::Mutex* getAudioSendIOBufMutex();
	android::Mutex* getGPSSendIOBufMutex();
	void sendVideo(int seq);
	void sendAudio(int seq);
	void copyVideo(char* data, int length, int seq);
	void copyAudio(char* data, int length, int seq);
private:
	VideoTransmit m_videoTransmit;
	AudioTransmit m_audioTransmit;
	GPSTransmit m_gpsTransmit;
	IOBuf* m_rtpIOBuf;
	int m_rtpSocketId;
	int m_rtpDataPort;
	char m_serverAddr[64];
	StreamIdMap m_streamIdMap[STREAM_COUNT];
	std::vector<Transmit*> m_audioPacketTransmit;
	IOBuf* m_videoSendIOBuf;
	IOBuf* m_audioSendIOBuf;
	IOBuf* m_gpsSendIOBuf;
	int m_currentMediaDir;
	android::Mutex m_videoSendIOBufMutex;
	android::Mutex m_audioSendIOBufMutex;
	android::Mutex m_gpsSendIOBufMutex;
	AudioBuffer m_audioBuffer[256];
	VideoBuffer m_videoBuffer[512];
};

class RTPModule: public Module {
public:
	RTPModule();
	~RTPModule();
	RTPFactory* getRTPFactory();
	void startupSendThread();
	void stopSendThread();
protected:
	virtual void initialize();
	virtual bool received(Message& msg, int id);
private:
	RTPFactory m_rtpFactory;
	SendThread *m_sendThread;
};

class MPURegisterHandler: public MessageHandler {
public:
	MPURegisterHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class MPUBroadcastInviteHandler: public MessageHandler {
public:
	MPUBroadcastInviteHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class MPUGetRTPHandler: public MessageHandler {
public:
	MPUGetRTPHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class AudioPacketMountPoint: public MessageHandler {
public:
	AudioPacketMountPoint(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class MPUNotifyInviteHandler: public MessageHandler {
public:
	MPUNotifyInviteHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

#endif
