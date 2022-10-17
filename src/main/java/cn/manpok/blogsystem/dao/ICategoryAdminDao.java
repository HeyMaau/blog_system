package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ICategoryAdminDao extends JpaRepository<BlogCategory, String>, JpaSpecificationExecutor<BlogCategory> {

    BlogCategory findCategoryById(String categoryID);
}
