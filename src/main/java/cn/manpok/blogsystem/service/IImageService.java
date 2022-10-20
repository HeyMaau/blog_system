package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    ResponseResult uploadImage(MultipartFile imageFile);

    void getImage(String imageID);

    ResponseResult getImages(int page, int size);

    ResponseResult deleteImage(String imageID);
}
