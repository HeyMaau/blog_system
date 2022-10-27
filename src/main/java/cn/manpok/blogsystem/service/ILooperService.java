package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogLooper;
import cn.manpok.blogsystem.response.ResponseResult;

public interface ILooperService {
    ResponseResult uploadLooper(BlogLooper blogLooper);

    ResponseResult getLooper(String looperID);

    ResponseResult getLoopers(int page, int size);

    ResponseResult updateLooper(BlogLooper blogLooper);

    ResponseResult deleteLooper(String looperID);

    /**
     * 给门户获取所有轮播图，不包含删除状态的轮播图
     * @return
     */
    ResponseResult getNormalLoopers();
}
