package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    ResponseResult uploadImage(MultipartFile imageFile, String type, String oldID);

    void getImage(String imageID);

    ResponseResult getImages(int page, int size);

    ResponseResult deleteImage(String imageID);

    void getCommentAvatar(String key);

    void removeArticleUnusedImages();

    void deleteImagePhysically();

    ResponseResult deleteImages(String[] imageIDs);
}
