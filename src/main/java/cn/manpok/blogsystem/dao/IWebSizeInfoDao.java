package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWebSizeInfoDao extends JpaRepository<BlogSetting, String> {

    BlogSetting findSettingByKey(String key);
}
