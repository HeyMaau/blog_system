package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IStatisticsAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsAdminApi {

    @Autowired
    private IStatisticsAdminService statisticsAdminService;

    @GetMapping("/total")
    @PreAuthorize("@permission.admin")
    public ResponseResult getTotalVisit() {
        return statisticsAdminService.getTotalVisit();
    }

    @GetMapping("/total/{client}")
    @PreAuthorize("@permission.admin")
    public ResponseResult getClientTotalVisit(@PathVariable("client") String client) {
        return statisticsAdminService.getClientTotalVisit(client);
    }
}
