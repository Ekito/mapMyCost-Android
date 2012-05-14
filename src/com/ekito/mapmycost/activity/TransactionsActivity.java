package com.ekito.mapmycost.activity;

import java.sql.Date;
import java.util.ArrayList;

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

		ArrayList<Transaction> list = new ArrayList<Transaction>();

		for (int i=0; i<100; i++) {
			list.add(new Transaction("Starbucks Coffee", new Date(System.currentTimeMillis()), 15));
		}

		setListAdapter(new EfficientListAdapter(this,list));
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, TransactionDetailsActivity.class);
		startActivity(intent);
	}
}
