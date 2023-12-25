package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogStatistics;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IStatisticsPortalService {

    /**
     * 埋点上报
     *
     * @param blogStatistics 埋点数据
     * @return
     */
    ResponseResult commitRecord(BlogStatistics blogStatistics);

    /**
     * 定时写到数据库里面，每分钟执行一次
     */
    void saveRecord2DB();
}
