package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IWebsiteInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理网站信息API
 */
@Slf4j
@RestController
@RequestMapping("/admin/website_info")
@PreAuthorize("@permission.admin")
public class WebsiteInfoAdminApi {

    @Autowired
    private IWebsiteInfoService websiteInfoService;

    /**
     * 获取网站标题
     *
     * @return
     */
    @GetMapping("/title")
    public ResponseResult getWebsiteTitle() {
        log.info("获取网站标题");
        return websiteInfoService.getWebsiteTitle();
    }

    /**
     * 修改网站标题
     *
     * @param title
     * @return
     */
    @PutMapping("/title")
    public ResponseResult updateWebsiteTitle(@RequestParam("title") String title) {
        log.info("修改网站标题 ----> " + title);
        return websiteInfoService.updateWebsiteTitle(title);
    }

    /**
     * 获取网站SEO信息
     *
     * @return
     */
    @GetMapping("/seo")
    public ResponseResult getSeoInfo() {
        log.info("获取网站SEO信息");
        return websiteInfoService.getSeoInfo();
    }

    /**
     * 修改网站SEO信息
     *
     * @param keywords
     * @param description
     * @return
     */
    @PutMapping("/seo")
    public ResponseResult updateSeoInfo(@RequestParam("keywords") String keywords, @RequestParam("description") String description) {
        log.info("修改网站SEO信息 ----> " + "keywords: " + keywords + "description: " + description);
        return websiteInfoService.updateSeoInfo(keywords, description);
    }

    /**
     * 获取网站访问量
     *
     * @return
     */
    @GetMapping("/view_count")
    public ResponseResult getWebsiteViewCount() {
        log.info("获取网站统计信息");
        return websiteInfoService.getWebsiteViewCount();
    }
}
