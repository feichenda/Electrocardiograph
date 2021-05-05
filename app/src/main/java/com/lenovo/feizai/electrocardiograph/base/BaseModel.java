package com.lenovo.feizai.electrocardiograph.base;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author feizai
 * @date 12/21/2020 021 10:05:39 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseModel<T> {
    private int code;//默认成功的时候为200
    private String message;
    private T data;
    private List<T> datas;
}
