package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理-分类API
 */
@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryApi {

    /**
     * 添加分类
     *
     * @param blogArticle
     * @return
     */
    @PostMapping
    public ResponseResult addCategory(@RequestBody BlogArticle blogArticle) {
        log.info("添加分类 ----> " + blogArticle.toString());
        return null;
    }

    /**
     * 删除分类
     *
     * @param categoryID
     * @return
     */
    @DeleteMapping("/{categoryID}")
    public ResponseResult deleteCategory(@PathVariable("categoryID") String categoryID) {
        log.info("删除分类 ----> " + categoryID);
        return null;
    }

    /**
     * 修改分类
     *
     * @param blogArticle
     * @return
     */
    @PutMapping
    public ResponseResult updateCategory(@RequestBody BlogArticle blogArticle) {
        log.info("修改分类 ----> " + blogArticle.toString());
        return null;
    }

    /**
     * 获取单个分类
     *
     * @param categoryID
     * @return
     */
    @GetMapping("{categoryID}")
    public ResponseResult getCategory(@PathVariable("categoryID") String categoryID) {
        log.info("获取单个分类 ----> " + categoryID);
        return null;
    }

    /**
     * 获取所有分类
     *
     * @param page 页码
     * @param size 每页大小
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getCategories(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("获取所有分类 ----> ");
        return null;
    }
}
