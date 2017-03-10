package com.zgy.parking.indoor.bean;


import org.litepal.crud.DataSupport;


public class PhotoImageInfo extends DataSupport {

	private int id;
	/**
	 * 
	 */
	private String cover;

	private String time;

	private String location;

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
