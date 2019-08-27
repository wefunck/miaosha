package com.miaoshaproject.dao;

import com.miaoshaproject.pojo.OrderDO;
import org.springframework.stereotype.Repository;

@Repository//注解
public interface OrderDOMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrderDO record);

    int insertSelective(OrderDO record);

    OrderDO selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderDO record);

    int updateByPrimaryKey(OrderDO record);
}