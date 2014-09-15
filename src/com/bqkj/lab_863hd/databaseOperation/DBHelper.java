package com.bqkj.lab_863hd.databaseOperation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lab_863hd.db";  
	private static final int DATABASE_VERSION = 1;  
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL("CREATE TABLE IF NOT EXISTS Device("
//				+ "_id INTEGER  PRIMARY KEY AUTOINCREMENT,"
//				+ "_value double ,"
//				+ "_time tIME DEFAULT CURRENT_TIME " + ")");
		db.execSQL("CREATE TABLE IF NOT EXISTS TempTB2("
				+ "_id INTEGER  PRIMARY KEY AUTOINCREMENT,"
				+ "_value1 double ,"
				+ "_value2 double ,"
				+ "_time tIME DEFAULT CURRENT_TIME " + ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE TempTB2 ADD COLUMN other STRING");  
	}

}
