package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

import java.util.Map;

public interface IQRCodeService {

    ResponseResult getQRCodeInfo();

    void getQRCodeImg(String code);

    ResponseResult changeQRCodeState2Enquire(String code, Map<String, String> tokenMap);

    ResponseResult changeQRCodeState2Confirm(String code);
}
