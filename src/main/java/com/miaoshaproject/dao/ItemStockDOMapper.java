package com.miaoshaproject.dao;

import com.miaoshaproject.pojo.ItemStockDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemStockDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemStockDO record);

    int insertSelective(ItemStockDO record);

    ItemStockDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemStockDO record);

    int updateByPrimaryKey(ItemStockDO record);

    ItemStockDO selectByItemId(Integer itemId);

    int decreaseStock(@Param("itemId")Integer itemId, @Param("amount")Integer amount);
}