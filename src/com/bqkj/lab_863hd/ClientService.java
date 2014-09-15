package com.bqkj.lab_863hd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import util.DateUtil;
import util.Util;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.bqkj.bean.User;
import com.bqkj.bean.WeatherInfo;
import com.bqkj.lab_863hd.databaseOperation.DBManager;
import com.bqkj.lab_863hd.databaseOperation.TempTB2Bean;

public class ClientService extends Service {

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

	public static String Send_ACTION_NAME = "android.intent.action.lab_863hdclient";
	public static String Rcv_ACTION_NAME = "com.bqkj863hd.clientservice";

	private Socket clientSocket;
	boolean isConnecting = false;
	CommandReceiver cmdReceiver;// 继承自BroadcastReceiver对象，用于得到Activity发送过来的命令
	int port = 50000;

	private static String tag = "tag_service";

	String action = "http://shbqkjwx.sinaapp.com/yixin.php";
	HttpPost httpRequest = null;
	List<NameValuePair> params = null;
	HttpResponse httpResponse;
	ScheduledThreadPoolExecutor exec;// 定时器类型的线程池
	PrintWriter mPrintWriterClient = null;
	BufferedReader mBufferedReaderClient = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		exec = new ScheduledThreadPoolExecutor(1);
		new Thread() {
			public void run() {
				while (true) {
					if (!isConnecting) {
						try {
							clientSocket = new Socket();
							clientSocket.connect(new InetSocketAddress(
									"192.168.0.115", port), 50000);
							clientSocket.setKeepAlive(true);
							// 接受客服端数据BufferedReader对象
							mBufferedReaderClient = new BufferedReader(
									new InputStreamReader(
											clientSocket.getInputStream()));
							mPrintWriterClient = new PrintWriter(
									new BufferedWriter(new OutputStreamWriter(
											clientSocket.getOutputStream())),
									true);
							isConnecting = true;
							showMyToast("连接服务器成功");
							try {
								readThread.start();
							} catch (Exception e) {
								// TODO: handle exception
							}

						} catch (IOException e) {
							showMyToast("连接服务器失败     \r\n" + e.getMessage());
							turnoffLight();
							try {
								Thread.sleep(9000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}
					}
				}
			}
		}.start();

		new Thread() {
			public void run() {
				while (true) {
					if (isConnecting) {
						try {
							mPrintWriterClient.write("1");
						} catch (Exception e) {
							showMyToast("与服务器通信失败");
							isConnecting = false;
							turnoffLight();
						}

						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						try {
							if (mBufferedReaderClient.read() == -1) {
								showMyToast("与服务器通信失败");
								isConnecting = false;
								turnoffLight();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							showMyToast("与服务器通信失败");
							isConnecting = false;
							turnoffLight();
						}
					}
				}

			}
		}.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		exec.scheduleAtFixedRate(weatherRunnable, 1000, 1000 * 60 * 3600,
				TimeUnit.MILLISECONDS);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		this.unregisterReceiver(cmdReceiver);// 取消注册的CommandReceiver
		closeSocket();
	}

	private void closeSocket() {
		try {
			if (clientSocket.getSoLinger() == -1) {// 在close之前把数据发送完，close是立马关闭
				clientSocket.setSoLinger(true, 60);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 前台Activity调用startService时，该方法自动执行
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		cmdReceiver = new CommandReceiver();
		IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
		// 注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
		filter.addAction((Rcv_ACTION_NAME));
		// 注册Broadcast Receiver
		registerReceiver(cmdReceiver, filter);
		return super.onStartCommand(intent, flags, startId);
	}

	// 接收Activity传送过来的命令
	private class CommandReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("shoudao action :", intent.getAction());
			if (intent.getAction().equals((Rcv_ACTION_NAME))) {
				int cmd = intent.getIntExtra("cmd", -1);// 获取Extra信息
				Log.i("shoudao cmd :", Integer.toString(cmd));
				if (cmd == 1) {
					Log.i("shoudao action :", intent.getStringExtra("str"));
				}
			}
		}
	}

	public void showMyToast(String str) {// 显示提示信息
		Intent intent = new Intent();
		intent.putExtra("cmd", CMD_SHOW_TOAST);
		intent.putExtra("str", str);
		intent.setAction(Send_ACTION_NAME);
		sendBroadcast(intent);
	}

	public void remove_User(User user) {// 显示提示信息
		Intent intent = new Intent();
		intent.putExtra("cmd", Remove_USER);
		intent.putExtra("user", user.userName_int);
		intent.setAction(Send_ACTION_NAME);
		sendBroadcast(intent);
	}

	public void showVal(String val, int type, int room) {// 显示提示信息
		Intent intent = new Intent();
		intent.putExtra("cmd", CMD_SHOW_Val);
		intent.putExtra("val", val);
		intent.putExtra("type", type);
		intent.putExtra("room", room);
		intent.setAction(Send_ACTION_NAME);
		sendBroadcast(intent);
	}

	public void showWeather(WeatherInfo weatherInfo) {// 显示提示信息
		Intent intent = new Intent();
		intent.putExtra("cmd", CMD_SHOW_Weather);
		intent.putExtra("weatherinfo", weatherInfo);
		intent.setAction(Send_ACTION_NAME);
		sendBroadcast(intent);
	}
	
	public void showTime(String timeString ) {// 显示提示信息
		Intent intent = new Intent();
		intent.putExtra("cmd", CMD_SHOW_TIME);
		intent.putExtra("time", timeString );
		intent.setAction(Send_ACTION_NAME);
		sendBroadcast(intent);
	}
	
	public void turnoffLight() {// 显示提示信息
		Intent intent = new Intent();
		intent.putExtra("cmd", TURNOFFLIGHT);
		intent.setAction(Send_ACTION_NAME);
		sendBroadcast(intent);
	}

	private String findRealDevice(String room) {
		String deviceName = "";
		int room_int = Integer.parseInt(room);
		switch (room_int) {
		case 80:
			deviceName = "次卧一";
			break;
		case 81:
			deviceName = "次卧一温湿度";
			break;
		case 82:
			deviceName = "客厅";
			break;
		case 83:
			deviceName = "次卧二";
			break;
		case 84:
			deviceName = "主卧";
			break;
		case 85:
			deviceName = "卫生间";
			break;
		case 86:
			deviceName = "客厅";
			break;
		case 87:
			deviceName = "过道";
			break;
		default:
			deviceName = "未知设备";
			break;
		}
		return deviceName;
	}

	Thread readThread = new Thread() {

		public void run() {

			char[] buffer_new = new char[23];
			int count = 0;

			while (isConnecting) {
				String rcvsmsg = "";// 服务器发来消息格式为：80#010320010000
				// char[] receiveBuffer = new char[1024];
				String[] smsg = null;
				try {
					if ((mBufferedReaderClient.read(buffer_new)) > 0) {
						rcvsmsg = getInfoBuff(buffer_new, 23);
					} else {
						break;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				smsg = rcvsmsg.trim().split("#");

				if (!smsg.equals(null) && smsg.length == 2 && !smsg.equals(" ")) {

//					if (smsg[1].length() > 12) {
//						String string1 = smsg[1].substring(6, 8);
//						String string2 = smsg[1].substring(4, 6);
//						if (string1.equals("01")) {
//							if (string2.equals("23")) {
//								smsg[0] = "81";
//							} else {
//								smsg[0] = "80";
//							}
//						} else {
//							smsg[0] = "8" + string1.substring(1, 2);
//						}
//					}

					showMyToast(smsg[0] + "----" + smsg[1]);
					if (smsg[1].length() == 14) {
						if (smsg[1].substring(0, 12).equals("010302000002")) {
							String val = smsg[1].substring(12, 14);
							Integer intval = Integer.parseInt(val, 16);
							showMyToast(findRealDevice(smsg[0])
									+ Integer.toString(intval) + "个模块在线");
						}
					}
					int m = 0;
					try {
						m = Integer.parseInt(smsg[0]);
					} catch (Exception e) {
						// TODO: handle exception
					}

					switch (m) {
					case 80:
						whichCmd(smsg[1], "01", 80);
						break;
					case 81:
						whichCmd(smsg[1], "01", 81);
						break;
					case 114:
						whichCmd(smsg[1], "01", 114);
						break;
					case 82:
						whichCmd(smsg[1], "02", 82);
						break;
					case 121:
						whichCmd(smsg[1], "02", 121);
						break;
					case 115:
						whichCmd(smsg[1], "02", 115);
						break;
					case 83:
						whichCmd(smsg[1], "03", 83);
						break;
					case 122:
						whichCmd(smsg[1], "03", 122);
						break;
					case 116:
						whichCmd(smsg[1], "03", 116);
						break;
					case 84:
						whichCmd(smsg[1], "04", 84);
						break;
					case 117:
						whichCmd(smsg[1], "04", 117);
						break;
					case 123:
						whichCmd(smsg[1], "04", 123);
						break;
					case 86:
						whichCmd(smsg[1], "05", 86);
						break;
					case 87:
						whichCmd(smsg[1], "06", 87);
						break;
					case 88:
						whichCmd(smsg[1], "06", 88);
						break;
					case 118:
						whichCmd(smsg[1], "06", 118);
						break;
					}
				}
			}
		}
	};

	private String getInfoBuff(char[] buff, int count) {
		char[] temp = new char[count];
		for (int i = 0; i < count; i++) {
			temp[i] = buff[i];
		}
		return new String(temp);
	}

	private String getNow() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
		String dateString = dateFormat.format(new Date());
		return dateString;
	}

	private void whichCmd(String smsg, String index, int roomid) {
			if (smsg.length() >= 20) {
				String rec = smsg.substring(0, 12);
				if (rec.equals("010317" + index + "0103")) {
					String val1 = smsg.substring(12, 16);
					double intval1 = Integer.parseInt(val1, 16);
					showVal(Double.toString(intval1), 17, roomid);
					// insert(smsg, intval1, 0);
				} else if (rec.equals("010318" + index + "0103")) {
					String val1 = smsg.substring(12, 16);
					double intval1 = Integer.parseInt(val1, 16);
					DecimalFormat df = new DecimalFormat(".00");
					showVal(df.format(intval1 / 10), 18, roomid);
					// insert(smsg, intval1, 0);
				}

				else if (rec.equals("010319" + index + "0103")) {
					String val1 = smsg.substring(12, 16);
					double intval1 = Integer.parseInt(val1, 16);
					DecimalFormat df = new DecimalFormat(".00");
					showVal(df.format(intval1 / 10), 19, roomid);
					// insert(smsg, intval1, 0);
				} else if (rec.equals("010320" + index + "0103")) {
					String val1 = smsg.substring(12, 16);
					DecimalFormat df = new DecimalFormat(".00");
					double intval1 = Integer.parseInt(val1, 16);
					showVal(df.format(intval1 / 10), 20, roomid);
					// insert(smsg, intval1, 0);
				} else if (rec.equals("010321" + index + "0103")) {
					String val1 = smsg.substring(12, 16);
					DecimalFormat df = new DecimalFormat(".00");
					double intval1 = Integer.parseInt(val1, 16);
					showVal(df.format(intval1 / 10), 21, roomid);
					// insert(smsg, intval1, 0);
				}

				else if (rec.equals("010322" + index + "0103")) {
					String val1 = smsg.substring(12, 16);
					DecimalFormat df = new DecimalFormat(".00");
					double intval1 = Integer.parseInt(val1, 16);
					showVal(df.format(intval1 / 10), 22, roomid);
					// insert(smsg, intval1, 0);
				}

				else if (rec.equals("010324" + index + "0103")) {
					String val1 = smsg.substring(12, 16);
					double intval1 = Integer.parseInt(val1, 16);
					showVal(Double.toString(intval1), 24, roomid);
					// insert(smsg, intval1, 0);
				} else if (rec.equals("0103ffff0103")) {// 由于该类传感器是一个单一的wifi模块，可根据地址知道传感器类型及所属房间，type类型是非必要的
														// 24可任意写成其他
					String val1 = smsg.substring(12, 16);
					double intval1 = Integer.parseInt(val1, 16);
					DecimalFormat df = new DecimalFormat(".00");
					showVal(df.format(intval1 / 10), 24, roomid);
					// insert(smsg, intval1, 0);
				}else if (rec.equals("010311010103")) {// co2
					String val1 = smsg.substring(12, 16);
					String val2 = smsg.substring(16, 20);
					double intval = Integer.parseInt(val1, 16);
					double intva2 = Integer.parseInt(val2, 16) / 10;
					showVal(Double.toString(intval), 111, roomid);
					showVal(Double.toString(intva2), 112, roomid);
					// insert(smsg, intval, intva2);
				} else if (rec.equals("010311020103")) {
					String val1 = smsg.substring(12, 16);
					String val2 = smsg.substring(16, 20);
					double intval = Integer.parseInt(val1, 16);
					double intva2 = Integer.parseInt(val2, 16) / 10;
					showVal(Double.toString(intval), 121, roomid);
					showVal(Double.toString(intva2), 122, roomid);
					// insert(smsg, intval, intva2);
				} else if (rec.equals("010311030103")) {
					String val1 = smsg.substring(12, 16);
					String val2 = smsg.substring(16, 20);
					double intval = Integer.parseInt(val1, 16);
					double intva2 = Integer.parseInt(val2, 16) / 10;
					showVal(Double.toString(intval), 131, roomid);
					showVal(Double.toString(intva2), 132, roomid);
					// insert(smsg, intval, intva2);
				}

				else if (rec.equals("010323" + index + "0103")) {
					String val1 = smsg.substring(12, 16);
					String val2 = smsg.substring(16, 20);
					double intval = Integer.parseInt(val1, 16) / 10;
					double intva2 = Integer.parseInt(val2, 16) / 10;
					showVal(Double.toString(intval) + "#"
							+ Double.toString(intva2), 23, roomid);
					// insert(smsg, intval, intva2);
				} else if (rec.equals("0103ffff0103")) {
					String val1 = smsg.substring(12, 16);
					String val2 = smsg.substring(16, 20);
					DecimalFormat df = new DecimalFormat(".00");
					double intval = Integer.parseInt(val1, 16);
					double intva2 = Integer.parseInt(val2, 16);
					showVal(df.format(intval / 10) + "#"
							+ df.format(intva2 / 10), 23, roomid);
					// insert(smsg, intval, intva2);
				}
			}
	}

	Runnable weatherRunnable = new Runnable() {

		@Override
		public void run() {
			getcityweather("郑州");
			showTime(DateUtil.Getdate());
		}
	};

	private void getcityweather(String stringmsg) {

		httpRequest = new HttpPost(action);
		/* Post运作传送变数必须用NameValuePair[]阵列储存 */
		params = new ArrayList<NameValuePair>();

		try {
			params.add(new BasicNameValuePair("city", stringmsg));
			// 发出HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 取得HTTP response
			httpResponse = new DefaultHttpClient().execute(httpRequest);
			// 若状态码为200
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取出回应字串
				String strResult = EntityUtils.toString(
						httpResponse.getEntity(), "utf-8");
				showWeather(Util.parseJsonMulti(strResult));
			}
		} catch (Exception e) {
			showMyToast("获取天气数据失败");
		}
	}

}
