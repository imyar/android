package com.bqkj.lab_863hd;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.Format;
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
import android.R.integer;
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

public class LabService extends Service {

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

	public static String Send_ACTION_NAME = "android.intent.action.lab_863hd";
	public static String Rcv_ACTION_NAME = "com.bqkj863hd.service";
	private ServerSocket serverSocket;
	private Socket clientSocket;
	boolean isConnecting = false;
	CommandReceiver cmdReceiver;// 继承自BroadcastReceiver对象，用于得到Activity发送过来的命令
	int port = 50000;
	private List<User> userList = new ArrayList<User>();
	private static String tag = "tag_service";

	private LisenThread lisenThread;
	private boolean isNormalExit = false;
	private Handler handler = new Handler();
	SendClass sendthread;
	Thread thread;
	String action = "http://shbqkjwx.sinaapp.com/yixin.php";
	HttpPost httpRequest = null;
	List<NameValuePair> params = null;
	HttpResponse httpResponse;
	ExecutorService pool;// 线程池，由于线程太多，方便管理
	ScheduledThreadPoolExecutor exec;// 定时器类型的线程池
	private DBManager mgr;
	List<TempTB2Bean> tempTB2Beans;

	List<byte[]> cmd_ciwo1;
	List<byte[]> cmd_ciwo2;
	List<byte[]> cmd_zhuwo;
	List<byte[]> cmd4;
	List<byte[]> cmd5;
	List<byte[]> cmd_cesuo;
	List<byte[]> cmd7;
	List<byte[]> cmd_keting;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		// 初始化DBManager
		mgr = new DBManager(this.getApplicationContext());
		initCmd();
		tempTB2Beans = new ArrayList<TempTB2Bean>();

		try {
			serverSocket = new ServerSocket(port);// 监听本机端口
			showMyToast("开始监听客户端" + serverSocket.getLocalSocketAddress() + ":"
					+ serverSocket.getLocalPort());
			isConnecting = true;
			pool = Executors.newCachedThreadPool();
			exec = new ScheduledThreadPoolExecutor(1);
		} catch (IOException e) {
			showMyToast("监听失败，请打开wifi");
		}
		if (isConnecting) {
			lisenThread = new LisenThread();// 开启监听线程
			pool.execute(lisenThread);
		}
		super.onCreate();
	}

	class LisenThread extends Thread {

		@Override
		public void run() {
			super.run();

			while (true) {
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
					break;// 异常退出循环
				}
				User user = null;
				try {
					user = new User(clientSocket);
					showMyToast(user.room + "无线设备上线");

					user.isConnecting = true;
					thread = new ReadClass(user);
					user.readthreadName = thread;
					pool.execute(thread);

					sendthread = new SendClass(user, user.userName_int);
					// handler.postDelayed(sendthread, 1000); // 开始Timer 一秒发一次
					user.sendthreadName = sendthread;

					exec.scheduleAtFixedRate(sendthread, 0, 1000,
							TimeUnit.MILLISECONDS);

				} catch (IOException e) {
					e.printStackTrace();
				}
				addUser(user);
			}
		}
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
		isNormalExit = true;
		if (userList.size() > 0) {
			for (int i = userList.size() - 1; i >= 0; i--) {// 依次删除用户
				removeUser(userList.get(i));
			}
		}
		pool.shutdown();
		exec.shutdown();
		handler.removeCallbacks(sendthread);
		this.unregisterReceiver(cmdReceiver);// 取消注册的CommandReceiver
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

	private void SendMessage(User user, byte[] buffer) {

		try {
			user.outputStream.write(buffer, 0, buffer.length);// 发送方向给服务器
			user.outputStream.flush();
		} catch (Exception e) {
			showMyToast(user.userName + ":发送数据异常");
			removeUser(user);
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

	public void showTime(String timeString) {// 显示提示信息
		Intent intent = new Intent();
		intent.putExtra("cmd", CMD_SHOW_TIME);
		intent.putExtra("time", timeString);
		intent.setAction(Send_ACTION_NAME);
		sendBroadcast(intent);
	}

	@SuppressWarnings("deprecation")
	private void removeUser(User user) {
		try {
			user.close();
			userList.remove(user);
			user.isConnecting = false;
			user.readthreadName.stop();
			exec.remove(user.sendthreadName);
			remove_User(user);// 发送用户掉线广播，更新界面
			showMyToast("用户  " + user.userName + "异常，停止接收数据");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addUser(User user) {
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).userName.equals(user.userName)) {
				userList.remove(i);
				userList.add(user);
				i = userList.size() - 1;
			} else {
				if (i == userList.size() - 1) {
					if (!userList.get(i).userName.equals(user.userName)) {
						userList.add(user);
					}
				}
			}
		}
	}

	class ReadClass extends Thread {
		private User myUser;

		public ReadClass(User myUser) {
			this.myUser = myUser;
		}

		public void run() {
			byte[] buffer_new = new byte[1024];
			int count = 0;
			String smsg = "";
			try {
				while (myUser.isConnecting) {
					if ((count = myUser.inputStream.read(buffer_new)) > 0) {
						smsg = Util.bytes2HexString(buffer_new, count);
					} else {
						break;
					}
					if (myUser.inputStream.available() == 0) {
						// showMyToast(myUser.room + "发来消息" + smsg);
					}

					if (smsg.length() == 14) {
						if (smsg.substring(0, 12).equals("010302000002")) {
							String val = smsg.substring(12, 14);
							Integer intval = Integer.parseInt(val, 16);
							showMyToast(myUser.room + Integer.toString(intval)
									+ "个模块在线");
						}
					}

					switch (myUser.userName_int) {
					case 80:
						whichCmd(smsg, "01", 80);
						break;
					case 81:
						whichCmd(smsg, "01", 81);
						break;
					case 114:
						whichCmd(smsg, "01", 114);
						break;
					case 82:
						whichCmd(smsg, "02", 82);
						break;
					case 121:
						whichCmd(smsg, "02", 121);
						break;
					case 115:
						whichCmd(smsg, "02", 115);
						break;
					case 83:
						whichCmd(smsg, "03", 83);
						break;
					case 122:
						whichCmd(smsg, "03", 122);
						break;
					case 116:
						whichCmd(smsg, "03", 116);
						break;
					case 84:
						whichCmd(smsg, "04", 84);
						break;
					case 117:
						whichCmd(smsg, "04", 117);
						break;
					case 123:
						whichCmd(smsg, "04", 123);
						break;
					case 86:
						whichCmd(smsg, "05", 86);
						break;
					case 87:
						whichCmd(smsg, "06", 87);
						break;
					case 88:
						whichCmd(smsg, "06", 88);
						break;
					case 118:
						whichCmd(smsg, "06", 118);
						break;

					default:
						break;
					}
				}
			} catch (IOException e) {
				myUser.isConnecting = false;
			}
		}

		private int getId(String getString) {
			int id = 0;
			String string = getString.substring(4, 8);
			id = Integer.parseInt(string);
			return id;
		}

		private void insert(String s, double a1, double a2) {
			try {
				TempTB2Bean tempTB2Bean = new TempTB2Bean(getId(s), a1, a2,
						getNow());
				mgr.addOne(tempTB2Bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private String getNow() {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-M-dd HH:mm");
			String dateString = dateFormat.format(new Date());
			return dateString;
		}

		private void whichCmd(String smsg, String index, int roomid) {
			if (smsg.length() >= 16) {
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
				} else if (rec.equals("0103FFFF0103")) {// 由于该类传感器是一个单一的wifi模块，可根据地址知道传感器类型及所属房间，type类型是非必要的
														// 24可任意写成其他
					if (roomid == 114 || roomid == 115 || roomid == 116
							|| roomid == 117 || roomid == 125 || roomid == 86
							|| roomid == 87 || roomid == 88 || roomid == 118) {
						String val1 = smsg.substring(12, 16);
						double intval1 = Integer.parseInt(val1, 16);
						DecimalFormat df = new DecimalFormat(".00");
						showVal(df.format(intval1 / 1000), 24, roomid);
					} else if (roomid == 121 || roomid == 122
							|| roomid == 123 || roomid == 124) {
						String val1 = smsg.substring(12, 16);
						String val2 = smsg.substring(16, 20);
						DecimalFormat df = new DecimalFormat(".00");
						double intval = Integer.parseInt(val1, 16);
						double intva2 = Integer.parseInt(val2, 16);
						showVal(df.format(intval / 10) + "#"
								+ df.format(intva2 / 10), 23, roomid);
					}
				} else if (rec.equals("010323010103")) {//次卧一的温湿度地址不是FFFF,而是2301
					String val1 = smsg.substring(12, 16);
					String val2 = smsg.substring(16, 20);
					DecimalFormat df = new DecimalFormat(".00");
					double intval = Integer.parseInt(val1, 16);
					double intva2 = Integer.parseInt(val2, 16);
					showVal(df.format(intval / 10) + "#"
							+ df.format(intva2 / 10), 23, roomid);
				}
				// else if (rec.equals("010311010103")) {// co2
				// String val1 = smsg.substring(12, 16);
				// String val2 = smsg.substring(16, 20);
				// double intval = Integer.parseInt(val1, 16);
				// double intva2 = Integer.parseInt(val2, 16) / 10;
				// showVal(Double.toString(intval), 111, roomid);
				// showVal(Double.toString(intva2), 112, roomid);
				// // insert(smsg, intval, intva2);
				// } else if (rec.equals("010311020103")) {
				// String val1 = smsg.substring(12, 16);
				// String val2 = smsg.substring(16, 20);
				// double intval = Integer.parseInt(val1, 16);
				// double intva2 = Integer.parseInt(val2, 16) / 10;
				// showVal(Double.toString(intval), 121, roomid);
				// showVal(Double.toString(intva2), 122, roomid);
				// // insert(smsg, intval, intva2);
				// } else if (rec.equals("010311030103")) {
				// String val1 = smsg.substring(12, 16);
				// String val2 = smsg.substring(16, 20);
				// double intval = Integer.parseInt(val1, 16);
				// double intva2 = Integer.parseInt(val2, 16) / 10;
				// showVal(Double.toString(intval), 131, roomid);
				// showVal(Double.toString(intva2), 132, roomid);
				// // insert(smsg, intval, intva2);
				// }
				//
				// else if (rec.equals("010323" + index + "0103")) {
				// String val1 = smsg.substring(12, 16);
				// String val2 = smsg.substring(16, 20);
				// double intval = Integer.parseInt(val1, 16) / 10;
				// double intva2 = Integer.parseInt(val2, 16) / 10;
				// showVal(Double.toString(intval) + "#"
				// + Double.toString(intva2), 23, roomid);
				// // insert(smsg, intval, intva2);
				// }
			}
		}
	}

	class SendClass implements Runnable {
		private User myUser;
		private int roomId;

		byte[] check_byte = new byte[] { 01, 03, 02, 00, 00, 02 };// 数目

		public SendClass(User myUser, int roomId) {
			this.myUser = myUser;
			this.roomId = roomId;
			// this.buff = buff;
		}

		public void run() {

			switch (roomId) {
			case 80:
				SendMessage(myUser, check_byte);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < cmd_ciwo1.size(); i++) {
					SendMessage(myUser, cmd_ciwo1.get(i));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;

			case 81:
				byte[] buff8 = new byte[] { 01, 03, (byte) 0x23, (byte) 01,
						01, 03 };
				SendMessage(myUser, buff8);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				break;
			case 82:
				SendMessage(myUser, check_byte);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < cmd_keting.size(); i++) {
					SendMessage(myUser, cmd_keting.get(i));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 83:
				SendMessage(myUser, check_byte);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < cmd_ciwo2.size(); i++) {
					SendMessage(myUser, cmd_ciwo2.get(i));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 84:
				SendMessage(myUser, check_byte);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < cmd_zhuwo.size(); i++) {
					SendMessage(myUser, cmd_zhuwo.get(i));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 85:
				SendMessage(myUser, check_byte);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < cmd4.size(); i++) {
					SendMessage(myUser, cmd4.get(i));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 86:
				byte[] buff86 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff86);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 87:
				byte[] buff87 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff87);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 88:
				byte[] buff88 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff88);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 118:
				byte[] buff118 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff118);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 114:
				byte[] buff114 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff114);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 115:
				byte[] buff115 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff115);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 121:
				byte[] buff121 = new byte[] { 01, 03, (byte) 255, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff121);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 116:
				byte[] buff116 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff116);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 122:
				byte[] buff122 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff122);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 117:
				byte[] buff117 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff117);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 123:
				byte[] buff123 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff123);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 124:
				byte[] buff124 = new byte[] { 01, 03, (byte) 0xFF, (byte) 0xFF,
						01, 03 };
				SendMessage(myUser, buff124);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}

			// handler.postDelayed(this, 1000); // postDelayed(this,1000)方法安排一个
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

	private void initCmd() {
		cmd_ciwo1 = new ArrayList<byte[]>();
		cmd_ciwo2 = new ArrayList<byte[]>();
		cmd_zhuwo = new ArrayList<byte[]>();
		cmd4 = new ArrayList<byte[]>();
		cmd5 = new ArrayList<byte[]>();
		cmd_cesuo = new ArrayList<byte[]>();
		cmd7 = new ArrayList<byte[]>();
		cmd_keting = new ArrayList<byte[]>();

		// 次卧一
		byte[] buff1 = new byte[] { 01, 03, 0x22, 01, 01, 03 };// 臭氧
		byte[] buff2 = new byte[] { 01, 03, 0x18, 01, 01, 03 }; // 氨气
		// byte[] buff3 = new byte[] { 01, 03, 0x11, 01, 01, 03 };// co2
		// byte[] buff4 = new byte[] { 01, 03, 0x11, 02, 01, 03 };//
		// byte[] buff5 = new byte[] { 01, 03, 0x11, 03, 01, 03 };
		byte[] buff7 = new byte[] { 01, 03, 0x20, 01, 01, 03 };// 甲醛
		byte[] buff10 = new byte[] { 01, 03, 0x19, 01, 01, 03 };// 墙上的co2
		byte[] buff11 = new byte[] { 01, 03, 0x17, 01, 01, 03 };// 墙上的co2
		byte[] buff12 = new byte[] { 01, 03, 0x24, 01, 01, 03 };// 压差
		cmd_ciwo1.add(buff1);
		cmd_ciwo1.add(buff2);
		// cmd_ciwo1.add(buff3);
		// cmd_ciwo1.add(buff4);
		// cmd_ciwo1.add(buff5);
		cmd_ciwo1.add(buff7);
		cmd_ciwo1.add(buff10);
		cmd_ciwo1.add(buff11);
		cmd_ciwo1.add(buff12);

		// 主卧
		byte[] buff21 = new byte[] { 01, 03, 0x22, 04, 01, 03 };// 臭氧
		byte[] buff22 = new byte[] { 01, 03, 0x18, 04, 01, 03 }; // 氨气
		byte[] buff24 = new byte[] { 01, 03, 0x20, 04, 01, 03 };// 甲醛
		byte[] buff26 = new byte[] { 01, 03, 0x17, 04, 01, 03 };// 墙上的co2
		byte[] buff27 = new byte[] { 01, 03, 0x24, 04, 01, 03 };// 压差
		cmd_zhuwo.add(buff21);
		cmd_zhuwo.add(buff22);
		cmd_zhuwo.add(buff24);
		cmd_zhuwo.add(buff26);
		cmd_zhuwo.add(buff27);

		// 次卧二
		byte[] buff31 = new byte[] { 01, 03, 0x22, 03, 01, 03 };// 臭氧
		byte[] buff32 = new byte[] { 01, 03, 0x18, 03, 01, 03 }; // 氨气
		byte[] buff34 = new byte[] { 01, 03, 0x20, 03, 01, 03 };// 甲醛
		byte[] buff36 = new byte[] { 01, 03, 0x17, 03, 01, 03 };// 墙上的co2
		byte[] buff37 = new byte[] { 01, 03, 0x24, 03, 01, 03 };// 压差
		cmd_ciwo2.add(buff31);
		cmd_ciwo2.add(buff32);
		cmd_ciwo2.add(buff34);
		cmd_ciwo2.add(buff36);
		cmd_ciwo2.add(buff37);

		// 卫生间
		byte[] buff51 = new byte[] { 01, 03, 0x18, 05, 01, 03 }; // 氨气
		byte[] buff52 = new byte[] { 01, 03, 0x17, 05, 01, 03 };// 墙上的co2
		byte[] buff53 = new byte[] { 01, 03, 0x19, 05, 01, 03 }; // 氨气
		cmd4.add(buff51);
		cmd4.add(buff52);
		cmd4.add(buff53);

		// // 餐厅
		// byte[] buff41 = new byte[] { 01, 03, 0x23, 05, 01, 03 }; // 温湿度
		// byte[] buff42 = new byte[] { 01, 03, 0x17, 05, 01, 03 };// 墙上的co2
		// cmd5.add(buff41);
		// cmd5.add(buff42);

		// // 厨房
		// byte[] buff71 = new byte[] { 01, 03, 0x23, 07, 01, 03 }; // 温湿度
		// byte[] buff72 = new byte[] { 01, 03, 0x20, 07, 01, 03 };// 墙上的co2
		// byte[] buff73 = new byte[] { 01, 03, 0x17, 07, 01, 03 };// 墙上的co2
		// cmd7.add(buff71);
		// cmd7.add(buff72);
		// cmd7.add(buff73);

		// 客厅
		byte[] buff81 = new byte[] { 01, 03, 0x22, 02, 01, 03 };// 臭氧
		byte[] buff82 = new byte[] { 01, 03, 0x18, 02, 01, 03 }; // 氨气
		byte[] buff84 = new byte[] { 01, 03, 0x20, 02, 01, 03 };// 甲醛
		byte[] buff86 = new byte[] { 01, 03, 0x17, 02, 01, 03 };// 墙上的co2
		byte[] buff87 = new byte[] { 01, 03, 0x24, 02, 01, 03 };// 压差
		byte[] buff88 = new byte[] { 01, 03, 0x19, 02, 01, 03 };// 苯
		cmd_keting.add(buff81);
		cmd_keting.add(buff82);
		cmd_keting.add(buff84);
		cmd_keting.add(buff86);
		cmd_keting.add(buff87);
		cmd_keting.add(buff88);
	}
}
