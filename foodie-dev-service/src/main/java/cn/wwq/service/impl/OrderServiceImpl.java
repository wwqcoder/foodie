package cn.wwq.service.impl;

import cn.wwq.enums.OrderStatusEnum;
import cn.wwq.enums.YesOrNo;
import cn.wwq.mapper.OrderItemsMapper;
import cn.wwq.mapper.OrderStatusMapper;
import cn.wwq.mapper.OrdersMapper;
import cn.wwq.pojo.*;
import cn.wwq.pojo.bo.SubmitOrderBO;
import cn.wwq.service.AddressService;
import cn.wwq.service.ItemService;
import cn.wwq.service.OrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String createOrder(SubmitOrderBO submitOrderBO) {

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
        for (String itemSpecId : itemSpecIdArr) {

            //TODO 整合redis后，商品购买的数量从redis的购物车中获取
            int buyCounts = 1;

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

        return orderId;
    }
}
