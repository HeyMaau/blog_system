package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ICategoryDao;
import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ICategoryService;
import cn.manpok.blogsystem.utils.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ICategoryDao categoryDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Gson gson;

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
        //清除redis中的缓存
        redisUtil.del(Constants.Category.KEY_CATEGORY_LIST_CACHE);
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
        queryCategory.setName(blogCategory.getName());
        queryCategory.setDescription(blogCategory.getDescription());
        queryCategory.setPinyin(blogCategory.getPinyin());
        queryCategory.setOrder(blogCategory.getOrder());
        queryCategory.setUpdateTime(new Date());
        //清除redis中的缓存
        redisUtil.del(Constants.Category.KEY_CATEGORY_LIST_CACHE);
        return ResponseResult.SUCCESS("更新文章分类成功");
    }

    @Override
    public ResponseResult deleteCategory(String categoryID) {
        BlogCategory queryCategory = categoryDao.findCategoryById(categoryID);
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        queryCategory.setState(Constants.STATE_FORBIDDEN);
        //清除redis中的缓存
        redisUtil.del(Constants.Category.KEY_CATEGORY_LIST_CACHE);
        return ResponseResult.SUCCESS("删除文章分类成功");
    }

    @Override
    public ResponseResult getNormalCategories() {
        List<BlogCategory> all;
        //从redis中取出缓存
        String categoryListCache = (String) redisUtil.get(Constants.Category.KEY_CATEGORY_LIST_CACHE);
        if (!TextUtil.isEmpty(categoryListCache)) {
            log.info("从redis中取出文章分类列表缓存");
            all = gson.fromJson(categoryListCache, new TypeToken<List<BlogCategory>>() {
            }.getType());
        } else {
            all = categoryDao.findAllCategoriesByState(Constants.STATE_NORMAL);
            categoryListCache = gson.toJson(all);
            redisUtil.set(Constants.Category.KEY_CATEGORY_LIST_CACHE, categoryListCache, Constants.TimeValue.HOUR_2);
            log.info("已缓存文章分类列表到redis");
        }
        return ResponseResult.SUCCESS("获取所有分类成功").setData(all);
    }

    @Override
    public ResponseResult recoverCategory(String categoryID) {
        BlogCategory queryCategory = categoryDao.findCategoryById(categoryID);
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        queryCategory.setState(Constants.STATE_NORMAL);
        //清除redis中的缓存
        redisUtil.del(Constants.Category.KEY_CATEGORY_LIST_CACHE);
        return ResponseResult.SUCCESS("恢复文章分类成功");
    }
}
