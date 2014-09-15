package com.bqkj.lab_863hd.databaseOperation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class DBManager {

	private DBHelper helper;
	private SQLiteDatabase db;

	Cursor cursor;

	// 查询出时间，及数据数组
//	public List<String> Xdate = new ArrayList<String>();
	private List<Double> Yva1 = new ArrayList<Double>();
	private String historyRec = ""; //
	private String startdate;

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	/**
	 * insert table
	 * 
	 * @param TempTB1Beans
	 */
	public void addList(List<TempTB2Bean> tempTB2Beans) {
		db.beginTransaction();// 开始事务
		try {
			for (TempTB2Bean tempTB2Bean : tempTB2Beans) {
				db.execSQL("INSERT INTO TempTB2 VALUES(?,?,?,?)",
						new Object[] { tempTB2Bean._deviceid, tempTB2Bean._value1,tempTB2Bean._value2,tempTB2Bean._time });
			}
			db.setTransactionSuccessful();// 设置事务成功完成
		} catch (Exception e) {
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * insert table
	 * 
	 * @param TempTB1Beans
	 */
	public void addOne(TempTB2Bean tempTB2Bean) {
		db.beginTransaction();// 开始事务
		try {
			db.execSQL("INSERT INTO TempTB2 VALUES(?,?,?,?)", new Object[] {
					tempTB2Bean._deviceid, tempTB2Bean._value1,
					tempTB2Bean._value2, tempTB2Bean._time });
			db.setTransactionSuccessful();// 设置事务成功完成
		} catch (Exception e) {
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * query all persons, return list
	 * 
	 * @return List<Person>
	 */
	// public List<TempTB1Bean> query(String startdate,String enddate) {
	// ArrayList<TempTB1Bean> tempTB1Beans = new ArrayList<TempTB1Bean>();
	// //Cursor c = queryTheCursor();
	// while (c.moveToNext()) {
	// TempTB1Bean tempTB1Bean = new TempTB1Bean();
	// tempTB1Bean._id = c.getInt(c.getColumnIndex("_id"));
	// tempTB1Bean.name = c.getString(c.getColumnIndex("name"));
	// tempTB1Bean.NewVal1 = c.getInt(c.getColumnIndex("NewVal1"));
	// tempTB1Bean.NewVal2 = c.getInt(c.getColumnIndex("NewVal2"));
	// tempTB1Bean.NewVal3 = c.getInt(c.getColumnIndex("NewVal3"));
	// tempTB1Bean.NewVal4 = c.getInt(c.getColumnIndex("NewVal4"));
	// tempTB1Bean.NewVal5 = c.getInt(c.getColumnIndex("NewVal5"));
	// tempTB1Bean.NewVal6 = c.getInt(c.getColumnIndex("NewVal6"));
	// tempTB1Bean.opDate = c.getString(c.getColumnIndex("opDate"));
	// tempTB1Bean.opTime = c.getString(c.getColumnIndex("opTime"));
	//
	// // String[]
	// dateStrings=c.getString(c.getColumnIndex("opDate")).split("-");
	// // int year=Integer.parseInt(dateStrings[0]);
	// // int month=Integer.parseInt(dateStrings[1]);
	// // int day=Integer.parseInt(dateStrings[2]);
	// //
	// // String[] dateStrings1=startdate.split("-");
	// // int syear=Integer.parseInt(dateStrings1[0]);
	// // int smonth=Integer.parseInt(dateStrings1[1]);
	// // int sday=Integer.parseInt(dateStrings1[2]);
	// //
	// // String[] dateStrings2=enddate.split("-");
	// // int eyear=Integer.parseInt(dateStrings2[0]);
	// // int emonth=Integer.parseInt(dateStrings2[1]);
	// // int eday=Integer.parseInt(dateStrings2[2]);
	// Date sdate=null;
	// Date ndate=null;
	// Date edate=null;
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	// try {
	// sdate = sdf.parse(startdate);
	// ndate = sdf.parse(c.getString(c.getColumnIndex("opDate")));
	// edate = sdf.parse(enddate);
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// if (ndate.after(sdate) && ndate.before(edate)) {
	// tempTB1Beans.add(tempTB1Bean);
	// }
	// }
	// c.close();
	// return tempTB1Beans;
	// }

	/**
	 * query history, return cursor
	 * 
	 * @return String
	 * @throws ParseException
	 */
	// public String queryTheCursor(String startdate, String enddate) {
	// String historyRec =""; //
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// Date sdate;
	// Date edate;
	// TempTB2Bean tb2Bean=new TempTB2Bean();
	// try {
	// sdate = sdf.parse(startdate+" 00:00:00");
	// edate = sdf.parse(enddate+" 23:59:59");
	// cursor=db.rawQuery("SELECT * FROM TempTB2 WHERE opDate >= ? AND opDate <=?",
	// new String[]{startdate,enddate} );
	// //cursor = db.query("TempTB2",new String[]{"*"},
	// "opDate "+"Between ? And ?", new String[]{startdate,enddate}, null, null,
	// null);
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// // 判断游标是否为空
	// if (cursor.moveToFirst()) {
	// // 遍历游标
	// while (!cursor.isAfterLast()) {
	// historyRec += "详细日期：" + cursor.getString(3) + " "
	// + cursor.getString(4) + "\r\n" + "实时值："
	// + cursor.getDouble(1) + "\r\n" + "峰值："
	// +cursor.getDouble(2)+ "\r\n" + "\r\n";
	// //
	// Xdate.add(cursor.getString(3) + " "+ cursor.getString(4));
	// Yva1.add(cursor.getDouble(1));
	// Yva2.add(cursor.getDouble(2));
	// }
	// cursor.moveToNext();
	// }
	// //String mString=historyRec;
	// return historyRec;
	// }

	public double[][]  queryTheCursor(String startdate, String enddate) {
		double[][] result=new double[][]{}; 
		db.beginTransaction();// 开始事务
		try {
			try {
				cursor = db.rawQuery(
						"SELECT * FROM TempTB2 WHERE _time >= ? AND _time <=?",
						new String[] { startdate, enddate });
			} catch (Exception e) {
				historyRec = "";
			}

			int count = cursor.getCount();
			if (count > 0) {
				for (int i = 0; i < count; i++) {

					cursor.moveToNext();
					historyRec += "详细日期：" + cursor.getString(2) + "\r\n" + "值："
							+ cursor.getDouble(1) + "\r\n";
					//Yva1.add(cursor.getDouble(1));
					result[0][i]=cursor.getDouble(1);
					result[1][i]=cursor.getDouble(2);
					// cursor.moveToNext();
				}
			}
			db.setTransactionSuccessful();// 设置事务成功完成
		} catch (Exception e) {
		} finally {
			db.endTransaction();
		}

		return result;
	}

	public void deleteAll() {
		db.delete("TempTB2", null, null);
	}

	public List<Double> getYva1() {
		return Yva1;
	}

	public String getHistoryRec() {
		return historyRec;
	}

	public void closeDB() {
		// cursor.close();
		db.close();
	}

}
