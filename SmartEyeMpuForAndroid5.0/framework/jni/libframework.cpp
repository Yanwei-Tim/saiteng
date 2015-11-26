// libcore.cpp : Defines the exported functions for the DLL application.
//
#include "libframework.h"
#include <android/log.h>
#undef	LOG_TAG
#define LOG_TAG "FRAMEWORK"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

FrameworkModule::FrameworkModule() :
		Module("Framework", "misc") {

}

FrameworkModule::~FrameworkModule() {
	Output("Unload module Framework");
}

void FrameworkModule::initialize() {
	Output("Initializing module Framework");
	Engine::install(new MessageRelay("engine.start", this, Private, 150));
	installRelay(Halt);
	installRelay(Private + 1, "engine.stop");
	Module::initialize();
}

bool FrameworkModule::received(Message &msg, int id) {
	if (msg == "engine.stop") {
		LOGI("stop");
	}
	return Module::received(msg, id);
}

INIT_PLUGIN(FrameworkModule);
