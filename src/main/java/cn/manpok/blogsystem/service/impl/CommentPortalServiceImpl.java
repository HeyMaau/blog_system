package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IArticleAdminSimpleDao;
import cn.manpok.blogsystem.dao.ICommentPortalDao;
import cn.manpok.blogsystem.pojo.BlogArticleSimple;
import cn.manpok.blogsystem.pojo.BlogComment;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.ICommentPortalService;
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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class CommentPortalServiceImpl implements ICommentPortalService {

    @Autowired
    private IUserService userService;

    @Autowired
    private IArticleAdminSimpleDao articleAdminSimpleDao;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ICommentPortalDao commentPortalDao;

    @Override
    public ResponseResult addComment(BlogComment blogComment) {
        //检查登录状态
        BlogUser user = userService.checkUserToken();
        if (user == null) {
            return ResponseResult.FAIL(ResponseState.NOT_LOGIN);
        }
        //检查评论的参数，必填项：文章ID，内容
        String articleID = blogComment.getArticleId();
        if (TextUtil.isEmpty(articleID)) {
            return ResponseResult.FAIL("文章ID为空");
        }
        BlogArticleSimple articleSimple = articleAdminSimpleDao.findArticleSimpleById(articleID);
        if (articleSimple == null) {
            return ResponseResult.FAIL("文章不存在");
        }
        if (TextUtil.isEmpty(blogComment.getContent())) {
            return ResponseResult.FAIL("评论内容为空");
        }
        //补充数据
        blogComment.setId(String.valueOf(snowflake.nextId()));
        blogComment.setUserId(user.getId());
        blogComment.setUserName(user.getUserName());
        blogComment.setUserAvatar(user.getAvatar());
        blogComment.setState(Constants.STATE_NORMAL);
        Date date = new Date();
        blogComment.setCreateTime(date);
        blogComment.setUpdateTime(date);
        commentPortalDao.save(blogComment);
        return ResponseResult.SUCCESS("发表评论成功");
    }

    @Override
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
        return ResponseResult.SUCCESS("删除评论成功");
    }

    @Override
    public ResponseResult getComments(String articleID, int page, int size) {
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.ASC, "createTime");
        Page<BlogComment> all = commentPortalDao.findAllCommentsByArticleId(articleID, pageable);
        return ResponseResult.SUCCESS("获取所有评论成功").setData(all);
    }
}
