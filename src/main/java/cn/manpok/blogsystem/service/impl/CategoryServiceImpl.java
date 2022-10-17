package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ICategoryDao;
import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ICategoryService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ICategoryDao categoryDao;

    @Override
    public ResponseResult addCategory(BlogCategory blogCategory) {
        //检查数据
        if (TextUtil.isEmpty(blogCategory.getName().trim())) {
            return ResponseResult.FAIL("分类名称为空");
        }
        if (TextUtil.isEmpty(blogCategory.getDescription())) {
            return ResponseResult.FAIL("分类描述为空");
        }
        if (TextUtil.isEmpty(blogCategory.getPinyin())) {
            return ResponseResult.FAIL("分类名称拼音为空");
        }
        //补充数据
        blogCategory.setId(String.valueOf(snowflake.nextId()));
        blogCategory.setState(Constants.DEFAULT_STATE);
        blogCategory.setCreateTime(new Date());
        blogCategory.setUpdateTime(new Date());
        //保存
        categoryDao.save(blogCategory);
        return ResponseResult.SUCCESS("添加文章分类成功");
    }
}
