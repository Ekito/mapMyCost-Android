package com.ekito.mapmycost.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.R;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ekito.mapmycost.adapter.EfficientListAdapter;
import com.ekito.mapmycost.communication.RequestHandler;
import com.ekito.mapmycost.model.DataProvider;
import com.ekito.mapmycost.model.Transaction;
import com.ekito.mapmycost.task.GetTransactionsTask;

public class TransactionsActivity extends SherlockListActivity implements OnItemClickListener {

	private static final String TAG = "TransactionsActivity";
	private static final int ACTIVITY_RESULT = 4321;
	private RequestHandler mRequestHandler;
	private EfficientListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRequestHandler = new RequestHandler(this, "Please wait...") {
			@Override
			public void onSuccess(JSONArray array) {

				try {
					if (array != null) {

						HashMap<String, Transaction> data = new HashMap<String, Transaction>();

						JSONObject currentTr;
						for(int i = 0; i<array.length(); i++) {
							currentTr = array.getJSONObject(i);
							data.put(currentTr.getString("id"), 
									new Transaction(
											currentTr.getString("id"),
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
				super.onSuccess(array);
			}
		};

		startTransactionsRequest();

		getListView().setOnItemClickListener(this);
		getListView().setDivider(new ColorDrawable(getResources().getColor(R.color.color_divider)));
		getListView().setDividerHeight(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.transactions, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			startTransactionsRequest();
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
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

				if (t1.isMapped() && t2.isMapped() || !t1.isMapped() && !t2.isMapped()) {
					result = -t1.getDate().compareTo(t2.getDate());
				} else if (t1.isMapped()) {
					result = 1;
				} else if (t2.isMapped()) {
					result = -1;
				}

				return result;
			}
		});

		if (mAdapter != null) {
			mAdapter.setData(list);
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new EfficientListAdapter(this,list);
			setListAdapter(mAdapter);
		}
		
		int nbNotMapped = 0;
		for (Transaction t : mAdapter.getData()) {
			if (t.isMapped()) 	break;
			else				nbNotMapped++;
		}
		Toast.makeText(this, "You have "+nbNotMapped+" transactions to map.", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, TransactionDetailsActivity.class);
		intent.putExtra(TransactionDetailsActivity.EXTRA_ID, mAdapter.getItem(position).getId());
		startActivityForResult(intent,ACTIVITY_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTIVITY_RESULT) {
			startTransactionsRequest();
		}
	}
}
