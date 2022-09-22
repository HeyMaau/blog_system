package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogLooper;
import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理轮播图Api
 */
@Slf4j
@RestController
@RequestMapping("/admin/looper")
public class LooperApi {

    /**
     * 上传轮播图
     *
     * @param blogLooper
     * @return
     */
    @PostMapping
    public ResponseResult uploadLooper(@RequestBody BlogLooper blogLooper) {
        log.info("上传轮播图 ----> " + blogLooper.toString());
        return null;
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
        return null;
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
        return null;
    }
}
