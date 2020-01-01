package cn.wwq.controller;

import cn.wwq.pojo.Orders;
import cn.wwq.pojo.Users;
import cn.wwq.pojo.vo.UsersVO;
import cn.wwq.service.center.MyOrdersService;
import cn.wwq.utils.IMOOCJSONResult;
import cn.wwq.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.util.UUID;

@Controller
public class BaseController {

    @Autowired
    private RedisOperator redisOperator;

    public static final String FOODIE_SHOPCART = "shopcart";
    public static final Integer COMMENT_PAGE_SIZE = 10;
    public static final Integer SEARCH_PAGE_SIZE = 20;

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    //支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    //微信支付成功  -》支付中心 -》 天天吃货平台
    //                         ｜ 回调通知的URL
    String payReturnUrl = "http://www.api.t.com/foodie-dev-api/orders/notifyMerchantOrderPaid";
    //用户上传头像的地址
    public static final String IMAGE_USER_FACE_LOCATION = File.separator+"Users"+File.separator+"wangweiqi"+File.separator+"IdeaProjects"+File.separator+"foodie-dev"+File.separator+"images";


    @Autowired
    public MyOrdersService myOrdersService;

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @return
     */
    public IMOOCJSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return IMOOCJSONResult.errorMsg("订单不存在！");
        }
        return IMOOCJSONResult.ok(order);
    }

    /**
     * 实现从users 到usersVO的转换
     * 将用户的token存入redis
     * @param users
     * @return
     */
    public UsersVO convert(Users users){
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + users.getId(),uniqueToken);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }

}
