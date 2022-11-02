package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ICommentPortalDao;
import cn.manpok.blogsystem.pojo.BlogComment;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ICommentAdminService;
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
}
