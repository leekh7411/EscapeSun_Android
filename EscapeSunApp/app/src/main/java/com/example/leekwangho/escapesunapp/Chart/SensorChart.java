package com.example.leekwangho.escapesunapp.Chart;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.leekwangho.escapesunapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by LeeKwangho on 2017-09-24.
 */

public class SensorChart {
    public LineChart chart = null;
    private ArrayList<Entry> data_Temperature = new ArrayList<>();
    private ArrayList<Entry> data_BodyHeat= new ArrayList<>();
    //private ArrayList<Entry> data_HeartScan= new ArrayList<>();
    private ArrayList<Entry> data_Humidity= new ArrayList<>();
    private ArrayList<Entry> data_Distance= new ArrayList<>();
    private LineDataSet setTempComp,setBodyComp,setHumiComp,setDistComp;
    private ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    private ArrayList<String> xVals = new ArrayList<>();
    private LineData data;
    private LinearLayout backLayout;
    private Activity mActivty;
    public SensorChart(Activity activity,int chart_ID,int chart_Layout_ID){
        mActivty = activity;
        chart = activity.findViewById(chart_ID);
        chart.setDrawGridBackground(false);
        if(chart_Layout_ID!=-1)backLayout = activity.findViewById(chart_Layout_ID);
        setTempComp = new LineDataSet(data_Temperature,"외부온도");
        setTempComp.setAxisDependency(YAxis.AxisDependency.LEFT);
        setTempComp.setColor(ColorTemplate.rgb("#33b5e5"));
        setTempComp.setCircleColor(ColorTemplate.rgb("#33b5e5"));
        setBodyComp = new LineDataSet(data_BodyHeat,"체온");
        setBodyComp.setAxisDependency(YAxis.AxisDependency.LEFT);
        setBodyComp.setColor(ColorTemplate.rgb("#ff8800"));
        setBodyComp.setCircleColor(ColorTemplate.rgb("#ff8800"));
        //setHeartComp = new LineDataSet(data_HeartScan,"심박수");
        //setHeartComp.setAxisDependency(YAxis.AxisDependency.LEFT);
        //setHeartComp.setColor(ColorTemplate.rgb("#FF9C9C"));
        //setHeartComp.setCircleColor(ColorTemplate.rgb("#FF9C9C"));
        setHumiComp = new LineDataSet(data_Humidity,"외부습도");
        setHumiComp.setAxisDependency(YAxis.AxisDependency.LEFT);
        setHumiComp.setColor(ColorTemplate.rgb("#aa66cc"));
        setHumiComp.setCircleColor(ColorTemplate.rgb("#aa66cc"));
        setDistComp = new LineDataSet(data_Distance,"이동거리");
        setDistComp.setAxisDependency(YAxis.AxisDependency.LEFT);
        setDistComp.setColor(ColorTemplate.rgb("#99cc00"));
        setDistComp.setCircleColor(ColorTemplate.rgb("#99cc00"));
        dataSets.add(setTempComp);
        dataSets.add(setBodyComp);
        //dataSets.add(setHeartComp);
        dataSets.add(setHumiComp);
        dataSets.add(setDistComp);
        data = new LineData(xVals,dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    public void AddEntry(int temp,int body,int heart,int humi,float dist){
        data = chart.getData();
        data_Temperature.add(new Entry(temp,data_Temperature.size()));
        data_BodyHeat.add(new Entry(body,data_BodyHeat.size()));
        //data_HeartScan.add(new Entry(heart,data_HeartScan.size()));
        data_Humidity.add(new Entry(humi,data_Humidity.size()));
        data_Distance.add(new Entry(dist,data_Distance.size()));
        xVals.add(((xVals.size()+1)) + "");
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(8);
        chart.moveViewTo(xVals.size() - 7, 50f, YAxis.AxisDependency.LEFT);
        //chart.invalidate();
    }

    public void setVisible(boolean b){
        if(b){
            chart.setVisibility(View.VISIBLE);
            backLayout.setVisibility(View.VISIBLE);
            // Gets the layout params that will allow you to resize the layout
            ViewGroup.LayoutParams params = backLayout.getLayoutParams();
            // Changes the height and width to the specified *pixels*
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 450, mActivty.getResources().getDisplayMetrics());
            params.height = height;
            backLayout.setLayoutParams(params);
        }else{
            chart.setVisibility(View.INVISIBLE);
            backLayout.setVisibility(View.INVISIBLE);
            // Gets the layout params that will allow you to resize the layout
            ViewGroup.LayoutParams params = backLayout.getLayoutParams();
            // Changes the height and width to the specified *pixels*
            params.height = 0;
            backLayout.setLayoutParams(params);
        }
    }
}
