package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogLooper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ILooperDao extends JpaRepository<BlogLooper, String>, JpaSpecificationExecutor<BlogLooper> {

    BlogLooper findLooperById(String id);
}
