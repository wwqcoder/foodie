package cn.wwq.controller.center;

import cn.wwq.controller.BaseController;
import cn.wwq.enums.YesOrNo;
import cn.wwq.pojo.OrderItems;
import cn.wwq.pojo.Orders;
import cn.wwq.pojo.bo.center.OrderItemsCommentBO;
import cn.wwq.pojo.vo.MyCommentVO;
import cn.wwq.service.center.MyCommentsService;
import cn.wwq.service.center.MyOrdersService;
import cn.wwq.utils.IMOOCJSONResult;
import cn.wwq.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "用户中心评价模块",tags = {"用户中心评价模块相关的接口"})
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    @Autowired
    private MyCommentsService MyCommentsService;

    @ApiOperation(value = "查询订单列表",notes = "查询订单列表",httpMethod = "POST")
    @PostMapping("/pending")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId",value = "用户ID",required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId",value = "订单ID",required = true)
            @RequestParam String orderId){

        //判断用户和订单是否关联
        IMOOCJSONResult res = checkUserOrder(userId, orderId);
        if (res.getStatus() != HttpStatus.OK.value()){
            return res;
        }

        //判断该笔订单是否已经评价过，评价过了就不再继续。
        Orders myOrder = (Orders) res.getData();
        if (myOrder.getIsComment() == YesOrNo.YES.type){
            return IMOOCJSONResult.errorMsg("该笔订单已经被评价过");
        }
        List<OrderItems> list = MyCommentsService.queryPendingComment(orderId);
        return IMOOCJSONResult.ok(list);
    }

    // 商家发货没有后端，所以这个接口仅仅只是用于模拟
    @ApiOperation(value="保存评论列表", notes="保存评论列表", httpMethod = "POST")
    @PostMapping("/saveList")
    public IMOOCJSONResult saveList(
            @ApiParam(name = "userId",value = "用户ID",required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> commentList){

        System.out.println(commentList);

        //判断用户和订单是否关联
        IMOOCJSONResult res = checkUserOrder(userId, orderId);
        if (res.getStatus() != HttpStatus.OK.value()){
            return res;
        }

        if (commentList == null || commentList.isEmpty() || commentList.size() ==0){
            return IMOOCJSONResult.errorMsg("评论内容不能为空");
        }
        MyCommentsService.saveComments(userId,orderId,commentList);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "分页查询我的评价",notes = "分页查询我的评价",httpMethod = "POST")
    @PostMapping("/query")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId",value = "用户ID",required = true)
            @RequestParam String userId,
            @ApiParam(name = "page",value = "查询下一页的第几页",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "分页的每一页显示的条数",required = false)
            @RequestParam Integer pageSize){

        if (StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = SEARCH_PAGE_SIZE;
        }
        PagedGridResult grid = MyCommentsService.queryMyComments(userId, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }
}
