package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogLooper;
import cn.manpok.blogsystem.response.ResponseResult;

public interface ILooperService {
    ResponseResult uploadLooper(BlogLooper blogLooper);

    ResponseResult getLooper(String looperID);

    ResponseResult getLoopers(int page, int size);

    ResponseResult updateLooper(BlogLooper blogLooper);
}
