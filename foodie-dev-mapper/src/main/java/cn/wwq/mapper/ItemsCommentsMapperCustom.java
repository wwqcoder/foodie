package cn.wwq.mapper;

import cn.wwq.my.mapper.MyMapper;
import cn.wwq.pojo.ItemsComments;
import cn.wwq.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    public void saveComments(Map<String,Object> map);

    public List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String,Object> map);
}