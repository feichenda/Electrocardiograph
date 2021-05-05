package com.lenovo.feizai.electrocardiograph.base;

import android.content.Context;

import com.lenovo.feizai.electrocardiograph.net.ExceptionHandle;
import com.orhanobut.logger.Logger;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author feizai
 * @date 12/22/2020 022 10:49:42 AM
 */
public abstract class BaseObserver<T> implements Observer<T> {

    private Context mContext;
    private int code;
    private String message;

    public BaseObserver(Context context){
        mContext=context;
    }

    @Override
    public void onSubscribe(Disposable d) {
        Logger.v("请求开始");
        showDialog();
    }

    @Override
    public void onNext(T t) {
        hideDialog();
        Logger.v("请求到数据");
        if (t instanceof BaseModel) {
            message = ((BaseModel) t).getMessage();
            code = ((BaseModel) t).getCode();
        }
        switch (code) {
            case 200:
                successful(t);
                break;
            default:
                defeated(t);
                break;
        }
    }

    @Override
    public void onError(Throwable e) {
        Logger.v("请求出错");
        Logger.e(e.getMessage());
        hideDialog();
        ExceptionHandle.ResponeThrowable error;
        if (e instanceof ExceptionHandle.ResponeThrowable) {
            error = (ExceptionHandle.ResponeThrowable) e;
        } else {
            error = new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN);
        }
        showErrorMessage(error);
    }

    @Override
    public void onComplete() {
        Logger.v("请求完成");
        hideDialog();
    }

    public void showErrorMessage(ExceptionHandle.ResponeThrowable e) {
        onError(e);
    }

    protected abstract void showDialog();

    protected abstract void hideDialog();

    protected abstract void successful(T t);

    protected abstract void defeated(T t);

    protected abstract void onError(ExceptionHandle.ResponeThrowable e);
}
