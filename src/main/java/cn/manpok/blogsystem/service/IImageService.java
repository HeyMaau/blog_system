package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    ResponseResult uploadImage(MultipartFile imageFile);

    ResponseResult uploadImageWithWatermark(MultipartFile imageFile);

    void getImage(String imageID);

    ResponseResult getImages(int page, int size);

    ResponseResult deleteImage(String imageID);

    void getCommentAvatar(String key);

    void removeUnusedImages();

    void deleteImagePhysically();

    ResponseResult deleteImages(String[] imageIDs);
}
