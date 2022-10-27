package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IArticleAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/list/{page}/{size}")
    public ResponseResult getArticles(@PathVariable("page") int page, @PathVariable("size") int size) {
        log.info("门户获取文章列表");
        return null;
    }

    /**
     * 门户根据分类获取文章列表
     *
     * @param categoryID
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{categoryID}/{page}/{size}")
    public ResponseResult getArticlesByCategory(@PathVariable("categoryID") String categoryID,
                                                @PathVariable("page") int page,
                                                @PathVariable("size") int size) {
        log.info("门户根据分类获取文章列表 ----> " + categoryID);
        return null;
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
