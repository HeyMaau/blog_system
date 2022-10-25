package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogArticleSimple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IArticleAdminSimpleDao extends JpaRepository<BlogArticleSimple, String>, JpaSpecificationExecutor<BlogArticleSimple> {
}
