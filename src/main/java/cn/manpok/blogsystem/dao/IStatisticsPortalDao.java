package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStatisticsPortalDao extends JpaRepository<BlogStatistics, String> {

    BlogStatistics findByPageAndEventAndRecordDateAndClient(String page, String event, String recordDate, String client);
}
