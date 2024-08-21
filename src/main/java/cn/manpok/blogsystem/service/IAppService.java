package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogApp;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IAppService {

    ResponseResult getAppDownloadUrl();

    ResponseResult updateAppInfo(BlogApp blogApp);

    ResponseResult deleteAppInfo(String id);

    ResponseResult checkAppUpdateInfo(Integer versionCode);
}
