package com.lenovo.feizai.electrocardiograph.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lenovo.feizai.electrocardiograph.R;
import com.lenovo.feizai.electrocardiograph.activity.MainActivity;
import com.lenovo.feizai.electrocardiograph.application.MyApplication;
import com.lenovo.feizai.electrocardiograph.base.BaseFragment;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
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
    @BindView(R.id.average_pressure_data)
    TextView average_pressure_data;

    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";
    private boolean isOpen;
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
    private Timer xdtTimer = new Timer();
    private Timer temperatureTimer = new Timer();
    private Timer pressureTimer = new Timer();
    private int i;

    public DataFragment() {
        super(R.layout.fragment_data);
    }

    @Override
    protected void initView(View view) {
        i = 0;
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
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持常亮的屏幕的状态
        // configButton.setEnabled(false);

        xdtTimer.schedule(xdtTask, 1000, 10); // 1000毫秒才执行第一次，后面每10毫秒执行一次
//        temperatureTimer.schedule(temperatureTask, 1000, 10); // 1000毫秒才执行第一次，后面每10毫秒执行一次
//        pressureTimer.schedule(pressureTask, 1000, 10); // 1000毫秒才执行第一次，后面每10毫秒执行一次
        Logger.e("定时器开始");
        xdtHandler = new Handler() {

            public void handleMessage(Message msg) {
                drawxdt(msg.arg1);
                if (msg.what == 2) {
                    drawxdt(msg.arg2);
                }
            }
        };
        temperatureHandler = new Handler() {

            public void handleMessage(Message msg) {
                String data = (String) msg.obj;
                String newdata = data.replace("\n", "");
                temperature_data.setText(newdata);
            }
        };
        pressureHandler = new Handler() {

            public void handleMessage(Message msg) {
                String data = (String) msg.obj;
                switch (i) {
                    case 0:
                        hight_pressure_data.setText(data);
                        i++;
                        break;
                    case 1:
                        low_pressure_data.setText(data);
                        i++;
                        break;
                    case 2:
                        average_pressure_data.setText(data);
                        i = 0;
                        break;
                }
            }
        };
        open();
    }

    public void open() {
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
            if (MyApplication.driver.SetConfig(baudRate, dataBit, stopBit, parity, // 配置串口波特率，函数说明可参照编程手册
                    flowControl)) {
                showToast("串口设置成功!");
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

    TimerTask xdtTask = new TimerTask() {
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
            Logger.e("" + length);

            if (length > 0) {
                msg.arg1 = buffer[0] & 0xff; // 因为byte范围是-128~+127，而单片机发来的数据是16进制的范围0~255，所以需要 & 0xff 转换
                if (length > 1)
                    msg.arg2 = buffer[1] & 0xff;
                msg.what = length;
                xdtHandler.sendMessage(msg);
            }
        }
    };

    TimerTask temperatureTask = new TimerTask() {
        @Override
        public void run() {
            byte[] buffer = new byte[4096];
            while (true) {

                Message msg = Message.obtain();
//                if (!isOpen) {
//                    break;
//                }
                int length = MyApplication.driver.ReadData(buffer, 4096);
                if (length > 0) {
//                    String recv = toHexString(buffer, length);        //以16进制输出
                    String recv = new String(buffer, 0, length);        //以字符串形式输出
                    msg.obj = recv;
                    temperatureHandler.sendMessage(msg);
                }
            }
        }
    };

    TimerTask pressureTask = new TimerTask() {
        @Override
        public void run() {
            byte[] buffer = new byte[4096];
            while (true) {

                Message msg = Message.obtain();
//                if (!isOpen) {
//                    break;
//                }
                int length = MyApplication.driver.ReadData(buffer, 4096);
                if (length > 0) {
//                    String recv = toHexString(buffer, length);        //以16进制输出
                    String recv = new String(buffer, 0, length);        //以字符串形式输出

                    msg.obj = recv;
                    pressureHandler.sendMessage(msg);
                }
            }
        }
    };

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
        if (bitmap != null)
            bitmap.recycle();
        bitmap = null;
        c2 = null;
        xdtTimer.cancel();
        temperatureTimer.cancel();
        pressureTimer.cancel();
        Logger.e("定时器停止");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        MyApplication.driver.CloseDevice();
        Logger.e("关闭串口");
        Logger.e("程序退出");
//        System.exit(0);
    }
}
