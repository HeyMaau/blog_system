package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理文章Api
 */
@Slf4j
@RestController
@RequestMapping("/admin/article")
public class ArticleApi {

    /**
     * 添加文章
     *
     * @param blogArticle
     * @return
     */
    @PostMapping
    public ResponseResult addArticle(@RequestBody BlogArticle blogArticle) {
        log.info("添加文章 ----> " + blogArticle.toString());
        return null;
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
        return null;
    }

    /**
     * 修改文章
     *
     * @param blogArticle
     * @return
     */
    @PutMapping
    public ResponseResult updateArticle(@RequestBody BlogArticle blogArticle) {
        log.info("修改文章 ----> " + blogArticle.toString());
        return null;
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
        return null;
    }

    /**
     * 获取文章列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getArticles(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("获取文章列表 ----> ");
        return null;
    }

    /**
     * 修改文章状态
     *
     * @param articleID
     * @param state
     * @return
     */
    @PutMapping("/state/{articleID}/{state}")
    public ResponseResult updateArticleState(@PathVariable("articleID") String articleID, @PathVariable("state") String state) {
        return null;
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
        return null;
    }
}
