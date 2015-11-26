#ifndef __RENDER_H__
#define __RENDER_H__
#include <yatengine.h>
#include <yatephone.h>
#include <framework.h>
#include <map>
#include <vector>
using namespace std;
using namespace TelEngine;

class AudioDecTransmit: public Transmit {
public:
	virtual void transmit(BigData*);
};

class RenderModule: public Module {
public:
	RenderModule();
	~RenderModule();

	Transmit* getAudioDecTransmit();
	IOBuf* getAudioDecIOBuf();
	void setAudioDecIOBuf(IOBuf* pBuf);
	android::Mutex* getFetchAudioMutex();
protected:
	virtual void initialize();
	virtual bool received(Message& msg, int id);
private:
	AudioDecTransmit m_audioDecTransmit;
	IOBuf *m_audioDecIOBuf;
	android::Mutex m_fetchAudioMutex;
};

class CodecingHandler: public MessageHandler {
public:
	CodecingHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

#endif
