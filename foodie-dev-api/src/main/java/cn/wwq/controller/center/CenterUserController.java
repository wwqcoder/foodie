package cn.wwq.controller.center;

import cn.wwq.controller.BaseController;
import cn.wwq.pojo.Users;
import cn.wwq.pojo.bo.center.CenterUserBO;
import cn.wwq.resource.FileUpload;
import cn.wwq.service.center.CenterUserService;
import cn.wwq.utils.CookieUtils;
import cn.wwq.utils.DateUtil;
import cn.wwq.utils.IMOOCJSONResult;
import cn.wwq.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.wwq.controller.BaseController.IMAGE_USER_FACE_LOCATION;


@Api(value = "用户信息接口",tags = {"用户信息相关的接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "修改用户信息",notes = "修改用户信息",httpMethod = "POST")
    @PostMapping("update")
    public IMOOCJSONResult update(
            @ApiParam(name = "userId",value = "用户ID",required = true)
            @RequestParam("userId") String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response){
        //判断BindingResult是否保存错误的验证，有，则直接返回
        if (result.hasErrors()){
            Map<String, String> errorMap = getError(result);
            return IMOOCJSONResult.errorMap(errorMap);

        }
        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);

        userResult = setNullProperty(userResult);

        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(userResult),true);

        //TODO 后续要改，增加令牌token，整合redis，分布式会话
        return IMOOCJSONResult.ok();

    }

    @ApiOperation(value = "修改用户头像",notes = "修改用户头像",httpMethod = "POST")
    @PostMapping("uploadFace")
    public IMOOCJSONResult update(
            @ApiParam(name = "userId",value = "用户ID",required = true)
            @RequestParam("userId") String userId,
            @ApiParam(name = "file",value = "用户头像",required = true)
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response){
        //定义头像保存的地址
        //String fileSpace = IMAGE_USER_FACE_LOCATION;
        String fileSpace = fileUpload.getImageUserFaceLocation();
        //区分不同的用户
        String uploadPathPrefix = File.separator + userId;
        if (file != null){
            FileOutputStream out = null;
            try {
                //获得文件上传的文件名称
                String fileName = file.getOriginalFilename();

                if (StringUtils.isNotBlank(fileName)){

                    String[] fileNameArr = fileName.split("\\.");
                    //获得文件的后缀名
                    String suffix = fileNameArr[fileNameArr.length - 1];

                    if (!suffix.equalsIgnoreCase("png")&&
                       !suffix.equalsIgnoreCase("jpg") &&
                       !suffix.equalsIgnoreCase("jpeg")){
                        return IMOOCJSONResult.errorMsg("图片格式不正确");
                    }



                    //face-{userId}.png
                    //文件名称重组 覆盖式上传 增量式  拼接时间
                    String newFileName = "face-"+userId+"."+suffix;

                    //上传头像最终保存地址
                    String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;
                    //用于提供给web服务访问的地址
                    uploadPathPrefix += ("/" + newFileName);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null){
                        //创建文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    //文件输出保存目录
                    out = new FileOutputStream(outFile);
                    InputStream in = file.getInputStream();
                    IOUtils.copy(in, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (out != null){
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }else {
            return IMOOCJSONResult.errorMsg("文件不能为空");
        }

        String imageServerUrl = fileUpload.getImageServerUrl();
        //由于浏览器可能存在缓存，加上时间戳保证图片及时更新
        String finalServerUrl = imageServerUrl + uploadPathPrefix
                +"?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
        //更新用户头像到数据库
        Users userResult = centerUserService.updateUserFace(userId, finalServerUrl);

        userResult = setNullProperty(userResult);

        CookieUtils.setCookie(request,response,"user",
                JsonUtils.objectToJson(userResult),true);

        //TODO 后续要改，增加令牌token，整合redis，分布式会话
        return IMOOCJSONResult.ok();

    }

    private Map<String,String> getError(BindingResult result){
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            //发生验证错误所对应的某一个属性
            String errorField = error.getField();
            //验证错误的信息
            String errorMsg = error.getDefaultMessage();
            map.put(errorField,errorMsg);
        }
        return map;
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
