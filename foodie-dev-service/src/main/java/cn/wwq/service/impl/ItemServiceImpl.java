package cn.wwq.service.impl;

import cn.wwq.enums.CommentLevel;
import cn.wwq.enums.YesOrNo;
import cn.wwq.mapper.*;
import cn.wwq.pojo.*;
import cn.wwq.pojo.vo.CommentLevelCountVO;
import cn.wwq.pojo.vo.ItemCommentVO;
import cn.wwq.pojo.vo.SearchItemsVO;
import cn.wwq.pojo.vo.ShopcartVO;
import cn.wwq.service.ItemService;
import cn.wwq.utils.DesensitizationUtil;
import cn.wwq.utils.PagedGridResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ItemsImgMapper itemsImgMapper;
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;
    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;



    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ItemsImg> queryItemImgList(String itemId) {

        Example itemImgExp = new Example(ItemsImg.class);
        Example.Criteria criteria = itemImgExp.createCriteria();
        criteria.andEqualTo("itemId",itemId);
        return itemsImgMapper.selectByExample(itemImgExp);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example itemSpecExp = new Example(ItemsSpec.class);
        Example.Criteria criteria = itemSpecExp.createCriteria();
        criteria.andEqualTo("itemId",itemId);
        return itemsSpecMapper.selectByExample(itemSpecExp);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ItemsParam queryItemParam(String itemId) {
        Example itemParamExp = new Example(ItemsParam.class);
        Example.Criteria criteria = itemParamExp.createCriteria();
        criteria.andEqualTo("itemId",itemId);
        return itemsParamMapper.selectOneByExample(itemParamExp);    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public CommentLevelCountVO queryCommentCounts(String itemId) {

        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.type);
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.type);
        Integer badCounts = getCommentCounts(itemId,CommentLevel.BAD.type);
        Integer totalCounts = goodCounts + normalCounts + badCounts;

        CommentLevelCountVO commentLevelCountVO = new CommentLevelCountVO();
        commentLevelCountVO.setTotalCounts(totalCounts);
        commentLevelCountVO.setGoodCounts(goodCounts);
        commentLevelCountVO.setNormalCounts(normalCounts);
        commentLevelCountVO.setBadCounts(badCounts);
        return commentLevelCountVO;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedGridResult queryPagedComments(String itemId, Integer level,Integer page,Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("itemId",itemId);
        map.put("level",level);

        // mybatis-pagehelper

        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);
        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);

        for (ItemCommentVO itemCommentVO : list) {
            String nickName = DesensitizationUtil.commonDisplay(itemCommentVO.getNickname());
            itemCommentVO.setNickname(nickName);
        }
        return setterPagedGrid(list,page);

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("keywords",keywords);
        map.put("sort",sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);
        return setterPagedGrid(list,page);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("catId",catId);
        map.put("sort",sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCat(map);
        return setterPagedGrid(list,page);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ShopcartVO> queryItemBySpecIds(String specIds) {

        String[] ids = specIds.split(",");
        ArrayList<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList,ids);
        return itemsMapperCustom.queryItemBySpecIds(specIdsList);

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ItemsSpec queryItemSpecBySpecId(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getCommentCounts(String itemId,Integer level){

        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);
        if (null != level){
            condition.setCommentLevel(level);
        }
        return itemsCommentsMapper.selectCount(condition);
    }

    private PagedGridResult setterPagedGrid(List<?> list,Integer page){
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public String queryItemMainImgById(String itemId) {

        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.type);
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result != null ? result.getUrl() : "";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void decreaseItemSpecStock(String specId, Integer buyCounts) {

        //synchronized 不推荐 集群下无用，性能低下
        //锁数据库 不推荐，导致数据库性能低下
        //分布式锁 zookeeper redis

        //lockutil.getLock()  --加锁

        //1.查询库存

        //2。判断库存，是否能够减少到0一下

        //lockutil.unLock()  --解锁

        int result = itemsMapperCustom.decreaseItemSpecStock(buyCounts, specId);
        if (result != 1){
            throw new RuntimeException("订单创建失败，原因：库存不足");
        }
    }
}
