package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ISettingDao extends JpaRepository<BlogSetting, String>, JpaSpecificationExecutor<BlogSetting> {

    BlogSetting findByKey(String key);
}
