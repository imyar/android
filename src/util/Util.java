package util;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;

import com.bqkj.bean.WeatherInfo;

/*
 * 实用工具类
 * by 王占良
 * @date 28/05/2014
 */
public class Util {

	// ASCII字符串转化为十六进制字节数组
	public static byte[] convert2HexArray(String apdu) {
		int len = apdu.length() / 2;
		char[] chars = apdu.toCharArray();
		String[] hexes = new String[len];
		byte[] bytes = new byte[len];
		for (int i = 0, j = 0; j < len; i = i + 2, j++) {
			hexes[j] = "" + chars[i] + chars[i + 1];
			bytes[j] = (byte) Integer.parseInt(hexes[j], 16);
		}
		return bytes;
	}

	// 十六进制字节数组转换为ASCII字符串，指定元素个数
	public static String bytes2HexString(byte[] b, int count) {
		String ret = "";
		for (int i = 0; i < count; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}
	/*
	 * 解析单个数据的Json by 王占良
	 * 
	 * @date 28/05/2014
	 */
	public static WeatherInfo parseJsonMulti(String strResult) {
		DateUtil dateUtil=new DateUtil();
		try {
			JSONObject jsonObj = new JSONObject(strResult);
			String _time = jsonObj.getString("time");
			String _city = jsonObj.getString("city");
			String _city_code = jsonObj.getString("city_code");
			String _weather = jsonObj.getString("weather");
			String _weather_real = jsonObj.getString("weather_real");
			String _city_info = jsonObj.getString("city_info");
			String _aqi = jsonObj.getString("aqi");
			String _quality = jsonObj.getString("quality");
			String _level = jsonObj.getString("level");
			String _primary_pollutant = jsonObj.getString("primary_pollutant");
			String _co = jsonObj.getString("co");
			String _no2 = jsonObj.getString("no2");
			String _so2 = jsonObj.getString("so2");
			String _o3 = jsonObj.getString("o3");
			String _pm10 = jsonObj.getString("pm10");
			String _pm2_5 = jsonObj.getString("pm2_5");
            String _weather_img= jsonObj.getString("weather_img");
			String[] w = _weather_real.split("；");
			String _tempature = w[0].substring(10, w[0].length() - 1);
			String _wet = w[2].substring(3, w[2].length() - 1);
			String _wind =  w[1].substring(6, w[1].length());
			String[] weatString = _weather.split(" ");
			String _rwether = weatString[1];
			
			
			WeatherInfo weatherInfo = new WeatherInfo();
			weatherInfo.set_aqi(_aqi);
			weatherInfo.set_city(_city);
			weatherInfo.set_city_code(_city_code);
			weatherInfo.set_city_info(_city_info);
			weatherInfo.set_co(_co);
			weatherInfo.set_level(_level);
			weatherInfo.set_no2(_no2);
			weatherInfo.set_o3(_o3);
			weatherInfo.set_pm10(_pm10);
			weatherInfo.set_pm2_5(_pm2_5);
			weatherInfo.set_primary_pollutant(_primary_pollutant);
			weatherInfo.set_quality(_quality);
			weatherInfo.set_rwether(_rwether);
			weatherInfo.set_so2(_so2);
			weatherInfo.set_wet(_wet);
			weatherInfo.set_weather_real(_rwether);
			weatherInfo.set_weather(_weather_real);
			weatherInfo.set_time(_time);
			weatherInfo.set_tempature(_tempature);
         
            weatherInfo.set_wind(_wind);
            weatherInfo.set_weather_img(_weather_img);
			return weatherInfo;
			// tv.setText(gender);
		} catch (JSONException e) {
			System.out.println("Jsons parse error !");
			e.printStackTrace();
			return null;
		}

	}

}
