package cn.wwq.interceptor;

import cn.wwq.utils.IMOOCJSONResult;
import cn.wwq.utils.JsonUtils;
import cn.wwq.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class UserTokenInterceptor implements HandlerInterceptor {


    @Autowired
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    /**
     * 拦截请求在访问之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if (StringUtils.isNotBlank(userId) &&
                StringUtils.isNotBlank(userId)
           ){

            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(uniqueToken)){
                returnErrorResponse(response,IMOOCJSONResult.errorMsg("请登录..."));
                return false;
            }else {
                if (!uniqueToken.equals(userToken)){
                    returnErrorResponse(response,IMOOCJSONResult.errorMsg("账号可能在异地登陆"));
                    return false;
                }
            }
        }else {
            returnErrorResponse(response,IMOOCJSONResult.errorMsg("请登录..."));
            return false;
        }
        /**
         * false ：请求被拦截
         * true： 请求经过校验以后，放行
         */
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, IMOOCJSONResult result){

        OutputStream out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("UTF-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (null != out){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 访问之后，在渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
