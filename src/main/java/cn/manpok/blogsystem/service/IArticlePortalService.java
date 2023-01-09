package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface IArticlePortalService {
    ResponseResult getRecommendArticle(String articleID, int size);

    ResponseResult getNormalArticles(int page, int size, String categoryID);

    ResponseResult getNormalArticle(String articleID);
}
