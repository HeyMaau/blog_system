package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogLabel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILabelDao extends JpaRepository<BlogLabel, String> {

    BlogLabel findLabelByName(String name);
}
