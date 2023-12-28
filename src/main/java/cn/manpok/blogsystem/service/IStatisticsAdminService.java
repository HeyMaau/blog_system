package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface IStatisticsAdminService {

    /**
     * 获取总访问量
     */
    ResponseResult getTotalVisit();

    /**
     * 分客户端查询总访问量
     *
     * @return
     */
    ResponseResult getClientTotalVisit(String client);
}
