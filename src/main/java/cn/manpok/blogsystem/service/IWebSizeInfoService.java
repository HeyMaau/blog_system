package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface IWebSizeInfoService {
    ResponseResult updateWebSizeTitle(String title);

    ResponseResult getWebSizeTitle();

    ResponseResult updateSeoInfo(String keywords, String description);
}
