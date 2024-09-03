package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ShareServiceImpl implements IShareService {

    @Value("${blog.system.share.article.url}")
    private String baseArticleShareLink;

    @Override
    public ResponseResult getArticleShareLink(String id) {
        String articleShareLink = baseArticleShareLink + "/" + id;
        Map<String, String> map = new HashMap<>(1);
        map.put("url", articleShareLink);
        return ResponseResult.SUCCESS("获取文章分享链接成功").setData(map);
    }
}
