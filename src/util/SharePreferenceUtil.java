package util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context) {
		sp = context.getSharedPreferences("qbjhBTAdata", context.MODE_WORLD_READABLE+context.MODE_WORLD_WRITEABLE);
		editor = sp.edit();
	}

	// �����豸��ַ
	public void setBTAddress(String address) {
		editor.putString("address", address);
		editor.commit();
	}

	public String getBTAddress() {
		return sp.getString("address", "");
	}
	
	// �Ƿ��һ������
	public void setIsFirst(boolean isFirst) {
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public boolean getisFirst() {
		return sp.getBoolean("isFirst", true);
	}
}
