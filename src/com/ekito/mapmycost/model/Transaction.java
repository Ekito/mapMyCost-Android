package com.ekito.mapmycost.model;

import java.sql.Date;

public class Transaction {
	
	private String id;
	private String title;
	private Date date;
	private String amount;
	private Boolean mapped;
	private Float longitude;
	private Float latitude;
	private String picture;
	
	public Transaction(String id, String title, Long date, String amount, Boolean mapped) {
		super();
		this.id = id;
		this.title = title;
		this.date = new Date(date);
		this.amount = amount;
		this.mapped = mapped;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Boolean isMapped() {
		return mapped;
	}

	public void isMapped(Boolean mapped) {
		this.mapped = mapped;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
}
