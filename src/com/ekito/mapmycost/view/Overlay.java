package com.ekito.mapmycost.view;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class Overlay extends ItemizedOverlay<OverlayItem> {

	ArrayList<OverlayItem> mOverlays;
	
	public Overlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		mOverlays = new ArrayList<OverlayItem>();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return mOverlays.get(arg0);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
