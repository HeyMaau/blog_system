package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICommentPortalDao extends JpaRepository<BlogComment, String> {

    BlogComment findCommentById(String id);

    Page<BlogComment> findAllCommentsByArticleId(String articleID, Pageable pageable);

}
