package cn.wwq.service;

import cn.wwq.pojo.Carousel;
import cn.wwq.pojo.bo.SubmitOrderBO;

import java.util.List;

public interface OrderService {
    /**
     * 用于创建订单
     * @param submitOrderBO
     * @return
     */
    public String createOrder(SubmitOrderBO submitOrderBO);
}
