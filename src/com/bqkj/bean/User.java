package com.bqkj.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class User {

	/*
	 * User类用于保存socket信息
	 * 
	 * @author 王占良
	 * 
	 * @since 11/06/2014
	 */
	public String userName;
	public String room;
	public int userName_int;
	private Socket client;
	public InputStream inputStream;
	public OutputStream outputStream;
	public Thread readthreadName;
	public Runnable sendthreadName;
	public boolean isConnecting = false;

	public User(Socket client) throws IOException {
		super();
		this.client = client;
		inputStream = client.getInputStream();
		outputStream = client.getOutputStream();
		userName = client.getRemoteSocketAddress().toString();
		userName_int = Integer.parseInt(userName.split(":")[0].trim()
				.replace('/', ' ').trim().replace(".", ":").split(":")[3]);
		room = findRealDevice(userName);
	}

	public String getUserName() {
		return userName;
	}

	public Socket getClient() {
		return client;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void close() throws IOException {
		client.close();
		inputStream.close();
		outputStream.close();
	}

	/*
	 * 
	 */
	private String findRealDevice(String remoteAddress) {
		String deviceName = "";
		switch (userName_int) {
		case 80:
			deviceName = "次卧一";
			break;
		case 81:
			deviceName = "次卧一温湿度";
			break;
		case 114:
			deviceName = "次卧一VOC";
			break;
		case 82:
			deviceName = "客厅";
			break;
		case 121:
			deviceName = "客厅温湿度";
			break;
		case 115:
			deviceName = "客厅VOC";
			break;
		case 83:
			deviceName = "次卧二";
			break;
		case 122:
			deviceName = "次卧二VOC";
			break;
		case 116:
			deviceName = "次卧二VOC";
			break;
		case 84:
			deviceName = "主卧";
			break;
		case 123:
			deviceName = "主卧温湿度";
			break;
		case 125:
			deviceName = "主卧甲醛";
			break;
		case 117:
			deviceName = "主卧ＶＯＣ";
		case 124:
			deviceName = "主卧温湿度（外）";
			break;
		case 85:
			deviceName = "卫生间";
			break;
		case 86:
			deviceName = "过道-苯";
			break;
		case 87:
			deviceName = "过道-甲醛";
			break;
		case 88:
			deviceName = "过道-氨气";
			break;
		case 118:
			deviceName = "过道-VOC";
			break;
		default:
			deviceName = "未知设备";
			break;
			
		}
		return deviceName;
	}

}
