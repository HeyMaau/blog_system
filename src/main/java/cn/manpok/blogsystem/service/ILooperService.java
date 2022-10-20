package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogLooper;
import cn.manpok.blogsystem.response.ResponseResult;

public interface ILooperService {
    ResponseResult uploadLooper(BlogLooper blogLooper);

    ResponseResult getLooper(String looperID);
}
