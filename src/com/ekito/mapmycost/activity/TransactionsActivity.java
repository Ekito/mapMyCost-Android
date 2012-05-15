package com.ekito.mapmycost.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockListActivity;
import com.ekito.mapmycost.adapter.EfficientListAdapter;
import com.ekito.mapmycost.communication.RequestHandler;
import com.ekito.mapmycost.model.DataProvider;
import com.ekito.mapmycost.model.Transaction;
import com.ekito.mapmycost.task.GetTransactionsTask;

public class TransactionsActivity extends SherlockListActivity implements OnItemClickListener {

	private static final String TAG = "TransactionsActivity";
	private RequestHandler mRequestHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRequestHandler = new RequestHandler(this, "Please wait...") {
			@Override
			public void onSuccess(JSONArray array) {
				super.onSuccess(array);

				try {
					if (array != null) {

						HashMap<String, Transaction> data = new HashMap<String, Transaction>();

						JSONObject currentTr;
						for(int i = 0; i<array.length(); i++) {
							currentTr = array.getJSONObject(i);
							data.put(currentTr.getString("id"), 
									new Transaction(
											currentTr.getString("title"), 
											currentTr.getLong("date"), 
											currentTr.getString("amount"), 
											currentTr.getBoolean("mapped")));
						}

						DataProvider.getInstance().setTransactions(data);

						updateList();
					}
				} catch(JSONException e) {
					Log.e(TAG,e.getMessage());
				}
			}
		};

		startTransactionsRequest();

		getListView().setOnItemClickListener(this);
	}

	private void startTransactionsRequest() {
		new GetTransactionsTask(mRequestHandler).execute();
	}

	private void updateList() {
		HashMap<String,Transaction> transactions = DataProvider.getInstance().getTransactions();

		ArrayList<Transaction> list = new ArrayList<Transaction>(transactions.values());
		Collections.sort(list, new Comparator<Transaction>() {

			public int compare(Transaction t1, Transaction t2) {

				int result = 0;

				if (t1.getMatched() && t2.getMatched() || !t1.getMatched() && !t2.getMatched()) {
					result = t1.getDate().compareTo(t2.getDate());
				} else if (t1.getMatched()) {
					result = -1;
				} else if (t2.getMatched()) {
					result = 1;
				}

				return result;
			}
		});

		setListAdapter(new EfficientListAdapter(this,list));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, TransactionDetailsActivity.class);
		startActivity(intent);
	}
}
