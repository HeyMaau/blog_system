package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogArticle;

public interface ISolrSearchService {

    void addArticle(BlogArticle blogArticle);

    void deleteArticle(String articleID);

    void updateArticle(BlogArticle blogArticle);
}
