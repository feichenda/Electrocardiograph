package com.lenovo.feizai.electrocardiograph.customView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lenovo.feizai.electrocardiograph.R;

/**
 * @author feizai
 * @date 2021/5/4 0004 下午 7:48:04
 */
public class TemperatureAddSubtractView extends LinearLayout implements View.OnClickListener {

    private TextView subtract;
    private TextView add;
    private EditText data;

    public TemperatureAddSubtractView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.temperature_add_subtract, this);
        initView();
        TypedArray ob = context.obtainStyledAttributes(attrs, R.styleable.TemperatureAddSubtractView);
        String text = ob.getString(R.styleable.TemperatureAddSubtractView_text);
        int color = ob.getColor(R.styleable.TemperatureAddSubtractView_textColor, Color.BLACK);
        data.setTextColor(color);
        data.setText(text);
        data.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = s.toString();
                if (temp.contains(".")) {
                    int index=temp.indexOf(".");
                    if (index + 3 < temp.length()) {
                        temp = temp.substring(0, index + 3);
                        data.setText(temp);
                        data.setSelection(temp.length());
                    }
                } else {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initView() {
        subtract = findViewById(R.id.subtract);
        add = findViewById(R.id.add);
        data = findViewById(R.id.data_edit);
        add.setOnClickListener(this);
        subtract.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String text = data.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            text = "0.00";
            data.setText(text);
            data.setSelection(text.length());//将光标移至文字末尾
        }
        try {
            Double value = Double.valueOf(text);
            switch (v.getId()) {
                case R.id.add:
                    value = value + 0.01;
                    data.setText(String.format("%.2f",value));
                    break;
                case R.id.subtract:
                    value = value - 0.01;
                    data.setText(String.format("%.2f",value));
                    break;
            }
            data.setSelection(text.length());//将光标移至文字末尾
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTectColor(int color) {
        data.setTextColor(color);
    }

    public void setTextColor(ColorStateList colors) {
        data.setTextColor(colors);
    }
}
