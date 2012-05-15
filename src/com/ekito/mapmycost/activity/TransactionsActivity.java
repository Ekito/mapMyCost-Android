package com.ekito.mapmycost.activity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockListActivity;
import com.ekito.mapmycost.adapter.EfficientListAdapter;
import com.ekito.mapmycost.model.Transaction;

public class TransactionsActivity extends SherlockListActivity implements OnItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		HashMap<String,Transaction> transactions = new HashMap<String,Transaction>();

		for (int i=0; i<100; i++) {
			transactions.put("id"+i,new Transaction("Starbucks Coffee", new Date(System.currentTimeMillis()), 15f, (i==3)));
		}
		
		ArrayList<Transaction> list = new ArrayList<Transaction>(transactions.values());
		Collections.sort(list, new Comparator<Transaction>() {

	        public int compare(Transaction t1, Transaction t2) {
	            return t1.getMatched()? -1 : ( t2.getMatched()? +1 : t1.getDate().compareTo(t2.getDate()));
	        }
	    });

		setListAdapter(new EfficientListAdapter(this,list));
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, TransactionDetailsActivity.class);
		startActivity(intent);
	}
}
