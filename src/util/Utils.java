package util;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Utils {

	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.density;
		} catch (Exception ex) {

		}
		return 1.0f;
	}
	
	public static void alertDialog(Context context,String str){
		new  AlertDialog.Builder(context)   
		                .setTitle("¾¯¸æ" )  
		              .setMessage(str )  
		                .setPositiveButton("ÊÇ" ,  null )
		                .setNegativeButton("·ñ", null)
		                .show();  
	
	}
	
	public static boolean isServiceRun(Context context,String service_Name){
		ActivityManager activityManager=(ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list=activityManager.getRunningServices(30);
		for(RunningServiceInfo info:list){
			if (info.service.getClassName().equals(service_Name)) {
				return true;
			}
		}
		return false;
	}
}