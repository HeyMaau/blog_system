package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.interceptors.CheckRepeatedCommit;
import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理-分类API
 */
@Slf4j
@RestController
@RequestMapping("/category")
@PreAuthorize("@permission.admin")
public class CategoryApi {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加分类
     *
     * @param blogCategory
     * @return
     */
    @CheckRepeatedCommit
    @PostMapping
    public ResponseResult addCategory(@RequestBody BlogCategory blogCategory) {
        log.info("添加分类 ----> " + blogCategory.toString());
        return categoryService.addCategory(blogCategory);
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
        return categoryService.deleteCategory(categoryID);
    }

    /**
     * 修改分类
     *
     * @param blogCategory
     * @return
     */
    @CheckRepeatedCommit
    @PutMapping
    public ResponseResult updateCategory(@RequestBody BlogCategory blogCategory) {
        log.info("修改分类 ----> " + blogCategory.toString());
        return categoryService.updateCategory(blogCategory);
    }

    /**
     * 获取单个分类
     *
     * @param categoryID
     * @return
     */
    @GetMapping("/{categoryID}")
    public ResponseResult getCategory(@PathVariable("categoryID") String categoryID) {
        log.info("获取单个分类 ----> " + categoryID);
        return categoryService.getCategory(categoryID);
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
        return categoryService.getCategories(page, size);
    }

    @PutMapping("/{categoryID}")
    public ResponseResult recoverCategory(@PathVariable("categoryID") String categoryID) {
        log.info("恢复分类 ----> " + categoryID);
        return categoryService.recoverCategory(categoryID);
    }
}
