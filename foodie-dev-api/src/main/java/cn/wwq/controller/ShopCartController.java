package cn.wwq.controller;

import cn.wwq.pojo.bo.ShopcartBO;
import cn.wwq.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "购物车接口controller",tags = "购物车接口相关的API")
@RestController
@RequestMapping("shopcart")
public class ShopCartController {

    @ApiOperation(value = "添加商品到购物车",notes = "添加商品到购物车",httpMethod = "POST")
    @PostMapping("/add")
    public IMOOCJSONResult add(
            @RequestParam String userId,
            @RequestBody ShopcartBO shopcartBO,
            HttpServletRequest request,
            HttpServletResponse response){

        if (StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("");
        }

        System.out.println(shopcartBO);

        //TODO 前端用户在登陆的情况下，添加商品到购物车，会同时在后端同步到redis缓存
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "删除购物车中的商品",notes = "删除购物车中的商品",httpMethod = "POST")
    @PostMapping("/del")
    public IMOOCJSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response){

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)){
            return IMOOCJSONResult.errorMsg("");
        }

        //TODO 用户在页面删除购物车中的商品，如果此时用户已经登陆，则需要同步删除后端购物车中的商品

        return IMOOCJSONResult.ok();
    }
}
