package cn.wwq.service.impl.center;

import cn.wwq.enums.YesOrNo;
import cn.wwq.mapper.ItemsCommentsMapperCustom;
import cn.wwq.mapper.OrderItemsMapper;
import cn.wwq.mapper.OrderStatusMapper;
import cn.wwq.mapper.OrdersMapper;
import cn.wwq.pojo.OrderItems;
import cn.wwq.pojo.OrderStatus;
import cn.wwq.pojo.Orders;
import cn.wwq.pojo.bo.center.OrderItemsCommentBO;
import cn.wwq.pojo.vo.MyCommentVO;
import cn.wwq.service.center.MyCommentsService;
import cn.wwq.utils.PagedGridResult;
import com.github.pagehelper.PageHelper;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<OrderItems> queryPendingComment(String orderId) {

        OrderItems query = new OrderItems();
        query.setOrderId(orderId);
        return orderItemsMapper.select(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveComments(String userId, String orderId,
                             List<OrderItemsCommentBO> commentList) {
        //1.保存评价 items_comments
        for (OrderItemsCommentBO oic : commentList) {
            oic.setCommentId(sid.nextShort());
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("commentList",commentList);
        map.put("userId",userId);
        itemsCommentsMapperCustom.saveComments(map);
        //2。修改订单表，改为已评价
        Orders order = new Orders();
        order.setId(orderId);
        order.setIsComment(YesOrNo.YES.type);
        ordersMapper.updateByPrimaryKeySelective(order);
        //3。修改订单状态表的留言时间
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        PageHelper.startPage(page,pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);
        return setterPagedGrid(list,page);
    }
}
