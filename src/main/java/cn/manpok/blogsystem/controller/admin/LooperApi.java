package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogLooper;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ILooperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理轮播图Api
 */
@Slf4j
@RestController
@RequestMapping("/admin/looper")
@PreAuthorize("@permission.admin")
public class LooperApi {

    @Autowired
    private ILooperService looperService;

    /**
     * 上传轮播图
     *
     * @param blogLooper
     * @return
     */
    @PostMapping
    public ResponseResult uploadLooper(@RequestBody BlogLooper blogLooper) {
        log.info("上传轮播图 ----> " + blogLooper.toString());
        return looperService.uploadLooper(blogLooper);
    }

    /**
     * 删除轮播图
     *
     * @param looperID
     * @return
     */
    @DeleteMapping("/{looperID}")
    public ResponseResult deleteLooper(@PathVariable("looperID") String looperID) {
        log.info("删除轮播图 ----> " + looperID);
        return null;
    }

    /**
     * 修改轮播图
     *
     * @param blogLooper
     * @return
     */
    @PutMapping
    public ResponseResult updateLooper(@RequestBody BlogLooper blogLooper) {
        log.info("修改轮播图 ----> " + blogLooper.toString());
        return null;
    }

    /**
     * 获取单张轮播图
     *
     * @param looperID
     * @return
     */
    @GetMapping("/{looperID}")
    public ResponseResult getLooper(@PathVariable("looperID") String looperID) {
        log.info("获取单张轮播图 ----> " + looperID);
        return looperService.getLooper(looperID);
    }

    /**
     * 获取所有轮播图
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getLoopers(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("获取所有轮播图 ----> ");
        return looperService.getLoopers(page, size);
    }
}
