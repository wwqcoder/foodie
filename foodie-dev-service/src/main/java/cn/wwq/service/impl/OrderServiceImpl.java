package cn.wwq.service.impl;

import cn.wwq.enums.OrderStatusEnum;
import cn.wwq.enums.YesOrNo;
import cn.wwq.mapper.OrderItemsMapper;
import cn.wwq.mapper.OrderStatusMapper;
import cn.wwq.mapper.OrdersMapper;
import cn.wwq.pojo.*;
import cn.wwq.pojo.bo.ShopcartBO;
import cn.wwq.pojo.bo.SubmitOrderBO;
import cn.wwq.pojo.vo.MerchantOrdersVO;
import cn.wwq.pojo.vo.OrderVO;
import cn.wwq.service.AddressService;
import cn.wwq.service.ItemService;
import cn.wwq.service.OrderService;
import cn.wwq.utils.DateUtil;
import cn.wwq.utils.RedisOperator;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private Sid sid;
    @Autowired
    AddressService addressService;
    @Autowired
    ItemService itemService;
    @Autowired
    private OrderItemsMapper orderItemsMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private RedisOperator redisOperator;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderVO createOrder(List<ShopcartBO> shopcartList, SubmitOrderBO submitOrderBO) {

        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        // 包邮费用设置为0
        Integer postAmount = 0;

        String orderId = sid.nextShort();

        UserAddress address = addressService.queryUserAddress(userId, addressId);
        //1.新订单数据保存
        Orders newOrders = new Orders();
        newOrders.setId(orderId);
        newOrders.setUserId(userId);
        newOrders.setReceiverName(address.getReceiver());
        newOrders.setReceiverMobile(address.getMobile());
        newOrders.setReceiverAddress(address.getProvince()+" "
                +address.getCity()+" "
                +address.getDistrict()+" "
                +address.getDetail());
        newOrders.setPostAmount(postAmount);
        newOrders.setPayMethod(payMethod);
        newOrders.setLeftMsg(leftMsg);
        newOrders.setIsComment(YesOrNo.NO.type);
        newOrders.setIsDelete(YesOrNo.NO.type);
        newOrders.setCreatedTime(new Date());
        newOrders.setUpdatedTime(new Date());
        //2。循环根据itemSpecIds保存订单商品信息表
        String[] itemSpecIdArr = itemSpecIds.split(",");
        //商品原价累计
        Integer totalAmount = 0;
        //优惠后的实付金额累计
        Integer realPayAmount = 0;
        List<ShopcartBO> toBeRemovedShopcartLIst = new ArrayList<>();
        for (String itemSpecId : itemSpecIdArr) {

            ShopcartBO cartItem = getBuyCountsFromShopcart(shopcartList, itemSpecId);
            //整合redis后，商品购买的数量从redis的购物车中获取
            int buyCounts = cartItem.getBuyCounts();
            toBeRemovedShopcartLIst.add(cartItem);

            //2.1 根据规格ID，查询规格的具体信息，主要获取价格
            ItemsSpec itemsSpec = itemService.queryItemSpecBySpecId(itemSpecId);
            totalAmount +=itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;

            //2.2 根据商品ID 获取商品信息以及商品图片
            String itemId = itemsSpec.getItemId();
            Items item = itemService.queryItemById(itemId);
            String imgUrl = itemService.queryItemMainImgById(itemId);

            //2.3循环保存自订单的数据
            String subOrderId = sid.nextShort();
            OrderItems subOrderItem = new OrderItems();
            subOrderItem.setId(subOrderId);
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setItemName(item.getItemName());
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setBuyCounts(buyCounts);
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemsSpec.getName());
            subOrderItem.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItem);
            //2.4 在用户提交订单后，规格表中需要扣除库存
            itemService.decreaseItemSpecStock(itemSpecId,buyCounts);
        }
       newOrders.setTotalAmount(totalAmount);
       newOrders.setRealPayAmount(realPayAmount);
       ordersMapper.insert(newOrders);

        //3。保存订单状态表
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        //4。构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + postAmount);
        merchantOrdersVO.setPayMethod(payMethod);

        OrderVO orderVO = new OrderVO();
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setOrderId(orderId);
        orderVO.setToBeRemovedShopcartLIst(toBeRemovedShopcartLIst);

        return orderVO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void closeOrder() {

        //查询所有未付款订单，判断时间是否超时（1天），超时则关闭交易
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> list = orderStatusMapper.select(orderStatus);
        for (OrderStatus os : list) {
            //获得订单创建时间
            Date createdTime = os.getCreatedTime();
            //和当前时间进行对比
            int days = DateUtil.daysBetween(createdTime, new Date());
            if (days >= 1){
                //超过1天，关闭订单
                doCloseOrder(os.getOrderId());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void doCloseOrder(String orderId){
        OrderStatus close = new OrderStatus();
        close.setOrderId(orderId);
        close.setOrderStatus(OrderStatusEnum.CLOSE.type);
        close.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(close);
    }

    /**
     * 从redis中的购物车中获取商品，目的：counts
     * @param shopcartList
     * @param specId
     * @return
     */
    private ShopcartBO getBuyCountsFromShopcart(List<ShopcartBO> shopcartList,String specId){
        for (ShopcartBO sc : shopcartList) {
            if (sc.getSpecId().equals(specId)){
                return sc;
            }
        }
        return null;
    }


}
