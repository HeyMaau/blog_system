package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogArticleSimple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IArticlePortalDao extends JpaRepository<BlogArticleSimple, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM `articles` WHERE labels LIKE ?1 AND id != ?2 LIMIT ?3")
    List<BlogArticleSimple> findArticlesByLabel(String label, String id, int size);

    @Query(nativeQuery = true, value = "SELECT * FROM `articles` ORDER BY create_time DESC LIMIT ?1")
    List<BlogArticleSimple> findLatestArticles(int size);
}
