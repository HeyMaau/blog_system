package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ICommentAdminDao extends JpaRepository<BlogComment, String>, JpaSpecificationExecutor<BlogComment> {

    int deleteCommentById(String id);

    int deleteCommentsByArticleId(String articleID);
}
