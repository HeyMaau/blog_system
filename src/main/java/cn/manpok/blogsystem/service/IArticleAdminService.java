package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IArticleAdminService {
    ResponseResult addArticle(BlogArticle blogArticle);

    ResponseResult getArticles(int page, int size, String keywords, String state);
}
