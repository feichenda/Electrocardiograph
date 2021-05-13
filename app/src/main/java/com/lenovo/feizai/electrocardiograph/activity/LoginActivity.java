package com.lenovo.feizai.electrocardiograph.activity;

import android.view.WindowManager;
import android.widget.TextView;

import com.lenovo.feizai.electrocardiograph.R;
import com.lenovo.feizai.electrocardiograph.base.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author feizai
 * @date 2021/5/3 0003 下午 11:19:01
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.time)
    TextView time;

    private Timer timer;
    private int count;

    public LoginActivity() {
        super(R.layout.activity_login);
    }

    @Override
    protected void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        count = 5;
        timer = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                if (count > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            time.setText(count+"S");
                            count--;
                        }
                    });
                }
                if (count == 0) {
                    timer.cancel();
                    startActivity(MainActivity.class);
                    finish();
                }
            }
        };
        timer.schedule(task1,2000,1000);
    }

    @OnClick(R.id.enter)
    public void enter() {
        count = 0;
        timer.cancel();
        startActivity(MainActivity.class);
        finish();
    }

    @OnClick(R.id.jump)
    public void jump() {
        count = 0;
        timer.cancel();
        startActivity(MainActivity.class);
        finish();
    }
}
