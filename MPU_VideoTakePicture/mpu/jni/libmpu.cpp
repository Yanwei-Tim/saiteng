#include <jni.h>
#include "libmpu.h"
#include "PUGlobal.h"
#include <android/log.h>
#undef	LOG_TAG
#define LOG_TAG "MPU"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

MPUModule::MPUModule() :
		Module("MPU", "misc") {

}

MPUModule::~MPUModule() {
	Output("Unload module MPU");
}

void MPUModule::initialize() {
	Output("Initializing module MPU");
	Engine::install(new MPUNotifyInviteHandler());
	Engine::install(new AudioRawMountPoint());
	Engine::install(new VideoRawMountPoint());
	Engine::install(new GPSRawMountPoint());
	Engine::install(new MPUDumpHandler());
	Engine::install(new MessageRelay("engine.start", this, Private, 150));
	installRelay(Halt);
	installRelay(Private + 1, "engine.stop");
	Module::initialize();
}

bool MPUModule::received(Message& msg, int id) {
	if (msg == "engine.start") {
		DialogData *pData = g_PUGlobal->getDialog()->getDialogData();
		IOBuf *pAudioRender = new IOBuf(800 * 32, 32, true);
		pData->setIOBuf(DIALOGDATA_LIST_AUDIORENDER, pAudioRender);
	} else if (msg == "engine.stop") {
		g_PUGlobal->getDialog()->getDialogData()->getIOBuf(
				DIALOGDATA_LIST_AUDIORENDER)->wakeup();
		LOGI("stop");
	}
	return Module::received(msg, id);
}

INIT_PLUGIN(MPUModule);

MPUNotifyInviteHandler::MPUNotifyInviteHandler(unsigned int prio) :
		MessageHandler("mpu.notify.invite", prio, __plugin.name()) {

}

bool MPUNotifyInviteHandler::received(Message& msg) {
	int mediaDir = msg.getIntValue("mediadir");
	int status = msg.getIntValue("status");
	int applierId = msg.getIntValue("applierid");
	g_PUGlobal->getDialogNotify()->onMPUDialogMessage(applierId, status,
			mediaDir);

	Message codec("mpu.codec");
	codec.addParam("mediadir", String(mediaDir));
	codec.addParam("status", "start");
	codec.addParam("width", String(g_PUGlobal->getPreviewWidth()));
	codec.addParam("height", String(g_PUGlobal->getPreviewHeight()));
	codec.addParam("buffersize",
			String(g_PUGlobal->getAudioRecordBufferSize()));
	codec.userData(
			(RefObject*) g_PUGlobal->getDialog()->getDialogData()->getIOBuf(
					DIALOGDATA_LIST_AUDIORENDER)->getObject("RefObject"));
	Engine::dispatch(codec);
	return false;
}

VideoRawMountPoint::VideoRawMountPoint(unsigned int prio) :
		MessageHandler("mpu.mount.video.raw", prio, __plugin.name()) {

}

bool VideoRawMountPoint::received(Message& msg) {
	RefObject *obj = (RefObject*) msg.userData();
	Transmit *t = (Transmit*) obj->getObject("Transmit");
	g_PUGlobal->addVideoRawTransmit(t);
	return true;
}

AudioRawMountPoint::AudioRawMountPoint(unsigned int prio) :
		MessageHandler("mpu.mount.audio.raw", prio, __plugin.name()) {

}

bool AudioRawMountPoint::received(Message& msg) {
	RefObject *obj = (RefObject*) msg.userData();
	Transmit *t = (Transmit*) obj->getObject("Transmit");
	g_PUGlobal->addAudioRawTransmit(t);
	return true;
}

GPSRawMountPoint::GPSRawMountPoint(unsigned int prio) :
		MessageHandler("mpu.mount.gps.raw", prio, __plugin.name()) {

}

bool GPSRawMountPoint::received(Message& msg) {
	RefObject *obj = (RefObject*) msg.userData();
	Transmit *t = (Transmit*) obj->getObject("Transmit");
	g_PUGlobal->addGPSRawTransmit(t);
	return true;
}

COptionDesc gOptionDesc[] = { MPU_OPTION(I_RECORD_VIDEOBR),
		MPU_OPTION(I_RECORD_VIDEOFR), MPU_OPTION(I_RECORD_VIDEOII),
		MPU_OPTION(I_RECORD_VIDEOW), MPU_OPTION(I_RECORD_VIDEOH),
		MPU_OPTION(I_RECORD_MEDIA), MPU_OPTION(I_RECORD_VIDEOTS),
		MPU_OPTION(I_RECORD_VIDEOKT), MPU_OPTION(I_RECORD_AUDIOTS),
		MPU_OPTION(I_RECORD_AUDIOKT), MPU_OPTION(I_RECORD_FILESECONDS), MPU_OPTION(I_SDK_MAINVERSION),
		MPU_OPTION(I_SDK_SUBVERSION), MPU_OPTION(S_SDK_BUILDTIME),
		MPU_OPTION(S_SYSTEM_MODEL), MPU_OPTION(I_SYSTEM_APILEVEL),
		MPU_OPTION(S_SYSTEM_MANUFACTURE), MPU_OPTION(S_SYSTEM_VERSION),

		MPU_OPTION(I_USERSTATE_CAMERA_INDEX), MPU_OPTION(I_USERSTATE_APPLIERID),
				MPU_OPTION(I_USERSTATE_MEDIADIR),
		MPU_OPTION(I_USERSTATE_STATUS), MPU_OPTION(I_CODEC_VIDEOW),
				MPU_OPTION(I_CODEC_VIDEOH), MPU_OPTION(I_CODEC_VIDEOBR),
				MPU_OPTION(I_CODEC_VIDEOFR), MPU_OPTION(I_CODEC_VIDEOII),
				MPU_OPTION(I_CODEC_VIDEOTS), MPU_OPTION(I_CODEC_VIDEOKT),
				MPU_OPTION(I_CODEC_AUDIOTS), MPU_OPTION(I_CODEC_AUDIOKT),

		MPU_OPTION_END, };
MPUDumpHandler::MPUDumpHandler(unsigned int prio) :
		MessageHandler("mpu.dump", prio, __plugin.name()) {

}

bool MPUDumpHandler::received(Message& msg) {
	for (int i = 0; i < sizeof(gOptionDesc) / sizeof(COptionDesc); i++) {
		if ((gOptionDesc[i].iOption & 0x80) == 0x80) {
			msg.addParam(gOptionDesc[i].sOption,
					g_PUGlobal->getOptionString(gOptionDesc[i].iOption).c_str());
		} else {
			msg.addParam(gOptionDesc[i].sOption,
					String(g_PUGlobal->getOptionInt(gOptionDesc[i].iOption)));
		}
	}
	return false;
}
