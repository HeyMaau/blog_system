package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface IQRCodeService {

    ResponseResult getQRCodeInfo();

    void getQRCodeImg(String code);
}
