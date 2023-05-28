package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.response.ResponseResult;

public interface ISolrSearchService {

    void addArticle(BlogArticle blogArticle);

    void deleteArticle(String articleID);

    void clearData();

    void updateArticle(BlogArticle blogArticle);

    ResponseResult queryArticle(String keyword, String categoryID, Integer sort, int page, int size);
}
