package com.ekito.mapmycost.model;

import java.sql.Date;

public class Transaction {
	
	private String title;
	private Date date;
	private Float amount;
	private Boolean matched;
	
	public Transaction(String title, Date date, Float amount, Boolean matched) {
		super();
		this.title = title;
		this.date = date;
		this.amount = amount;
		this.matched = matched;
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
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Boolean getMatched() {
		return matched;
	}

	public void setMatched(Boolean matched) {
		this.matched = matched;
	}
}
