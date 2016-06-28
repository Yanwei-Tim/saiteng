#ifndef __CODEC_H__
#define __CODEC_H__
extern "C" {
#include <SAVCodec.h>
}
;
#include <yatengine.h>
#include <yatephone.h>
#include <framework.h>
#include <map>
#include <vector>
#include <media/stagefright/OMXClient.h>
#include <media/stagefright/OMXCodec.h>
#include <media/stagefright/MediaSource.h>
#include <media/stagefright/MetaData.h>
#include <media/stagefright/DataSource.h>
#include <media/stagefright/MediaBufferGroup.h>
using namespace std;
using namespace TelEngine;
using namespace android;

class CodecFactory;

class CodecInfo {
public:
	CodecInfo();
	SAVCodec_Context* getAudioEncCtx(SAVCodec_Context*);
	SAVCodec_Context* getAudioDecCtx(SAVCodec_Context*);
	void closeCtx(SAVCodec_Context* ctx);
};

class VideoEncThread: public SimpleThread {
public:
	VideoEncThread(CodecFactory* pCodec);
	virtual ~VideoEncThread();
	virtual void run();
	virtual void cleanup();
	virtual void exit();
	void broadcastCodecInfo(char* data, int size);
private:
	CodecFactory *m_codecFactory;
	IOBuf* m_videoEncIOBuf;
};

class AudioEncThread: public SimpleThread {
public:
	AudioEncThread(CodecFactory* pCodec);
	virtual ~AudioEncThread();
	virtual void run();
	virtual void cleanup();
	virtual void exit();
private:
	SAVCodec_Context m_audioEncCtx;
	CodecFactory *m_codecFactory;
	IOBuf *m_audioEncIOBuf;
};

class AudioDecThread: public SimpleThread {
public:
	AudioDecThread(CodecFactory* pCodec);
	virtual ~AudioDecThread();
	virtual void run();
	virtual void cleanup();
	virtual void exit();
private:
	SAVCodec_Context m_audioDecCtx;
	CodecFactory *m_codecFactory;
	IOBuf *m_audioDecIOBuf;
};

class VideoTransmit: public Transmit {
public:
	VideoTransmit();
	virtual void transmit(BigData*);
};

class AudioTransmit: public Transmit {
public:
	AudioTransmit();
	virtual void transmit(BigData*);
};

class AudioPacketTransmit: public Transmit {
public:
	AudioPacketTransmit();
	virtual void transmit(BigData*);
};

class CodecFactory {
public:
	CodecFactory();
	~CodecFactory();

	void setAudioEncIOBuf(IOBuf*);
	void setVideoEncIOBuf(IOBuf*);
	void setAudioDecIOBuf(IOBuf*);
	IOBuf* getAudioEncIOBuf();
	IOBuf* getVideoEncIOBuf();
	IOBuf* getAudioDecIOBuf();
	void setPreviewSize(int w, int h);
	int getPreviewWidth();
	int getPreviewHeight();
	void videoEncTransmit(BigData *);
	void audioEncTransmit(BigData *);
	void audioDecTransmit(BigData *);
	void addVideoEncTransmit(Transmit*);
	void addAudioEncTransmit(Transmit*);
	void addAudioDecTransmit(Transmit*);
	Transmit* getAudioRawTransmit();
	Transmit* getVideoRawTransmit();
	Transmit* getAudioPacketTransmit();
	void setCurrentMediaDir(int dir);
	int getCurrentMediaDir();

	int CodecFactory::getFrameCount();
	void CodecFactory::resetFrameCount();
	void CodecFactory::increaseFrameCount(int iCount);

	int CodecFactory::getUploadCount();
	void CodecFactory::resetUploadCount();
	void CodecFactory::increaseUploadCount(int iCount);

	android::Mutex* getAudioEncMutex();
	android::Mutex* getVideoEncMutex();
	android::Mutex* getAudioDecMutex();
private:
	IOBuf *m_audioEncIOBuf;
	IOBuf *m_videoEncIOBuf;
	IOBuf *m_audioDecIOBuf;
	std::vector<Transmit*> m_videoEncTransmit;
	std::vector<Transmit*> m_audioEncTransmit;
	std::vector<Transmit*> m_audioDecTransmit;
	VideoTransmit m_videoRawTransmit;
	AudioTransmit m_audioRawTransmit;
	AudioPacketTransmit m_audioPacketTransmit;
	int m_currentMediaDir;
	android::Mutex m_audioEncMutex;
	android::Mutex m_videoEncMutex;
	android::Mutex m_audioDecMutex;
	int m_width;
	int m_height;

	int m_totalframecount;
	int m_totaluploadcount;
};

class CodecModule: public Module {
public:
	CodecModule();
	~CodecModule();

	CodecInfo* getCodecInfo();
	CodecFactory* getCodecFactory();

	void startupVideoEncThread();
	void stopVideoEncThread();
	void startupAudioEncThread();
	void stopAudioEncThread();
	void startupAudioDecThread();
	void stopAudioDecThread();

protected:
	virtual void initialize();
	virtual bool received(Message& msg, int id);
private:
	CodecInfo m_codecInfo;
	CodecFactory m_codecFactory;
	VideoEncThread *m_videoEncThread;
	AudioEncThread *m_audioEncThread;
	AudioDecThread *m_audioDecThread;
};

class CodecingHandler: public MessageHandler {
public:
	CodecingHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

struct H264MediaSource: public MediaSource {
	H264MediaSource(int width, int height, IOBuf *videoEncIOBuf);
	virtual status_t start(MetaData *params = NULL);
	virtual status_t stop();
	virtual sp<MetaData> getFormat();

	virtual status_t read(MediaBuffer **out, const ReadOptions *options = NULL);
	virtual ~H264MediaSource();

	void setFrameRate(int fr);
	void setBitRate(int br);
	void setIFramesInterval(int ifi);
private:
	sp<MetaData> meta;
	MediaBufferGroup *mGroup;
	MediaBuffer* m_mediaBuffer;
	int m_width;
	int m_height;
	int m_frameRate;
	int m_bitRate;
	int m_IFramesInterval;
	IOBuf *m_videoEncIOBuf;
	int m_keyTime;
};

class VideoEncMountPoint: public MessageHandler {
public:
	VideoEncMountPoint(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class AudioEncMountPoint: public MessageHandler {
public:
	AudioEncMountPoint(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

class AudioDecMountPoint: public MessageHandler {
public:
	AudioDecMountPoint(unsigned int prio = 100);
	virtual bool received(Message& msg);
};
class FrameCountMountPoint: public MessageHandler {
public:
	FrameCountMountPoint(unsigned int prio = 100);
	virtual bool received(Message& msg);
};
class UploadCountMountPoint: public MessageHandler {
public:
	UploadCountMountPoint(unsigned int prio = 100);
	virtual bool received(Message& msg);
};
#endif
