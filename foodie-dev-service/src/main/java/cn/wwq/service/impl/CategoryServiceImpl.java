package cn.wwq.service.impl;

import cn.wwq.mapper.CategoryMapper;
import cn.wwq.mapper.CategoryMapperCustom;
import cn.wwq.pojo.Category;
import cn.wwq.pojo.vo.CategoryVO;
import cn.wwq.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    @Override
    @Transactional(propagation= Propagation.SUPPORTS)
    public List<Category> queryAllRootLevelCat() {
        Class entityClass;
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",1);
        List<Category> result = categoryMapper.selectByExample(example);

        return result;
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        return categoryMapperCustom.getSubCatList(rootCatId);
    }
}
