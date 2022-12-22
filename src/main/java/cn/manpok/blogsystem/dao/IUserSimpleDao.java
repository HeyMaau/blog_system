package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogUserSimple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IUserSimpleDao extends JpaRepository<BlogUserSimple, String>, JpaSpecificationExecutor<BlogUserSimple> {
}
