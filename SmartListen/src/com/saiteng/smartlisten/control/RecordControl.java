package com.saiteng.smartlisten.control;

import com.google.gson.Gson;
import com.saiteng.smartlisten.common.MPUPath;
import com.saiteng.smartlisten.service.MPUListenService;
import com.smarteye.adapter.BVPU_AudioRecordParam;
import com.smarteye.coresdk.AudioHelper;

public class RecordControl {
	private MPUListenService mpuListenService=new MPUListenService();
	public void startAudioRecord() {
		BVPU_AudioRecordParam param = new BVPU_AudioRecordParam();
		param.iRecordDuration = 10;
		param.szFilePath = MPUPath.MPU_PATH_AUDIO;
		Gson gson = new Gson();
		String json = gson.toJson(param, BVPU_AudioRecordParam.class);
		AudioHelper.GetAudioHelper().CaptureControl(true);
		mpuListenService.audioRecord(json);
	}

}
