#ifndef __STORAGE_H__
#define __STORAGE_H__
#include <yatengine.h>
#include <yatephone.h>
#include <framework.h>
#include <map>
#include <vector>
extern "C" {
#include <SAVCodec.h>
#include <SAVContainer.h>
#include <libavformat/avformat.h>
}
;
#include <media/stagefright/OMXClient.h>
#include <media/stagefright/OMXCodec.h>
#include <media/stagefright/MediaSource.h>
#include <media/stagefright/MetaData.h>
#include <media/stagefright/DataSource.h>
#include <media/stagefright/MediaBufferGroup.h>
using namespace android;
using namespace std;
using namespace TelEngine;

class StorageFactory;
class StorageThread;

class CodecInfo {
public:
	CodecInfo();
	SAVCodec_Context* getAudioEncCtx(SAVCodec_Context*);
	SAVCodec_Context* getAudioDecCtx(SAVCodec_Context*);
	void closeCtx(SAVCodec_Context* ctx);
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
	int64_t pts;
};

class VideoEncThread: public SimpleThread {
public:
	VideoEncThread(StorageThread* pStorage);
	virtual ~VideoEncThread();
	virtual void run();
	virtual void cleanup();
	virtual void exit();
	void broadcastCodecInfo(char* data, int size);
private:
	StorageThread *m_storageThread;
};

class AudioEncThread: public SimpleThread {
public:
	AudioEncThread(StorageThread* pStorage);
	virtual ~AudioEncThread();
	virtual void run();
	virtual void cleanup();
	virtual void exit();
private:
	SAVCodec_Context m_audioEncCtx;
	StorageThread *m_storageThread;
};

typedef struct _FileParam {
	char filePath[128];
	char fileName[128];
	int fileLen;
} FileParam;

class StorageFile {
public:
	StorageFile(FileParam*);
	void open(int64_t ts);
	int64_t write(SAV_Packet * packet);
	int64_t writeVideo(SAV_Packet *packet);
	int64_t writeAudio(SAV_Packet *packet);
	void close();
	void setExtraData(char* data, int size);
private:
	SAVContainer_Context m_context;
	SAVCodec_Context* m_pStreams[3];
	SAVCodec_Context m_ctxVideoDec;
	SAVCodec_Context m_ctxAudioDec;
	SAV_TYPE_INT8 m_entry_key[12];
	SAV_TYPE_INT8 m_entry_value[24];
	SAVDict_Entry m_entry;
	SAV_TYPE_INT8 m_fileName[255 + 1];
	bool m_open;
	int64_t m_createTime;
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

class StorageThread: public SimpleThread {
public:
	StorageThread();
	~StorageThread();
	virtual void run();
	virtual void cleanup();
	virtual void exit();

	void setAudioStorageIOBuf(IOBuf*);
	void setVideoStorageIOBuf(IOBuf*);
	void setVideoEncIOBuf(IOBuf*);
	void setAudioEncIOBuf(IOBuf*);
	IOBuf* getAudioStorageIOBuf();
	IOBuf* getVideoStorageIOBuf();
	IOBuf* getVideoEncIOBuf();
	IOBuf* getAudioEncIOBuf();
private:
	AudioEncThread *m_audioEncThread;
	VideoEncThread *m_videoEncThread;
	IOBuf *m_audioStorageIOBuf;
	IOBuf *m_videoStorageIOBuf;
	IOBuf *m_videoEncIOBuf;
	IOBuf *m_audioEncIOBuf;
	volatile bool m_exit;
};

class StorageModule: public Module {
public:
	StorageModule();
	~StorageModule();

	CodecInfo* getCodecInfo();
	StorageThread* getStorageThread();
	void setStorageThread(StorageThread*);

	void startupStorageThread();
	void stopStorageThread();

	AudioTransmit* getAudioTransmit();
	VideoTransmit* getVideoTransmit();
	android::Mutex* getStorageThreadMutex();
	char* getFileName();
	void setFileName(char*);
	char* getFilePath();
	void setFilePath(char*);
	int getFileLenInSeconds();
	void setFileLenInSeconds(int);
protected:
	virtual void initialize();
	virtual bool received(Message& msg, int id);
private:
	StorageThread *m_storageThread;
	CodecInfo m_codecInfo;
	AudioTransmit m_audioTransmit;
	VideoTransmit m_videoTransmit;
	android::Mutex m_storageThreadMutex;
	char m_fileName[255 + 1];
	char m_filePath[255 + 1];
	int m_fileLenInSeconds;
};

class StoragingHandler: public MessageHandler {
public:
	StoragingHandler(unsigned int prio = 100);
	virtual bool received(Message& msg);
};

#endif
