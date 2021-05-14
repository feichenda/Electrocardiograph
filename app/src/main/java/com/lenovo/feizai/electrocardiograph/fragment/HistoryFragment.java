package com.lenovo.feizai.electrocardiograph.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.lenovo.feizai.electrocardiograph.R;
import com.lenovo.feizai.electrocardiograph.base.BaseFragment;

import butterknife.BindView;

/**
 * @author feizai
 * @date 2021/5/3 0003 下午 10:16:14
 */
public class HistoryFragment extends BaseFragment {
    private float low_temperature;
    private float hight_temperature;
    private int hight_hpressure;
    private int low_hpressure;
    private int hight_lpressure;
    private int low_lpressure;
    private int hight_heartbeat;
    private int low_heartbeat;
    private float temperature_data1;
    private int low_pressure_data1;
    private int hight_pressure_data1;
    private int heartbeat_data1;

    @BindView(R.id.temperature_data)
    TextView temperature_data;
    @BindView(R.id.hight_pressure_data)
    TextView hight_pressure_data;
    @BindView(R.id.low_pressure_data)
    TextView low_pressure_data;
    @BindView(R.id.heartbeat_data)
    TextView heartbeat_data;

    public HistoryFragment() {
        super(R.layout.fragment_hsitory);
    }

    @Override
    protected void initView(View view) {
        SharedPreferences history = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        String history_temperature_data = history.getString("temperature", "");
        String history_low_pressure_data = history.getString("low_pressure", "");
        String history_hight_pressure_data = history.getString("hight_pressure", "");
        String history_heartbeat_data = history.getString("heartbeat", "");

        SharedPreferences setting = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        hight_temperature = setting.getFloat("hight_temperature", 37.30f);
        low_temperature = setting.getFloat("low_temperature", 35.50f);

        hight_hpressure = setting.getInt("hight_hpressure", 140);
        low_hpressure = setting.getInt("low_hpressure", 90);

        hight_lpressure = setting.getInt("hight_lpressure", 90);
        low_lpressure = setting.getInt("low_lpressure", 60);

        hight_heartbeat = setting.getInt("hight_heartbeat", 100);
        low_heartbeat = setting.getInt("low_heartbeat", 60);

        try {
            if (!history_temperature_data.isEmpty()) {
                temperature_data1 = Float.parseFloat(history_temperature_data);
            }
            if (!history_temperature_data.isEmpty()) {
                low_pressure_data1 = Integer.parseInt(history_low_pressure_data);
            }
            if (!history_temperature_data.isEmpty()) {
                hight_pressure_data1 = Integer.parseInt(history_hight_pressure_data);
            }
            if (!history_temperature_data.isEmpty()) {
                heartbeat_data1 = Integer.parseInt(history_heartbeat_data);
            }

            if (temperature_data1 >= hight_temperature) {
                temperature_data.setTextColor(Color.RED);
            } else if (temperature_data1 <= low_temperature) {
                temperature_data.setTextColor(Color.YELLOW);
            } else {
                temperature_data.setTextColor(Color.GREEN);
            }
            if (hight_pressure_data1 >= hight_hpressure) {
                hight_pressure_data.setTextColor(Color.RED);
            } else if (hight_pressure_data1 <= low_hpressure) {
                hight_pressure_data.setTextColor(Color.YELLOW);
            } else {
                hight_pressure_data.setTextColor(Color.GREEN);
            }

            if (low_pressure_data1 >= hight_lpressure) {
                low_pressure_data.setTextColor(Color.RED);
            } else if (low_pressure_data1 <= low_lpressure) {
                low_pressure_data.setTextColor(Color.YELLOW);
            } else {
                low_pressure_data.setTextColor(Color.GREEN);
            }

            if (heartbeat_data1 >= hight_heartbeat) {
                heartbeat_data.setTextColor(Color.RED);
            } else if (heartbeat_data1 <= low_heartbeat) {
                heartbeat_data.setTextColor(Color.YELLOW);
            } else {
                heartbeat_data.setTextColor(Color.GREEN);
            }

            temperature_data.setText(String.format("%.2f", temperature_data1));
            hight_pressure_data.setText(String.format("%03d", hight_pressure_data1));
            low_pressure_data.setText(String.format("%03d", low_pressure_data1));
            heartbeat_data.setText(String.format("%03d", heartbeat_data1));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
