package com.saiteng.smartlisten.process;

import com.smarteye.adapter.BVPU_MediaDir;
import com.smarteye.adapter.BVPU_VideoControl_Encode;
import com.smarteye.bean.JNIMessage;
import com.smarteye.coresdk.AudioHelper;
import com.smarteye.coresdk.BVPU;

public class AVDialogProcess implements Process{
	static boolean  mAudioCapture = false;
	
	public AVDialogProcess(){
		
	}
	
	@Override
	public boolean process(JNIMessage message) {
		if ("invite.av".equals(message
				.getStrParam(JNIMessage.Key.JNIMESSAGE_KEY_S_ID.getName()))) {
			int avDir = message.getIntParam(
					JNIMessage.Key.JNIMESSAGE_KEY_I_MEDIADIR.getName(), 0);
			if (avDir != 0) {
				String avDesc = "";
				if ((avDir & BVPU_MediaDir.BVPU_MEDIADIR_AUDIORECV) == BVPU_MediaDir.BVPU_MEDIADIR_AUDIORECV) {
					avDesc += "音频接收";
				}
				if ((avDir & BVPU_MediaDir.BVPU_MEDIADIR_AUDIOSEND) == BVPU_MediaDir.BVPU_MEDIADIR_AUDIOSEND) {
					avDesc += "音频发送";//
				}
				if ((avDir & BVPU_MediaDir.BVPU_MEDIADIR_VIDEOSEND) == BVPU_MediaDir.BVPU_MEDIADIR_VIDEOSEND) {
					avDesc += "视频发送";
				}
				{
				    
					BVPU_VideoControl_Encode encode=null;//
					openDialog(encode, avDir);
				}
			} else {
				closeDialog();
			}
		}
		return false;
	}

	private void closeDialog() {
		// TODO Auto-generated method stub
		
	}

	private void openDialog(BVPU_VideoControl_Encode encode, int dir) {
		if ((dir & BVPU_MediaDir.BVPU_MEDIADIR_AUDIOSEND) == BVPU_MediaDir.BVPU_MEDIADIR_AUDIOSEND){
			if(!mAudioCapture){
				AudioHelper.GetAudioHelper().CaptureControl(true);
				mAudioCapture = true;
			}
		}
		else{
			if(mAudioCapture){
				AudioHelper.GetAudioHelper().CaptureControl(false);
				mAudioCapture = false;
			}
		}
		JNIMessage message = new JNIMessage();
		message.addStrParam(JNIMessage.Key.JNIMESSAGE_KEY_S_ID.getName(),
				"invite.av");
		message.addIntParam(JNIMessage.Key.JNIMESSAGE_KEY_I_MEDIADIR.getName(),
				dir);
		message.addIntParam(JNIMessage.Key.JNIMESSAGE_KEY_I_TOKEN.getName(),
				0);
		message.setObj(encode);
		BVPU.PostMessageToNative(message);
		
	}

}
