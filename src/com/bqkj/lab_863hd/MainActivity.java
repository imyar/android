package com.bqkj.lab_863hd;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import util.DateUtil;
import util.Utils;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bqkj.bean.WeatherInfo;

public class MainActivity extends RoboActivity implements OnClickListener {

	/************** service 命令 *********/
	static final int CMD_STOP_SERVICE = 0x01;
	static final int CMD_SEND_DATA = 0x02;
	static final int CMD_SYSTEM_EXIT = 0x03;
	static final int CMD_SHOW_TOAST = 0x04;
	static final int CMD_Service_Started = 0x05;// 界面上显示toast
	static final int CMD_SEND_Address = 0x06;// 界面上显示toast
	static final int CMD_SHOW_Val = 0x07;
	static final int CONNECT_FAILD = 0x08;
	static final int UPDATE_DATE = 0x09;
	static final int UPDATE_WEATHER = 0xa;
	static final int CMD_SHOW_Weather = 0xb;
	static final int Remove_USER = 0xc;
	static final int TURNOFFLIGHT = 0xd;
	static final int CMD_SHOW_TIME = 0xe;

	public static String Rcv_ACTION_NAME;
	public static String Send_ACTION_NAME;
	private MyReceiver receiver;
	private Context context;

	@InjectView(R.id.rlayout1)
	RelativeLayout rlayout1;
	@InjectView(R.id.main_r_room2)
	RelativeLayout rlayout2;

	//
	@InjectView(R.id.main_txt_week)
	TextView tv_week;
	@InjectView(R.id.main_txt_year)
	TextView tv_year;
	@InjectView(R.id.mian_txt_lunar)
	TextView tv_lunar;
	@InjectView(R.id.mian_txt_animalyear)
	TextView tv_anmialyear;
	@InjectView(R.id.main_txt_aqi)
	TextView tv_aqi;
	@InjectView(R.id.main_txt_city)
	TextView tv_city;
	@InjectView(R.id.main_txt_co)
	TextView tv_cor;
	@InjectView(R.id.main_txt_level)
	TextView tv_level;
	@InjectView(R.id.main_txt_no2)
	TextView tv_no2;
	@InjectView(R.id.main_txt_o31h)
	TextView tv_o31h;
	@InjectView(R.id.main_txt_o38h)
	TextView tv_o38h;
	@InjectView(R.id.main_txt_pm10)
	TextView tv_pm10;
	@InjectView(R.id.main_txt_pm25)
	TextView tv_pm25;
	@InjectView(R.id.main_txt_prm)
	TextView tv_prm;
	@InjectView(R.id.main_txt_so2)
	TextView tv_so2;
	@InjectView(R.id.main_txt_temp)
	TextView tv_temp;
	@InjectView(R.id.main_txt_wet)
	TextView tv_wet;
	@InjectView(R.id.main_txt_wind)
	TextView tv_wind;
	@InjectView(R.id.main_txt_quality)
	TextView tv_quality;
	@InjectView(R.id.main_txt_rain)
	TextView tv_rain;

	// 次卧一为第一，因为以次卧一作为第一个测试的房间
	@InjectView(R.id.main_r2_txt_wendu)
	TextView tv1_wendu;
	@InjectView(R.id.main_r2_txt_anqi)
	TextView tv1_an;
	@InjectView(R.id.main_r2_txt_jiaquan)
	TextView tv1_jiaquan;
	@InjectView(R.id.main_r2_txt_co2)
	TextView tv1_co2;
	@InjectView(R.id.main_r2_txt_o3)
	TextView tv1_o3;
	@InjectView(R.id.main_r2_txt_wet)
	TextView tv1_wet;
	@InjectView(R.id.main_r2_txt_yacha)
	TextView tv1_yacha;
	@InjectView(R.id.main_r2_txt_voc)
	TextView tv1_voc;

	@InjectView(R.id.img_r2_wendu)
	ImageView img1_wendu;
	@InjectView(R.id.img_r2_anqi)
	ImageView img1_an;
	@InjectView(R.id.img_r2_c2h)
	ImageView img1_jiaquan;
	@InjectView(R.id.img_r2_co2)
	ImageView img1_co2;
	@InjectView(R.id.img_r2_o3)
	ImageView img1_o3;
	@InjectView(R.id.img_r2_wet)
	ImageView img1_wet;
	@InjectView(R.id.img_r2_yacha)
	ImageView img1_yacha;
	@InjectView(R.id.img_r2_voc)
	ImageView img1_voc;

	@InjectView(R.id.main_r1_txt_wendu)
	TextView tv2_wendu;
	@InjectView(R.id.main_r1_txt_wet)
	TextView tv2_wet;
	@InjectView(R.id.main_r1_txt_anqi)
	TextView tv2_an;
	@InjectView(R.id.main_r1_txt_jiaquan)
	TextView tv2_jiaquan;
	@InjectView(R.id.main_r1_txt_co2)
	TextView tv2_co2;
	@InjectView(R.id.main_r1_txt_o3)
	TextView tv2_o3;
	@InjectView(R.id.main_r1_txt_yacha)
	TextView tv2_yacha;
	@InjectView(R.id.main_r1_txt_voc)
	TextView tv2_voc;

	@InjectView(R.id.img_r1_wendu)
	ImageView img2_wendu;
	@InjectView(R.id.img_r1_wet)
	ImageView img2_wet;
	@InjectView(R.id.img_r1_anqi)
	ImageView img2_an;
	@InjectView(R.id.img_r1_c2h)
	ImageView img2_jiaquan;
	@InjectView(R.id.img_r1_co2)
	ImageView img2_co2;
	@InjectView(R.id.img_r1_o3)
	ImageView img2_o3;
	@InjectView(R.id.img_r1_yacha)
	ImageView img2_yacha;
	@InjectView(R.id.img_r1_voc)
	ImageView img2_voc;

	@InjectView(R.id.main_r3_txt_wendu)
	TextView tv3_wendu;
	@InjectView(R.id.main_r3_txt_wet)
	TextView tv3_wet;
	@InjectView(R.id.main_r3_txt_anqi)
	TextView tv3_an;
	@InjectView(R.id.main_r3_txt_jiaquan)
	TextView tv3_jiaquan;
	@InjectView(R.id.main_r3_txt_co2)
	TextView tv3_co2;
	@InjectView(R.id.main_r3_txt_o3)
	TextView tv3_o3;
	@InjectView(R.id.main_r3_txt_yacha)
	TextView tv3_yacha;
	@InjectView(R.id.main_r3_txt_voc)
	TextView tv3_voc;

	@InjectView(R.id.img_r3_wendu)
	ImageView img3_wendu;
	@InjectView(R.id.img_r3_wet)
	ImageView img3_wet;
	@InjectView(R.id.img_r3_anqi)
	ImageView img3_an;
	@InjectView(R.id.img_r3_c2h)
	ImageView img3_jiaquan;
	@InjectView(R.id.img_r3_co2)
	ImageView img3_co2;
	@InjectView(R.id.img_r3_o3)
	ImageView img3_o3;
	@InjectView(R.id.img_r3_yacha)
	ImageView img3_yacha;
	@InjectView(R.id.img_r3_voc)
	ImageView img3_voc;

	@InjectView(R.id.main_r4_txt_anqi)
	TextView tv4_anqi;
	@InjectView(R.id.main_r4_txt_co2)
	TextView tv4_co2;
	@InjectView(R.id.main_r4_txt_ben)
	TextView tv4_ben;

	@InjectView(R.id.img_r4_co2)
	ImageView img4_co2;
	@InjectView(R.id.img_r4_anqi)
	ImageView img4_an;
	@InjectView(R.id.img_r4_ben)
	ImageView img4_ben;

	@InjectView(R.id.main_r5_txt_wet)
	TextView tv5_wet;
	@InjectView(R.id.main_r5_txt_co2)
	TextView tv5_co2;
	@InjectView(R.id.img_r5_wet)
	ImageView img5_wendu;
	@InjectView(R.id.img_r5_co2)
	ImageView img5_co2;

	@InjectView(R.id.main_r6_txt_ben)
	TextView tv6_ben;
	@InjectView(R.id.main_r6_txt_jiaquan)
	TextView tv6_jiaquan;
	@InjectView(R.id.main_r6_txt_anqi)
	TextView tv6_anqi;
	@InjectView(R.id.main_r6_txt_voc)
	TextView tv6_voc;
	@InjectView(R.id.img_r6_ben)
	ImageView img6_ben;
	@InjectView(R.id.img_r6_c2h)
	ImageView img6_jiaquan;
	@InjectView(R.id.img_r6_anqi)
	ImageView img6_an;
	@InjectView(R.id.img_r6_voc)
	ImageView img6_voc;

	@InjectView(R.id.main_r7_txt_wet)
	TextView tv7_wet;
	@InjectView(R.id.main_r7_txt_jiaquan)
	TextView tv7_jiaquan;
	@InjectView(R.id.main_r7_txt_co2)
	TextView tv7_co2;

	@InjectView(R.id.img_r7_wet)
	ImageView img7_wet;
	@InjectView(R.id.img_r7_c2h)
	ImageView img7_jiaquan;
	@InjectView(R.id.img_r7_co2)
	ImageView img7_co2;

	@InjectView(R.id.main_r8_txt_wet)
	TextView tv8_wendu;
	@InjectView(R.id.main_r8_txt_anqi)
	TextView tv8_an;
	@InjectView(R.id.main_r8_txt_jiaquan)
	TextView tv8_jiaquan;
	@InjectView(R.id.main_r8_txt_co2)
	TextView tv8_co2;
	@InjectView(R.id.main_r8_txt_o3)
	TextView tv8_o3;
	@InjectView(R.id.main_r8_txt_ben)
	TextView tv8_ben;
	@InjectView(R.id.main_r8_txt_yacha)
	TextView tv8_yacha;
	@InjectView(R.id.main_r8_txt_voc)
	TextView tv8_voc;

	@InjectView(R.id.img_r8_wet)
	ImageView img8_wendu;
	@InjectView(R.id.img_r8_anqi)
	ImageView img8_an;
	@InjectView(R.id.img_r8_jiaquan)
	ImageView img8_jiaquan;
	@InjectView(R.id.img_r8_co2)
	ImageView img8_co2;
	@InjectView(R.id.img_r8_o3)
	ImageView img8_o3;
	@InjectView(R.id.img_r8_ben)
	ImageView img8_ben;
	@InjectView(R.id.img_r8_yacha)
	ImageView img8_yacha;
	@InjectView(R.id.img_r8_voc)
	ImageView img8_voc;

	@InjectView(R.id.main_img)
	ImageView iv_weather_img;
	Intent serviceintent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		char ch = getIntent().getCharExtra("ch", '1');
		switch (ch) {
		case 's':// 作为服务器启动LabService
			Send_ACTION_NAME = "com.bqkj863hd.service";
			boolean isServiceRun = Utils
					.isServiceRun(context, Send_ACTION_NAME);
			Rcv_ACTION_NAME = "android.intent.action.lab_863hd";
			if (!isServiceRun) {
				serviceintent = new Intent(MainActivity.this, LabService.class);
				startService(serviceintent);
			}
			break;
		case 'c':// 作为客户端，启动ClientService
			Send_ACTION_NAME = "com.bqkj863hd.clientservice";
			Rcv_ACTION_NAME = "android.intent.action.lab_863hdclient";
			boolean isClientServiceRun = Utils.isServiceRun(context,
					Send_ACTION_NAME);
			if (!isClientServiceRun) {
				serviceintent = new Intent(MainActivity.this,
						ClientService.class);
				startService(serviceintent);
			}
			break;
		default:
			break;
		}

		initDateWegit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面
		// 经过测试,如果是乐Phone返回桌面会报错
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 创建退出系统提示框
			if (notSupportKeyCodeBack()) {
				new AlertDialog.Builder(this)
						.setMessage("确认退出？")
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										onDestroy(); // 退出应用处理
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create().show();
			} else {
				// 返回桌面,经测试,有一些手机不支持,查看 notSupportKeyCodeBack 方法
				Intent i = new Intent(Intent.ACTION_MAIN);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				i.addCategory(Intent.CATEGORY_HOME);
				startActivity(i);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean notSupportKeyCodeBack() {
		if ("3GW100".equals(Build.MODEL) || "3GW101".equals(Build.MODEL)
				|| "3GC101".equals(Build.MODEL)) {
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (receiver != null) {
			MainActivity.this.unregisterReceiver(receiver);
		}
	}

	public void showToast(String str) {// 显示提示信息
		Toast.makeText(getApplicationContext(), str, 100).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Rcv_ACTION_NAME);
		MainActivity.this.registerReceiver(receiver, filter);
	}
	
	//屏幕发生旋转时候，不做任何操作
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// Nothing need to be done here

		} else {
			// Nothing need to be done here
		}

	}
	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Rcv_ACTION_NAME)) {
				Bundle bundle = intent.getExtras();
				int cmd = bundle.getInt("cmd");

				if (cmd == CMD_SHOW_TOAST) {
					String str = bundle.getString("str");
					showToast(str);
				} else if (cmd == TURNOFFLIGHT) {
					darkAllDevice();
				} else if (cmd == Remove_USER) {
					int user = bundle.getInt("user");
					remove_user(user);

				} else if (cmd == CMD_SHOW_TIME) {
					String str_time = bundle.getString("time");
					String[] daStrings = str_time.split("#");
					tv_week.setText(daStrings[0]);
					tv_anmialyear.setText(daStrings[3]);
					tv_year.setText(daStrings[1]);
					tv_lunar.setText(daStrings[2]);
				} else if (cmd == CMD_SHOW_Weather) {
					WeatherInfo weatherInfo = (WeatherInfo) intent
							.getParcelableExtra("weatherinfo");

					tv_aqi.setText(weatherInfo.get_aqi());
					tv_city.setText(weatherInfo.get_city());
					tv_level.setText(weatherInfo.get_level());
					tv_no2.setText(weatherInfo.get_no2());
					tv_o31h.setText(weatherInfo.get_o3());
					// tv_o38h.setText(weatherInfo.get_aqi());
					tv_pm10.setText(weatherInfo.get_pm10());
					tv_pm25.setText(weatherInfo.get_pm2_5());
					tv_prm.setText(weatherInfo.get_primary_pollutant());
					tv_so2.setText(weatherInfo.get_so2());
					tv_temp.setText(weatherInfo.get_tempature() + "℃");
					tv_wet.setText(weatherInfo.get_wet() + "%");
					tv_quality.setText(weatherInfo.get_quality());
					tv_wind.setText(weatherInfo.get_wind());
					tv_rain.setText(weatherInfo.get_weather_real());
					iv_weather_img.setImageResource(getImg(weatherInfo
							.get_weather_img()));

				} else if (cmd == CMD_SHOW_Val) {
					int type = bundle.getInt("type");
					int room = bundle.getInt("room");
					String val = bundle.getString("val");

					switch (room) {
					case 80:
						switch (type) {
						case 17:
							tv1_co2.setText(val + " ppm");
							lightDevice(img1_co2);
							break;
						case 18:
							tv1_an.setText(val + " ppm");
							lightDevice(img1_an);
							break;
						case 20:
							tv1_jiaquan.setText(val + " ppm");
							lightDevice(img1_jiaquan);
							break;
						case 22:
							tv1_o3.setText(val + " ppm");
							lightDevice(img1_o3);
							break;
						case 24:
							tv1_yacha.setText(val + " ppm");
							lightDevice(img1_yacha);
							break;
						default:
							break;
						}
						break;

					case 81:
						String[] sval = val.split("#");
						tv1_wendu.setText(sval[0] + " ℃");
						lightDevice(img1_wendu);
						tv1_wet.setText(sval[1] + " %");
						lightDevice(img1_wet);
						break;
					case 114:
						tv1_voc.setText(val + " ppm");
						lightDevice(img1_voc);
						break;
					case 82:
						switch (type) {
						case 17:
							tv8_co2.setText(val + " ppm");
							lightDevice(img8_co2);
							break;
						case 18:
							tv8_an.setText(val + " ppm");
							lightDevice(img8_an);
							break;
						case 19:
							tv8_ben.setText(val + " ppm");
							lightDevice(img8_ben);
							break;
						case 20:
							tv8_jiaquan.setText(val + " ppm");
							lightDevice(img8_jiaquan);
							break;
						case 22:
							tv8_o3.setText(val + " ppm");
							lightDevice(img8_o3);
							break;
						case 24:
							tv8_yacha.setText(val + " ppm");
							lightDevice(img8_yacha);
							break;
						default:
							break;
						}
						break;

					case 121:
						String[] val121 = val.split("#");
						tv8_wendu.setText(val121[0] + " ℃   " + val121[1]
								+ " %");
						lightDevice(img8_wendu);
						break;
					case 115:
						tv8_voc.setText(val + " ppm");
						lightDevice(img8_voc);
						break;
					case 83:
						switch (type) {
						case 17:
							tv3_co2.setText(val + " ppm");
							lightDevice(img3_co2);
							break;
						case 18:
							tv3_an.setText(val + " ppm");
							lightDevice(img3_an);
							break;
						case 20:
							tv3_jiaquan.setText(val + " ppm");
							lightDevice(img3_jiaquan);
							break;
						case 22:
							tv3_o3.setText(val + " ppm");
							lightDevice(img3_o3);
							break;
						case 24:
							tv3_yacha.setText(val + " ppm");
							lightDevice(img3_yacha);
							break;
						default:
							break;
						}
						break;

					case 122:
						String[] val122 = val.split("#");
						tv3_wendu.setText(val122[0] + " ℃");
						lightDevice(img3_wendu);
						tv3_wet.setText(val122[1] + " %");
						lightDevice(img3_wet);
						break;
					case 116:
						tv3_voc.setText(val + " ppm");
						lightDevice(img3_voc);
						break;
					case 84:
						switch (type) {
						case 17:
							tv2_co2.setText(val + " ppm");
							lightDevice(img2_co2);
							break;
						case 18:
							tv2_an.setText(val + " ppm");
							lightDevice(img2_an);
							break;
						case 20:
							tv2_jiaquan.setText(val + " ppm");
							lightDevice(img2_jiaquan);
							break;
						case 22:
							tv2_o3.setText(val + " ppm");
							lightDevice(img2_o3);
							break;
						case 24:
							tv2_yacha.setText(val + " ppm");
							lightDevice(img2_yacha);
							break;
						default:
							break;
						}
						break;
					case 123:
						String[] val123 = val.split("#");
						tv2_wendu.setText(val123[0] + " ℃");
						lightDevice(img2_wendu);
						tv2_wet.setText(val123[1] + " %");
						lightDevice(img2_wet);
						break;
					case 117:
						tv2_voc.setText(val + " ppm");
						lightDevice(img2_voc);
						break;
					case 85:
						switch (type) {
						case 17:
							tv4_co2.setText(val + " ppm");
							lightDevice(img4_co2);
							break;
						case 18:
							tv4_anqi.setText(val + " ppm");
							lightDevice(img4_an);
							break;
						case 19:
							tv4_ben.setText(val + " ppm");
							lightDevice(img4_ben);
							break;
						default:
							break;
						}
						break;
					case 86:
						tv6_ben.setText(val + " ppm");
						lightDevice(img6_ben);
						break;
					case 87:
						tv6_jiaquan.setText(val + " ppm");
						lightDevice(img6_jiaquan);
						break;
					case 88:
						tv6_anqi.setText(val + " ppm");
						lightDevice(img6_an);
						break;
					case 118:
						tv6_voc.setText(val + " ppm");
						lightDevice(img6_voc);
						break;
					default:
						break;
					}

				}

				else if (cmd == CMD_SYSTEM_EXIT) {
					System.exit(0);
				} else if (cmd == CMD_Service_Started) {
					int val = bundle.getInt("state");
					// mDialog.dismiss();
					if (val == 1) {
						showToast("蓝牙建立成功");

					} else if (val == 0) {
						showToast("蓝牙建立失败");
					}
				}

			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.rlayout1:
			Intent intent = new Intent(MainActivity.this, Room2Activity.class);
			startActivity(intent);
			break;

		case R.id.main_r_room2:
			Intent intent2 = new Intent(MainActivity.this,
					Room2FrashAirActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	private void initDateWegit() {
		// DateUtil dateUtil=new DateUtil();
		// tv_week.setText(dateUtil.Getdate()[0]);
		// tv_anmialyear.setText(dateUtil.Getdate()[3]);
		// tv_year.setText(dateUtil.Getdate()[1]);
		// tv_lunar.setText(dateUtil.Getdate()[2]);

		// rlayout1.setOnClickListener(this);
		// rlayout2.setOnClickListener(this);

	}

	private void remove_user(int index) {
		switch (index) {
		case 80:
			darkDevice(img1_an);
			darkDevice(img1_jiaquan);
			darkDevice(img1_voc);

			darkDevice(img1_co2);
			darkDevice(img1_yacha);
			darkDevice(img1_o3);

			tv1_an.setText("无数据");
			tv1_jiaquan.setText("无数据");
			tv1_co2.setText("无数据");
			tv1_o3.setText("无数据");
			tv1_yacha.setText("无数据");
			tv1_voc.setText("无数据");
			break;
		case 81:
			darkDevice(img1_wendu);
			tv1_wendu.setText("无数据");
			darkDevice(img1_wet);
			tv1_wet.setText("无数据");
			break;

		case 82:
			darkDevice(img8_wendu);
			darkDevice(img8_an);
			darkDevice(img8_voc);
			darkDevice(img8_jiaquan);
			darkDevice(img8_o3);
			darkDevice(img8_yacha);
			darkDevice(img8_co2);
			darkDevice(img8_ben);
			tv8_an.setText("无数据");
			tv8_jiaquan.setText("无数据");
			tv8_co2.setText("无数据");
			tv8_ben.setText("无数据");
			tv8_o3.setText("无数据");
			tv8_yacha.setText("无数据");
			tv8_voc.setText("无数据");
			tv8_wendu.setText("无数据");
			break;

		case 83:
			darkDevice(img3_wendu);
			darkDevice(img3_an);
			darkDevice(img3_voc);
			darkDevice(img3_jiaquan);
			darkDevice(img3_o3);
			darkDevice(img3_yacha);
			darkDevice(img3_co2);
			tv3_an.setText("无数据");
			tv3_jiaquan.setText("无数据");
			tv3_co2.setText("无数据");
			tv3_o3.setText("无数据");
			tv3_yacha.setText("无数据");
			tv3_voc.setText("无数据");
			tv3_wendu.setText("无数据");
			break;

		case 84:
			darkDevice(img2_wendu);
			darkDevice(img2_an);
			darkDevice(img2_voc);
			darkDevice(img2_jiaquan);
			darkDevice(img2_o3);
			darkDevice(img2_yacha);
			darkDevice(img2_co2);
			tv2_an.setText("无数据");
			tv2_jiaquan.setText("无数据");
			tv2_co2.setText("无数据");
			tv2_o3.setText("无数据");
			tv2_yacha.setText("无数据");
			tv2_voc.setText("无数据");
			tv2_wendu.setText("无数据");
			break;

		case 85:
			darkDevice(img1_wendu);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void sendCmd(byte[] value) {
		Intent intent = new Intent();// 创建Intent对象
		intent.setAction("android.intent.action.cmd");
		intent.putExtra("cmd", CMD_SEND_DATA);
		intent.putExtra("value", value);
		sendBroadcast(intent);// 发送广播
	}

	public void sendCmd(String value) {
		Intent intent = new Intent();// 创建Intent对象
		intent.setAction("android.intent.action.cmd");
		intent.putExtra("cmd", CMD_SEND_Address);
		intent.putExtra("address", value);
		sendBroadcast(intent);// 发送广播
	}

	/*
	 * 根据名称获得资源id，如; a_1.gif ,根据a_1获得资源id
	 * 
	 * @author 王占良
	 */
	public int getImg(String index) {

		if (index == null || index.trim().equals("")) {
			return R.drawable.a_nothing;
		}
		String string = "a_" + index.substring(0, index.length() - 4);
		Resources res = getResources();
		int m = res.getIdentifier(string, "drawable", getPackageName());
		return m;
	}

	private void lightDevice(ImageView img) {
		img.setImageResource(R.drawable.connect_green);
	}

	private void darkDevice(ImageView img) {
		img.setImageResource(R.drawable.connect_red);
	}

	private void darkAllDevice() {
		img1_an.setImageResource(R.drawable.connect_red);
		img1_co2.setImageResource(R.drawable.connect_red);
		img1_jiaquan.setImageResource(R.drawable.connect_red);
		img1_o3.setImageResource(R.drawable.connect_red);
		img1_voc.setImageResource(R.drawable.connect_red);
		img1_wendu.setImageResource(R.drawable.connect_red);
		img1_wet.setImageResource(R.drawable.connect_red);
		img1_yacha.setImageResource(R.drawable.connect_red);
		img2_an.setImageResource(R.drawable.connect_red);
		img2_co2.setImageResource(R.drawable.connect_red);
		img2_jiaquan.setImageResource(R.drawable.connect_red);
		img2_o3.setImageResource(R.drawable.connect_red);
		img2_voc.setImageResource(R.drawable.connect_red);
		img2_wendu.setImageResource(R.drawable.connect_red);
		img2_wet.setImageResource(R.drawable.connect_red);
		img2_yacha.setImageResource(R.drawable.connect_red);
		img3_an.setImageResource(R.drawable.connect_red);
		img3_co2.setImageResource(R.drawable.connect_red);
		img3_jiaquan.setImageResource(R.drawable.connect_red);
		img3_o3.setImageResource(R.drawable.connect_red);
		img3_voc.setImageResource(R.drawable.connect_red);
		img1_an.setImageResource(R.drawable.connect_red);
		img3_wendu.setImageResource(R.drawable.connect_red);
		img3_wet.setImageResource(R.drawable.connect_red);
		img3_yacha.setImageResource(R.drawable.connect_red);
		img4_an.setImageResource(R.drawable.connect_red);
		img4_ben.setImageResource(R.drawable.connect_red);
		img4_co2.setImageResource(R.drawable.connect_red);
		img5_co2.setImageResource(R.drawable.connect_red);
		img5_wendu.setImageResource(R.drawable.connect_red);
		img6_an.setImageResource(R.drawable.connect_red);
		img6_ben.setImageResource(R.drawable.connect_red);
		img6_jiaquan.setImageResource(R.drawable.connect_red);
		img6_voc.setImageResource(R.drawable.connect_red);
		img7_jiaquan.setImageResource(R.drawable.connect_red);
		img7_co2.setImageResource(R.drawable.connect_red);
		img7_wet.setImageResource(R.drawable.connect_red);
		img8_an.setImageResource(R.drawable.connect_red);
		img8_ben.setImageResource(R.drawable.connect_red);
		img8_o3.setImageResource(R.drawable.connect_red);
		img8_co2.setImageResource(R.drawable.connect_red);
		img8_jiaquan.setImageResource(R.drawable.connect_red);
		img8_voc.setImageResource(R.drawable.connect_red);
		img8_wendu.setImageResource(R.drawable.connect_red);
		img8_yacha.setImageResource(R.drawable.connect_red);

	}

}
