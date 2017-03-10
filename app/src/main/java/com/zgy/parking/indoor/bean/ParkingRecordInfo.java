package com.zgy.parking.indoor.bean;


import org.litepal.crud.DataSupport;


public class ParkingRecordInfo extends DataSupport {

	private int id;
	/**
	 * 停车场名字
	 */
	private String parkingName;

	/**
	 * 停车场id
	 */
	private String parkingCode;

	/**
	 * 楼层1
	 */
	private String floor1;

	/**
	 * 楼层2
	 */
	private String floor2;

	/**
	 * 所在的停车位1
	 */
	private String parkingNum1;

	/**
	 * 所在的停车位2
	 */
	private String parkingNum2;

	public String getParkingNum2() {
		return parkingNum2;
	}

	public void setParkingNum2(String parkingNum2) {
		this.parkingNum2 = parkingNum2;
	}

	public String getParkingNum1() {
		return parkingNum1;
	}

	public void setParkingNum1(String parkingNum1) {
		this.parkingNum1 = parkingNum1;
	}

	public String getParkingName() {
		return parkingName;
	}

	public void setParkingName(String parkingName) {
		this.parkingName = parkingName;
	}

	public String getFloor1() {
		return floor1;
	}

	public void setFloor1(String floor1) {
		this.floor1 = floor1;
	}

	public String getFloor2() {
		return floor2;
	}

	public void setFloor2(String floor2) {
		this.floor2 = floor2;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getParkingCode() {
		return parkingCode;
	}

	public void setParkingCode(String parkingCode) {
		this.parkingCode = parkingCode;
	}
}
