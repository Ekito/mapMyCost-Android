package com.ekito.mapmycost.tools;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class Formatter {

	public static String formatDate(Date date) {
		SimpleDateFormat mDf = new SimpleDateFormat("dd MMMM yyyy - HH:mm:ss.SSS");
		return mDf.format(date);
	}
}
