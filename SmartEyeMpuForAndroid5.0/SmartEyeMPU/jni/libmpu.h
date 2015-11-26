#ifndef __CODEC_H__
#define __CODEC_H__
#include <yatengine.h>
#include <yatephone.h>
#include <framework.h>
#include <map>
#include <vector>
using namespace std;
using namespace TelEngine;

class MPUModule: public Module {
public:
	MPUModule();
	~MPUModule();
protected:
	virtual void initialize();
	virtual bool received(Message& msg, int id);
};

class MPUNotifyInviteHandler: public MessageHandler {
public:
	MPUNotifyInviteHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class VideoRawMountPoint: public MessageHandler {
public:
	VideoRawMountPoint(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class AudioRawMountPoint: public MessageHandler {
public:
	AudioRawMountPoint(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class GPSRawMountPoint: public MessageHandler {
public:
	GPSRawMountPoint(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class MPUDumpHandler: public MessageHandler {
public:
	MPUDumpHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

#endif
