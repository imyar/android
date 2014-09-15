package com.bqkj.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherInfo implements Parcelable {

	String _weather_img;
	
	public String get_weather_img() {
		return _weather_img;
	}

	public void set_weather_img(String _weather_img) {
		this._weather_img = _weather_img;
	}

	String _wind;
	public String get_wind() {
		return _wind;
	}

	public void set_wind(String _wind) {
		this._wind = _wind;
	}



	String _time;

	public String get_time() {
		return _time;
	}

	public void set_time(String _time) {
		this._time = _time;
	}

	public String get_city() {
		return _city;
	}

	public void set_city(String _city) {
		this._city = _city;
	}

	public String get_city_code() {
		return _city_code;
	}

	public void set_city_code(String _city_code) {
		this._city_code = _city_code;
	}

	public String get_weather() {
		return _weather;
	}

	public void set_weather(String _weather) {
		this._weather = _weather;
	}

	public String get_weather_real() {
		return _weather_real;
	}

	public void set_weather_real(String _weather_real) {
		this._weather_real = _weather_real;
	}

	public String get_city_info() {
		return _city_info;
	}

	public void set_city_info(String _city_info) {
		this._city_info = _city_info;
	}

	public String get_aqi() {
		return _aqi;
	}

	public void set_aqi(String _aqi) {
		this._aqi = _aqi;
	}

	public String get_quality() {
		return _quality;
	}

	public void set_quality(String _quality) {
		this._quality = _quality;
	}

	public String get_level() {
		return _level;
	}

	public void set_level(String _level) {
		this._level = _level;
	}

	public String get_primary_pollutant() {
		return _primary_pollutant;
	}

	public void set_primary_pollutant(String _primary_pollutant) {
		this._primary_pollutant = _primary_pollutant;
	}

	public String get_co() {
		return _co;
	}

	public void set_co(String _co) {
		this._co = _co;
	}

	public String get_no2() {
		return _no2;
	}

	public void set_no2(String _no2) {
		this._no2 = _no2;
	}

	public String get_so2() {
		return _so2;
	}

	public void set_so2(String _so2) {
		this._so2 = _so2;
	}

	public String get_o3() {
		return _o3;
	}

	public void set_o3(String _o3) {
		this._o3 = _o3;
	}

	public String get_pm10() {
		return _pm10;
	}

	public void set_pm10(String _pm10) {
		this._pm10 = _pm10;
	}

	public String get_pm2_5() {
		return _pm2_5;
	}

	public void set_pm2_5(String _pm2_5) {
		this._pm2_5 = _pm2_5;
	}

	public String get_tempature() {
		return _tempature;
	}

	public void set_tempature(String _tempature) {
		this._tempature = _tempature;
	}

	public String get_wet() {
		return _wet;
	}

	public void set_wet(String _wet) {
		this._wet = _wet;
	}

	public String get_rwether() {
		return _rwether;
	}

	public void set_rwether(String _rwether) {
		this._rwether = _rwether;
	}

	String _city;
	String _city_code;
	String _weather;
	String _weather_real;
	String _city_info;
	String _aqi;
	String _quality;
	String _level;
	String _primary_pollutant;
	String _co;
	String _no2;
	String _so2;
	String _o3;
	String _pm10;
	String _pm2_5;

	String _tempature;
	String _wet;
	String _rwether;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(_aqi);
		dest.writeString(_city);
		dest.writeString(_city_code);
		dest.writeString(_city_info);
		dest.writeString(_co);
		dest.writeString(_level);
		dest.writeString(_no2);
		dest.writeString(_o3);
		dest.writeString(_pm10);
		dest.writeString(_pm2_5);
		dest.writeString(_primary_pollutant);
		dest.writeString(_quality);
		dest.writeString(_rwether);
		dest.writeString(_so2);
		dest.writeString(_wet);
		dest.writeString(_weather_real);
		dest.writeString(_time);
		dest.writeString(_tempature);
		dest.writeString(_wind);
		dest.writeString(_weather_img);
	}

	public static final Parcelable.Creator<WeatherInfo> CREATOR = new Creator<WeatherInfo>() {
		@Override
		public WeatherInfo[] newArray(int size) {
			return new WeatherInfo[size];
		}

		@Override
		public WeatherInfo createFromParcel(Parcel in) {
			WeatherInfo weatherInfo = new WeatherInfo();
			weatherInfo._aqi = in.readString();
			weatherInfo._city = in.readString();
			weatherInfo._city_code = in.readString();
			weatherInfo._city_info = in.readString();
			weatherInfo._co = in.readString();
			weatherInfo._level = in.readString();
			weatherInfo._no2 = in.readString();
			weatherInfo._o3 = in.readString();
			weatherInfo._pm10 = in.readString();
			weatherInfo._pm2_5 = in.readString();
			weatherInfo._primary_pollutant = in.readString();
			weatherInfo._quality = in.readString();
			weatherInfo._rwether = in.readString();
			weatherInfo._so2 = in.readString();
			weatherInfo._wet = in.readString();
			weatherInfo._weather_real = in.readString();
			weatherInfo._time = in.readString();
			weatherInfo._tempature = in.readString();
			weatherInfo._wind = in.readString();
			weatherInfo._weather_img = in.readString();
			return weatherInfo;
		}
	};

}
