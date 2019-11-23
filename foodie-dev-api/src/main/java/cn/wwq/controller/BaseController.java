package cn.wwq.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;

@Controller
public class BaseController {


    public static final String FOODIE_SHOPCART = "shopcart";
    public static final Integer COMMENT_PAGE_SIZE = 10;
    public static final Integer SEARCH_PAGE_SIZE = 20;

    //支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    //微信支付成功  -》支付中心 -》 天天吃货平台
    //                         ｜ 回调通知的URL
    String payReturnUrl = "http://jc73nw.natappfree.cc/orders/notifyMerchantOrderPaid";
    //用户上传头像的地址
    public static final String IMAGE_USER_FACE_LOCATION = File.separator+"Users"+File.separator+"wangweiqi"+File.separator+"IdeaProjects"+File.separator+"foodie-dev"+File.separator+"images";


}
