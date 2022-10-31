package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface IArticlePortalService {
    ResponseResult getRecommendArticle(String articleID, int size);
}
