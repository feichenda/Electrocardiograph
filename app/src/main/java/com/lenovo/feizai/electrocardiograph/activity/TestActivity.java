package com.lenovo.feizai.electrocardiograph.activity;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lenovo.feizai.electrocardiograph.R;
import com.lenovo.feizai.electrocardiograph.base.BaseActivity;
import com.lenovo.feizai.electrocardiograph.fragment.DataFragment;
import com.lenovo.feizai.electrocardiograph.fragment.HistoryFragment;
import com.lenovo.feizai.electrocardiograph.fragment.SettingFragment;

import butterknife.BindView;

/**
 * @author feizai
 * @date 2021/5/3 0003 下午 9:48:12
 */
public class TestActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottom_navigation_view;

    public TestActivity() {
        super(R.layout.activity_test);
    }

    @Override
    protected void initView() {
        bottom_navigation_view.setOnNavigationItemSelectedListener(this);
        bottom_navigation_view.setSelectedItemId(R.id.navigation_data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_history:
                HistoryFragment historyFragment = new HistoryFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, historyFragment).commitNow();
                return true;
            case R.id.navigation_data:
                DataFragment dataFragment = new DataFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, dataFragment).commitNow();
                return true;
            case R.id.navigation_setting:
                SettingFragment settingFragment = new SettingFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, settingFragment).commitNow();
                return true;
        }
        return false;
    }
}
