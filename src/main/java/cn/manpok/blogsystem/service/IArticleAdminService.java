package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IArticleAdminService {
    ResponseResult addArticle(BlogArticle blogArticle);

    ResponseResult getArticles(int page, int size, String category, String keywords, String state);

    ResponseResult getArticle(String articleID);

    ResponseResult topArticle(String articleID);

    ResponseResult deleteArticle(String articleID);

    ResponseResult updateArticleState(String articleID);

    ResponseResult updateArticle(BlogArticle blogArticle);

    ResponseResult getNormalArticle(String articleID);
}
