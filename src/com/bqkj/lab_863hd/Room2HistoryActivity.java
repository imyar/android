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
import util.DialogFactory;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bqkj.lab_863hd.databaseOperation.DBManager;
import com.view.WheelView.ArrayWheelAdapter;
import com.view.WheelView.WheelView;

public class Room2HistoryActivity extends Activity implements OnClickListener {

	// @InjectView (R.id.umeng_fb_back) ImageView iv_back;
	@InjectView(R.id.umeng_fb_save)
	ImageView iv_save;
	@InjectView(R.id.title_txt)
	TextView title;

	LinearLayout dis1;
	LinearLayout dis2;
	LinearLayout dis3;
	TextView tv_startdate1;
	TextView tv_enddate1;
	TextView tv_startdate2;
	TextView tv_enddate2;
	TextView tv_startdate3;
	TextView tv_enddate3;

	Button bt_sureButton1;
	Button bt_sureButton2;
	Button bt_sureButton3;
	Button bt_sureButton4;
	Button bt_sureButton5;
	Button bt_sureButton6;

	private WheelView wv_y1 = null;
	private WheelView wv_m1 = null;
	private WheelView wv_d1 = null;

	private WheelView wv_y2 = null;
	private WheelView wv_m2 = null;
	private WheelView wv_d2 = null;

	private WheelView wv_y3 = null;
	private WheelView wv_m3 = null;
	private WheelView wv_d3 = null;

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

	String[] titles = new String[] { "二氧化碳历史值", "温度历史值" };

	String[] year;
	String[] month;
	String[] day;

	double[][] result1;
	double[][] result2;
	double[][] result3;
	DBManager mgr;
	private Dialog mDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room2history);
		findView();
		initiWheelView();
		ImageView iv_back = (ImageView) findViewById(R.id.umeng_fb_back);
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Room2HistoryActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});

		dis1 = (LinearLayout) findViewById(R.id.r2h_dis1);
		dis2 = (LinearLayout) findViewById(R.id.r2h_dis2);
		dis3 = (LinearLayout) findViewById(R.id.r2h_dis3);

		x.add(new double[] { 0 });
		x.add(new double[] { 0 });
		y.add(new double[] { 0 });
		y.add(new double[] { 0 });
		// 加入坐标图显示
		addChart();
	}

	private void showRequestDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, " 查找数据中...");
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
	}

	private void addChart() {
		context = this.getApplicationContext();
		mgr = new DBManager(context);
		// 创建一个数据集的实例，这个数据集将被用来创建图表
		// mDataset = new XYMultipleSeriesDataset();
		mDataset1 = buildDataset(titles, x, y, series1);
		mDataset2 = buildDataset(titles, x, y, series2);
		mDataset3 = buildDataset(titles, x, y, series3);

		int[] colors1 = new int[] { Color.BLUE, Color.GREEN };
		int[] colors2 = new int[] { Color.RED, Color.GREEN };
		int[] colors3 = new int[] { Color.CYAN, Color.GREEN };

		PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND,
				PointStyle.CIRCLE };
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

		chart2 = ChartFactory.getLineChartView(context, mDataset2, renderer2);
		dis2.addView(chart2);

		chart3 = ChartFactory.getLineChartView(context, mDataset3, renderer3);
		dis3.addView(chart3);
		// 更新坐标图
	}

	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List xValues, List yValues, XYSeries[] series) {
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
		renderer.setXLabels(20); // 设置合适的刻度,在轴上显示的数量是 MAX / labels
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
		renderer.setShowLegend(false);
	}

	public void showToast(String str) {// 显示提示信息
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 初始化控件和UI视图
	 */
	private void findView() {
		wv_y1 = (WheelView) findViewById(R.id.r2h_y1);
		wv_m1 = (WheelView) findViewById(R.id.r2h_m1);
		wv_d1 = (WheelView) findViewById(R.id.r2h_d1);

		wv_y2 = (WheelView) findViewById(R.id.r2h_y2);
		wv_m2 = (WheelView) findViewById(R.id.r2h_m2);
		wv_d2 = (WheelView) findViewById(R.id.r2h_d2);

		wv_y3 = (WheelView) findViewById(R.id.r2h_y3);
		wv_m3 = (WheelView) findViewById(R.id.r2h_m3);
		wv_d3 = (WheelView) findViewById(R.id.r2h_d3);

		tv_startdate1 = (TextView) findViewById(R.id.r2h_txt_startdate1);
		tv_enddate1 = (TextView) findViewById(R.id.r2h_txt_enddate1);

		tv_startdate2 = (TextView) findViewById(R.id.r2h_txt_startdate2);
		tv_enddate2 = (TextView) findViewById(R.id.r2h_txt_enddate2);

		tv_startdate3 = (TextView) findViewById(R.id.r2h_txt_startdate3);
		tv_enddate3 = (TextView) findViewById(R.id.r2h_txt_enddate3);

		bt_sureButton1 = (Button) findViewById(R.id.r2h_wh_sure1);
		bt_sureButton2 = (Button) findViewById(R.id.r2h_wh_sure2);

		bt_sureButton3 = (Button) findViewById(R.id.r2h_wh_sure3);
		bt_sureButton4 = (Button) findViewById(R.id.r2h_wh_sure4);

		bt_sureButton5 = (Button) findViewById(R.id.r2h_wh_sure5);
		bt_sureButton6 = (Button) findViewById(R.id.r2h_wh_sure6);

		bt_sureButton1.setOnClickListener(this);
		bt_sureButton2.setOnClickListener(this);
		bt_sureButton3.setOnClickListener(this);
		bt_sureButton4.setOnClickListener(this);
		bt_sureButton5.setOnClickListener(this);
		bt_sureButton6.setOnClickListener(this);
	}

	private void initiWheelView() {

		year = getResources().getStringArray(R.array.year_array);
		month = getResources().getStringArray(R.array.month_array);
		day = getResources().getStringArray(R.array.day_array);

		wv_y1.setAdapter(new ArrayWheelAdapter<String>(year));
		wv_y1.setVisibleItems(7);
		wv_y1.setCyclic(true);
		wv_y1.setCurrentItem(0);

		wv_m1.setAdapter(new ArrayWheelAdapter<String>(month));
		wv_m1.setVisibleItems(7);
		wv_m1.setCyclic(true);
		wv_m1.setCurrentItem(0);

		wv_d1.setAdapter(new ArrayWheelAdapter<String>(day));
		wv_d1.setVisibleItems(7);
		wv_d1.setCyclic(true);
		wv_d1.setCurrentItem(05);

		wv_y2.setAdapter(new ArrayWheelAdapter<String>(year));
		wv_y2.setVisibleItems(7);
		wv_y2.setCyclic(true);
		wv_y2.setCurrentItem(0);

		wv_m2.setAdapter(new ArrayWheelAdapter<String>(month));
		wv_m2.setVisibleItems(7);
		wv_m2.setCyclic(true);
		wv_m2.setCurrentItem(0);

		wv_d2.setAdapter(new ArrayWheelAdapter<String>(day));
		wv_d2.setVisibleItems(7);
		wv_d2.setCyclic(true);
		wv_d2.setCurrentItem(05);

		wv_y3.setAdapter(new ArrayWheelAdapter<String>(year));
		wv_y3.setVisibleItems(7);
		wv_y3.setCyclic(true);
		wv_y3.setCurrentItem(0);

		wv_m3.setAdapter(new ArrayWheelAdapter<String>(month));
		wv_m3.setVisibleItems(7);
		wv_m3.setCyclic(true);
		wv_m3.setCurrentItem(0);

		wv_d3.setAdapter(new ArrayWheelAdapter<String>(day));
		wv_d3.setVisibleItems(7);
		wv_d3.setCyclic(true);
		wv_d3.setCurrentItem(05);
	}

	private int getWheelValue(int id) {
		return getWheel(id).getCurrentItem();
	}

	/**
	 * Returns wheel by Id
	 * 
	 * @param id
	 *            the wheel Id
	 * @return the wheel with passed Id 鎴戞坊鍔犵殑
	 * 
	 */
	private WheelView getWheel(int id) {
		return (WheelView) findViewById(id);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mDialog.dismiss();
			switch (msg.what) {
			case 1:
				if (!result1.equals(null)) {
					double[] r1 = new double[result1.length/2];
					double[] r2 = new double[result1.length/2];
					for (int j = 0; j < result1.length; j++) {
						r1[j] = result1[0][j];
						r2[j] = result1[1][j];
					}

					updateChart(r1, series1[0], 1);
					updateChart(r2, series1[1], 1);
				} else {
					showToast("未查到数据");
				}

				break;
			case 2:

				break;
			case 3:

				break;

			default:
				break;
			}
		}
	};

	private void updateChart(double[] d, XYSeries series, int which) {
		switch (which) {
		case 1:
			chart = chart1;
			mDataset = mDataset1;
			break;

		case 2:
			chart = chart2;
			mDataset = mDataset2;
			break;
		case 3:
			chart = chart3;
			mDataset = mDataset3;
			break;

		default:
			break;
		}

		// 移除所有点,始终只显示50个点
		mDataset.removeSeries(series);

		series.clear();

		series.clear();

		for (int i = 0; i < d.length; i++) {
			series.add(i, d[i]);
		}

		mDataset.addSeries(series);
		// 视图更新，没有这一步，曲线不会呈现动态
		// 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		chart.invalidate();
	}

	@Override
	public void onClick(View v) {
		String y1 = year[getWheelValue(R.id.r2h_y1)] + "-"
				+ month[getWheelValue(R.id.r2h_m1)] + "-"
				+ day[getWheelValue(R.id.r2h_d1)];
		String y2 = year[getWheelValue(R.id.r2h_y1)] + "-"
				+ month[getWheelValue(R.id.r2h_m1)] + "-"
				+ day[getWheelValue(R.id.r2h_d1)];
		String y3 = year[getWheelValue(R.id.r2h_y2)] + "-"
				+ month[getWheelValue(R.id.r2h_m2)] + "-"
				+ day[getWheelValue(R.id.r2h_d2)];
		String y4 = year[getWheelValue(R.id.r2h_y2)] + "-"
				+ month[getWheelValue(R.id.r2h_m2)] + "-"
				+ day[getWheelValue(R.id.r2h_d2)];
		String y5 = year[getWheelValue(R.id.r2h_y3)] + "-"
				+ month[getWheelValue(R.id.r2h_m3)] + "-"
				+ day[getWheelValue(R.id.r2h_d3)];
		String y6 = year[getWheelValue(R.id.r2h_y3)] + "-"
				+ month[getWheelValue(R.id.r2h_m3)] + "-"
				+ day[getWheelValue(R.id.r2h_d3)];
		switch (v.getId()) {

		case R.id.r2h_wh_sure1:

			tv_startdate1.setText(y1);
			break;
		case R.id.r2h_wh_sure2:
			tv_enddate1.setText(y2);

			showRequestDialog();
			new Thread() {
				public void run() {
					result1 = mgr.queryTheCursor(tv_startdate1.getText()
							.toString(), tv_enddate1.getText().toString());
				}
			}.start();

			handler.sendEmptyMessage(1);
			break;
		case R.id.r2h_wh_sure3:
			tv_startdate2.setText(y3);

			break;
		case R.id.r2h_wh_sure4:

			tv_enddate2.setText(y4);
			showRequestDialog();
			new Thread() {
				public void run() {
					result2 = mgr.queryTheCursor(tv_startdate2.getText()
							.toString(), tv_enddate2.getText().toString());
				}
			}.start();

			handler.sendEmptyMessage(2);
			break;
		case R.id.r2h_wh_sure5:

			tv_startdate3.setText(y5);
			break;
		case R.id.r2h_wh_sure6:

			tv_enddate3.setText(y6);
			showRequestDialog();
			new Thread() {
				public void run() {
					result3 = mgr.queryTheCursor(tv_startdate3.getText()
							.toString(), tv_enddate3.getText().toString());
				}
			}.start();

			handler.sendEmptyMessage(3);
		default:
			break;
		}
	}

	// class QueryDBRunnable implements Runnable {
	// private String startdate;
	// private String enddate;
	//
	// public QueryDBRunnable(String s, String e) {
	// this.startdate = s;
	// this.enddate = e;
	// // this.buff = buff;
	// }
	//
	// public void run() {
	// mgr.queryTheCursor(startdate, enddate);
	// }
	// }
}
