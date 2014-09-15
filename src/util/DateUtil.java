package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static String Getdate() {

		Lunar l = new Lunar(Calendar.getInstance());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy��M��dd�� HH:mm:ss E");
		String aString = dateFormat.format(new Date());
		String[] dateStrings = aString.split(" ");

		String[] dStrings = new String[4];
		dStrings[0] = dateStrings[2];
		dStrings[1] = dateStrings[0];
		dStrings[2] = "����   " + l.get_month() + "��" + l.get_date();
		dStrings[3] = l.cyclical() + "(" + l.animalsYear() + ")" + "��";
		String ss=dStrings[0]+"#"+dStrings[1]+"#"+dStrings[2]+"#"+dStrings[3];
		return ss;
	}
}
