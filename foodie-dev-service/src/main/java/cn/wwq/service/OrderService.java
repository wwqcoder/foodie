package cn.wwq.service;

import cn.wwq.pojo.OrderStatus;
import cn.wwq.pojo.bo.ShopcartBO;
import cn.wwq.pojo.bo.SubmitOrderBO;
import cn.wwq.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {
    /**
     * 用于创建订单
     *
     * @param shopcartList
     * @param submitOrderBO
     * @return
     */
    public OrderVO createOrder(List<ShopcartBO> shopcartList, SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId,Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时未支付订单
     */
    public void closeOrder();
}
