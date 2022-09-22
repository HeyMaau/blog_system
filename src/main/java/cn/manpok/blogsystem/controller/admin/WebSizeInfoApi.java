package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理网站信息API
 */
@Slf4j
@RestController
@RequestMapping("/admin/web_size_info")
public class WebSizeInfoApi {

    /**
     * 获取网站标题
     *
     * @return
     */
    @GetMapping("/title")
    public ResponseResult getWebSizeTitle() {
        log.info("获取网站标题");
        return null;
    }

    /**
     * 修改网站标题
     *
     * @param title
     * @return
     */
    @PutMapping("/title")
    public ResponseResult updateWebSizeTitle(@RequestParam("title") String title) {
        log.info("修改网站标题 ----> " + title);
        return null;
    }

    /**
     * 获取网站SEO信息
     *
     * @return
     */
    @GetMapping("/seo")
    public ResponseResult getSeoInfo() {
        log.info("获取网站SEO信息");
        return null;
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
        log.info("修改网站SEO信息 ----> " + "kewords: " + keywords + "description: " + description);
        return null;
    }

    /**
     * 获取网站统计信息
     *
     * @return
     */
    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount() {
        log.info("获取网站统计信息");
        return null;
    }
}
