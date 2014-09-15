package com.bqkj.lab_863hd;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Room5FrashAirActivity extends Activity {

	// @InjectView (R.id.umeng_fb_back) ImageView iv_back;
	@InjectView(R.id.umeng_fb_save)
	ImageView iv_save;
	@InjectView(R.id.title_txt)
	TextView title;
	// @InjectView (R.id.r2_wendu) TextView tv_wendu;
	// @InjectView (R.id.r2_jiaquan) TextView tv_jiaquan;
	// @InjectView (R.id.r2_voc) TextView rv_voc;
	// @InjectView (R.id.r2_dis1) LinearLayout dis1;
	// @InjectView (R.id.r2_dis2) LinearLayout dis2;
	// @InjectView (R.id.r2_dis3) LinearLayout dis3;

	TextView tv_wendu1;
	TextView tv_wendu2;
	TextView tv_wendu3;
	TextView tv_co21;
	TextView tv_co22;
	TextView tv_co23;
	TextView tv_title;
	LinearLayout dis1;
	LinearLayout dis2;
	LinearLayout dis3;

	public static String Rcv_ACTION_NAME = "android.intent.action.lab_863hd";
	public static String Send_ACTION_NAME = "com.bqkj863hd.service";
	private MyReceiver receiver;

	private double addX1 = -1, addY1;
	private double addX2 = -1, addY2;
	private double addX3 = -1, addY3;
	
	// achart相关声明
	private XYMultipleSeriesDataset mDataset1;
	private XYMultipleSeriesDataset mDataset2;
	private XYMultipleSeriesDataset mDataset3;
	private GraphicalView chart1;
	private GraphicalView chart2;
	private GraphicalView chart3;

	private XYMultipleSeriesDataset mDataset;
	private GraphicalView chart;
	
	private XYMultipleSeriesRenderer renderer1;
	private XYMultipleSeriesRenderer renderer2;
	private XYMultipleSeriesRenderer renderer3;
	private Context context;
	List x = new ArrayList();
	List y = new ArrayList();
	XYSeries series1[] = new XYSeries[2]; // 两条线
	XYSeries series2[] = new XYSeries[2]; // 两条线
	XYSeries series3[] = new XYSeries[2]; // 两条线
	
	String[] titles = new String[] { "二氧化碳实时值", "温度实时值"};

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room5freshair);

		ImageView iv_back = (ImageView) findViewById(R.id.umeng_fb_back);
		ImageView iv_more = (ImageView) findViewById(R.id.umeng_fb_save);
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Room5FrashAirActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});
		
		iv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Room5FrashAirActivity.this,
						Room2HistoryActivity.class);
				startActivity(intent);
			}
		});

		tv_wendu1 = (TextView) findViewById(R.id.r2f_wendu1);
		tv_wendu2 = (TextView) findViewById(R.id.r2f_wendu2);
		tv_wendu3 = (TextView) findViewById(R.id.r2f_wendu3);

		tv_co21 = (TextView) findViewById(R.id.r2f_co21);
		tv_co22 = (TextView) findViewById(R.id.r2f_co22);
		tv_co23 = (TextView) findViewById(R.id.r2f_co23);

		tv_title = (TextView) findViewById(R.id.title_txt);

		dis1 = (LinearLayout) findViewById(R.id.r2f_dis1);
		dis2 = (LinearLayout) findViewById(R.id.r2f_dis2);
		dis3 = (LinearLayout) findViewById(R.id.r2f_dis3);

		tv_title.setText("新风测试");
		x.add(new double[] { 0 });
		x.add(new double[] { 0 });
		y.add(new double[] { 0 });
		y.add(new double[] { 0 });
		// 加入坐标图显示
		addChart();
	}

	private void initachart() {
		// x.add(new double[] { 0 });
		//
		// y.add(new double[] { 0 });
		// 加入坐标图显示
		addChart();
	}

	private void addChart() {
		context = this.getApplicationContext();

		// 创建一个数据集的实例，这个数据集将被用来创建图表
		// mDataset = new XYMultipleSeriesDataset();
		mDataset1 = buildDataset(titles, x, y,series1);
		mDataset2 = buildDataset(titles, x, y,series2);
		mDataset3 = buildDataset(titles, x, y,series3);

		int[] colors1 = new int[] { Color.BLUE,Color.GREEN };
		int[] colors2 = new int[] { Color.RED,Color.GREEN };
		int[] colors3 = new int[] { Color.CYAN ,Color.GREEN };
		PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND ,PointStyle.CIRCLE};
		renderer1 = buildRenderer(colors1, styles, true);
		renderer2 = buildRenderer(colors2, styles, true);
		renderer3 = buildRenderer(colors3, styles, true);
		// 设置好图表的样式,x,y坐标轴的刻度,xy轴颜色,xy标签及标题颜色
		setChartSettings(renderer1, "第一通道", "时间间隔/s", "", 0, 100, 0, 9000,
				Color.GRAY, Color.RED);

		setChartSettings(renderer2, "第二通道", "时间间隔/s", "", 0, 100, 0, 9000,
				Color.GRAY, Color.RED);
		setChartSettings(renderer3, "第三通道", "时间间隔/s", "", 0, 100, 0, 9000,
				Color.GRAY, Color.RED);
		// 生成图表,加入布局框显示
		chart1 = ChartFactory.getLineChartView(context, mDataset1, renderer1);
		chart2 = ChartFactory.getLineChartView(context, mDataset2, renderer2);
		chart3 = ChartFactory.getLineChartView(context, mDataset3, renderer3);

		dis1.addView(chart1);

		
		dis2.addView(chart2);

		dis3.addView(chart3);
		// 更新坐标图
	}

	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List xValues, List yValues,XYSeries[] series) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		int length = titles.length; // 有几条线
		for (int i = 0; i < length; i++) {
			series[i] = new XYSeries(titles[i]); // 根据每条线的名称创建点集合
			double[] xV = (double[]) xValues.get(i); // 获取第i条线的数据
			double[] yV = (double[]) yValues.get(i);
			int seriesLength = xV.length; // 有几个点

			for (int k = 0; k < seriesLength; k++) // 每条线里有几个点
			{
				series[i].add(xV[k], yV[k]);
			}

			dataset.addSeries(series[i]); // 点集合加入到数据集中
		}

		return dataset;
	}

	private void updateChart(double point, XYSeries series, int which) {
		double addX, addY;
		switch (which) {
		case 1:
			addX=addX1;
			addY=addY1;
			chart=chart1;
			mDataset=mDataset1;
			break;

		case 2:
			addX=addX2;
			addY=addY2;
			chart=chart2;
			mDataset=mDataset2;
			break;
		case 3:
			addX=addX3;
			addY=addY3;
			chart=chart3;
			mDataset=mDataset3;
			break;

		default:
			break;
		}
		addX = 0;
		addY = point;
		int[] xv = new int[100]; // 横坐标值
		double[] yv = new double[100]; // 纵坐标值

		// 移除所有点,始终只显示50个点
		mDataset.removeSeries(series);
		int num = series.getItemCount();
		if (num > 100) {
			num = 0;
		}
		// for (int i = 0; i < num; i++) {
		// xv[i] = (int) (series.getX(i) + 1);
		// yv[i] = (double) series.getY(i);
		// }

		for (int i = 0; i < num; i++) {
			xv[i] = (int) (series.getX(i));
			yv[i] = (double) series.getY(i);
		}

		// System.out.println(Arrays.toString(yv));
		// 点集先清空，为了做成新的点集而准备
		series.clear();

		// 将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
		// 这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点

		// for (int k = 0; k < num; k++) {
		// series.add(xv[k], yv[k]);
		// }
		series.add(addX, addY);
		for (int k = 0; k < num; k++) {
			series.add(xv[k], yv[k]);
		}
		series.add(num, addY);
		// series.add(addX, addY);

		// 在数据集中添加新的点集
		mDataset.addSeries(series);

		// 视图更新，没有这一步，曲线不会呈现动态
		// 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		chart.invalidate();
	}

	protected XYMultipleSeriesRenderer buildRenderer(int colors[],
			PointStyle style[], boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer = new XYMultipleSeriesRenderer();
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(style[i]);
			r.setFillPoints(fill);
			r.setLineWidth(3);
			renderer.addSeriesRenderer(r);
		}

		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		// 有关对图表的渲染可参看api文档
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor); // xy轴的颜色
		renderer.setShowGrid(true); // 显示网格CC33CC
		renderer.setGridColor(Color.rgb(0xCC, 0x33, 0xCC)); // 网格颜色
		renderer.setApplyBackgroundColor(true); // 不使用默认背景色
		renderer.setBackgroundColor(Color.WHITE); // 设置图表背景色
		renderer.setMarginsColor(Color.BLACK); // 设置空白区颜色
		renderer.setLabelsColor(Color.YELLOW); // xy轴标签名字的颜色
		renderer.setXLabels(30); // 设置合适的刻度,在轴上显示的数量是 MAX / labels
		renderer.setYLabels(30); // 纵坐标标签个数
		renderer.setLabelsTextSize(10); // 设置刻度显示文字的大小(XY轴都会被设置)
		renderer.setAxisTitleTextSize(16); // 设置坐标轴标签字体大小
		renderer.setChartTitleTextSize(20); // 标题字体大小

		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float) 2);
		renderer.setShowLegend(true); // 是否显示图例
	}

	protected XYMultipleSeriesRenderer buildRenderer(int color,
			PointStyle style, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		// 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(color);
		r.setPointStyle(style);
		r.setFillPoints(fill);
		r.setLineWidth(1);
		renderer.addSeriesRenderer(r);
		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String xTitle, String yTitle, double xMin, double xMax,
			double yMin, double yMax, int axesColor, int labelsColor,
			String title) {
		// 有关对图表的渲染可参看api文档
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.GREEN);
		renderer.setXLabels(2);
		renderer.setYLabels(10);
		// renderer.setXTitle("time");
		// renderer.setYTitle("KN");
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float) 2);
        renderer.setZoomEnabled(true, true);
        renderer.setZoomButtonsVisible(true);
		renderer.setShowLegend(true);
	}

	public void sendCmd(byte[] value) {
		Intent intent = new Intent();// 创建Intent对象
		intent.setAction("android.intent.action.cmd");
		intent.putExtra("cmd", CMD_SEND_DATA);
		intent.putExtra("value", value);
		sendBroadcast(intent);// 发送广播
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (receiver != null) {
			Room5FrashAirActivity.this.unregisterReceiver(receiver);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Rcv_ACTION_NAME);
		Room5FrashAirActivity.this.registerReceiver(receiver, filter);
	}

	public void showToast(String str) {// 显示提示信息
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Rcv_ACTION_NAME)) {
				Bundle bundle = intent.getExtras();
				int cmd = bundle.getInt("cmd");

				if (cmd == CMD_SHOW_TOAST) {
					String str = bundle.getString("str");
					showToast(str);
				} else if (cmd == CMD_SHOW_Val) {
					int type = bundle.getInt("type");
					double val = bundle.getDouble("val");
					switch (type) {
					case 111:
						tv_co21.setText(Double.toString(val) + " mg/m3");
						updateChart(val, series1[0], 1);
						break;
					case 112:
						tv_wendu1.setText(Double.toString(val) + " ℃");
						updateChart(val, series1[1], 1);
						break;
					case 121:
						tv_co22.setText(Double.toString(val) + " mg/m3");
						updateChart(val, series2[0], 2);
						break;
					case 122:
						tv_wendu2.setText(Double.toString(val) + " ℃");
						updateChart(val, series2[1], 2);
						break;
					case 131:
						tv_co23.setText(Double.toString(val) + " mg/m3");
						updateChart(val, series3[0], 3);
						
						break;
					case 132:
						tv_wendu3.setText(Double.toString(val) + " ℃");
						updateChart(val, series3[1], 3);
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
}
