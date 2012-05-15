package com.ekito.mapmycost.activity;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ekito.mapmycost.R;
import com.ekito.mapmycost.exception.HardwareException;
import com.ekito.mapmycost.task.StoreImageTask;
import com.ekito.mapmycost.view.CameraPreview;

public class CameraActivity extends Activity {

	private static final String TAG = "CameraActivity";
	private Camera camera;
	private CameraPreview cameraPreview;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		try {

			camera = getCameraInstance();
			cameraPreview = new CameraPreview(this, camera);
			final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(cameraPreview);

		} catch (HardwareException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/** A safe way to get an instance of the Camera object. */
	protected Camera getCameraInstance() throws HardwareException {
		if (camera == null) {
			try {
				camera = Camera.open(); // attempt to get a Camera instance

				// get Camera parameters
				final Camera.Parameters params = camera.getParameters();

				final List<String> focusModes = params.getSupportedFocusModes();
				if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
					// Autofocus mode is supported

					// set the focus mode
					params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
					// set Camera parameters
					camera.setParameters(params);
				}

			} catch (final Exception e) {
				// Camera is not available (in use or does not exist)
				Log.e(TAG, "Camera is not available (in use or does not exist)");

				throw new HardwareException(
						"Camera is not available (in use or does not exist)", e);
			}
		}
		return camera; // returns null if camera is unavailable
	}

	public void takePhoto(View target) {
		// autofocus on the picture
		camera.autoFocus(new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(final boolean success,final Camera camera) {
				// then take the picture and save it
				camera.takePicture(null, null,new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(final byte[] data,final Camera camera) {
						new StoreImageTask(CameraActivity.this).execute(data);
					}
				});

			}
		});
	}

	public void shotCallback(String path) {

		float[] output = null;
		try {
			ExifInterface blabla = new ExifInterface(path);
			blabla.getLatLong(output);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int msg_id;
		
		if (path == null) {
			msg_id = R.string.shot_callback_error;
		} else {
			msg_id = R.string.shot_callback_ok;
			
			// store to album
			MediaScannerConnection.scanFile(this,
					new String[] { path }, null,
					new MediaScannerConnection.OnScanCompletedListener() {
				public void onScanCompleted(String path, Uri uri) {
					Log.d(TAG, "scanned : " + path);
				}
			});
			
		}
		Toast.makeText(this, getResources().getString(msg_id), Toast.LENGTH_LONG).show();
		camera.startPreview();
	}
	
	public void goToCosts(View target) {
		Intent intent = new Intent(this, TransactionsActivity.class);
		startActivity(intent);
	}
}