package cn.wwq.service;

import cn.wwq.pojo.Carousel;
import cn.wwq.pojo.Category;
import cn.wwq.pojo.vo.CategoryVO;
import cn.wwq.pojo.vo.NewItemsVO;

import java.util.List;

public interface CategoryService {
    /**
     * 查询所有一级分类
     *
     * @return
     */
    public List<Category> queryAllRootLevelCat();

    /**
     * 根据一级分类ID 查询二级分类
     * @param rootCatId
     * @return
     */
    public List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询首页每个一级分类下的6条最新商品数据
     * @param rootCatId
     * @return
     */
    public List<NewItemsVO> getSixNewItemLazy(Integer rootCatId);
}
