package com.ekito.mapmycost.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ekito.mapmycost.R;
import com.ekito.mapmycost.model.Transaction;
import com.ekito.mapmycost.tools.Formatter;

public class EfficientListAdapter extends BaseAdapter {
	
	private static final int TYPE_MATCHED = 0;
    private static final int TYPE_NOT_MATCHED = 1;
	
	private LayoutInflater mInflater;
	private ArrayList<Transaction> mData;

	public EfficientListAdapter(Context context, ArrayList<Transaction> data) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		mData = data;
	}

	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 *
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		return mData.size();
	}

	/**
	 * Since the data comes from an array, just returning the index is
	 * sufficent to get at the data. If we were using a more complex data
	 * structure, we would return whatever object represents one row in the
	 * list.
	 *
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Transaction getItem(int position) {
		return mData.get(position);
	}

	/**
	 * Use the array index as a unique id.
	 *
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Make a view to hold each row.
	 *
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unneccessary calls
		// to findViewById() on each row.
		ViewHolder holder;
		int type = getItemViewType(position);
		
		Transaction transaction = mData.get(position);

		// When convertView is not null, we can reuse it directly, there is no need
		// to reinflate it. We only inflate a new View when the convertView supplied
		// by ListView is null.
		if (convertView == null) {
			
			int layout_id = (type == TYPE_MATCHED)? R.layout.raw : R.layout.highlighted_raw;

			convertView = mInflater.inflate(layout_id, null);

			// Creates a ViewHolder and store references to the two children views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.amount = (TextView) convertView.findViewById(R.id.amount);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.title.setText(transaction.getTitle());
		holder.date.setText(Formatter.formatDate(transaction.getDate()));
		holder.amount.setText(transaction.getAmount().toString()+"Û");
		
		return convertView;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		return mData.get(position).isMapped()? TYPE_MATCHED : TYPE_NOT_MATCHED;
	}
	
	public ArrayList<Transaction> getData() {
		return mData;
	}
	
	public void setData(ArrayList<Transaction> data) {
		mData = data;
	}

	static class ViewHolder {
		TextView title;
		TextView date;
		TextView amount;
	}
}