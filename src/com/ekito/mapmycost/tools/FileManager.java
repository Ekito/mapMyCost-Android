package com.ekito.mapmycost.tools;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;

public class FileManager {

	public static String saveImageToAlbum(Context context, final byte[] data) {

		String path = Environment.getExternalStorageDirectory().toString();
		File dir = new File(path, "/MapMyCost/");
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}

		long now = System.currentTimeMillis();
		String fname = "Image-"+ now +".jpg";
		File file = new File (dir, fname);
		if (file.exists ()) {
			file.delete ();
			file = null;
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(data);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return file==null? null : file.getAbsolutePath();
	}
}
