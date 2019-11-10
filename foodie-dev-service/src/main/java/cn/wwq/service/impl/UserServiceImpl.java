package cn.wwq.service.impl;

import cn.wwq.enums.Sex;
import cn.wwq.mapper.UsersMapper;
import cn.wwq.pojo.Users;
import cn.wwq.pojo.bo.UserBO;
import cn.wwq.service.UserService;
import cn.wwq.utils.DateUtil;
import cn.wwq.utils.MD5Utils;
import org.n3r.idworker.IdWorker;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username",username);

        Users result = usersMapper.selectOneByExample(userExample);

        return result != null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBO userBO) {

        Users user = new Users();

        //主键
        String userId = sid.nextShort();
        user.setId(userId);
        user.setUsername(userBO.getUsername());
        try {
            user.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //默认用户昵称
        user.setNickname(userBO.getUsername());
        //默认头像
        user.setFace(USER_FACE);
        user.setBirthday(DateUtil.stringToDate("1970-01-01"));
        //默认性别为保密
        user.setSex(Sex.secret.type);
        //创建时间
        user.setCreatedTime(new Date());
        //修改时间
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }
}
