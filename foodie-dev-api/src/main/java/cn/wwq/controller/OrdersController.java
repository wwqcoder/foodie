package cn.wwq.controller;

import cn.wwq.enums.PayMethod;
import cn.wwq.pojo.UserAddress;
import cn.wwq.pojo.bo.AddressBO;
import cn.wwq.pojo.bo.SubmitOrderBO;
import cn.wwq.service.AddressService;
import cn.wwq.service.OrderService;
import cn.wwq.utils.CookieUtils;
import cn.wwq.utils.IMOOCJSONResult;
import cn.wwq.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "订单相关",tags = "订单相关的API")
@RestController
@RequestMapping("orders")
public class OrdersController extends BaseController {

    @Autowired
    private OrderService orderService;


    /**
     * 用户在确认订单页面，可以针对收货地址做如下操作
     *   1。查询用户的所有收货地址列表
     *   2。新增收货地址
     *   3。删除收货地址
     *   4。修改收货地址
     *   5。设置默认地址
     */

    @ApiOperation(value = "用户下单",notes = "用户下单",httpMethod = "POST")
    @PostMapping("create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                    HttpServletRequest request,
                                    HttpServletResponse response
                                    ){

        if (submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type
        && submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type){

            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }

        System.out.println(submitOrderBO);
        // 1.创建订单
        String orderId = orderService.createOrder(submitOrderBO);

        // 2。创建订单以后，移除购物车中已结算的商品

        /**
         * 1001,1002,1003
         *
         *  用户购买成功的商品在购物车中移除
         */

        //TODO 整合redis之后，完善购物车中已结算商品的移除，并且同步到前端的cookie
        //CookieUtils.setCookie(request,response,FOODIE_SHOPCART,"",true);
        // 3。向支付中心发送当前订单，用于保存支付中心的订单数据

        return IMOOCJSONResult.ok(orderId);
    }



}
