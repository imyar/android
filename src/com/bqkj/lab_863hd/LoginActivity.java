package com.bqkj.lab_863hd;

import roboguice.RoboGuice.util;
import util.WifiAutoConnectManager;
import util.WifiAutoConnectManager.WifiCipherType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		findViewById(R.id.log_button1).setOnClickListener(this);
		findViewById(R.id.log_button2).setOnClickListener(this);
//		WifiManager wifiManager=(WifiManager) getSystemService(Context.WIFI_SERVICE);
//		WifiAutoConnectManager wacManager =new WifiAutoConnectManager(wifiManager);
//		wacManager.connect("wang", "1234567890", WifiCipherType.WIFICIPHER_WPA);
	}

	@Override
	public void onClick(View v) {
		Intent i1 = new Intent();
		i1.setClass(this, MainActivity.class);
		switch (v.getId()) {
		case R.id.log_button1:
//			WifiFunction wifiFunction=new WifiFunction(this);
//			if (wifiFunction.CreateWifiInfo("MeetingRoom5", "kyzx2014", 3)) {
//				if (wifiFunction.set_static("MeetinlgRoom5", "192.168.0.100", "192.168.0.1", "192.168.0.1")) {
//					Toast.makeText(this, "转换服务器成功", Toast.LENGTH_LONG).show();
					i1.putExtra("ch", 's');
					startActivity(i1);
//				}else {
//					Toast.makeText(this, "设置静态ip失败，请手动设置", Toast.LENGTH_LONG).show();
//				}
//			}else {
//				Toast.makeText(this, "连接SSID为 ＪＨＳＹＳ的无线网失败", Toast.LENGTH_LONG).show();
//			}
			
			break;
		case R.id.log_button2:
			i1.putExtra("ch", 'c');
			WifiManager wifiManager=(WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiAutoConnectManager wacManager =new WifiAutoConnectManager(wifiManager);
			wacManager.connect("MeetingRoom5", "kyzx2014", WifiCipherType.WIFICIPHER_WPA);
			wacManager.set_static("MeetingRoom5","192.168.0.222","192.168.0.1","255.255.255.0");
			startActivity(i1);
			break;
		default:
			break;
		}
		
		this.finish();
	}

}
