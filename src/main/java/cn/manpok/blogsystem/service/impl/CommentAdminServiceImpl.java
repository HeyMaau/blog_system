package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ICommentAdminDao;
import cn.manpok.blogsystem.dao.ICommentPortalDao;
import cn.manpok.blogsystem.pojo.BlogComment;
import cn.manpok.blogsystem.pojo.BlogPaging;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.ICommentAdminService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.PageUtil;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CommentAdminServiceImpl implements ICommentAdminService {

    @Autowired
    private ICommentPortalDao commentPortalDao;

    @Autowired
    private ICommentAdminDao commentAdminDao;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResponseResult getComment(String commentID) {
        BlogComment queryComment = commentPortalDao.findCommentById(commentID);
        if (queryComment == null) {
            return ResponseResult.FAIL("评论不存在");
        }
        return ResponseResult.SUCCESS("获取评论成功").setData(queryComment);
    }

    @Override
    public ResponseResult topComment(String commentID) {
        BlogComment queryComment = commentPortalDao.findCommentById(commentID);
        if (queryComment == null) {
            return ResponseResult.FAIL("评论不存在");
        }
        switch (queryComment.getState()) {
            case Constants.STATE_NORMAL -> {
                queryComment.setState(Constants.Comment.STATE_TOP);
                //清除redis中的缓存
                redisUtil.del(Constants.Comment.KEY_ARTICLE_COMMENTS_CACHE + queryComment.getArticleId());
                redisUtil.del(Constants.Comment.KEY_THINKING_COMMENTS_CACHE + queryComment.getArticleId());
                return ResponseResult.SUCCESS("置顶评论成功");
            }
            case Constants.Comment.STATE_TOP -> {
                queryComment.setState(Constants.STATE_NORMAL);
                //清除redis中的缓存
                redisUtil.del(Constants.Comment.KEY_ARTICLE_COMMENTS_CACHE + queryComment.getArticleId());
                redisUtil.del(Constants.Comment.KEY_THINKING_COMMENTS_CACHE + queryComment.getArticleId());
                return ResponseResult.SUCCESS("取消置顶评论成功");
            }
            default -> {
                return ResponseResult.FAIL(ResponseState.OPERATION_NOT_PERMITTED);
            }
        }
    }

    @Override
    public ResponseResult deleteCommentByState(String commentID) {
        BlogComment queryComment = commentPortalDao.findCommentById(commentID);
        if (queryComment == null) {
            return ResponseResult.FAIL("评论不存在");
        }
        queryComment.setState(Constants.STATE_FORBIDDEN);
        //清除redis中的缓存
        redisUtil.del(Constants.Comment.KEY_ARTICLE_COMMENTS_CACHE + queryComment.getArticleId());
        redisUtil.del(Constants.Comment.KEY_THINKING_COMMENTS_CACHE + queryComment.getArticleId());
        return ResponseResult.SUCCESS("通过状态删除评论成功");
    }

    @Override
    public ResponseResult deleteComment(String commentID) {
        BlogComment queryComment = commentPortalDao.findCommentById(commentID);
        if (queryComment == null) {
            return ResponseResult.FAIL("评论不存在");
        }
        int deleteCount = commentAdminDao.deleteCommentById(commentID);
        if (deleteCount < 1) {
            return ResponseResult.FAIL("删除评论失败");
        }
        //清除redis中的缓存
        redisUtil.del(Constants.Comment.KEY_ARTICLE_COMMENTS_CACHE + queryComment.getArticleId());
        redisUtil.del(Constants.Comment.KEY_THINKING_COMMENTS_CACHE + queryComment.getArticleId());
        return ResponseResult.SUCCESS("删除评论成功");
    }

    @Override
    public ResponseResult getComments(int page, int size, String state, String type) {
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.DESC, "createTime");
        Page<BlogComment> all = commentAdminDao.findAll((Specification<BlogComment>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!TextUtil.isEmpty(state)) {
                Predicate statePre = criteriaBuilder.equal(root.get("state"), state);
                predicateList.add(statePre);
            }
            if (!TextUtil.isEmpty(type)) {
                Predicate typePre = criteriaBuilder.equal(root.get("type"), type);
                predicateList.add(typePre);
            }
            Predicate[] predicates = predicateList.toArray(new Predicate[0]);
            return criteriaBuilder.and(predicates);
        }, pageable);
        BlogPaging<List<BlogComment>> paging = new BlogPaging<>(pageInfo.page, pageInfo.size, all.getTotalElements(), all.getContent());
        return ResponseResult.SUCCESS("获取评论列表成功").setData(paging);
    }

    @Override
    public ResponseResult recoverComment(String commentID) {
        BlogComment queryComment = commentPortalDao.findCommentById(commentID);
        if (queryComment == null) {
            return ResponseResult.FAIL("评论不存在");
        }
        queryComment.setState(Constants.STATE_NORMAL);
        return ResponseResult.SUCCESS("恢复评论成功");
    }
}
