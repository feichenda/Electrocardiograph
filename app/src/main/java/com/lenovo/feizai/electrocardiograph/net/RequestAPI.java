package com.lenovo.feizai.electrocardiograph.net;

import com.lenovo.feizai.electrocardiograph.base.BaseModel;
import com.lenovo.feizai.electrocardiograph.entity.User;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

/**
 * @author feizai
 * @date 12/21/2020 021 10:02:37 PM
 */
public interface RequestAPI {

    public final static String baseURL = "http://39.108.48.82:8080/Parking/";
    public final static String baseImageURL = "http://39.108.48.82:8080/";

    @GET("api/user/selectAllUser")
    Observable<BaseModel<User>> selectAllUser();

}

