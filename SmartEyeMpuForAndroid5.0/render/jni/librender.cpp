#include <jni.h>
#include "librender.h"
#include <android/log.h>
#undef	LOG_TAG
#define LOG_TAG "RENDER"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

RenderModule::RenderModule() :
		Module("Render", "misc") {
	m_audioDecIOBuf = NULL;
}

RenderModule::~RenderModule() {
	Output("Unload module Render");
}

Transmit* RenderModule::getAudioDecTransmit() {
	return &m_audioDecTransmit;
}

IOBuf* RenderModule::getAudioDecIOBuf() {
	return m_audioDecIOBuf;
}

void RenderModule::setAudioDecIOBuf(IOBuf* pBuf) {
	m_audioDecIOBuf = pBuf;
}

android::Mutex* RenderModule::getFetchAudioMutex() {
	return &m_fetchAudioMutex;
}

void RenderModule::initialize() {
	Output("Initializing module Render");
	Engine::install(new MessageRelay("engine.start", this, Private, 150));
	Engine::install(new CodecingHandler(100));
	installRelay(Halt);
	installRelay(Private + 1, "engine.stop");
	Module::initialize();
}

INIT_PLUGIN(RenderModule);

bool RenderModule::received(Message& msg, int id) {
	if (msg == "engine.start") {
		Message msg0("mpu.mount.audio.dec");
		msg0.userData(
				(RefObject*) __plugin.getAudioDecTransmit()->getObject(
						"RefObject"));
		Engine::dispatch(msg0);
	} else if (msg == "engine.stop") {
		android::Mutex *mutex = __plugin.getFetchAudioMutex();
		mutex->lock();
		if (m_audioDecIOBuf) {
			IOBuf::IOUnit *pUnit = m_audioDecIOBuf->produceBegin(1);
			if (pUnit) {
				memset(&pUnit->data.frame, 0, sizeof(SAV_Frame));
				pUnit->data.frame.iDataSize[0] = 0;
				m_audioDecIOBuf->produceEnd(pUnit);
			}
		}
		mutex->unlock();
		LOGI("stop");
	}
	return Module::received(msg, id);
}

void AudioDecTransmit::transmit(BigData *pData) {
	IOBuf *pAudioDecIOBuf = __plugin.getAudioDecIOBuf();
	if (pAudioDecIOBuf) {
		android::Mutex *mutex = __plugin.getFetchAudioMutex();
		mutex->lock();
		IOBuf::IOUnit *pUnit = pAudioDecIOBuf->produceBegin(
				pData->frame.iDataSize[0]);
		if (pUnit) {
			pUnit->data.frame.iDataSize[0] = pData->frame.iDataSize[0];
			pUnit->data.frame.iPTS = pData->frame.iPTS;
			pUnit->data.frame.ppData[0] = (SAV_TYPE_UINT8*) pUnit->pBuf;
			memcpy(pUnit->data.frame.ppData[0], pData->frame.ppData[0],
					pUnit->data.frame.iDataSize[0]);
			pAudioDecIOBuf->produceEnd(pUnit);
		}
		mutex->unlock();
	}
}

CodecingHandler::CodecingHandler(unsigned int prio) :
		MessageHandler("mpu.codec", prio, __plugin.name()) {

}

bool CodecingHandler::received(Message& msg) {
	RefObject *obj = msg.userData();
	__plugin.setAudioDecIOBuf((IOBuf*) obj->getObject("IOBuf"));
	return false;
}
