package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IAppDao;
import cn.manpok.blogsystem.pojo.BlogApp;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IAppService;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AppServiceImpl implements IAppService {

    private final IAppDao appDao;

    private final Snowflake snowflake;

    public AppServiceImpl(IAppDao appDao, Snowflake snowflake) {
        this.appDao = appDao;
        this.snowflake = snowflake;
    }

    @Override
    public ResponseResult getAppDownloadUrl() {
        BlogApp appInfo = appDao.getLatestAppInfo();
        if (appInfo != null) {
            return ResponseResult.SUCCESS().setData(appInfo);
        }
        return ResponseResult.FAIL("获取APP下载链接失败");
    }

    @Override
    @Transactional
    public ResponseResult updateAppInfo(BlogApp blogApp) {
        if (TextUtil.isEmpty(blogApp.getAppName())
                || TextUtil.isEmpty(blogApp.getVersionName())
                || TextUtil.isEmpty(blogApp.getDownloadUrl())
                || blogApp.getVersionCode() == 0) {
            return ResponseResult.FAIL("应用名称、VersionName、VersionCode、下载链接都不可空");
        }
        BlogApp queryBlogApp = null;
        Date date = new Date();
        if (!TextUtil.isEmpty(blogApp.getId())) {
            queryBlogApp = appDao.findAppById(blogApp.getId());
        }
        if (queryBlogApp == null) {
            queryBlogApp = new BlogApp();
            queryBlogApp.setId(String.valueOf(snowflake.nextId()));
            queryBlogApp.setCreateTime(date);
        }
        queryBlogApp.setAppName(blogApp.getAppName());
        queryBlogApp.setVersionName(blogApp.getVersionName());
        queryBlogApp.setVersionCode(blogApp.getVersionCode());
        queryBlogApp.setDownloadUrl(blogApp.getDownloadUrl());
        queryBlogApp.setChangeLog(blogApp.getChangeLog());
        queryBlogApp.setUpdateTime(date);
        queryBlogApp.setForceUpdate(blogApp.getForceUpdate());
        appDao.save(queryBlogApp);
        return ResponseResult.SUCCESS("更新APP信息成功");
    }

    @Override
    @Transactional
    public ResponseResult deleteAppInfo(String id) {
        if (TextUtil.isEmpty(id)) {
            return ResponseResult.FAIL("ID不可为空");
        }
        int count = appDao.deleteAppById(id);
        if (count <= 0) {
            return ResponseResult.FAIL("删除APP信息失败");
        }
        return ResponseResult.SUCCESS("删除APP信息成功");
    }

    @Override
    public ResponseResult checkAppUpdateInfo(Integer versionCode) {
        if (versionCode == null) {
            return ResponseResult.FAIL("versionCode为空");
        }
        BlogApp latestAppInfo = appDao.getLatestAppInfo();
        if (latestAppInfo.getVersionCode() > versionCode) {
            return ResponseResult.SUCCESS("APP有更新").setData(latestAppInfo);
        }
        return ResponseResult.SUCCESS("APP暂无更新");
    }

    @Override
    public ResponseResult getAppInfoList() {
        List<BlogApp> appInfoList = appDao.findAll();
        return ResponseResult.SUCCESS("获取APP信息列表成功").setData(appInfoList);
    }
}
