package com.miaoshaproject.service.Impl;

import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.OrderDOMapper;
import com.miaoshaproject.dao.SequenceDOMapper;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.pojo.OrderDO;
import com.miaoshaproject.pojo.SequenceDO;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Beans;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional//使用事务创建商品
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException {
        //1.检测下单状态，下单的商品，用户是否存在，数量是否合法
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null){
            throw new BusinessException(EmBusinessError.ITEM_NOT_EXIT);
        }

        UserModel userModel = userService.getUserById(userId);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        if(amount <= 0 || amount > 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"订单数量信息错误");
        }

        //2.落单减库存，支付减库存
        boolean result = itemService.decreaseStock(itemId,amount);
        if(!result){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"库存不够");
        }
        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setAmount(amount);
        orderModel.setItemId(itemId);
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setUserId(userId);
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));
            //生成交易流水号，订单号



            //存入数据库
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //销量增加
        itemService.increaseSales(itemId,amount);

        //4.返回前端
        return orderModel;
    }

    //生成订单号
    //事务处理的回滚应该是内部事务不回滚，为了保证自增序列的全局唯一性
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNo(){
        StringBuilder id = new StringBuilder();
        //订单号有16位
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        id.append(now.format(DateTimeFormatter.ISO_DATE).replace("-",""));
        //中间6位为递增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());//当前序列已经拿走了，需要增加步长
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);

        String sequenceStr = String.valueOf(sequence);
        for(int i = 0; i < 6-sequenceStr.length(); i++){
            id.append(0);
        }
        id.append(sequenceStr);
        //后面2位为分库分表位
        id.append("00");

        return id.toString();
    }


    private OrderDO convertFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        return orderDO;
    }
}
