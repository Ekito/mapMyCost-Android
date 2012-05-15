package com.ekito.mapmycost.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.httpimage.HttpImageManager;
import android.httpimage.HttpImageManager.LoadRequest;
import android.httpimage.HttpImageManager.OnLoadResponseListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.ekito.mapmycost.MMCApplication;
import com.ekito.mapmycost.R;
import com.ekito.mapmycost.communication.RequestHandler;
import com.ekito.mapmycost.communication.RestClient;
import com.ekito.mapmycost.model.DataProvider;
import com.ekito.mapmycost.model.Transaction;
import com.ekito.mapmycost.task.GetTransactionTask;
import com.ekito.mapmycost.task.MappingTask;
import com.ekito.mapmycost.tools.Formatter;
import com.ekito.mapmycost.view.MapView;
import com.ekito.mapmycost.view.Overlay;
import com.ekito.mapmycost.view.TouchableImageButton;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class TransactionDetailsActivity extends SherlockMapActivity implements OnLoadResponseListener {

	private static final String TAG = "TransactionDetailsActivity";
	public static final String EXTRA_ID = "tr_id";
	private static final int GALLERY_ACT_RESULT = 1234;

	private RequestHandler mTransactionRH, mMappingRH;

	private Transaction mTransaction;
	private TextView title, date, amount;
	private ImageView picture;
	private MapView mapView;
	private TouchableImageButton pickImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction);

		String trId = (String) getIntent().getExtras().get(EXTRA_ID);
		mTransaction = DataProvider.getInstance().getTransactions().get(trId);

		title = (TextView) findViewById(R.id.title);
		date = (TextView) findViewById(R.id.date);
		amount = (TextView) findViewById(R.id.amount);
		picture = (ImageView) findViewById(R.id.picture);
		pickImage = (TouchableImageButton) findViewById(R.id.pick_image);
		mapView = (MapView) findViewById(R.id.mapview);

		title.setText(mTransaction.getTitle());
		date.setText(Formatter.formatDate(mTransaction.getDate()));
		amount.setText(mTransaction.getAmount());

		mTransactionRH = new RequestHandler(this, "Please wait...") {
			@Override
			public void onSuccess(JSONObject result) {
				try {
					mTransaction.setLatitude(result.has("latitude")? Float.valueOf(result.getString("latitude")) : 0f);
					mTransaction.setLongitude(result.has("longitude")? Float.valueOf(result.getString("longitude")) : 0f);
					mTransaction.setPicture(result.has("picture")?result.getString("picture") : null);
					updateUI();
				} catch (NumberFormatException e) {
					Log.e(TAG,e.getMessage());
				} catch (JSONException e) {
					Log.e(TAG,e.getMessage());
				}
				super.onSuccess(result);
			}
		};
		mMappingRH = new RequestHandler(this, "Uploading image...") {};
		startTransactionRequest();
	}

	private void startTransactionRequest() {
		new GetTransactionTask(mTransactionRH).execute(mTransaction.getId());
	}
	
	private void startMappingTask(Bitmap b) {
		new MappingTask(
				mTransaction.getId(),
				mTransaction.getLatitude(),
				mTransaction.getLongitude(),
				b,
				mMappingRH
				).execute();	
	}

	private void updateUI() {

		if (mTransaction.getPicture() != null) {
			final Uri uri = Uri.parse(RestClient.BASE_URL + mTransaction.getPicture());
			Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri, picture, this));
			if (bitmap != null) {
				picture.setImageBitmap(bitmap);
				displayImage(true);
			}
		} else {
			displayImage(false);
		}
		
		if (mTransaction.getLatitude() == 0f && mTransaction.getLongitude() == 0f) {
			displayMap(false);
		} else {
			float e6 = 1000000;
			int latitude = (int)(mTransaction.getLatitude()*e6);
			int longitude = (int)(mTransaction.getLongitude()*e6);
			GeoPoint gp = new GeoPoint(latitude,longitude);
			displayMap(true);
			mapView.getController().animateTo(gp);
			mapView.getController().setZoom(15);
			Overlay overlays = new Overlay(getResources().getDrawable(R.drawable.pin));
			overlays.addOverlay(new OverlayItem(gp, "", ""));
			mapView.getOverlays().add(overlays);
		}
	}

	private HttpImageManager getHttpImageManager () {
		return ((MMCApplication) getApplication()).getHttpImageManager();
	}

	@Override
	public void onLoadResponse(LoadRequest r, Bitmap data) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				displayImage(true);
			}
		});
	}

	@Override
	public void onLoadError(LoadRequest r, Throwable e) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				displayImage(false);
			}
		});
	}

	private void displayImage(Boolean flag) {
		if (flag) {
			picture.setVisibility(View.VISIBLE);
			pickImage.setVisibility(View.GONE);			
		} else {
			picture.setVisibility(View.GONE);
			pickImage.setVisibility(View.VISIBLE);			
		}
	}
	
	private void displayMap(Boolean flag) {
		if (flag) {
			mapView.setVisibility(View.VISIBLE);			
		} else {
			mapView.setVisibility(View.GONE);		
		}
	}

	public void pickImageFromAlbum(View target) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),GALLERY_ACT_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == GALLERY_ACT_RESULT && data != null && data.getData() != null){
			Uri _uri = data.getData();

			if (_uri != null) {
				//User had pick an image.
				Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
				cursor.moveToFirst();

				//Link to the image
				final String imageFilePath = cursor.getString(0);
				Log.v("imageFilePath", imageFilePath);
				File photos= new File(imageFilePath);
				Bitmap b = decodeFile(photos);
				b = Bitmap.createScaledBitmap(b,200, 200*b.getHeight()/b.getWidth(), true);
				picture.setImageBitmap(b);
				displayImage(true);
				cursor.close();
				
				startMappingTask(b);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Bitmap decodeFile(File f){
		try {
			//decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f),null,o);

			//Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE=70;
			int width_tmp=o.outWidth, height_tmp=o.outHeight;
			int scale=1;
			while(true){
				if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
					break;
				width_tmp/=2;
				height_tmp/=2;
				scale++;
			}

			//decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
