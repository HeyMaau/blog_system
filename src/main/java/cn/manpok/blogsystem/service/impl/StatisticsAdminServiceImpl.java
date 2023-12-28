package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IStatisticsDao;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IStatisticsAdminService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StatisticsAdminServiceImpl implements IStatisticsAdminService {

    @Autowired
    private IStatisticsDao statisticsDao;

    @Override
    public ResponseResult getTotalVisit() {
        long totalVisit = statisticsDao.getTotalVisit();
        log.info("管理后台获取总访问量：" + totalVisit);
        Map<String, Long> result = new HashMap<>();
        result.put(Constants.Statistics.KEY_RESPONSE_FIELD_TOTAL_VISIT, totalVisit);
        return ResponseResult.SUCCESS("获取总访问量成功").setData(result);
    }

    @Override
    public ResponseResult getClientTotalVisit(String client) {
        if (TextUtil.isEmpty(client)
                || (!client.equalsIgnoreCase(Constants.Statistics.CLIENT_NAME_DESKTOP)
                && !client.equalsIgnoreCase(Constants.Statistics.CLIENT_NAME_MOBILE))) {
            return ResponseResult.FAIL("客户端名称错误");
        }
        Long clientTotalVisit = statisticsDao.getClientTotalVisit(client);
        if (clientTotalVisit == null) {
            return ResponseResult.FAIL(client + "端总访问量不存在");
        }
        Map<String, Long> result = new HashMap<>();
        result.put(Constants.Statistics.KEY_RESPONSE_FIELD_TOTAL_VISIT, clientTotalVisit);
        return ResponseResult.SUCCESS("获取" + client + "端总访问量成功").setData(result);
    }
}
