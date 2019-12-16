package cn.wwq.controller;

import cn.wwq.pojo.Users;
import cn.wwq.pojo.bo.ShopcartBO;
import cn.wwq.pojo.bo.UserBO;
import cn.wwq.service.UserService;
import cn.wwq.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "注册登陆",tags = {"用于注册登陆的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

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

        //TODO 生成用户token，存入redis会话

        // 同步购物车数据
        synchShopCartData(userResult.getId(),request,response);

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

        //TODO 生成用户token，存入redis会话

        // 同步购物车数据
        synchShopCartData(userResult.getId(),request,response);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户退出登陆",notes = "用户退出登陆",httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response
                                                            ){

        //清除用户相关的cookie
        CookieUtils.deleteCookie(request,response,"user");

        //TODO 分布式会话中需要清除用户数据

        // 用户退出登陆，需要清空购物车
        CookieUtils.deleteCookie(request,response,FOODIE_SHOPCART);




        return IMOOCJSONResult.ok();
    }

    /**
     * 注册登陆成功后，同步cookie和redis中购物车的数据
     */
    private void synchShopCartData(String userId,HttpServletRequest request,
                                   HttpServletResponse response){
        /**
         * 1。redis中无数据，cookie中购物车无数据  不做任何处理
         *                  cookie中购物车有数据  此时直接放入redis中
         * 2。redis中有数据  cookie中购物车无数据  redis中的购物车覆盖本地cookie
         *                  cookie中购物车有数据 如果cookie中的某些商品在redis中存在，那么，
         *                                  以cookie为主，
         *                                  删除redis中的，把cookie中的商品直接覆盖redis
         * 3。同步到redis中以后，覆盖本地cookie购物车的数据，保证本地cookie购物车中的数据是同步最新的
         */

        //1。从redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        //2。从cookie中获取购物车
        String shopcartJsonCookie = CookieUtils.getCookieValue(request,FOODIE_SHOPCART,true);

        if (StringUtils.isBlank(shopcartJsonRedis)){
            //redis中无数据，cookie中购物车有数据  此时直接放入redis中
            if (StringUtils.isNotBlank(shopcartJsonCookie)){
                redisOperator.set(FOODIE_SHOPCART + ":" + userId,shopcartJsonCookie);
            }
        }else {
            //redis不为空，cookie不为空，合并cookie与redis中的购物车数据
            if (StringUtils.isNotBlank(shopcartJsonCookie)){
                /**
                 * 1。已经存在的，把cookie汇总对应的数量覆盖redis
                 * 2。该项商品标记为待删除，统一放入一个待删除的list中
                 * 3。从cookie中清除所有待删除的list
                 * 4。合并redis和cookie中的数据
                 * 5。更新到redis和cookie中
                 */
                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartJsonCookie, ShopcartBO.class);
                //待删除的list
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();
                for (ShopcartBO redisShopcart : shopcartListRedis){
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)){
                            //覆盖购买数量，不累加
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            //把cookieShopcart加入到待删除的列表，用于最后删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }
                    }
                }
                //从现有cookie中删除对应的覆盖过的数据
                shopcartListCookie.removeAll(pendingDeleteList);
                //合并两个list
                shopcartListRedis.addAll(shopcartListCookie);

                //更新redis与cookie
                CookieUtils.setCookie(request,response,FOODIE_SHOPCART,JsonUtils.objectToJson(shopcartListRedis),true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId,JsonUtils.objectToJson(shopcartListRedis));
            }else {
                //redis不为空，cookie为空,直接把redis覆盖cookie
                CookieUtils.setCookie(request,response,FOODIE_SHOPCART,shopcartJsonRedis,true);
            }
        }
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
