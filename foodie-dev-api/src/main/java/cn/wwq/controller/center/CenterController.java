package cn.wwq.controller.center;

import cn.wwq.pojo.Users;
import cn.wwq.service.center.CenterUserService;
import cn.wwq.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(value = "center - 用户中心",tags = {"用户中心相关的接口"})
@RestController
@RequestMapping("center")
public class CenterController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "获取用户信息",notes = "获取用户信息",httpMethod = "GET")
    @GetMapping("userInfo")
    public IMOOCJSONResult userInfo(
            @ApiParam(name = "userId",value = "用户ID",required = true)
           @RequestParam("userId") String userId){

        Users users = centerUserService.queryUserInfo(userId);

        return IMOOCJSONResult.ok(users);

    }
}
