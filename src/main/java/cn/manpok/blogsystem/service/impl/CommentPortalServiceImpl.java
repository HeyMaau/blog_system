package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IArticleAdminSimpleDao;
import cn.manpok.blogsystem.dao.ICommentPortalDao;
import cn.manpok.blogsystem.dao.ICommentSimplePortalDao;
import cn.manpok.blogsystem.dao.IThinkingDao;
import cn.manpok.blogsystem.pojo.*;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ICommentPortalService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class CommentPortalServiceImpl implements ICommentPortalService {

    @Autowired
    private IUserService userService;

    @Autowired
    private IArticleAdminSimpleDao articleAdminSimpleDao;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ICommentPortalDao commentPortalDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Gson gson;

    @Autowired
    private ICommentSimplePortalDao commentSimplePortalDao;

    @Autowired
    private IThinkingDao thinkingDao;

    @Override
    public ResponseResult addComment(BlogComment blogComment) {
        //检查评论的参数，必填项：文章ID，内容
        String articleID = blogComment.getArticleId();
        if (TextUtil.isEmpty(articleID)) {
            return ResponseResult.FAIL("文章ID为空");
        }
        switch (blogComment.getType()) {
            case Constants.Comment.TYPE_ARTICLE:
                BlogArticleSimple articleSimple = articleAdminSimpleDao.findArticleSimpleById(articleID);
                if (articleSimple == null) {
                    return ResponseResult.FAIL("文章不存在");
                }
                break;
            case Constants.Comment.TYPE_THINKING:
                BlogThinking thinking = thinkingDao.findThinkingById(articleID);
                if (thinking == null) {
                    return ResponseResult.FAIL("想法不存在");
                }
                break;
            default:
        }
        if (TextUtil.isEmpty(blogComment.getContent())) {
            return ResponseResult.FAIL("评论内容为空");
        }
        if (TextUtil.isEmpty(blogComment.getUserEmail())) {
            return ResponseResult.FAIL("Email为空");
        }
        if (TextUtil.isEmpty(blogComment.getUserName())) {
            return ResponseResult.FAIL("昵称为空");
        }
        if (!blogComment.getType().equals(Constants.Comment.TYPE_ARTICLE) && !blogComment.getType().equals(Constants.Comment.TYPE_THINKING)) {
            return ResponseResult.FAIL("评论类型不正确");
        }
        //补充数据
        blogComment.setId(String.valueOf(snowflake.nextId()));
        blogComment.setState(Constants.STATE_NORMAL);
        //用EMAIL的MD5作为头像值
        blogComment.setUserAvatar(DigestUtils.md5DigestAsHex(blogComment.getUserEmail().getBytes()));
        Date date = new Date();
        blogComment.setCreateTime(date);
        blogComment.setUpdateTime(date);
        commentPortalDao.save(blogComment);
        //清除redis中的缓存
        switch (blogComment.getType()) {
            case Constants.Comment.TYPE_ARTICLE:
                redisUtil.del(Constants.Comment.KEY_ARTICLE_COMMENTS_CACHE + blogComment.getArticleId());
                break;
            case Constants.Comment.TYPE_THINKING:
                redisUtil.del(Constants.Comment.KEY_THINKING_COMMENTS_CACHE + blogComment.getArticleId());
                break;
            default:
        }
        return ResponseResult.SUCCESS("发表评论成功");
    }

    /*@Override
    public ResponseResult deleteCommend(String commentID) {
        //数据库查询对应的评论
        BlogComment queryComment = commentPortalDao.findCommentById(commentID);
        if (queryComment == null) {
            return ResponseResult.FAIL("评论不存在");
        }
        //获取用户登录信息
        BlogUser user = userService.checkUserToken();
        //对比评论所属用户ID是否为登录用户ID，只有自己的评论才可以删除
        if (!queryComment.getUserId().equals(user.getId())) {
            return ResponseResult.FAIL(ResponseState.OPERATION_NOT_PERMITTED);
        }
        queryComment.setState(Constants.STATE_FORBIDDEN);
        //清除redis中的缓存
        redisUtil.del(Constants.Comment.KEY_COMMENTS_CACHE + queryComment.getArticleId());
        return ResponseResult.SUCCESS("删除评论成功");
    }*/

    @Override
    public ResponseResult getComments(String articleID, String type, int page, int size) {
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        if (pageInfo.page == 1) {
            String commentsStr = null;
            switch (type) {
                case Constants.Comment.TYPE_ARTICLE:
                    commentsStr = (String) redisUtil.get(Constants.Comment.KEY_ARTICLE_COMMENTS_CACHE + articleID);
                    break;
                case Constants.Comment.TYPE_THINKING:
                    commentsStr = (String) redisUtil.get(Constants.Comment.KEY_THINKING_COMMENTS_CACHE + articleID);
                    break;
                default:
            }
            if (!TextUtil.isEmpty(commentsStr)) {
                BlogPaging<List<BlogCommentSimple>> commentsCache = gson.fromJson(commentsStr, new TypeToken<BlogPaging<List<BlogCommentSimple>>>() {
                }.getType());
                log.info("从redis中取出文章评论 ----> " + articleID);
                return ResponseResult.SUCCESS("获取所有评论成功").setData(commentsCache);
            }
        }
        //先找出文章的根评论
        Sort.Order stateOrder = new Sort.Order(Sort.Direction.DESC, "state");
        Sort.Order createTimeOrder = new Sort.Order(Sort.Direction.ASC, "createTime");
        Sort sort = Sort.by(stateOrder, createTimeOrder);
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, sort);
        Page<BlogCommentSimple> rootComments = commentSimplePortalDao.findAll(new Specification<BlogCommentSimple>() {
            @Override
            public Predicate toPredicate(Root<BlogCommentSimple> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate statePredicate = criteriaBuilder.notEqual(root.get("state"), Constants.STATE_FORBIDDEN);
                Predicate articlePredicate = criteriaBuilder.equal(root.get("articleId"), articleID);
                Predicate typePre = criteriaBuilder.equal(root.get("type"), type);
                Predicate parentCommentPredicate = criteriaBuilder.isNull(root.get("parentCommentId"));
                return criteriaBuilder.and(statePredicate, articlePredicate, parentCommentPredicate, typePre);
            }
        }, pageable);
        //根据根评论找出所属的子评论（回复评论）
        for (BlogCommentSimple rootComment : rootComments.getContent()) {
            List<BlogCommentSimple> children = commentSimplePortalDao.findChildrenByParentCommentId(rootComment.getId());
            rootComment.setChildren(children);
        }
        //要把分页封装到自定义的Paging中，因gson序列化与反序列化需要
        BlogPaging<List<BlogCommentSimple>> paging = new BlogPaging<>(pageInfo.page, pageInfo.size, rootComments.getTotalElements(), rootComments.getContent());
        //如果是第一页的评论，缓存到redis中
        if (pageInfo.page == 1) {
            switch (type) {
                case Constants.Comment.TYPE_ARTICLE:
                    redisUtil.set(Constants.Comment.KEY_ARTICLE_COMMENTS_CACHE + articleID, gson.toJson(paging), Constants.TimeValue.HOUR_2);
                    break;
                case Constants.Comment.TYPE_THINKING:
                    redisUtil.set(Constants.Comment.KEY_THINKING_COMMENTS_CACHE + articleID, gson.toJson(paging), Constants.TimeValue.HOUR_2);
                    break;
                default:
            }
            log.info("文章第一页评论已缓存到redis ----> " + articleID);
        }
        return ResponseResult.SUCCESS("获取所有评论成功").setData(paging);
    }

    /*@Override
    public ResponseResult updateComment(BlogComment blogComment) {
        //查询数据库
        BlogComment queryComment = commentPortalDao.findCommentById(blogComment.getId());
        if (queryComment == null) {
            return ResponseResult.FAIL("评论不存在");
        }
        //检查用户登录
        BlogUser user = userService.checkUserToken();
        if (user == null) {
            return ResponseResult.FAIL(ResponseState.NOT_LOGIN);
        }
        if (!queryComment.getUserId().equals(user.getId())) {
            return ResponseResult.FAIL(ResponseState.OPERATION_NOT_PERMITTED);
        }
        //检查评论内容
        if (TextUtil.isEmpty(blogComment.getContent())) {
            return ResponseResult.FAIL("评论内容为空");
        }
        queryComment.setContent(blogComment.getContent());
        queryComment.setUpdateTime(new Date());
        //清除redis中的缓存
        redisUtil.del(Constants.Comment.KEY_COMMENTS_CACHE + blogComment.getArticleId());
        return ResponseResult.SUCCESS("修改评论成功");
    }*/
}
