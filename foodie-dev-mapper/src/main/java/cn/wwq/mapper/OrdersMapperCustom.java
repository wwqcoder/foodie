package cn.wwq.mapper;

import cn.wwq.pojo.vo.ItemCommentVO;
import cn.wwq.pojo.vo.MyOrdersVO;
import cn.wwq.pojo.vo.SearchItemsVO;
import cn.wwq.pojo.vo.ShopcartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrdersMapperCustom {

    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String,Object> map);




}