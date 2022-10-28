package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IArticleAdminService;
import cn.manpok.blogsystem.service.ILabelService;
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

    @Autowired
    private ILabelService labelService;

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

    @GetMapping("/top/list")
    public ResponseResult getTopArticles(@RequestParam(value = "category", required = false) String category) {
        log.info("门户获取置顶文章 ----> ");
        return articleAdminService.getArticles(Constants.Page.DEFAULT_PAGE, Constants.Page.TOP_ARTICLES_SIZE,
                category, null, Constants.Article.STATE_TOP);
    }

    @GetMapping("/labels")
    public ResponseResult getLabelsData(@RequestParam("size") int size) {
        log.info("门户获取标签信息");
        return labelService.getLabelsData(size);
    }

    @GetMapping("/list/label/{labelName}")
    public ResponseResult getArticlesByLabel(@PathVariable("labelName") String labelName,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size) {
        log.info("门户根据标签获取文章列表 ----> " + labelName);
        return articleAdminService.getArticlesByLabel(page, size, labelName);
    }
}
