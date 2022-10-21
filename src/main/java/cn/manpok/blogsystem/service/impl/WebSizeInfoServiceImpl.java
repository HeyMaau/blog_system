package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IWebSizeInfoDao;
import cn.manpok.blogsystem.pojo.BlogSetting;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IWebSizeInfoService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
public class WebSizeInfoServiceImpl implements IWebSizeInfoService {

    @Autowired
    private IWebSizeInfoDao webSizeInfoDao;

    @Autowired
    private Snowflake snowflake;

    @Override
    public ResponseResult updateWebSizeTitle(String title) {
        if (TextUtil.isEmpty(title)) {
            return ResponseResult.FAIL("网站标题为空");
        }
        //先从数据库中查询出来
        BlogSetting queryTitle = webSizeInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_TITLE);
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
        webSizeInfoDao.save(queryTitle);
        return ResponseResult.SUCCESS("保存网站标题成功");
    }

    @Override
    public ResponseResult getWebSizeTitle() {
        BlogSetting queryTitle = webSizeInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_TITLE);
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
        BlogSetting queryKeywords = webSizeInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_SEO_KEYWORDS);
        BlogSetting queryDescription = webSizeInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_SEO_DESCRIPTION);
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
        webSizeInfoDao.save(queryKeywords);
        webSizeInfoDao.save(queryDescription);
        return ResponseResult.SUCCESS("修改网站SEO信息成功");
    }

    @Override
    public ResponseResult getSeoInfo() {
        //从数据库中查询
        BlogSetting queryKeywords = webSizeInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_SEO_KEYWORDS);
        BlogSetting queryDescription = webSizeInfoDao.findSettingByKey(Constants.Setting.WEB_SIZE_INFO_SEO_DESCRIPTION);
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
}
