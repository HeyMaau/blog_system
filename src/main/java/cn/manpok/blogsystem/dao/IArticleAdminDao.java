package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IArticleAdminDao extends JpaRepository<BlogArticle, String> {

    BlogArticle findArticleById(String id);

    int deleteArticleById(String id);
}
