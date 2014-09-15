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

public class Room4Activity extends Activity {

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

	TextView tv_wendu;
	TextView tv_jiaquan;
	TextView tv_voc;

	LinearLayout dis1;
	LinearLayout dis2;
	LinearLayout dis3;
	
	public static String Rcv_ACTION_NAME = "android.intent.action.lab_863hd";
	public static String Send_ACTION_NAME = "com.bqkj863hd.service";
	private MyReceiver receiver;
	
	// achart�������
	private XYMultipleSeriesDataset mDataset1;
	private XYMultipleSeriesDataset mDataset2;
	private XYMultipleSeriesDataset mDataset3;
	private GraphicalView chart1;
	private GraphicalView chart2;
	private GraphicalView chart3;

	private XYMultipleSeriesRenderer renderer1;
	private XYMultipleSeriesRenderer renderer2;
	private XYMultipleSeriesRenderer renderer3;
	private Context context;
	List<double[]> x = new ArrayList<double[]>();
	List<double[]> y = new ArrayList<double[]>();
	XYSeries series[] = new XYSeries[1]; // ������
	String[] titles = new String[] { "ʵʱֵ" };

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
	static final int UPDATE_WEATHER = 0x10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room4);
		ImageView iv_back = (ImageView) findViewById(R.id.umeng_fb_back);
		title.setText("����һ����");
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Room4Activity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});

		tv_jiaquan = (TextView) findViewById(R.id.r2_jiaquan);
		tv_voc = (TextView) findViewById(R.id.r2_voc);
		tv_wendu = (TextView) findViewById(R.id.r2_wendu);
		dis1 = (LinearLayout) findViewById(R.id.r2_dis1);
		dis2 = (LinearLayout) findViewById(R.id.r2_dis2);
		dis3 = (LinearLayout) findViewById(R.id.r2_dis3);

		x.add(new double[] { 0 });

		y.add(new double[] { 0 });
		// ��������ͼ��ʾ
		addChart();
	}


	private void addChart() {
		context = this.getApplicationContext();

		// ����һ�����ݼ���ʵ����������ݼ�������������ͼ��
		// mDataset = new XYMultipleSeriesDataset();
		mDataset1 = buildDataset(titles, x, y);
		mDataset2 = buildDataset(titles, x, y);
		mDataset3 = buildDataset(titles, x, y);

		int[] colors = new int[] { Color.BLUE };
		PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND };
		renderer1 = buildRenderer(colors, styles, true);
		renderer2 = buildRenderer(colors, styles, true);
		renderer3 = buildRenderer(colors, styles, true);
		// ���ú�ͼ�����ʽ,x,y������Ŀ̶�,xy����ɫ,xy��ǩ��������ɫ
		setChartSettings(renderer1, "��һ����ʾֵ", "ʱ����/s", "P", 0, 20, 0, 30,
				Color.GRAY, Color.RED);

		setChartSettings(renderer2, "��һ����ʾֵ", "ʱ����/s", "P", 0, 20, 0, 30,
				Color.GRAY, Color.RED);
		setChartSettings(renderer3, "��һ����ʾֵ", "ʱ����/s", "P", 0, 20, 0, 30,
				Color.GRAY, Color.RED);
		// ����ͼ��,���벼�ֿ���ʾ
		chart1 = ChartFactory.getLineChartView(context, mDataset1, renderer1);
		chart2 = ChartFactory.getLineChartView(context, mDataset2, renderer2);
		chart3 = ChartFactory.getLineChartView(context, mDataset3, renderer3);

		dis1.addView(chart1);

		chart2 = ChartFactory.getLineChartView(context, mDataset2, renderer2);
		dis2.addView(chart2);

		chart3 = ChartFactory.getLineChartView(context, mDataset3, renderer3);
		dis3.addView(chart3);
		// ��������ͼ
	}

	protected XYMultipleSeriesDataset buildDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
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
		renderer.setXLabels(20); // ���ú��ʵĿ̶�,��������ʾ�������� MAX / labels
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
		renderer.setXTitle("time");
		renderer.setYTitle("KN");
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float) 2);

		renderer.setShowLegend(false);
	}

	public void sendCmd(byte[] value) {
		Intent intent = new Intent();// ����Intent����
		intent.setAction("android.intent.action.cmd");
		intent.putExtra("cmd", CMD_SEND_DATA);
		intent.putExtra("value", value);
		sendBroadcast(intent);// ���͹㲥
	}

	// ���������߳�
	Runnable sendThread = new Runnable() {

		@Override
		public void run() {
			sendCmd(new byte[] { 01, 03, 02, 00, 00, 02 });
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			sendCmd(new byte[] { 01, 03, 02, 00, 00, 02 });
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			sendCmd(new byte[] { 01, 03, 02, 00, 00, 02 });
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.lxx");
		Room4Activity.this.registerReceiver(receiver, filter);
	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("android.intent.action.lxx")) {
				Bundle bundle = intent.getExtras();
				int cmd = bundle.getInt("cmd");

				if (cmd == CONNECT_FAILD) {
					// imageView.setImageResource(R.drawable.connect_red);
					// Utils.alertDialog(MainActivity.this, "�뵥Ƭ������ͨ�ŶϿ���������")
				}
				if (cmd == CMD_SHOW_Val) {
					// imageView.setImageResource(R.drawable.connect_green);
					String str = bundle.getString("str");
					if (str.length() > 12) {
						
					}
					else if (cmd == CMD_SYSTEM_EXIT) {
						System.exit(0);

					}
				}
			}
		}
	}
}
