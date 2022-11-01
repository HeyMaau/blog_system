package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICommentPortalDao extends JpaRepository<BlogComment, String> {
}
