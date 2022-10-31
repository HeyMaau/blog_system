package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IArticleAdminSimpleDao;
import cn.manpok.blogsystem.dao.IArticlePortalDao;
import cn.manpok.blogsystem.pojo.BlogArticleSimple;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IArticlePortalService;
import cn.manpok.blogsystem.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


@Service
@Slf4j
public class ArticlePortalServiceImpl implements IArticlePortalService {

    @Autowired
    private IArticleAdminSimpleDao articleAdminSimpleDao;

    @Autowired
    private Random random;

    @Autowired
    private IArticlePortalDao articlePortalDao;

    @Override
    public ResponseResult getRecommendArticle(String articleID, int size) {
        //从数据库中查询本文章对应的标签
        BlogArticleSimple queryArticle = articleAdminSimpleDao.findArticleSimpleById(articleID);
        if (queryArticle == null) {
            return ResponseResult.FAIL("文章不存在");
        }
        //分割标签
        String[] labels = queryArticle.getLabels().split(Constants.Label.LABEL_SEPARATOR);
        //随机选取标签
        String label = labels[random.nextInt(labels.length)];
        log.info("本次随机选取的label ----> " + label);
        //从数据库中查询与标签相关的文章，不包含本文章
        List<BlogArticleSimple> recommendArticles = articlePortalDao.findArticlesByLabel("%" + label + "%", articleID, size);
        //如果获取标签相关的文章数量少于需要的数量，则从最新的文章中补充
        if (recommendArticles.size() < size) {
            List<BlogArticleSimple> latestArticles = articlePortalDao.findLatestArticles(size - recommendArticles.size());
            recommendArticles.addAll(latestArticles);
        }
        return ResponseResult.SUCCESS("获取推荐文章成功").setData(recommendArticles);
    }
}
