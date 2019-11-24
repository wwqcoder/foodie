package cn.wwq.service.center;

import cn.wwq.pojo.OrderItems;
import cn.wwq.pojo.Orders;
import cn.wwq.pojo.bo.center.OrderItemsCommentBO;
import cn.wwq.utils.PagedGridResult;

import java.util.List;

public interface MyCommentsService {

    /**
     * 根据订单ID 查询关联的商品
     * @param orderId
     * @return
     */
    public List<OrderItems> queryPendingComment(String orderId);

    /**
     * 保存评论
     * @param userId
     * @param orderId
     * @param commentList
     */
    public void saveComments(String userId,String orderId,List<OrderItemsCommentBO> commentList);

    /**
     * 分页查询我的评论
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyComments(String userId,Integer page,
                                           Integer pageSize);
}
