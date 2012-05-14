package com.ekito.mapmycost.activity;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListActivity;
import com.ekito.mapmycost.R;

public class CostsActivity extends SherlockListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.costs);
	}
}
