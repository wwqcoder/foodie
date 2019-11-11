package cn.wwq.controller;

import cn.wwq.pojo.Users;
import cn.wwq.pojo.bo.UserBO;
import cn.wwq.service.UserService;
import cn.wwq.utils.CookieUtils;
import cn.wwq.utils.IMOOCJSONResult;
import cn.wwq.utils.JsonUtils;
import cn.wwq.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登陆",tags = {"用于注册登陆的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户名是否存在",notes = "用户名是否存在",httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username){

        //判断用户名不能为空
        if (StringUtils.isBlank(username)){
                return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        //2。查找注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist){
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }
        //3.请求成功，用户名没有重复
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户注册",notes = "用户注册",httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO,HttpServletRequest request,
                                  HttpServletResponse response){

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        //0.判断用户名密码必须不为空
        if(StringUtils.isBlank(username)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(confirmPwd)){
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }

        //1。查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist){
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }

        //2。密码长度不能少于六位
        if (password.length() < 6){
            return IMOOCJSONResult.errorMsg("密码长度不能少于六位");
        }

        //3。判断两次密码是否一致
        if (!password.equals(confirmPwd)){
            return IMOOCJSONResult.errorMsg("两次密码输入不一致");
        }
        //4。实现注册
        Users userResult = userService.createUser(userBO);

        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(userResult),true);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户登陆",notes = "用户登陆",httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        //0.判断用户名密码必须不为空
        if(StringUtils.isBlank(username)
                || StringUtils.isBlank(password)){
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }

        //4。实现注册
        Users userResult = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));

        if (null == userResult){
            return IMOOCJSONResult.errorMsg("用户名或者密码不正确");
        }

        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(userResult),true);
        return IMOOCJSONResult.ok();
    }

    private Users setNullProperty(Users userResult){
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
