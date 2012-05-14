package com.ekito.mapmycost.activity;

import java.util.ArrayList;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListActivity;
import com.ekito.mapmycost.adapter.EfficientListAdapter;
import com.ekito.mapmycost.model.Transaction;

public class CostsActivity extends SherlockListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ArrayList<Transaction> list = new ArrayList<Transaction>();
		list.add(new Transaction("Starbucks Coffee", "14/05/2012", "15$"));
		
		setListAdapter(new EfficientListAdapter(this,list));
	}
}
