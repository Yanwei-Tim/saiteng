package com.smarteye.mpu.bean;

public class WallTime {
	private short year;
	private char month;
	private char day;
	private char hour;
	private char minute;

	public short getYear() {
		return year;
	}

	public void setYear(short year) {
		this.year = year;
	}

	public char getMonth() {
		return month;
	}

	public void setMonth(char month) {
		this.month = month;
	}

	public char getDay() {
		return day;
	}

	public void setDay(char day) {
		this.day = day;
	}

	public char getHour() {
		return hour;
	}

	public void setHour(char hour) {
		this.hour = hour;
	}

	public char getMinute() {
		return minute;
	}

	public void setMinute(char minute) {
		this.minute = minute;
	}

	public char getSecond() {
		return second;
	}

	public void setSecond(char second) {
		this.second = second;
	}

	private char second;
}
