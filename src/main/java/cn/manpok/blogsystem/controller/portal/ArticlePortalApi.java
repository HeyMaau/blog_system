package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IArticleAdminService;
import cn.manpok.blogsystem.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 门户文章API
 */
@Slf4j
@RestController
@RequestMapping("/portal/article")
public class ArticlePortalApi {

    @Autowired
    private IArticleAdminService articleAdminService;

    /**
     * 门户获取文章列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getArticles(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("门户获取文章列表");
        return articleAdminService.getArticles(page, size, null, null, Constants.Article.STATE_PUBLISH);
    }

    /**
     * 门户根据分类获取文章列表
     *
     * @param categoryID
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{categoryID}")
    public ResponseResult getArticlesByCategory(@PathVariable("categoryID") String categoryID,
                                                @RequestParam("page") int page,
                                                @RequestParam("size") int size) {
        log.info("门户根据分类获取文章列表 ----> " + categoryID);
        return articleAdminService.getArticles(page, size, categoryID, null, Constants.Article.STATE_PUBLISH);
    }

    /**
     * 门户获取文章详情
     *
     * @param articleID
     * @return
     */
    @GetMapping("/{articleID}")
    public ResponseResult getArticleDetail(@PathVariable("articleID") String articleID) {
        log.info("门户获取文章详情 ----> " + articleID);
        return articleAdminService.getNormalArticle(articleID);
    }

    /**
     * 门户获取推荐文章
     *
     * @param articleID
     * @return
     */
    @GetMapping("/recommend/{articleID}")
    public ResponseResult getRecommendArticle(@PathVariable("articleID") String articleID) {
        log.info("门户获取推荐文章 ----> " + articleID);
        return null;
    }
}
