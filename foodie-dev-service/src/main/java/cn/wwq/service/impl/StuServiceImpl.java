package cn.wwq.service.impl;

import cn.wwq.mapper.StuMapper;
import cn.wwq.pojo.Stu;
import cn.wwq.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    @Transactional(propagation=Propagation.SUPPORTS)
    @Override
    public Stu getStuInfo(int id) {
        return stuMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void saveStu() {
        Stu stu = new Stu();
        stu.setName("jack");
        stu.setAge(19);
        stuMapper.insert(stu);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void updateStu(int id) {

        Stu stu = new Stu();
        stu.setName("rose");
        stu.setAge(18);
        stu.setId(id);
        stuMapper.updateByPrimaryKey(stu);

    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void deleteStu(int id) {
        stuMapper.deleteByPrimaryKey(id);
    }
}
