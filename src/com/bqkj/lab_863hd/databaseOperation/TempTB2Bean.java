package com.bqkj.lab_863hd.databaseOperation;

public class TempTB2Bean {

	public int _deviceid;
	public double _value1;
	public double _value2;
	public String _time;
	
	
	public TempTB2Bean(int id, double value1,double value2,
			String opDate) {
		super();
		
		this._deviceid = id;
		this._value1 = value1;
		this._value2 = value2;
		this._time = opDate;
	}
	
}
