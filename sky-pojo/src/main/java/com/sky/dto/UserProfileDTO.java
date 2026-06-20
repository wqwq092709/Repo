package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户资料
 */
@Data
public class UserProfileDTO implements Serializable {

    private String avatar;

    private String nickName;
}
