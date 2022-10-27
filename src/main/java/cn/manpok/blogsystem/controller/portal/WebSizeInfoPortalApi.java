package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ILooperService;
import cn.manpok.blogsystem.service.IWebSizeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户网站信息API
 */
@Slf4j
@RestController
@RequestMapping("/portal/web_size_info")
public class WebSizeInfoPortalApi {

    @Autowired
    private IWebSizeInfoService webSizeInfoService;

    @Autowired
    private ILooperService looperService;

    /**
     * 门户获取所有分类
     *
     * @return
     */
    @GetMapping("/categories")
    public ResponseResult getCategories() {
        log.info("门户获取所有分类");
        return null;
    }

    /**
     * 门户获取网站标题
     *
     * @return
     */
    @GetMapping("/title")
    public ResponseResult getWebSizeTitle() {
        log.info("门户获取网站标题");
        return webSizeInfoService.getWebSizeTitle();
    }

    /**
     * 门户获取网站访问量
     *
     * @return
     */
    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount() {
        log.info("门户获取网站访问量");
        return webSizeInfoService.getWebSizeViewCount();
    }

    /**
     * 门户获取轮播图
     *
     * @return
     */
    @GetMapping("/loopers")
    public ResponseResult getLoopers() {
        log.info("门户获取轮播图");
        return looperService.getNormalLoopers();
    }

    /**
     * 门户获取所有友情链接
     *
     * @return
     */
    @GetMapping("/friend_links")
    public ResponseResult getFriendLinks() {
        log.info("门户获取所有友情链接");
        return null;
    }
}
