package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogArticleSimple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IArticleAdminSimpleDao extends JpaRepository<BlogArticleSimple, String>, JpaSpecificationExecutor<BlogArticleSimple> {

    Page<BlogArticleSimple> findAllArticlesByLabelsContaining(String label, Pageable pageable);

    BlogArticleSimple findArticleSimpleById(String id);
}
