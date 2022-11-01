package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogComment;
import cn.manpok.blogsystem.response.ResponseResult;

public interface ICommentPortalService {
    ResponseResult addComment(BlogComment blogComment);

    ResponseResult deleteCommend(String commentID);
}