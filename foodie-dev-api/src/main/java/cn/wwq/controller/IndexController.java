package cn.wwq.controller;

import cn.wwq.enums.YesOrNo;
import cn.wwq.pojo.Carousel;
import cn.wwq.pojo.Category;
import cn.wwq.pojo.vo.CategoryVO;
import cn.wwq.pojo.vo.NewItemsVO;
import cn.wwq.service.CarouselService;
import cn.wwq.service.CategoryService;
import cn.wwq.utils.IMOOCJSONResult;
import cn.wwq.utils.JsonUtils;
import cn.wwq.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(value = "首页",tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 防止缓存穿透
     * @return
     */
    @ApiOperation(value = "获取首页轮播图列表",notes = "获取首页轮播图列表",httpMethod = "GET")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel(){

        String carouselStr = redisOperator.get("carousel");
        List<Carousel> list = null;
        if (StringUtils.isBlank(carouselStr)){
            list = carouselService.queryAll(YesOrNo.YES.type);
            if (null != list && list.size() > 0){
                redisOperator.set("carousel", JsonUtils.objectToJson(list));
            }
            redisOperator.set("carousel", JsonUtils.objectToJson(list),5*60);
        }else {
            list = JsonUtils.jsonToList(carouselStr, Carousel.class);
        }

        return IMOOCJSONResult.ok(list);
    }

    /**
     * 1。广告图发生改变，删除缓存，然后重置
     * 2。定时重置
     * 3。每个轮播图有可能是一个广告，可能会过期，过期后在重置。
     */


    /**
     * 首页分类展示需求
     * 1。第一次刷新主页查询大分类，渲染展示到首页
     * 2。如果鼠标上移到大分类，则加载其自分类的内容，
     *   如果已经存在自分类，那就不需要加载。(懒加载)
     */
    @ApiOperation(value = "获取商品分类(一级分类)",notes = "获取商品分类(一级分类)",httpMethod = "GET")
    @GetMapping("/cats")
    public IMOOCJSONResult cats(){

        String categoryStr = redisOperator.get("category");
        List<Category> list = null;
        if (StringUtils.isBlank(categoryStr)){
            list = categoryService.queryAllRootLevelCat();
            redisOperator.set("category", JsonUtils.objectToJson(list));
        }else {
            list = JsonUtils.jsonToList(categoryStr, Category.class);
        }
        return IMOOCJSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品子分类",notes = "获取商品子分类",httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(
            @ApiParam(name = "rootCatId",value = "一级分类ID",required = true)
            @PathVariable Integer rootCatId){

        if (null == rootCatId){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        String subCategoryStr = redisOperator.get("subCategory:"+rootCatId);
        List<CategoryVO> list = null;
        if (StringUtils.isBlank(subCategoryStr)){
            list = categoryService.getSubCatList(rootCatId);
            redisOperator.set("subCategory:"+rootCatId, JsonUtils.objectToJson(list));
        }else {
            list = JsonUtils.jsonToList(subCategoryStr, CategoryVO.class);
        }
        return IMOOCJSONResult.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条数据",notes = "查询每个一级分类下的最新6条数据",httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(
            @ApiParam(name = "rootCatId",value = "一级分类ID",required = true)
            @PathVariable Integer rootCatId){

        if (null == rootCatId){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> list = categoryService.getSixNewItemLazy(rootCatId);
        return IMOOCJSONResult.ok(list);
    }
}
