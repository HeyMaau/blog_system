package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogImage;
import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理图片Api
 */
@Slf4j
@RestController
@RequestMapping("/admin/image")
public class ImageApi {

    /**
     * 上传图片
     *
     * @param blogImage
     * @return
     */
    @PostMapping
    public ResponseResult uploadImage(@RequestBody BlogImage blogImage) {
        log.info("上传图片 ----> " + blogImage.toString());
        return null;
    }

    /**
     * 删除图片
     *
     * @param imageID
     * @return
     */
    @DeleteMapping("/{imageID}")
    public ResponseResult deleteImage(@PathVariable("imageID") String imageID) {
        log.info("删除图片 ----> " + imageID);
        return null;
    }

    /**
     * 修改图片
     *
     * @param blogImage
     * @return
     */
    @PutMapping
    public ResponseResult updateImage(@RequestBody BlogImage blogImage) {
        log.info("修改图片 ----> " + blogImage.toString());
        return null;
    }

    /**
     * 获取单张图片
     *
     * @param imageID
     * @return
     */
    @GetMapping("/{imageID}")
    public ResponseResult getCategory(@PathVariable("imageID") String imageID) {
        log.info("获取单张图片 ----> " + imageID);
        return null;
    }

    /**
     * 获取所有图片
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getCategories(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("获取所有图片 ----> ");
        return null;
    }
}
