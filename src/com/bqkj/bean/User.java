package com.bqkj.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class User {

	/*
	 * User�����ڱ���socket��Ϣ
	 * 
	 * @author ��ռ��
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
			deviceName = "����һ";
			break;
		case 81:
			deviceName = "����һ��ʪ��";
			break;
		case 114:
			deviceName = "����һVOC";
			break;
		case 82:
			deviceName = "����";
			break;
		case 121:
			deviceName = "������ʪ��";
			break;
		case 115:
			deviceName = "����VOC";
			break;
		case 83:
			deviceName = "���Զ�";
			break;
		case 122:
			deviceName = "���Զ�VOC";
			break;
		case 116:
			deviceName = "���Զ�VOC";
			break;
		case 84:
			deviceName = "����";
			break;
		case 123:
			deviceName = "������ʪ��";
			break;
		case 125:
			deviceName = "���Լ�ȩ";
			break;
		case 117:
			deviceName = "���ԣ֣ϣ�";
		case 124:
			deviceName = "������ʪ�ȣ��⣩";
			break;
		case 85:
			deviceName = "������";
			break;
		case 86:
			deviceName = "����-��";
			break;
		case 87:
			deviceName = "����-��ȩ";
			break;
		case 88:
			deviceName = "����-����";
			break;
		case 118:
			deviceName = "����-VOC";
			break;
		default:
			deviceName = "δ֪�豸";
			break;
			
		}
		return deviceName;
	}

}
