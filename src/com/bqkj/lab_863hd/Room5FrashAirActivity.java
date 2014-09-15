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
	
	// achart�������
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
	XYSeries series1[] = new XYSeries[2]; // ������
	XYSeries series2[] = new XYSeries[2]; // ������
	XYSeries series3[] = new XYSeries[2]; // ������
	
	String[] titles = new String[] { "������̼ʵʱֵ", "�¶�ʵʱֵ"};

	/************** service ���� *********/
	static final int CMD_STOP_SERVICE = 0x01;
	static final int CMD_SEND_DATA = 0x02;
	static final int CMD_SYSTEM_EXIT = 0x03;
	static final int CMD_SHOW_TOAST = 0x04;
	static final int CMD_Service_Started = 0x05;// ��������ʾtoast
	static final int CMD_SEND_Address = 0x06;// ��������ʾtoast
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

		tv_title.setText("�·����");
		x.add(new double[] { 0 });
		x.add(new double[] { 0 });
		y.add(new double[] { 0 });
		y.add(new double[] { 0 });
		// ��������ͼ��ʾ
		addChart();
	}

	private void initachart() {
		// x.add(new double[] { 0 });
		//
		// y.add(new double[] { 0 });
		// ��������ͼ��ʾ
		addChart();
	}

	private void addChart() {
		context = this.getApplicationContext();

		// ����һ�����ݼ���ʵ����������ݼ�������������ͼ��
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
		// ���ú�ͼ�����ʽ,x,y������Ŀ̶�,xy����ɫ,xy��ǩ��������ɫ
		setChartSettings(renderer1, "��һͨ��", "ʱ����/s", "", 0, 100, 0, 9000,
				Color.GRAY, Color.RED);

		setChartSettings(renderer2, "�ڶ�ͨ��", "ʱ����/s", "", 0, 100, 0, 9000,
				Color.GRAY, Color.RED);
		setChartSettings(renderer3, "����ͨ��", "ʱ����/s", "", 0, 100, 0, 9000,
				Color.GRAY, Color.RED);
		// ����ͼ��,���벼�ֿ���ʾ
		chart1 = ChartFactory.getLineChartView(context, mDataset1, renderer1);
		chart2 = ChartFactory.getLineChartView(context, mDataset2, renderer2);
		chart3 = ChartFactory.getLineChartView(context, mDataset3, renderer3);

		dis1.addView(chart1);

		
		dis2.addView(chart2);

		dis3.addView(chart3);
		// ��������ͼ
	}

	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List xValues, List yValues,XYSeries[] series) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		int length = titles.length; // �м�����
		for (int i = 0; i < length; i++) {
			series[i] = new XYSeries(titles[i]); // ����ÿ���ߵ����ƴ����㼯��
			double[] xV = (double[]) xValues.get(i); // ��ȡ��i���ߵ�����
			double[] yV = (double[]) yValues.get(i);
			int seriesLength = xV.length; // �м�����

			for (int k = 0; k < seriesLength; k++) // ÿ�������м�����
			{
				series[i].add(xV[k], yV[k]);
			}

			dataset.addSeries(series[i]); // �㼯�ϼ��뵽���ݼ���
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
		int[] xv = new int[100]; // ������ֵ
		double[] yv = new double[100]; // ������ֵ

		// �Ƴ����е�,ʼ��ֻ��ʾ50����
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
		// �㼯����գ�Ϊ�������µĵ㼯��׼��
		series.clear();

		// ���²����ĵ����ȼ��뵽�㼯�У�Ȼ����ѭ�����н�����任���һϵ�е㶼���¼��뵽�㼯��
		// �����������һ�°�˳��ߵ�������ʲôЧ������������ѭ���壬������²����ĵ�

		// for (int k = 0; k < num; k++) {
		// series.add(xv[k], yv[k]);
		// }
		series.add(addX, addY);
		for (int k = 0; k < num; k++) {
			series.add(xv[k], yv[k]);
		}
		series.add(num, addY);
		// series.add(addX, addY);

		// �����ݼ�������µĵ㼯
		mDataset.addSeries(series);

		// ��ͼ���£�û����һ�������߲�����ֶ�̬
		// ����ڷ�UI���߳��У���Ҫ����postInvalidate()������ο�api
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
		// �йض�ͼ�����Ⱦ�ɲο�api�ĵ�
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor); // xy�����ɫ
		renderer.setShowGrid(true); // ��ʾ����CC33CC
		renderer.setGridColor(Color.rgb(0xCC, 0x33, 0xCC)); // ������ɫ
		renderer.setApplyBackgroundColor(true); // ��ʹ��Ĭ�ϱ���ɫ
		renderer.setBackgroundColor(Color.WHITE); // ����ͼ����ɫ
		renderer.setMarginsColor(Color.BLACK); // ���ÿհ�����ɫ
		renderer.setLabelsColor(Color.YELLOW); // xy���ǩ���ֵ���ɫ
		renderer.setXLabels(30); // ���ú��ʵĿ̶�,��������ʾ�������� MAX / labels
		renderer.setYLabels(30); // �������ǩ����
		renderer.setLabelsTextSize(10); // ���ÿ̶���ʾ���ֵĴ�С(XY�ᶼ�ᱻ����)
		renderer.setAxisTitleTextSize(16); // �����������ǩ�����С
		renderer.setChartTitleTextSize(20); // ���������С

		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float) 2);
		renderer.setShowLegend(true); // �Ƿ���ʾͼ��
	}

	protected XYMultipleSeriesRenderer buildRenderer(int color,
			PointStyle style, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		// ����ͼ�������߱������ʽ��������ɫ����Ĵ�С�Լ��ߵĴ�ϸ��
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
		// �йض�ͼ�����Ⱦ�ɲο�api�ĵ�
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
		Intent intent = new Intent();// ����Intent����
		intent.setAction("android.intent.action.cmd");
		intent.putExtra("cmd", CMD_SEND_DATA);
		intent.putExtra("value", value);
		sendBroadcast(intent);// ���͹㲥
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

	public void showToast(String str) {// ��ʾ��ʾ��Ϣ
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
						tv_wendu1.setText(Double.toString(val) + " ��");
						updateChart(val, series1[1], 1);
						break;
					case 121:
						tv_co22.setText(Double.toString(val) + " mg/m3");
						updateChart(val, series2[0], 2);
						break;
					case 122:
						tv_wendu2.setText(Double.toString(val) + " ��");
						updateChart(val, series2[1], 2);
						break;
					case 131:
						tv_co23.setText(Double.toString(val) + " mg/m3");
						updateChart(val, series3[0], 3);
						
						break;
					case 132:
						tv_wendu3.setText(Double.toString(val) + " ��");
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
						showToast("���������ɹ�");

					} else if (val == 0) {
						showToast("��������ʧ��");
					}
				}

			}

		}
	}
}
