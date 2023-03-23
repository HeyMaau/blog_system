package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogCommentSimple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ICommentSimplePortalDao extends JpaRepository<BlogCommentSimple, String>, JpaSpecificationExecutor<BlogCommentSimple> {

    List<BlogCommentSimple> findChildrenByParentCommentId(String parentCommentID);
}
