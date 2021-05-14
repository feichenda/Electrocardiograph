package com.lenovo.feizai.electrocardiograph.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lenovo.feizai.electrocardiograph.R;
import com.lenovo.feizai.electrocardiograph.application.MyApplication;
import com.lenovo.feizai.electrocardiograph.base.BaseFragment;
import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.wch.ch34xuartdriver.CH34xUARTDriver;

/**
 * @author feizai
 * @date 2021/5/3 0003 下午 10:16:14
 */
public class DataFragment extends BaseFragment {

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.temperature_data)
    TextView temperature_data;
    @BindView(R.id.hight_pressure_data)
    TextView hight_pressure_data;
    @BindView(R.id.low_pressure_data)
    TextView low_pressure_data;
    @BindView(R.id.heartbeat_data)
    TextView heartbeat_data;
    @BindView(R.id.link)
    Button link;
    @BindView(R.id.temperature)
    RadioButton temperature;
    @BindView(R.id.pressure)
    RadioButton pressure;
    @BindView(R.id.xdt)
    RadioButton xdt;
    @BindView(R.id.radiogroup)
    RadioGroup radioGroup;

    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";
    private boolean isOpen = false;
    private int retval;
    private int x, x0, w, h, y0;
    private Canvas c2 = null;
    private int baudRate;
    private byte stopBit;
    private byte dataBit;
    private byte parity;
    private byte flowControl;
    private Paint paint = new Paint();
    private SurfaceHolder holder = null;
    private Bitmap bitmap = null;
    private boolean dyc = true;// 定时器第一次运行
    private Handler xdtHandler;
    private Handler temperatureHandler;
    private Handler pressureHandler;
    private Timer timer = new Timer();
    private Thread temperatureThread = null;
    private Thread pressureThread = null;
    private TimerTask xdtTask = null;
    private int i;
    private boolean isFirst;
    private boolean flag;

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


    public DataFragment() {
        super(R.layout.fragment_data);
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

    private void getHistory() {
        SharedPreferences history = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        String history_temperature_data = history.getString("temperature", "");
        String history_low_pressure_data = history.getString("low_pressure", "");
        String history_hight_pressure_data = history.getString("hight_pressure", "");
        String history_heartbeat_data = history.getString("heartbeat", "");

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

    @Override
    protected void initView(View view) {
        isOpen = false;
        isFirst = false;
        flag = true;
        i = 0;

        initValue();
        getHistory();

        MyApplication.driver = new CH34xUARTDriver((UsbManager) getActivity().getSystemService(Context.USB_SERVICE), getContext(), ACTION_USB_PERMISSION);
        holder = surfaceView.getHolder();

        /* by default it is 9600 */
        baudRate = 19200;// 9600;

        /* default is stop bit 1 */
        stopBit = 1;

        /* default data bit is 8 bit */
        dataBit = 8;

        /* default is none */
        parity = 0;

        /* default flow control is is none */
        flowControl = 0;
        if (!MyApplication.driver.UsbFeatureSupported())// 判断系统是否支持USB HOST
        {
            new MaterialDialog.Builder(getContext()).title("提示").content("您的手机不支持USB HOST，请更换其他手机再试！")
                    .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    getActivity().finish();
                }
            }).show();
        }

        Logger.e("定时器开始");
    }

    @OnClick(R.id.link)
    public void link() {
        if (!isOpen) {
            retval = MyApplication.driver.ResumeUsbPermission();
            if (retval == 0) {
                retval = MyApplication.driver.ResumeUsbList();
                if (retval == -1)// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
                {
                    showToast("打开设备失败!");
                    MyApplication.driver.CloseDevice();
                } else if (retval == 0) {
                    if (!MyApplication.driver.UartInit()) {// 对串口设备进行初始化操作
                        showToast("设备初始化失败!");
                        showToast("打开设备失败!");
                        return;
                    }
                    showToast("打开设备成功!");
                    isOpen = true;
                    link.setText("断开设备");
                    if (MyApplication.driver.SetConfig(baudRate, dataBit, stopBit, parity, // 配置串口波特率，函数说明可参照编程手册
                            flowControl)) {
                        showToast("串口设置成功!");
                        radioGroup.clearCheck();
                    } else {
                        showToast("串口设置失败!");
                    }
                } else {

                    new MaterialDialog.Builder(getContext())
                            .icon(getResources().getDrawable(R.drawable.icon))
                            .title("未授权限")
                            .content("确认退出吗？")
                            .positiveText("确定")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    getActivity().finish();
                                }
                            })
                            .negativeText("返回").show();
                }
            }
        } else {
            temperature.setChecked(false);
            pressure.setChecked(false);
            xdt.setChecked(false);
            link.setText("连接设备");
            isOpen = false;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MyApplication.driver.CloseDevice();
        }
    }

    @OnClick(R.id.temperature)
    public void temperature() {
        if (!isOpen) {
            showToast("请先连接设备");
            temperature.setChecked(false);
            return;
        }
        cancel();
        temperature.setChecked(true);
        showToast("您已选择体温功能");
        temperatureHandler = new Handler() {
            public void handleMessage(Message msg) {
                String data = (String) msg.obj;
                String newdata = data.replace("\n", "");
                try {
                    Float temp = Float.valueOf(newdata);
                    if (temp >= hight_temperature) {
                        temperature_data.setTextColor(Color.RED);
                    } else {
                        if (temp <= low_temperature) {
                            temperature_data.setTextColor(Color.YELLOW);
                        } else {
                            temperature_data.setTextColor(Color.GREEN);
                        }
                    }
                    temperature_data.setText(String.format("%.2f",temp));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        temperatureThread = new Thread() {
            @Override
            public void run() {
                byte[] buffer = new byte[4096];
                while (flag) {
                    Message msg = Message.obtain();
                    if (!isOpen) {
                        break;
                    }
                    int length = MyApplication.driver.ReadData(buffer, 4096);
                    Logger.e(length + "");
                    if (length > 0) {
                        String recv = new String(buffer, 0, length);        //以字符串形式输出
                        msg.obj = recv;
                        temperatureHandler.sendMessage(msg);
                    }
                }
            }
        };
        temperatureThread.start();
    }

    @OnClick(R.id.pressure)
    public void pressure() {
        if (!isOpen) {
            showToast("请先连接设备");
            pressure.setChecked(false);
            return;
        }
        cancel();
        pressure.setChecked(true);
        showToast("您已选择血压功能");
        pressureHandler = new Handler() {
            public void handleMessage(Message msg) {
                String data = (String) msg.obj;
                String newdata = data.substring(0, 3);
                try {
                    Integer temp = Integer.valueOf(newdata);
                    switch (i) {
                        case 0:
                            if (temp >= hight_hpressure) {
                                hight_pressure_data.setTextColor(Color.RED);
                            } else {
                                if (temp <= low_hpressure) {
                                    hight_pressure_data.setTextColor(Color.YELLOW);
                                } else {
                                    hight_pressure_data.setTextColor(Color.GREEN);
                                }
                            }
                            hight_pressure_data.setText(newdata);
                            i++;
                            break;
                        case 1:
                            if (temp >= hight_lpressure) {
                                low_pressure_data.setTextColor(Color.RED);
                            } else {
                                if (temp <= low_lpressure) {
                                    low_pressure_data.setTextColor(Color.YELLOW);
                                } else {
                                    low_pressure_data.setTextColor(Color.GREEN);
                                }
                            }
                            low_pressure_data.setText(newdata);
                            i++;
                            break;
                        case 2:
                            if (temp >= hight_heartbeat) {
                                heartbeat_data.setTextColor(Color.RED);
                            } else {
                                if (temp <= low_heartbeat) {
                                    heartbeat_data.setTextColor(Color.YELLOW);
                                } else {
                                    heartbeat_data.setTextColor(Color.GREEN);
                                }
                            }
                            heartbeat_data.setText(newdata);
                            i = 0;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        pressureThread = new Thread() {
            @Override
            public void run() {
                byte[] buffer = new byte[4096];
                while (flag) {
                    Message msg = Message.obtain();
                    if (!isOpen) {
                        break;
                    }
                    int length = MyApplication.driver.ReadData(buffer, 4096);
                    Logger.e(length + "");
                    if (length > 0) {
                        String recv = new String(buffer, 0, length);        //以字符串形式输出

                        msg.obj = recv;
                        pressureHandler.sendMessage(msg);
                    }
                }
            }
        };
        pressureThread.start();
    }

    @OnClick(R.id.xdt)
    public void xdt() {
        if (!isOpen) {
            showToast("请先连接设备");
            xdt.setChecked(false);
            return;
        }
        cancel();
        timer = new Timer();
        xdt.setChecked(true);
        isFirst = true;
        showToast("您已选择心电图功能");
        xdtHandler = new Handler() {
            public void handleMessage(Message msg) {
                drawxdt(msg.arg1);
                if (msg.what == 2) {
                    drawxdt(msg.arg2);
                }
            }
        };
        xdtTask = new TimerTask() {
            @Override
            public void run() {
                if (dyc) {// 定时器第一次运行
                    dyc = false;
                    paint.setColor(0xff000000);
                    paint.setStrokeWidth(2); // 线宽
                    h = surfaceView.getHeight();
                    w = surfaceView.getWidth();
                    bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    c2 = new Canvas(bitmap);
                    c2.drawColor(0xffcccccc);
                    return;
                }
                Message msg = Message.obtain();
                byte[] buffer = new byte[2];

                // 一次读取两个数据是为了防止读取速度跟不上单片机发来的速度，导致心电数据不能实时显示，且误差可能会越来越大
                int length = MyApplication.driver.ReadData(buffer, 2);

                if (length > 0) {
                    msg.arg1 = buffer[0] & 0xff; // 因为byte范围是-128~+127，而单片机发来的数据是16进制的范围0~255，所以需要 & 0xff 转换
                    if (length > 1)
                        msg.arg2 = buffer[1] & 0xff;
                    msg.what = length;
                    xdtHandler.sendMessage(msg);
                }
            }
        };
        timer.schedule(xdtTask, 1000, 10);
    }

    private void cancel() {
        try {
            flag = false;
            Thread.sleep(200);
            flag = true;
            timer.cancel();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    private void drawxdt(int xd) {
        if (x0 < x)
            c2.drawLine(x0, y0, x, h - h * xd / 300, paint); // xd范围0-255，mSurfaceHolder显示范围0-300
        x0 = x;
        y0 = h - h * xd / 300;
        x = x + 5;
        if (x > w) {
            x = 0;
            c2.drawColor(0xffcccccc);
        }

        Canvas c = holder.lockCanvas();
        c.drawBitmap(bitmap, 0, 0, null);
        holder.unlockCanvasAndPost(c);
    }


    /**
     * 将byte[]数组转化为String类型
     *
     * @param arg    需要转换的byte[]数组
     * @param length 需要转换的数组长度
     * @return 转换后的String队形
     */
    private String toHexString(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }

    /**
     * 将String转化为byte[]数组
     *
     * @param arg 需要转换的String对象
     * @return 转换后的byte[]数组
     */
    private byte[] toByteArray(String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            /* 将char数组中的值转成一个实际的十进制数组 */
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                /* 将 每个char的值每两个组成一个16进制数据 */
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[]{};
    }

    /**
     * 将String转化为byte[]数组
     *
     * @param arg 需要转换的String对象
     * @return 转换后的byte[]数组
     */
    private byte[] toByteArray2(String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }

            byte[] byteArray = new byte[length];
            for (int i = 0; i < length; i++) {
                byteArray[i] = (byte) NewArray[i];
            }
            return byteArray;

        }
        return new byte[]{};
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancel();
        timer.cancel();
//        if (bitmap != null)
//            bitmap.recycle();
//        bitmap = null;
//        c2 = null;

        Logger.e("定时器停止");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        getNowData();
        isOpen = false;
        MyApplication.driver.CloseDevice();
        Logger.e("关闭串口");
//        System.exit(0);
    }

    private void getNowData() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("temperature", temperature_data.getText().toString());
        editor.putString("low_pressure", low_pressure_data.getText().toString());
        editor.putString("hight_pressure", hight_pressure_data.getText().toString());
        editor.putString("heartbeat", heartbeat_data.getText().toString());
        editor.commit();
        editor.apply();
    }

}
