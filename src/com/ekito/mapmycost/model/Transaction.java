package com.ekito.mapmycost.model;

public class Transaction {
	
	private String title;
	private String date;
	private String amount;
	
	public Transaction(String title, String date, String amount) {
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	
}
