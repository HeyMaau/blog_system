package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IArticleAdminDao extends JpaRepository<BlogArticle, String>, JpaSpecificationExecutor<BlogArticle> {
}
