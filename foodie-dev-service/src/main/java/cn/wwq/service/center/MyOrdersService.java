package cn.wwq.service.center;

import cn.wwq.pojo.Orders;
import cn.wwq.pojo.Users;
import cn.wwq.pojo.bo.center.CenterUserBO;
import cn.wwq.utils.PagedGridResult;

public interface MyOrdersService {

    /**
     * 根据用户ID分页查询订单
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyOrders(String userId,Integer orderStatus,
                                         Integer page,Integer pageSize);

    /**
     * @Description: 订单状态 --> 商家发货
     */
    public void updateDeliverOrderStatus(String orderId);

    /**
     * 查询我的订单
     *
     * @param userId
     * @param orderId
     * @return
     */
    public Orders queryMyOrder(String userId, String orderId);

    /**
     * 更新订单状态 —> 确认收货
     *
     * @return
     */
    public boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单（逻辑删除）
     * @param userId
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String userId, String orderId);
}
