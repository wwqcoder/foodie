package cn.wwq.controller;

import cn.wwq.enums.YesOrNo;
import cn.wwq.pojo.*;
import cn.wwq.pojo.vo.CategoryVO;
import cn.wwq.pojo.vo.CommentLevelCountVO;
import cn.wwq.pojo.vo.ItemInfoVO;
import cn.wwq.pojo.vo.NewItemsVO;
import cn.wwq.service.CarouselService;
import cn.wwq.service.CategoryService;
import cn.wwq.service.ItemService;
import cn.wwq.utils.IMOOCJSONResult;
import cn.wwq.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品接口",tags = {"商品信息展示的相关接口"})
@RestController
@RequestMapping("items")
public class ItemsController extends BaseController {

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品评价等级",notes = "查询商品评价等级",httpMethod = "GET")
    @GetMapping("/commentLevel")
    public IMOOCJSONResult commentLevel(
            @ApiParam(name = "itemId",value = "商品ID",required = true)
            @RequestParam String itemId){

        if (StringUtils.isBlank(itemId)){
            return IMOOCJSONResult.errorMsg("商品不存在");
        }

        CommentLevelCountVO countVO = itemService.queryCommentCounts(itemId);

        return IMOOCJSONResult.ok(countVO);
    }

    @ApiOperation(value = "查询商品详情",notes = "查询商品详情",httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public IMOOCJSONResult info(
            @ApiParam(name = "itemId",value = "商品ID",required = true)
            @PathVariable String itemId){

        if (StringUtils.isBlank(itemId)){
            return IMOOCJSONResult.errorMsg("商品不存在");
        }

        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemsImgList = itemService.queryItemImgList(itemId);

        List<ItemsSpec> itemsSpecList = itemService.queryItemSpecList(itemId);

        ItemsParam itemsParam = itemService.queryItemParam(itemId);


        ItemInfoVO itemInfoVO = new ItemInfoVO();

        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemsImgList);
        itemInfoVO.setItemSpecList(itemsSpecList);
        itemInfoVO.setItemParams(itemsParam);

        return IMOOCJSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "分页查询商品评论",notes = "分页查询商品评论",httpMethod = "GET")
    @GetMapping("/comments")
    public IMOOCJSONResult comments(
            @ApiParam(name = "itemId",value = "商品ID",required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level",value = "评价等级",required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page",value = "查询下一页的第几页",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "分页的每一页显示的条数",required = false)
            @RequestParam Integer pageSize){

        if (StringUtils.isBlank(itemId)){
            return IMOOCJSONResult.errorMsg("商品不存在");
        }
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = COMMENT_PAGE_SIZE;
        }
        PagedGridResult grid = itemService.queryPagedComments(itemId, level,
                page, pageSize);

        return IMOOCJSONResult.ok(grid);
    }

    @ApiOperation(value = "根据关键字和排序规则分页查询商品列表",notes = "根据关键字和排序规则分页查询商品列表",httpMethod = "GET")
    @GetMapping("/search")
    public IMOOCJSONResult comments(
            @ApiParam(name = "keywords",value = "关键字",required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort",value = "排序",required = false)
            @RequestParam String sort,
            @ApiParam(name = "page",value = "查询下一页的第几页",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "分页的每一页显示的条数",required = false)
            @RequestParam Integer pageSize){

        if (StringUtils.isBlank(keywords)){
            return IMOOCJSONResult.errorMsg("商品不存在");
        }
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = SEARCH_PAGE_SIZE;
        }
        PagedGridResult grid = itemService.searchItems(keywords,sort,
                page,pageSize);

        return IMOOCJSONResult.ok(grid);
    }

    @ApiOperation(value = "根据分类ID分页查询商品列表",notes = "根据分类ID分页查询商品列表",httpMethod = "GET")
    @GetMapping("/catItems")
    public IMOOCJSONResult catItems(
            @ApiParam(name = "catId",value = "三级分类ID",required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort",value = "排序",required = false)
            @RequestParam String sort,
            @ApiParam(name = "page",value = "查询下一页的第几页",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "分页的每一页显示的条数",required = false)
            @RequestParam Integer pageSize){

        if (catId == null){
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = SEARCH_PAGE_SIZE;
        }
        PagedGridResult grid = itemService.searchItemsByThirdCat(catId,sort,
                page,pageSize);

        return IMOOCJSONResult.ok(grid);
    }



}
