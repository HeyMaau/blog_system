package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IStatisticsDao extends JpaRepository<BlogStatistics, String> {

    BlogStatistics findByPageAndEventAndRecordDateAndClient(String page, String event, String recordDate, String client);

    @Query(nativeQuery = true, value = "SELECT SUM(count) FROM statistics")
    long getTotalVisit();

    @Query(nativeQuery = true, value = "SELECT SUM(count) FROM statistics WHERE client = :client")
    Long getClientTotalVisit(String client);
}
