package com.lenovo.feizai.electrocardiograph.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author feizai
 * @date 2021/5/3 0003 下午 5:09:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String name;
    private String password;
}
