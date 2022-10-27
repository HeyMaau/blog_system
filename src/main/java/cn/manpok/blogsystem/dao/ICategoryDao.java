package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICategoryDao extends JpaRepository<BlogCategory, String> {

    BlogCategory findCategoryById(String categoryID);

    List<BlogCategory> findAllCategoriesByState(String state);
}
