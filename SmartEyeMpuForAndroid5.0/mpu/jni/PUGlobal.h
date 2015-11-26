#ifndef __PU_GLOBAL_H__
#define __PU_GLOBAL_H__
#include "Call.h"
#include <utils/threads.h>
#include <map>
#include <string>
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
#include "LoginNotify.h"
#include "DialogNotify.h"
#include "RegisterInfo.h"
#include "MPUDefine.h"
using namespace std;
using namespace android;
using namespace BVCU::PUConfig;
using namespace SmartEye;
using namespace SmartEye::InviteSDP;

class PUGlobal;
extern PUGlobal* g_PUGlobal;
extern JavaVM *g_jvm;

struct Model {
	char model[64];
	int flags;
};

class LoginCallback: public OnEventCallback {
public:
	LoginCallback();
	void onEvent(int id, int iEventCode, void* pParam);
	void* getParam();
};

class PUGlobal {
public:
	PUGlobal();
	~PUGlobal();

	void restart();
	void login(RegisterInfo* info);

	void setAudioRecordBufferSize(int size);
	int getAudioRecordBufferSize();
	void setPreviewSize(int width, int height);
	int getPreviewWidth();
	int getPreviewHeight();
	char* getVideoBuffer();
	char* getVideoYUV420PBuffer();
	char* getAudioBuffer();

	Call* getDialog();
	void setAndroidModel(char* model);
	LoginNotify* getLoginNotify();
	void setLoginNotify(LoginNotify*);
	void setDialogNotify(DialogNotify*);
	DialogNotify* getDialogNotify();
	Condition* getAudioFetchCond();
	char* getAudioFetchBuffer();
	int getAudioFetchBufferSize();
	void setAudioFetchBufferSize(int size);

	void setOptionInt(int opt, int val);
	int getOptionInt(int opt);

	void setOptionString(int opt, std::string val);
	std::string getOptionString(int opt);

	void addAudioRawTransmit(Transmit*);
	void addVideoRawTransmit(Transmit*);
	void addGPSRawTransmit(Transmit*);
	void inputAudioData(char*, int, int64_t);
	void inputVideoData(char*, int, int64_t);
	void inputGPSData(BVCU_PUCFG_GPSData*);

	void* ConvertYUV420SP_TO_YUV420P(unsigned char* yuv420sp,
			unsigned char* yuv420p, int width, int height, int size);
private:
	static void yuv420sp_to_yuv420p(unsigned char* yuv420sp,
			unsigned char* yuv420p, int width, int height);
	char *m_videoBuffer;
	char *m_videoYUV420PBuffer;
	char *m_audioBuffer;
	int m_width;
	int m_height;
	Call m_dialog;
	Condition m_audioFetchCond;
	char *m_audioFetchBuffer;
	int m_audioFetchBufferSize;
	std::map<std::string, int> m_optValue;
	static const Model s_androidModel[];
	char m_androidModel[64];
	volatile int m_androidFlag;
	int m_audioRecordBufferSize;
	LoginCallback m_loginCallback;
	LoginNotify *m_loginNotify;
	DialogNotify *m_dialogNotify;
	std::vector<Transmit*> m_videoRawTransmit;
	std::vector<Transmit*> m_gpsRawTransmit;
	std::vector<Transmit*> m_audioRawTransmit;
	std::map<int, int> m_optionIntMap;
	std::map<int, std::string> m_optionStrMap;
};

#endif
