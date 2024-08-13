package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IArticleAdminDao;
import cn.manpok.blogsystem.dao.IArticleAdminSimpleDao;
import cn.manpok.blogsystem.dao.IArticlePortalDao;
import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.pojo.BlogArticleSimple;
import cn.manpok.blogsystem.pojo.BlogPaging;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IArticlePortalService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.PageUtil;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.TextUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Gson gson;

    @Autowired
    private IArticleAdminDao articleAdminDao;

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

    @Override
    public ResponseResult getNormalArticles(int page, int size, String categoryID) {
        //检查页码参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        if (pageInfo.page == 1) {
            String key = Constants.Article.KEY_ARTICLE_LIST_CACHE;
            if (categoryID != null) {
                key += categoryID;
            }
            String articlesStr = (String) redisUtil.get(key);
            if (!TextUtil.isEmpty(articlesStr)) {
                BlogPaging<List<BlogArticle>> articleListCache = gson.fromJson(articlesStr, new TypeToken<BlogPaging<List<BlogArticle>>>() {
                }.getType());
                log.info("从redis中取出文章列表第一页");
                return ResponseResult.SUCCESS("获取文章列表成功").setData(articleListCache);
            }
        }
        //构建分页
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.DESC, "state", "createTime");
        //构建条件查询
        Page<BlogArticle> all = articleAdminDao.findAll((Specification<BlogArticle>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            //条件一：筛选文章状态为发布或置顶的
            Predicate statePublishPredicate = criteriaBuilder.equal(root.get("state"), Constants.Article.STATE_PUBLISH);
            Predicate stateTopPredicate = criteriaBuilder.equal(root.get("state"), Constants.Article.STATE_TOP);
            predicateList.add(criteriaBuilder.or(statePublishPredicate, stateTopPredicate));
            //条件二：当分类号不为空时，匹配分类号
            if (!TextUtil.isEmpty(categoryID)) {
                Predicate categoryPredicate = criteriaBuilder.equal(root.get("categoryId"), categoryID);
                predicateList.add(categoryPredicate);
            }
            Predicate[] predicates = new Predicate[predicateList.size()];
            predicateList.toArray(predicates);
            //条件一、条件二用and连接
            return criteriaBuilder.and(predicates);
        }, pageable);
        //返回前端的文章内容需要去除html标签
        for (BlogArticle article : all.getContent()) {
            String text = Jsoup.parse(article.getContent()).text();
            article.setContent(text);
        }
        //要把分页封装到自定义的Paging中，因gson序列化与反序列化需要
        BlogPaging<List<BlogArticle>> paging = new BlogPaging<>(pageInfo.page, pageInfo.size, all.getTotalElements(), all.getContent());
        //如果是第一页的文章，缓存到redis中
        if (pageInfo.page == 1) {
            String key = Constants.Article.KEY_ARTICLE_LIST_CACHE;
            if (categoryID != null) {
                key += categoryID;
            }
            redisUtil.set(key, gson.toJson(paging), Constants.TimeValue.MIN_10);
            log.info("文章列表第一页已缓存到redis");
        }
        return ResponseResult.SUCCESS("获取文章列表成功").setData(paging);
    }

    @Override
    public ResponseResult getNormalArticle(String articleID) {
        //先从缓存中查文章对应的阅读量
        Long viewCountCache = (Long) redisUtil.get(Constants.Article.KEY_VIEW_COUNT_CACHE + articleID);
        if (viewCountCache == null) {
            BlogArticle queryArticle = articleAdminDao.findArticleById(articleID);
            if (queryArticle == null) {
                return ResponseResult.FAIL("文章不存在");
            }
            long viewCount = queryArticle.getViewCount();
            queryArticle.setViewCount(++viewCount);
            redisUtil.set(Constants.Article.KEY_ARTICLE_CACHE + articleID, gson.toJson(queryArticle), Constants.TimeValue.HOUR_2);
            redisUtil.set(Constants.Article.KEY_VIEW_COUNT_CACHE + articleID, viewCount);
            return ResponseResult.SUCCESS("获取文章成功").setData(queryArticle);
        }
        //若有阅读量缓存，则从redis中查文章的缓存
        String articleCache = (String) redisUtil.get(Constants.Article.KEY_ARTICLE_CACHE + articleID);
        if (!TextUtil.isEmpty(articleCache)) {
            //如果缓存中有，则直接返回结果，不再查询数据库
            BlogArticle article = gson.fromJson(articleCache, BlogArticle.class);
            //阅读量+1
            article.setViewCount(++viewCountCache);
            //刷新阅读量缓存
            redisUtil.set(Constants.Article.KEY_VIEW_COUNT_CACHE + articleID, viewCountCache);
            //刷新文章缓存
            redisUtil.set(Constants.Article.KEY_ARTICLE_CACHE + articleID, gson.toJson(article), Constants.TimeValue.HOUR_2);
            return ResponseResult.SUCCESS("获取文章成功").setData(article);
        }
        //缓存中没有，则从数据库中查询
        BlogArticle queryArticle = articleAdminDao.findArticleById(articleID);
        if (queryArticle == null) {
            return ResponseResult.FAIL("文章不存在");
        }
        String state = queryArticle.getState();
        if (state.equals(Constants.Article.STATE_DELETE) || state.equals(Constants.Article.STATE_DRAFT)) {
            return ResponseResult.FAIL(ResponseState.OPERATION_NOT_PERMITTED);
        }
        //将文章访问量缓存写回数据库
        queryArticle.setViewCount(++viewCountCache);
        //刷新文章访问量缓存
        redisUtil.set(Constants.Article.KEY_VIEW_COUNT_CACHE + articleID, viewCountCache);
        //保存文章缓存
        redisUtil.set(Constants.Article.KEY_ARTICLE_CACHE + articleID, gson.toJson(queryArticle), Constants.TimeValue.HOUR_2);
        return ResponseResult.SUCCESS("获取文章成功").setData(queryArticle);
    }
}
