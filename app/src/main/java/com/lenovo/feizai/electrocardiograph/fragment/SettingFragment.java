package com.lenovo.feizai.electrocardiograph.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.lenovo.feizai.electrocardiograph.R;
import com.lenovo.feizai.electrocardiograph.base.BaseFragment;
import com.lenovo.feizai.electrocardiograph.customView.PressureAddSubtractView;
import com.lenovo.feizai.electrocardiograph.customView.TemperatureAddSubtractView;

import butterknife.BindView;

/**
 * @author feizai
 * @date 2021/5/3 0003 下午 10:16:14
 */
public class SettingFragment extends BaseFragment {

    @BindView(R.id.hight_temperature_data)
    TemperatureAddSubtractView hight_temperature_data;
    @BindView(R.id.low_temperature_data)
    TemperatureAddSubtractView low_temperature_data;
    @BindView(R.id.hight_hpressure_data)
    PressureAddSubtractView hight_hpressure_data;
    @BindView(R.id.low_hpressure_data)
    PressureAddSubtractView low_hpressure_data;
    @BindView(R.id.hight_lpressure_data)
    PressureAddSubtractView hight_lpressure_data;
    @BindView(R.id.low_lpressure_data)
    PressureAddSubtractView low_lpressure_data;
    @BindView(R.id.hight_heartbeat_data)
    PressureAddSubtractView hight_heartbeat_data;
    @BindView(R.id.low_heartbeat_data)
    PressureAddSubtractView low_heartbeat_data;

    private float low_temperature;
    private float hight_temperature;
    private int hight_hpressure;
    private int low_hpressure;
    private int hight_lpressure;
    private int low_lpressure;
    private int hight_heartbeat;
    private int low_heartbeat;

    public SettingFragment() {
        super(R.layout.fragment_setting);
    }

    @Override
    protected void initView(View view) {
        initValue();
        initText();
        getValue();
    }

    private void initValue() {
        SharedPreferences preferences = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        hight_temperature = preferences.getFloat("hight_temperature", 37.30f);
        low_temperature = preferences.getFloat("low_temperature", 35.50f);

        hight_hpressure = preferences.getInt("hight_hpressure",140);
        low_hpressure = preferences.getInt("low_hpressure",90);

        hight_lpressure = preferences.getInt("hight_lpressure",90);
        low_lpressure = preferences.getInt("low_lpressure",60);

        hight_heartbeat = preferences.getInt("hight_heartbeat",100);
        low_heartbeat = preferences.getInt("low_heartbeat",60);
    }

    private void initText() {
        hight_temperature_data.setText(String.format("%.2f",hight_temperature));
        low_temperature_data.setText(String.format("%.2f",low_temperature));

        hight_hpressure_data.setText(String.format("%03d",hight_hpressure));
        low_hpressure_data.setText(String.format("%03d",low_hpressure));

        hight_lpressure_data.setText(String.format("%03d",hight_lpressure));
        low_lpressure_data.setText(String.format("%03d",low_lpressure));

        hight_heartbeat_data.setText(String.format("%03d",hight_heartbeat));
        low_heartbeat_data.setText(String.format("%03d",low_heartbeat));
    }

    private void getValue() {
        hight_temperature = hight_temperature_data.getValue();
        low_temperature = low_temperature_data.getValue();

        hight_hpressure = hight_hpressure_data.getValue();
        low_hpressure = low_hpressure_data.getValue();

        hight_lpressure = hight_lpressure_data.getValue();
        low_lpressure = low_lpressure_data.getValue();

        hight_heartbeat = hight_heartbeat_data.getValue();
        low_heartbeat = low_heartbeat_data.getValue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getValue();
        saveValue();
    }

    private void saveValue() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
        editor.putFloat("hight_temperature",hight_temperature);
        editor.putFloat("low_temperature",low_temperature);

        editor.putInt("hight_hpressure", hight_hpressure);
        editor.putInt("low_hpressure", low_hpressure);

        editor.putInt("hight_lpressure", hight_lpressure);
        editor.putInt("low_lpressure", low_lpressure);

        editor.putInt("hight_heartbeat", hight_heartbeat);
        editor.putInt("low_heartbeat", low_heartbeat);

        editor.commit();
        editor.apply();
    }
}
