package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ICommentPortalDao;
import cn.manpok.blogsystem.pojo.BlogComment;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.ICommentAdminService;
import cn.manpok.blogsystem.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class CommentAdminServiceImpl implements ICommentAdminService {

    @Autowired
    private ICommentPortalDao commentPortalDao;

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
                return ResponseResult.SUCCESS("置顶评论成功");
            }
            case Constants.Comment.STATE_TOP -> {
                queryComment.setState(Constants.STATE_NORMAL);
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
        return ResponseResult.SUCCESS("通过状态删除评论成功");
    }

    @Override
    public ResponseResult deleteComment(String commentID) {
        BlogComment queryComment = commentPortalDao.findCommentById(commentID);
        if (queryComment == null) {
            return ResponseResult.FAIL("评论不存在");
        }
        int deleteCount = commentPortalDao.deleteCommentById(commentID);
        if (deleteCount < 1) {
            return ResponseResult.FAIL("删除评论失败");
        }
        return ResponseResult.SUCCESS("删除评论成功");
    }
}
