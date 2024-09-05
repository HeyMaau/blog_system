package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.service.IShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/portal/share")
public class SharePortalApi {
    private final IShareService shareService;

    public SharePortalApi(IShareService shareService) {
        this.shareService = shareService;
    }

    /*@GetMapping("/article/{id}")
    public ResponseResult getArticleShareLink(@PathVariable String id) {
        log.info("前端获取文章分享链接：" + id);
        return shareService.getArticleShareLink(id);
    }*/
}
