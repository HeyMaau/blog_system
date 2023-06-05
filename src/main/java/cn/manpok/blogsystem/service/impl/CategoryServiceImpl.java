package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ICategoryDao;
import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ICategoryService;
import cn.manpok.blogsystem.service.IImageService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.PageUtil;
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
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ICategoryDao categoryDao;

    @Autowired
    private IImageService imageService;

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
        if (TextUtil.isEmpty(blogCategory.getCover())) {
            return ResponseResult.FAIL("分类封面为空");
        }
        //补充数据
        blogCategory.setId(String.valueOf(snowflake.nextId()));
        blogCategory.setState(Constants.STATE_NORMAL);
        blogCategory.setCreateTime(new Date());
        blogCategory.setUpdateTime(new Date());
        //保存
        categoryDao.save(blogCategory);
        return ResponseResult.SUCCESS("添加文章分类成功");
    }

    @Override
    public ResponseResult getCategory(String categoryID) {
        BlogCategory queryCategory = categoryDao.findCategoryById(categoryID);
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        return ResponseResult.SUCCESS("获取文章分类成功").setData(queryCategory);
    }

    @Override
    public ResponseResult getCategories(int page, int size) {
        //检查分页参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        //做分页查询
        Pageable pageAble = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.ASC, "createTime");
        Page<BlogCategory> queryCategories = categoryDao.findAll(pageAble);
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
        if (TextUtil.isEmpty(blogCategory.getCover())) {
            return ResponseResult.FAIL("分类封面为空");
        }
        //先从数据库查询
        BlogCategory queryCategory = categoryDao.findCategoryById(blogCategory.getId());
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        //清理旧封面
        String originCover = queryCategory.getCover();
        if (!TextUtil.isEmpty(originCover)) {
            imageService.deleteImage(originCover);
        }
        queryCategory.setName(blogCategory.getName());
        queryCategory.setDescription(blogCategory.getDescription());
        queryCategory.setPinyin(blogCategory.getPinyin());
        queryCategory.setOrder(blogCategory.getOrder());
        queryCategory.setUpdateTime(new Date());
        return ResponseResult.SUCCESS("更新文章分类成功");
    }

    @Override
    public ResponseResult deleteCategory(String categoryID) {
        BlogCategory queryCategory = categoryDao.findCategoryById(categoryID);
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        queryCategory.setState(Constants.STATE_FORBIDDEN);
        return ResponseResult.SUCCESS("删除文章分类成功");
    }

    @Override
    public ResponseResult getNormalCategories() {
        List<BlogCategory> all = categoryDao.findAllCategoriesByState(Constants.STATE_NORMAL);
        return ResponseResult.SUCCESS("获取所有分类成功").setData(all);
    }

    @Override
    public ResponseResult recoverCategory(String categoryID) {
        BlogCategory queryCategory = categoryDao.findCategoryById(categoryID);
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        queryCategory.setState(Constants.STATE_NORMAL);
        return ResponseResult.SUCCESS("恢复文章分类成功");
    }
}
