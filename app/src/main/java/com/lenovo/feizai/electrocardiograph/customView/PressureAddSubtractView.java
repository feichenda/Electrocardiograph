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
 * @date 2021/5/5 0005 下午 7:55:35
 */
public class PressureAddSubtractView extends LinearLayout implements View.OnClickListener {

    private TextView subtract;
    private TextView add;
    private EditText data;

    public PressureAddSubtractView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.pressure_add_subtract, this);
        initView();
        TypedArray ob = context.obtainStyledAttributes(attrs, R.styleable.PressureAddSubtractView);
        String text = ob.getString(R.styleable.PressureAddSubtractView_text);
        int color = ob.getColor(R.styleable.PressureAddSubtractView_textColor, Color.BLACK);
        data.setTextColor(color);
        data.setText(text);
        data.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = s.toString();
                try {
                    if (temp.length() == 3) {
                        return;
                    }
                    if (temp.length() > 3) {
                        Integer value = Integer.valueOf(temp.substring(0, 3));
                        data.setText(String.format("%03d", value));
                    }
                    data.setSelection(text.length());//将光标移至文字末尾
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                String temp = s.toString();
//                try {
//                    if (temp.length() < 3) {
//                        Integer value = Integer.valueOf(temp);
//                        data.setText(String.format("%03d",value));
//                    } else {
//                        if (temp.length() == 3) {
//                            return;
//                        } else {
//                            Integer value = Integer.valueOf(temp.substring(0, 2));
//                            data.setText(String.format("%03d",value));
//                        }
//                    }
//                    data.setSelection(text.length());//将光标移至文字末尾
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        String text = data.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            text = "000";
            data.setText(text);
            data.setSelection(text.length());//将光标移至文字末尾
        }
        try {
            Integer value = Integer.valueOf(text);
            switch (v.getId()) {
                case R.id.add:
                    value = value + 1;
                    data.setText(String.format("%03d", value));
                    break;
                case R.id.subtract:
                    if (value > 0) {
                        value = value - 1;
                    }
                    data.setText(String.format("%03d", value));
                    break;
            }
            data.setSelection(text.length());//将光标移至文字末尾
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        subtract = findViewById(R.id.subtract);
        add = findViewById(R.id.add);
        data = findViewById(R.id.data_edit);
        add.setOnClickListener(this);
        subtract.setOnClickListener(this);
    }

    public void setTectColor(int color) {
        data.setTextColor(color);
    }

    public void setTextColor(ColorStateList colors) {
        data.setTextColor(colors);
    }
}
