package cn.wwq.pojo.vo;

import cn.wwq.pojo.bo.ShopcartBO;

import java.util.List;

public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopcartBO> toBeRemovedShopcartLIst;

    public List<ShopcartBO> getToBeRemovedShopcartLIst() {
        return toBeRemovedShopcartLIst;
    }

    public void setToBeRemovedShopcartLIst(List<ShopcartBO> toBeRemovedShopcartLIst) {
        this.toBeRemovedShopcartLIst = toBeRemovedShopcartLIst;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }
}