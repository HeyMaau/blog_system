package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IArticleAdminService {
    ResponseResult addArticle(BlogArticle blogArticle);
}
