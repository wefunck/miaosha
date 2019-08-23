package com.miaoshaproject.dao;

import com.miaoshaproject.pojo.ItemDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemDO record);

    int insertSelective(ItemDO record);

    ItemDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemDO record);

    int updateByPrimaryKey(ItemDO record);

    List<ItemDO>  listItem();
}