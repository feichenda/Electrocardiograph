package com.lenovo.feizai.electrocardiograph.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lenovo.feizai.electrocardiograph.R;
import com.lenovo.feizai.electrocardiograph.application.MyApplication;
import com.lenovo.feizai.electrocardiograph.base.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.wch.ch34xuartdriver.CH34xUARTDriver;

public class MainActivity extends BaseActivity {

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.open)
    Button open;

    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";
    private boolean isOpen;
    private int retval;
    private Handler handler;
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

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        MyApplication.driver = new CH34xUARTDriver((UsbManager) getSystemService(Context.USB_SERVICE), this, ACTION_USB_PERMISSION);
        holder = surfaceView.getHolder();

        /* by default it is 9600 */
        // baudRate = 115200;
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
            new MaterialDialog.Builder(this).title("提示").content("您的手机不支持USB HOST，请更换其他手机再试！")
                    .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    System.exit(0);
                }
            }).show();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持常亮的屏幕的状态
        isOpen = false;
        // configButton.setEnabled(false);

        timer.schedule(task, 1000, 10); // 100毫秒才执行第一次，后面每10毫秒执行一次
        Logger.e("定时器开始");
        handler = new Handler() {

            public void handleMessage(Message msg) {
                drawxdt(msg.arg1);
                if (msg.what == 2) {
                    drawxdt(msg.arg2);
                }
            }
        };
    }

    private void drawxdt(int xd) {
        if (x0 < x)
            c2.drawLine(x0, y0, x, h - h * xd / 100, paint); // xd范围0-255，mSurfaceHolder显示范围0-300
        x0 = x;
        y0 = h - h * xd / 100;
        x = x + 15;
        if (x > w) {
            x = 0;
            c2.drawColor(0xffcccccc);
        }

        Canvas c = holder.lockCanvas();
        c.drawBitmap(bitmap, 0, 0, null);
        holder.unlockCanvasAndPost(c);
    }

    @OnClick(R.id.open)
    public void open() {
        if (!isOpen) {
            retval = MyApplication.driver.ResumeUsbList();
            if (retval == -1)// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
            {
                Toast.makeText(MainActivity.this, "打开设备失败!", Toast.LENGTH_SHORT).show();
                MyApplication.driver.CloseDevice();
            } else if (retval == 0) {
                if (!MyApplication.driver.UartInit()) {// 对串口设备进行初始化操作
                    Toast.makeText(MainActivity.this, "设备初始化失败!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "打开" + "设备失败!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "打开设备成功!", Toast.LENGTH_SHORT).show();
                isOpen = true;
                open.setText("断开");

                if (MyApplication.driver.SetConfig(baudRate, dataBit, stopBit, parity, // 配置串口波特率，函数说明可参照编程手册
                        flowControl)) {
                    Toast.makeText(MainActivity.this, "串口设置成功!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "串口设置失败!", Toast.LENGTH_SHORT).show();
                }

            } else {

                new MaterialDialog.Builder(this)
                        .icon(getResources().getDrawable(R.drawable.icon))
                        .title("未授权限")
                        .content("确认退出吗？")
                        .positiveText("确定")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                System.exit(0);
                            }
                        })
                        .negativeText("返回").show();
            }
        } else {
            open.setText("连接");
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

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (dyc) {// 定时器第一次运行
                dyc = false;
                paint.setColor(0xff000000);
                paint.setStrokeWidth(4); // 线宽
                h = surfaceView.getHeight();
                w = surfaceView.getWidth();
                bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                c2 = new Canvas(bitmap);
                c2.drawColor(0xffcccccc);
                return;
            }
            Message msg = Message.obtain();
            if (isOpen) {
                byte[] buffer = new byte[2];
                // 一次读取两个数据是为了防止读取速度跟不上单片机发来的速度，导致心电数据不能实时显示，且误差可能会越来越大
                int length = MyApplication.driver.ReadData(buffer, 2);
                Logger.e( "" + length);

                if (length > 0) {
                    msg.arg1 = buffer[0] & 0xff; // 因为byte范围是-128~+127，而单片机发来的数据是16进制的范围0~255，所以需要 & 0xff 转换
                    if (length > 1)
                        msg.arg2 = buffer[1] & 0xff;
                    msg.what = length;
                    handler.sendMessage(msg);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
        bitmap = null;
        c2 = null;
        timer.cancel();
        Logger.e( "定时器停止");
        if (isOpen) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MyApplication.driver.CloseDevice();
            Logger.e("关闭串口");
        }
        Logger.e( "程序退出");
        System.exit(0);
    }
}