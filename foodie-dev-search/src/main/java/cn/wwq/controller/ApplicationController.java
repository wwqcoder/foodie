package cn.wwq.controller;

import cn.wwq.service.ItemsESService;
import cn.wwq.utils.IMOOCJSONResult;
import cn.wwq.utils.PagedGridResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("items")
public class ApplicationController {

    @Autowired
    private ItemsESService itemsESService;

    @GetMapping("/hello")
    public String hello(){
        return "Hello World";
    }

    @ApiOperation(value = "根据关键字和排序规则分页查询商品列表",notes = "根据关键字和排序规则分页查询商品列表",httpMethod = "GET")
    @GetMapping("/es/search")
    public IMOOCJSONResult comments(
                            String keywords,
                            String sort,
                            Integer page,
                            Integer pageSize){

        if (StringUtils.isBlank(keywords)){
            return IMOOCJSONResult.errorMsg("商品不存在");
        }
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = 20;
        }

        page --;

        PagedGridResult grid = itemsESService.searchItems(keywords,sort,
                page,pageSize);

        return IMOOCJSONResult.ok(grid);
    }
}
