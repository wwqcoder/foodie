package cn.wwq.mapper;

import cn.wwq.my.mapper.MyMapper;
import cn.wwq.pojo.Category;

import java.util.List;

public interface CategoryMapperCustom {

    public List getSubCatList(Integer rootCatId);
}