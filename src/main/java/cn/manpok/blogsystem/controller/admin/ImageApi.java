package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理图片Api
 */
@Slf4j
@RestController
@RequestMapping("/admin/image")
@PreAuthorize("@permission.admin")
public class ImageApi {

    @Autowired
    private IImageService imageService;

    /**
     * 上传图片
     *
     * @param imageFile
     * @return
     */
    @PostMapping
    public ResponseResult uploadImage(@RequestParam("file") MultipartFile imageFile) {
        return imageService.uploadImage(imageFile);
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
     * 获取单张图片
     *
     * @param imageID
     */
    @GetMapping("/{imageID}")
    public void getImage(@PathVariable("imageID") String imageID) {
        log.info("获取单张图片 ----> " + imageID);
        imageService.getImage(imageID);
    }

    /**
     * 获取所有图片
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getImages(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("获取所有图片 ----> ");
        return imageService.getImages(page, size);
    }
}
