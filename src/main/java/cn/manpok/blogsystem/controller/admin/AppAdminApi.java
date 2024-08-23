package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogApp;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/app")
@PreAuthorize("@permission.admin")
public class AppAdminApi {

    private final IAppService appService;

    public AppAdminApi(IAppService appService) {
        this.appService = appService;
    }

    @PostMapping
    public ResponseResult updateAppInfo(@RequestBody BlogApp blogApp) {
        log.info("管理平台更新APP信息");
        return appService.updateAppInfo(blogApp);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteAppInfo(@PathVariable("id") String id) {
        log.info("管理平台删除APP信息 id：" + id);
        return appService.deleteAppInfo(id);
    }

    @GetMapping("/list")
    public ResponseResult getAppInfoList() {
        log.info("管理平台获取APP信息列表");
        return appService.getAppInfoList();
    }
}
