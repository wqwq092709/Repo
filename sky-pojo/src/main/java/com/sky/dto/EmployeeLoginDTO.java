package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeLoginDTO implements Serializable {

    @ApiModelProperty(value = "用户名",example = "wangqi")
    private String username;

    @ApiModelProperty(value = "密码",example = "123456")
    private String password;

}
