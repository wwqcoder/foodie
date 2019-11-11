package cn.wwq.service;

import cn.wwq.pojo.Stu;
import cn.wwq.pojo.Users;
import cn.wwq.pojo.bo.UserBO;

public interface UserService {

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 创建用户
     * @param userBO
     * @return
     */
    public Users createUser(UserBO userBO);

    /**
     * 检索用户名和密码是否匹配
     * @param username
     * @param password
     * @return
     */
    public Users queryUserForLogin(String username,String password);
}
