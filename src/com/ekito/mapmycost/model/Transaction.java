package com.ekito.mapmycost.model;

import java.sql.Date;

public class Transaction {
	
	private String title;
	private Date date;
	private Integer amount;
	
	public Transaction(String title, Date date, Integer amount) {
		super();
		this.title = title;
		this.date = date;
		this.amount = amount;
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
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
}
