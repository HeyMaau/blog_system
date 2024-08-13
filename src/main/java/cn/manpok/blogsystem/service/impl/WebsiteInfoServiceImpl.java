package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IWebsiteInfoDao;
import cn.manpok.blogsystem.pojo.BlogSetting;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IWebsiteInfoService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
@Slf4j
public class WebsiteInfoServiceImpl implements IWebsiteInfoService {

    @Autowired
    private IWebsiteInfoDao websiteInfoDao;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResponseResult updateWebsiteTitle(String title) {
        if (TextUtil.isEmpty(title)) {
            return ResponseResult.FAIL("网站标题为空");
        }
        //先从数据库中查询出来
        BlogSetting queryTitle = websiteInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_TITLE);
        //如果为空，则创建新的
        Date date = new Date();
        if (queryTitle == null) {
            queryTitle = new BlogSetting();
            queryTitle.setId(String.valueOf(snowflake.nextId()));
            queryTitle.setKey(Constants.Setting.WEB_SIZE_INFO_TITLE);
            queryTitle.setCreateTime(date);
        }
        queryTitle.setValue(title);
        queryTitle.setUpdateTime(date);
        websiteInfoDao.save(queryTitle);
        return ResponseResult.SUCCESS("保存网站标题成功");
    }

    @Override
    public ResponseResult getWebsiteTitle() {
        BlogSetting queryTitle = websiteInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_TITLE);
        if (queryTitle == null) {
            return ResponseResult.FAIL("网站标题为空");
        }
        Map<String, String> result = new HashMap<>(1);
        result.put("title", queryTitle.getValue());
        return ResponseResult.SUCCESS("获取网站标题成功").setData(result);
    }

    @Override
    public ResponseResult updateSeoInfo(String keywords, String description) {
        //校验参数
        if (TextUtil.isEmpty(keywords)) {
            return ResponseResult.FAIL("SEO关键字为空");
        }
        if (TextUtil.isEmpty(description)) {
            return ResponseResult.FAIL("SEO描述为空");
        }
        //从数据库中查询
        BlogSetting queryKeywords = websiteInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_SEO_KEYWORDS);
        BlogSetting queryDescription = websiteInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_SEO_DESCRIPTION);
        //若关键字为空，则创建，并保存
        Date date = new Date();
        if (queryKeywords == null) {
            queryKeywords = new BlogSetting();
            queryKeywords.setId(String.valueOf(snowflake.nextId()));
            queryKeywords.setKey(Constants.Setting.WEB_SIZE_INFO_SEO_KEYWORDS);
            queryKeywords.setCreateTime(date);
        }
        queryKeywords.setValue(keywords);
        queryKeywords.setUpdateTime(date);
        //若描述为空，则创建，并保存
        if (queryDescription == null) {
            queryDescription = new BlogSetting();
            queryDescription.setId(String.valueOf(snowflake.nextId()));
            queryDescription.setKey(Constants.Setting.WEB_SIZE_INFO_SEO_DESCRIPTION);
            queryDescription.setCreateTime(date);
        }
        queryDescription.setValue(description);
        queryDescription.setUpdateTime(date);
        websiteInfoDao.save(queryKeywords);
        websiteInfoDao.save(queryDescription);
        return ResponseResult.SUCCESS("修改网站SEO信息成功");
    }

    @Override
    public ResponseResult getSeoInfo() {
        //从数据库中查询
        BlogSetting queryKeywords = websiteInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_SEO_KEYWORDS);
        BlogSetting queryDescription = websiteInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_SEO_DESCRIPTION);
        Map<String, String> result = new HashMap<>(2);
        if (queryKeywords == null) {
            result.put("keywords", "");
        } else {
            result.put("keywords", queryKeywords.getValue());
        }
        if (queryDescription == null) {
            result.put("description", "");
        } else {
            result.put("description", queryDescription.getValue());
        }
        return ResponseResult.SUCCESS("查询网站SEO信息成功").setData(result);
    }

    @Override
    public ResponseResult getWebsiteViewCount() {
        //先从数据库中查询
        BlogSetting queryViewCount = websiteInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_VIEW_COUNT);
        //如果为空，则创建一个新的
        if (queryViewCount == null) {
            queryViewCount = initViewCountInDB();
        }
        //从redis中查询访问量数据
        Integer viewCount = (Integer) redisUtil.get(Constants.Setting.WEB_SIZE_INFO_VIEW_COUNT);
        //如果redis中有数据，将新的数据保存到数据库中
        if (viewCount != null) {
            queryViewCount.setValue(String.valueOf(viewCount));
            queryViewCount.setUpdateTime(new Date());
            websiteInfoDao.save(queryViewCount);
        }
        Map<String, String> result = new HashMap<>(1);
        result.put("view_count", queryViewCount.getValue());
        return ResponseResult.SUCCESS("查询网站访问量成功").setData(result);
    }

    /**
     * 初始化数据库中的访问量
     */
    private BlogSetting initViewCountInDB() {
        BlogSetting blogSetting = new BlogSetting();
        blogSetting.setId(String.valueOf(snowflake.nextId()));
        blogSetting.setKey(Constants.Setting.WEB_SIZE_INFO_VIEW_COUNT);
        blogSetting.setValue("1");
        Date date = new Date();
        blogSetting.setCreateTime(date);
        blogSetting.setUpdateTime(date);
        websiteInfoDao.save(blogSetting);
        return blogSetting;
    }

    @Override
    public void updateWebsiteViewCount() {
        //先查询redis
        Integer viewCount = (Integer) redisUtil.get(Constants.Setting.WEB_SIZE_INFO_VIEW_COUNT);
        if (viewCount == null) {
            //如果redis中没有访问量数据，则从数据库中查询
            BlogSetting queryViewCountInDB = websiteInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_VIEW_COUNT);
            if (queryViewCountInDB == null) {
                queryViewCountInDB = initViewCountInDB();
            }
            viewCount = Integer.valueOf(queryViewCountInDB.getValue());
        }
        //如果redis里面有，则直接redis数据+1
        redisUtil.set(Constants.Setting.WEB_SIZE_INFO_VIEW_COUNT, ++viewCount);
        log.info("viewCount ----> " + viewCount);
    }
}
