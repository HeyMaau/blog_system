package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IAppDao;
import cn.manpok.blogsystem.pojo.BlogApp;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IAppService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import com.google.gson.Gson;
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

    private final RedisUtil redisUtil;

    private final Gson gson;

    public AppServiceImpl(IAppDao appDao, Snowflake snowflake, RedisUtil redisUtil, Gson gson) {
        this.appDao = appDao;
        this.snowflake = snowflake;
        this.redisUtil = redisUtil;
        this.gson = gson;
    }

    @Override
    public ResponseResult getAppDownloadUrl() {
        String appInfoStr = (String) redisUtil.get(Constants.APP.KEY_LATEST_APP_INFO);
        if (!TextUtil.isEmpty(appInfoStr)) {
            BlogApp appInfoCache = gson.fromJson(appInfoStr, BlogApp.class);
            if (appInfoCache != null) {
                log.info("从redis取出APP下载链接");
                return ResponseResult.SUCCESS().setData(appInfoCache);
            }
        }
        BlogApp appInfo = appDao.getLatestAppInfo();
        if (appInfo != null) {
            appInfoStr = gson.toJson(appInfo);
            redisUtil.set(Constants.APP.KEY_LATEST_APP_INFO, appInfoStr, Constants.TimeValue.HOUR_2);
            log.info("APP下载链接已存入redis");
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
        redisUtil.del(Constants.APP.KEY_LATEST_APP_INFO);
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
        redisUtil.del(Constants.APP.KEY_LATEST_APP_INFO);
        return ResponseResult.SUCCESS("删除APP信息成功");
    }

    @Override
    public ResponseResult checkAppUpdateInfo(Integer versionCode) {
        if (versionCode == null) {
            return ResponseResult.FAIL("versionCode为空");
        }
        BlogApp latestAppInfo = null;
        String appInfoStr = (String) redisUtil.get(Constants.APP.KEY_LATEST_APP_INFO);
        if (!TextUtil.isEmpty(appInfoStr)) {
            latestAppInfo = gson.fromJson(appInfoStr, BlogApp.class);
        }
        if (latestAppInfo != null) {
            log.info("从redis取出最新APP信息");
        } else {
            latestAppInfo = appDao.getLatestAppInfo();
            appInfoStr = gson.toJson(latestAppInfo);
            redisUtil.set(Constants.APP.KEY_LATEST_APP_INFO, appInfoStr, Constants.TimeValue.HOUR_2);
            log.info("最新APP信息已存入redis");
        }
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
