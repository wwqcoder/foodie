package cn.wwq.controller;

import cn.wwq.enums.OrderStatusEnum;
import cn.wwq.enums.PayMethod;
import cn.wwq.pojo.OrderStatus;
import cn.wwq.pojo.UserAddress;
import cn.wwq.pojo.bo.AddressBO;
import cn.wwq.pojo.bo.ShopcartBO;
import cn.wwq.pojo.bo.SubmitOrderBO;
import cn.wwq.pojo.vo.MerchantOrdersVO;
import cn.wwq.pojo.vo.OrderVO;
import cn.wwq.service.AddressService;
import cn.wwq.service.OrderService;
import cn.wwq.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "订单相关",tags = "订单相关的API")
@RestController
@RequestMapping("orders")
public class OrdersController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;


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

        //System.out.println(submitOrderBO);
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopcartJson)) {
            return IMOOCJSONResult.errorMsg("购物数据不正确");
        }
        List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);

        // 1.创建订单
        OrderVO orderVO = orderService.createOrder(shopcartList,submitOrderBO);
        String orderId = orderVO.getOrderId();
        List<ShopcartBO> toBeRemovedShopcartLIst = orderVO.getToBeRemovedShopcartLIst();


        // 2。创建订单以后，移除购物车中已结算的商品

        /**
         * 1001,1002,1003
         *
         *  用户购买成功的商品在购物车中移除
         */

        //清理覆盖现有的redis中的购物车数据
        shopcartList.removeAll(toBeRemovedShopcartLIst);
        redisOperator.set(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId(), JsonUtils.objectToJson(shopcartList));
        // 整合redis之后，完善购物车中已结算商品的移除，并且同步到前端的cookie
        CookieUtils.setCookie(request,response,FOODIE_SHOPCART,JsonUtils.objectToJson(shopcartList),true);



        // 3。向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);
        //方便测试，支付1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId","imooc");
        headers.add("password","imooc");

        HttpEntity<MerchantOrdersVO> entity =
                new HttpEntity<>(merchantOrdersVO,headers);

        ResponseEntity<IMOOCJSONResult> body = restTemplate.postForEntity(paymentUrl, entity, IMOOCJSONResult.class);
        IMOOCJSONResult result = body.getBody();
        if (result.getStatus() != 200){
            return IMOOCJSONResult.errorMsg("支付中心订单创建失败，请联系管理员");
        }
        return IMOOCJSONResult.ok(orderId);
    }

    @ApiOperation(value = "通知商户修改订单状态",notes = "通知商户修改订单状态",httpMethod = "POST")
    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId){
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public IMOOCJSONResult getPaidOrderInfo(String orderId){
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return IMOOCJSONResult.ok(orderStatus);
    }



}
