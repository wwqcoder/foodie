package cn.wwq.mapper;

import cn.wwq.my.mapper.MyMapper;
import cn.wwq.pojo.Category;
import cn.wwq.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CategoryMapperCustom {

    public List getSubCatList(Integer rootCatId);

    public List<NewItemsVO> getSixNewItemLazy(@Param("paramsMap") Map<String,Object> map);
}