package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IAppDao extends JpaRepository<BlogApp, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM app ORDER BY version_code DESC LIMIT 1")
    BlogApp getLatestAppInfo();

    BlogApp findAppById(String id);

    int deleteAppById(String id);
}
