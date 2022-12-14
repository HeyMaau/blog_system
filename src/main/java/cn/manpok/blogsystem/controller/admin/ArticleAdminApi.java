package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.interceptors.CheckRepeatedCommit;
import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IArticleAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理文章Api
 */
@Slf4j
@RestController
@RequestMapping("/admin/article")
@PreAuthorize("@permission.admin")
public class ArticleAdminApi {

    @Autowired
    private IArticleAdminService articleAdminService;

    /**
     * 添加文章
     *
     * @param blogArticle
     * @return
     */
    @CheckRepeatedCommit
    @PostMapping
    public ResponseResult addArticle(@RequestBody BlogArticle blogArticle) {
        log.info("添加文章 ----> " + blogArticle.toString());
        return articleAdminService.addArticle(blogArticle);
    }

    /**
     * 删除文章
     *
     * @param articleID
     * @return
     */
    @DeleteMapping("/{articleID}")
    public ResponseResult deleteArticle(@PathVariable("articleID") String articleID) {
        log.info("删除文章 ----> " + articleID);
        return articleAdminService.deleteArticle(articleID);
    }

    /**
     * 修改文章
     *
     * @param blogArticle
     * @return
     */
    @CheckRepeatedCommit
    @PutMapping
    public ResponseResult updateArticle(@RequestBody BlogArticle blogArticle) {
        log.info("修改文章 ----> " + blogArticle.toString());
        return articleAdminService.updateArticle(blogArticle);
    }

    /**
     * 获取文章
     *
     * @param articleID
     * @return
     */
    @GetMapping("/{articleID}")
    public ResponseResult getArticle(@PathVariable("articleID") String articleID) {
        log.info("获取文章 ----> " + articleID);
        return articleAdminService.getArticle(articleID);
    }

    /**
     * 获取文章列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getArticles(@RequestParam("page") int page, @RequestParam("size") int size,
                                      @RequestParam(value = "category", required = false) String category,
                                      @RequestParam(value = "keywords", required = false) String keywords,
                                      @RequestParam(value = "state", required = false) String state) {
        log.info("获取文章列表 ----> ");
        return articleAdminService.getArticles(page, size, category, keywords, state);
    }

    /**
     * 通过修改文章状态删除文章
     *
     * @param articleID
     * @return
     */
    @DeleteMapping("/state/{articleID}")
    public ResponseResult updateArticleState(@PathVariable("articleID") String articleID) {
        log.info("通过修改状态删除文章 ----> " + articleID);
        return articleAdminService.updateArticleState(articleID);
    }

    /**
     * 文章置顶
     *
     * @param articleID
     * @return
     */
    @PutMapping("/top/{articleID}")
    public ResponseResult topArticle(@PathVariable("articleID") String articleID) {
        log.info("置顶文章 ----> " + articleID);
        return articleAdminService.topArticle(articleID);
    }
}
