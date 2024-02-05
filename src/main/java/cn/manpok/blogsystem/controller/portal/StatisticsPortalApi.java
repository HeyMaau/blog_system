package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.pojo.BlogStatistics;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IStatisticsPortalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/portal/statistics")
public class StatisticsPortalApi {

    @Autowired
    private IStatisticsPortalService statisticsPortalService;

    @PostMapping("/record")
    public ResponseResult commitRecord(@RequestBody BlogStatistics blogStatistics) {
        log.info("数据埋点：page: " + blogStatistics.getPage() + " component: " + blogStatistics.getComponent() + " event: " + blogStatistics.getEvent() + " from: " + blogStatistics.getClient());
        return statisticsPortalService.commitRecord(blogStatistics);
    }
}
