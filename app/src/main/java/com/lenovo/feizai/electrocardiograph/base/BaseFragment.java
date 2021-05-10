package com.lenovo.feizai.electrocardiograph.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * @author feizai
 * @date 2021/5/3 0003 下午 4:24:08
 */
public abstract class BaseFragment extends Fragment {
    //布局文件ID
    int resLayout;
    //用户同意权限权限申请
    public Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    //用户拒绝权限申请
    public Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap<>();
    //用户彻底禁止权限申请
    public Map<Integer, Runnable> completebanPermissionRunnables = new HashMap<>();

    public BaseFragment(int resLayout) {
        this.resLayout = resLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(resLayout, container, false);
        Logger.addLogAdapter(new AndroidLogAdapter());
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    public void requestPermission(int id, String permission, Runnable allowableRunnable, Runnable disallowableRunnable, Runnable completebanRunable) {
        if (allowableRunnable == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }
        if (disallowableRunnable == null) {
            throw new IllegalArgumentException("disallowableRunnable == null");
        }
        if (completebanRunable == null) {
            throw new IllegalArgumentException("completebanRunable == null");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查是否拥有权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, id);
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (isTip) {//表明用户没有彻底禁止弹出权限请求
                    Runnable disallowRun = disallowablePermissionRunnables.get(requestCode);
                    if (disallowRun != null) {
                        disallowRun.run();
                    }else{
                        throw  new NullPointerException("disallowRun == null");
                    }
                } else {//表明用户已经彻底禁止弹出权限请求
                    //这里一般会提示用户进入权限设置界面
                    Runnable completebanRun = completebanPermissionRunnables.get(requestCode);
                    if (completebanRun != null) {
                        completebanRun.run();
                    } else {
                        throw  new NullPointerException("completebanRun == null");
                    }
                }
                return;
            } else {
                Runnable allowRun = allowablePermissionRunnables.get(requestCode);
                if (allowRun != null) {
                    allowRun.run();
                }else {
                    throw  new NullPointerException("allowRun == null");
                }
            }
        }
    }

    /*获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）*/
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getActivity().getPackageName());
        }
        return localIntent;
    }

    protected abstract void initView(View view);

    public void showToast(String content) {
        Toast.makeText(getContext(),content,Toast.LENGTH_SHORT).show();
    }
}
