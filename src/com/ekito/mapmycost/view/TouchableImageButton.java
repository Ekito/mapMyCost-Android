package com.ekito.mapmycost.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * An ImageButton which colors change when touching.
 * 
 * @author ndeverge
 * 
 */
public class TouchableImageButton extends ImageButton {

	public TouchableImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent me) {
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					((ImageButton) v).setColorFilter(Color.argb(150, 155, 155,
					        155)); // apply a grey filter
				} else if (me.getAction() == MotionEvent.ACTION_UP) {
					((ImageButton) v).setColorFilter(Color.argb(0, 155, 155,
					        155)); // back to normal
				}
				return false; // do not consume event
			}
		});
	}

}
