package com.smarteye.mpu.bean;

public class StorageInfo {
	private String fileName;
	private String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private int mediaType;
	private String status;

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private int fileLenInSeconds;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileLenInSeconds() {
		return fileLenInSeconds;
	}

	public void setFileLenInSeconds(int fileLenInSeconds) {
		this.fileLenInSeconds = fileLenInSeconds;
	}
}
