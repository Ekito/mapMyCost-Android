package com.ekito.mapmycost.model;

import java.util.HashMap;

public class DataProvider {

	private HashMap<String,Transaction> transactions;
	
	
	private static DataProvider instance = null;
	protected DataProvider() {
		transactions = new HashMap<String, Transaction>();
	}
	public static DataProvider getInstance() {
		if(instance == null) {
			instance = new DataProvider();
		}
		return instance;
	}
	public HashMap<String, Transaction> getTransactions() {
		return transactions;
	}
	public void addTransactions(HashMap<String, Transaction> transactions) {
		this.transactions.putAll(transactions);
	}
	public static void setInstance(DataProvider instance) {
		DataProvider.instance = instance;
	}
}
