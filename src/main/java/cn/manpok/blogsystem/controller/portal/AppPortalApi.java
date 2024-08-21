package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/portal/app")
public class AppPortalApi {

    private final IAppService appService;

    public AppPortalApi(IAppService appService) {
        this.appService = appService;
    }

    @GetMapping("/getDownloadUrl")
    public ResponseResult getAppDownloadUrl() {
        log.info("前端获取APP下载链接");
        return appService.getAppDownloadUrl();
    }

    @GetMapping("/checkUpdateInfo")
    public ResponseResult checkAppUpdateInfo(@RequestParam("version_code") Integer versionCode) {
        log.info("前端检查APP更新");
        return appService.checkAppUpdateInfo(versionCode);
    }
}
