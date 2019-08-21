package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;

public interface UserService {
    //通过用户ID获取用户对象
    public UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;
}
