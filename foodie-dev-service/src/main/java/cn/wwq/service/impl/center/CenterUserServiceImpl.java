package cn.wwq.service.impl.center;

import cn.wwq.mapper.UsersMapper;
import cn.wwq.pojo.Users;
import cn.wwq.pojo.bo.center.CenterUserBO;
import cn.wwq.service.center.CenterUserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CenterUserServiceImpl implements CenterUserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserInfo(String userId) {
        Users user = usersMapper.selectByPrimaryKey(userId);
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {

        Users updateUser = new Users();
        BeanUtils.copyProperties(centerUserBO,updateUser);
        updateUser.setId(userId);
        updateUser.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(updateUser);

        return queryUserInfo(userId);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Users updateUserFace(String userId, String faceUrl) {
        Users updateUser = new Users();
        updateUser.setId(userId);
        updateUser.setFace(faceUrl);
        updateUser.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(updateUser);
        return queryUserInfo(userId);    }
}
