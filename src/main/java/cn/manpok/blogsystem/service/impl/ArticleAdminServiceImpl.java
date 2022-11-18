package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IArticleAdminDao;
import cn.manpok.blogsystem.dao.IArticleAdminSimpleDao;
import cn.manpok.blogsystem.dao.ICategoryDao;
import cn.manpok.blogsystem.dao.ICommentAdminDao;
import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.pojo.BlogArticleSimple;
import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IArticleAdminService;
import cn.manpok.blogsystem.service.ILabelService;
import cn.manpok.blogsystem.service.ISolrSearchService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.*;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ArticleAdminServiceImpl implements IArticleAdminService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IArticleAdminDao articleAdminDao;

    @Autowired
    private IArticleAdminSimpleDao articleAdminSimpleDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICategoryDao categoryDao;

    @Autowired
    private ILabelService labelService;

    @Autowired
    private ISolrSearchService solrSearchService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Gson gson;

    @Autowired
    private ICommentAdminDao commentAdminDao;

    @Override
    public ResponseResult addArticle(BlogArticle blogArticle) {
        BlogArticle article2Save = null;
        //若请求包含ID，先从数据库从查询文章
        String id = blogArticle.getId();
        if (!TextUtil.isEmpty(id)) {
            article2Save = articleAdminDao.findArticleById(id);
        }
        //如果数据库查询出来为空，则新创建一个article，需要填入文章ID和用户ID
        if (article2Save == null) {
            article2Save = new BlogArticle();
            article2Save.setId(String.valueOf(snowflake.nextId()));
            BlogUser user = userService.checkUserToken();
            article2Save.setUserId(user.getId());
        } else {
            //已发布的文章不允许再请求本接口
            if (article2Save.getState().equals(Constants.Article.STATE_PUBLISH)) {
                return ResponseResult.FAIL(ResponseState.OPERATION_NOT_PERMITTED);
            }
        }
        //检查分类ID是否为空
        BlogCategory queryCategory = categoryDao.findCategoryById(blogArticle.getCategoryId());
        if (queryCategory == null) {
            return ResponseResult.FAIL("文章分类不存在");
        }
        //检查文章类型：富文本、MD
        String type = blogArticle.getType();
        if (!type.equals(Constants.Article.TYPE_RICH_TEXT) && !type.equals(Constants.Article.TYPE_MARKDOWN)) {
            return ResponseResult.FAIL("文章类型不正确");
        }
        //检查文章类型：草稿、发表，其他类型在此API不允许
        String state = blogArticle.getState();
        if (state.equals(Constants.Article.STATE_DRAFT)) {
            //草稿类型：
            //1、不允许标题为空
            if (TextUtil.isEmpty(blogArticle.getTitle())) {
                return ResponseResult.FAIL("草稿标题为空");
            }
            //2、内容、摘要都为空
            if (TextUtil.isEmpty(blogArticle.getContent())
                    && TextUtil.isEmpty(blogArticle.getSummary())) {
                return ResponseResult.FAIL("草稿的内容、摘要均为空");
            }
        } else if (state.equals(Constants.Article.STATE_PUBLISH)) {
            //发布类型：标题、内容、摘要、标签都不允许为空
            if (TextUtil.isEmpty(blogArticle.getTitle())) {
                return ResponseResult.FAIL("文章标题为空");
            }
            if (TextUtil.isEmpty(blogArticle.getContent())) {
                return ResponseResult.FAIL("文章内容为空");
            }
            if (TextUtil.isEmpty(blogArticle.getSummary())) {
                return ResponseResult.FAIL("文章摘要为空");
            }
            if (TextUtil.isEmpty(blogArticle.getLabels())) {
                return ResponseResult.FAIL("文章标签为空");
            }
        } else {
            return ResponseResult.FAIL(ResponseState.OPERATION_NOT_PERMITTED);
        }
        //补充数据
        article2Save.setTitle(blogArticle.getTitle());
        article2Save.setCategoryId(blogArticle.getCategoryId());
        article2Save.setContent(blogArticle.getContent());
        article2Save.setType(blogArticle.getType());
        article2Save.setState(blogArticle.getState());
        article2Save.setSummary(blogArticle.getSummary());
        article2Save.setLabels(blogArticle.getLabels());
        article2Save.setViewCount(Constants.Article.INITIAL_VIEW_COUNT);
        Date date = new Date();
        article2Save.setCreateTime(date);
        article2Save.setUpdateTime(date);
        articleAdminDao.save(article2Save);
        //保存标签数据
        labelService.addLabelInDB(blogArticle.getLabels());
        //保存到SOLR
        solrSearchService.addArticle(article2Save);
        return ResponseResult.SUCCESS("添加文章成功").setData(article2Save.getId());
    }

    @Override
    public ResponseResult getArticles(int page, int size, String categoryID, String keywords, String state) {
        //检查页码参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        //构建分页
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.DESC, "updateTime");
        //构建条件查询
        Page<BlogArticleSimple> all = articleAdminSimpleDao.findAll((Specification<BlogArticleSimple>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            //条件一：当关键词不为空时，模糊匹配标题、内容、摘要，用or连接
            if (!TextUtil.isEmpty(keywords)) {
                Predicate title = criteriaBuilder.like(root.get("title"), "%" + keywords + "%");
                Predicate content = criteriaBuilder.like(root.get("content"), "%" + keywords + "%");
                Predicate summary = criteriaBuilder.like(root.get("summary"), "%" + keywords + "%");
                Predicate keywordsPredicate = criteriaBuilder.or(title, content, summary);
                predicateList.add(keywordsPredicate);
            }
            //条件二：当文章状态不为空时，筛选文章状态
            if (!TextUtil.isEmpty(state)) {
                Predicate statePredicate = criteriaBuilder.equal(root.get("state"), state);
                predicateList.add(statePredicate);
            }
            //条件三：当分类号不为空时，匹配分类号
            if (!TextUtil.isEmpty(categoryID)) {
                Predicate categoryPredicate = criteriaBuilder.equal(root.get("categoryId"), categoryID);
                predicateList.add(categoryPredicate);
            }
            Predicate[] predicates = new Predicate[predicateList.size()];
            predicateList.toArray(predicates);
            //条件一、条件二、条件三用and连接
            return criteriaBuilder.and(predicates);
        }, pageable);
        return ResponseResult.SUCCESS("获取文章列表成功").setData(all);
    }

    @Override
    public ResponseResult getArticle(String articleID) {
        BlogArticle queryArticle = articleAdminDao.findArticleById(articleID);
        if (queryArticle == null) {
            return ResponseResult.FAIL("文章不存在");
        }
        return ResponseResult.SUCCESS("获取文章成功").setData(queryArticle);
    }

    @Override
    public ResponseResult topArticle(String articleID) {
        BlogArticle queryArticle = articleAdminDao.findArticleById(articleID);
        if (queryArticle == null) {
            return ResponseResult.FAIL("文章不存在");
        }
        String state = queryArticle.getState();
        //如果文章已置顶，则取消置顶
        if (state.equals(Constants.Article.STATE_TOP)) {
            queryArticle.setState(Constants.Article.STATE_PUBLISH);
            return ResponseResult.SUCCESS("取消置顶文章成功");
        }
        //只有已发布的文章才允许置顶操作
        if (!queryArticle.getState().equals(Constants.Article.STATE_PUBLISH)) {
            return ResponseResult.FAIL(ResponseState.OPERATION_NOT_PERMITTED);
        }
        queryArticle.setState(Constants.Article.STATE_TOP);
        return ResponseResult.SUCCESS("置顶文章成功");
    }

    @Override
    public ResponseResult deleteArticle(String articleID) {
        //先删除文章下所有的评论
        int commentsDeleteCount = commentAdminDao.deleteCommentsByArticleId(articleID);
        log.info("删除文章 ----> " + articleID + "所有评论 ----> " + commentsDeleteCount);
        //再删除数据库中的文章
        int deleteCount = articleAdminDao.deleteArticleById(articleID);
        //solr中的文章也要删除
        solrSearchService.deleteArticle(articleID);
        //redis中的缓存也要删掉，包括阅读量和文章
        redisUtil.del(Constants.Article.KEY_ARTICLE_CACHE + articleID);
        redisUtil.del(Constants.Article.KEY_VIEW_COUNT_CACHE + articleID);
        if (deleteCount < 1) {
            return ResponseResult.FAIL("删除文章失败");
        }
        return ResponseResult.SUCCESS("删除文章成功");
    }

    @Override
    public ResponseResult updateArticleState(String articleID) {
        BlogArticle queryArticle = articleAdminDao.findArticleById(articleID);
        if (queryArticle == null) {
            return ResponseResult.FAIL("文章不存在");
        }
        queryArticle.setState(Constants.Article.STATE_DELETE);
        return ResponseResult.SUCCESS("删除文章成功");
    }

    @Override
    public ResponseResult updateArticle(BlogArticle blogArticle) {
        //检查不允许为空的参数
        if (TextUtil.isEmpty(blogArticle.getTitle())) {
            return ResponseResult.FAIL("文章标题为空");
        }
        if (TextUtil.isEmpty(blogArticle.getContent())) {
            return ResponseResult.FAIL("文章内容为空");
        }
        if (TextUtil.isEmpty(blogArticle.getSummary())) {
            return ResponseResult.FAIL("文章摘要为空");
        }
        if (TextUtil.isEmpty(blogArticle.getLabels())) {
            return ResponseResult.FAIL("文章标签为空");
        }
        String type = blogArticle.getType();
        if (!type.equals(Constants.Article.TYPE_RICH_TEXT) && !type.equals(Constants.Article.TYPE_MARKDOWN)) {
            return ResponseResult.FAIL("文章类型错误");
        }
        BlogArticle queryArticle = articleAdminDao.findArticleById(blogArticle.getId());
        if (queryArticle == null) {
            return ResponseResult.FAIL("文章不存在");
        }
        BlogCategory queryCategory = categoryDao.findCategoryById(blogArticle.getCategoryId());
        if (queryCategory == null) {
            return ResponseResult.FAIL("分类不存在");
        }
        if (!queryArticle.getState().equals(Constants.Article.STATE_PUBLISH)) {
            return ResponseResult.FAIL("文章未发表");
        }
        //更新标签
        labelService.updateLabelInDB(queryArticle.getLabels(), blogArticle.getLabels());
        //更新数据
        queryArticle.setTitle(blogArticle.getTitle());
        queryArticle.setContent(blogArticle.getContent());
        queryArticle.setType(blogArticle.getType());
        queryArticle.setSummary(blogArticle.getSummary());
        queryArticle.setLabels(blogArticle.getLabels());
        queryArticle.setCategoryId(blogArticle.getCategoryId());
        queryArticle.setUpdateTime(new Date());
        //更新solr
        solrSearchService.updateArticle(queryArticle);
        //更新redis中的缓存
        redisUtil.set(Constants.Article.KEY_ARTICLE_CACHE + blogArticle.getId(), gson.toJson(queryArticle), Constants.TimeValue.HOUR_2);
        return ResponseResult.SUCCESS("修改文章成功");
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
        redisUtil.set(Constants.Article.KEY_ARTICLE_CACHE + articleID, gson.toJson(queryArticle));
        return ResponseResult.SUCCESS("获取文章成功").setData(queryArticle);
    }

    @Override
    public ResponseResult getArticlesByLabel(int page, int size, String label) {
        //检查页码参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        //构建分页
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.DESC, "updateTime");
        Page<BlogArticleSimple> all = articleAdminSimpleDao.findAllArticlesByLabelsContaining(label, pageable);
        return ResponseResult.SUCCESS("获取文章列表成功").setData(all);
    }


}
