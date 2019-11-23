package cn.wwq.service.center;

import cn.wwq.pojo.Users;
import cn.wwq.pojo.bo.UserBO;
import cn.wwq.pojo.bo.center.CenterUserBO;

public interface CenterUserService {

    /**
     * 根据用户ID查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 修改用户信息
     * @param userId
     * @param centerUserBO
     */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    /**
     * 更新用户头像
     * @param userId
     * @param faceUrl
     * @return
     */
    public Users updateUserFace(String userId,String faceUrl);
}
