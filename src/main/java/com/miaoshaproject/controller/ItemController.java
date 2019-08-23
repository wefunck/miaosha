package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.ItemVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true")
public class ItemController extends BaseController{

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/createItem",method ={RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title")String title,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "imgUrl") String imgUrl
                                       ) throws BusinessException {
        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setDescription(description);
        itemModel.setImgurl(imgUrl);
        itemModel.setTitle(title);

        ItemModel itemModelReturn = itemService.createItem(itemModel);

        ItemVO itemVO = convertFromItemModel(itemModelReturn);

        return CommonReturnType.create(itemVO);

    }
    private ItemVO convertFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        return itemVO;
    }
    //获得商品详情
    @RequestMapping(value = "/get",method ={RequestMethod.GET})
    @ResponseBody
    public CommonReturnType get(@RequestParam(name = "id")Integer id) throws BusinessException {
        ItemModel itemModel = itemService.getItemById(id);
        if(itemModel == null){
            throw new BusinessException(EmBusinessError.ITEM_NOT_EXIT);
        }
        ItemVO itemVO = convertFromItemModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    //商品列表页面浏览
    @RequestMapping(value = "/list",method ={RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem(){
        //使用stream api将list中的itemModel转化为itemVO
        List<ItemModel> itemModelList = itemService.listItem();
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel->{
           ItemVO itemVO = this.convertFromItemModel(itemModel);
           return itemVO;
        }).collect(Collectors.toList());

        return CommonReturnType.create(itemModelList);
    }


}
