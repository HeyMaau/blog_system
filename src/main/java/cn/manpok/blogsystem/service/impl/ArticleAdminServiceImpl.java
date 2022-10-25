package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IArticleAdminDao;
import cn.manpok.blogsystem.dao.ICategoryDao;
import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.pojo.BlogCategory;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IArticleAdminService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.PageUtil;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
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
public class ArticleAdminServiceImpl implements IArticleAdminService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IArticleAdminDao articleAdminDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICategoryDao categoryDao;

    @Override
    public ResponseResult addArticle(BlogArticle blogArticle) {
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
            //草稿类型：不允许标题、内容、摘要都为空
            if (TextUtil.isEmpty(blogArticle.getContent())
                    && TextUtil.isEmpty(blogArticle.getTitle())
                    && TextUtil.isEmpty(blogArticle.getSummary())) {
                return ResponseResult.FAIL("草稿的标题、内容、摘要均为空");
            }
        } else if (state.equals(Constants.Article.STATE_PUBLISH)) {
            //发布类型：标题、内容、摘要不允许为空
            if (TextUtil.isEmpty(blogArticle.getTitle())) {
                return ResponseResult.FAIL("文章标题为空");
            }
            if (TextUtil.isEmpty(blogArticle.getContent())) {
                return ResponseResult.FAIL("文章内容为空");
            }
            if (TextUtil.isEmpty(blogArticle.getSummary())) {
                return ResponseResult.FAIL("文章摘要为空");
            }
        } else {
            return ResponseResult.FAIL(ResponseState.OPERATION_NOT_PERMITTED);
        }
        //补充数据
        blogArticle.setId(String.valueOf(snowflake.nextId()));
        BlogUser user = userService.checkUserToken();
        blogArticle.setUserId(user.getId());
        Date date = new Date();
        blogArticle.setViewCount(Constants.Article.INITIAL_VIEW_COUNT);
        blogArticle.setCreateTime(date);
        blogArticle.setUpdateTime(date);
        articleAdminDao.save(blogArticle);
        return ResponseResult.SUCCESS("添加文章成功");
    }

    @Override
    public ResponseResult getArticles(int page, int size, String keywords, String state) {
        //检查页码参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        //构建分页
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.DESC, "updateTime");
        //构建条件查询
        Page<BlogArticle> all = articleAdminDao.findAll((Specification<BlogArticle>) (root, query, criteriaBuilder) -> {
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
            Predicate[] predicates = new Predicate[predicateList.size()];
            predicateList.toArray(predicates);
            //条件一与条件二用and连接
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
}
