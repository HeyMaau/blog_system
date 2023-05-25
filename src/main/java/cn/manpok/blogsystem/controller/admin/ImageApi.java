package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.interceptors.CheckRepeatedCommit;
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
@RequestMapping("/image")
public class ImageApi {

    @Autowired
    private IImageService imageService;

    /**
     * 上传图片
     *
     * @param imageFile
     * @return
     */
    @CheckRepeatedCommit
    @PreAuthorize("@permission.admin")
    @PostMapping
    public ResponseResult uploadImage(@RequestParam("file") MultipartFile imageFile, @RequestParam(value = "type", required = false, defaultValue = "0") String type) {
        return imageService.uploadImage(imageFile, type);
    }

    /**
     * 删除图片
     *
     * @param imageID
     * @return
     */
    @DeleteMapping("/{imageID}")
    @PreAuthorize("@permission.admin")
    public ResponseResult deleteImage(@PathVariable("imageID") String imageID) {
        log.info("删除图片 ----> " + imageID);
        return imageService.deleteImage(imageID);
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
    @PreAuthorize("@permission.admin")
    public ResponseResult getImages(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("获取所有图片 ----> ");
        return imageService.getImages(page, size);
    }

    /**
     * 获取评论的头像
     *
     * @param key
     */
    @GetMapping("/comment/{key}")
    public void getCommentAvatar(@PathVariable("key") String key) {
        log.info("获取评论头像 ----> " + key);
        imageService.getCommentAvatar(key);
    }
}
