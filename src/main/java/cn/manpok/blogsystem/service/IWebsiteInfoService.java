package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface IWebsiteInfoService {
    ResponseResult updateWebsiteTitle(String title);

    ResponseResult getWebsiteTitle();

    ResponseResult updateSeoInfo(String keywords, String description);

    ResponseResult getSeoInfo();

    ResponseResult getWebsiteViewCount();

    void updateWebsiteViewCount();

}
