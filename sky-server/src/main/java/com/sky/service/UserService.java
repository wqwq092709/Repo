package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.dto.UserProfileDTO;
import com.sky.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 更新当前登录用户资料
     */
    void updateProfile(UserProfileDTO userProfileDTO);
}
