package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.response.ResponseResult;

public interface ICategoryAdminService {
    ResponseResult addCategory(BlogCategory blogCategory);

    ResponseResult getCategory(String categoryID);

    ResponseResult getCategories(int page, int size);

    ResponseResult updateCategory(BlogCategory blogCategory);
}
