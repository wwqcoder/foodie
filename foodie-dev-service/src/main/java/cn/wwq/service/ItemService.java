package cn.wwq.service;

import cn.wwq.pojo.*;
import cn.wwq.pojo.vo.CommentLevelCountVO;
import cn.wwq.pojo.vo.ItemCommentVO;
import cn.wwq.pojo.vo.ShopcartVO;
import cn.wwq.utils.PagedGridResult;

import java.util.List;

public interface ItemService {

    /**
     * 根据商品ID查询详情
     * @param itemId
     * @return
     */
    public Items queryItemById(String itemId);

    /**
     * 根据商品ID查询商品图片列表
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品ID查询商品规格列表
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品ID查询商品参数
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品ID查询商品的评价等级数量
     * @param itemId
     * @return
     */
    public CommentLevelCountVO queryCommentCounts(String itemId);

    /**
     * 根据商品ID分页查询商品的评价
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagedComments(String itemId, Integer level,
                                              Integer page, Integer pageSize);
    /**
     * 根据关键字和排序规则分页查询商品列表
     * @param keywords
     * @param sort
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort,
                                              Integer page, Integer pageSize);

    /**
     * 根据分类ID分页查询商品列表
     * @param catId
     * @param sort
     * @return
     */
    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort,
                                       Integer page, Integer pageSize);

    /**
     * 根据规格ids查询最新购物车中的商品数据（用于渲染刷新购物车中的商品数据）
     * @param specIds
     * @return
     */
    public List<ShopcartVO> queryItemBySpecIds(String specIds);

    /**
     * 根据商品规格ID获取规格对象的具体信息
     * @param specId
     * @return
     */
    public ItemsSpec queryItemSpecBySpecId(String specId);

    /**
     * 根据商品ID获得商品主图的URL
     * @param itemId
     * @return
     */
    public String queryItemMainImgById(String itemId);

    /**
     * 减少库存
     * @param specId
     * @param buyCounts
     */
    public void decreaseItemSpecStock(String specId,Integer buyCounts);

}
