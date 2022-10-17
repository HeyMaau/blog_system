package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ICategoryAdminDao;
import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ICategoryAdminService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class CategoryAdminServiceImpl implements ICategoryAdminService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ICategoryAdminDao categoryAdminDao;

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
        categoryAdminDao.save(blogCategory);
        return ResponseResult.SUCCESS("添加文章分类成功");
    }

    @Override
    public ResponseResult getCategory(String categoryID) {
        BlogCategory queryCategory = categoryAdminDao.findCategoryById(categoryID);
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        return ResponseResult.SUCCESS("获取文章分类成功").setData(queryCategory);
    }

    @Override
    public ResponseResult getCategories(int page, int size) {
        //检查页数、每页条数是否正确
        if (page < Constants.Page.DEFAULT_PAGE) {
            page = Constants.Page.DEFAULT_PAGE;
        }
        if (size < Constants.Page.DEFAULT_SIZE) {
            size = Constants.Page.DEFAULT_SIZE;
        }
        //做分页查询
        Pageable pageAble = PageRequest.of(page - 1, size, Sort.Direction.ASC, "createTime");
        Page<BlogCategory> queryCategories = categoryAdminDao.findAll(pageAble);
        return ResponseResult.SUCCESS("获取所有文章分类成功").setData(queryCategories);
    }

    @Override
    public ResponseResult updateCategory(BlogCategory blogCategory) {
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
        //先从数据库查询
        BlogCategory queryCategory = categoryAdminDao.findCategoryById(blogCategory.getId());
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        queryCategory.setName(blogCategory.getName());
        queryCategory.setDescription(blogCategory.getDescription());
        queryCategory.setPinyin(blogCategory.getPinyin());
        queryCategory.setOrder(blogCategory.getOrder());
        queryCategory.setUpdateTime(new Date());
        return ResponseResult.SUCCESS("更新文章分类成功");
    }
}
