package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.response.ResponseResult;

public interface ICategoryService {
    ResponseResult addCategory(BlogCategory blogCategory);
}
