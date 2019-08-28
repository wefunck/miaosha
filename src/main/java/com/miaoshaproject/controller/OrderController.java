package com.miaoshaproject.controller;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true")//允许跨域请求
public class OrderController extends BaseController{

    @Autowired
    OrderService orderService;

    @Autowired
    HttpServletRequest httpServletRequest;

    //封装下单信息
    @RequestMapping(value =  "/createorder",method ={RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType  createOrder(@RequestParam(name = "itemId")Integer itemId, @RequestParam(name = "amount")Integer amount) throws BusinessException {
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null || !isLogin.booleanValue()){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        //获取登录用户的信息
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"无法获取登陆用户信息");
        }

        OrderModel orderModel = orderService.createOrder(userModel.getId(),itemId,amount);

        return CommonReturnType.create(null);

    }



}
