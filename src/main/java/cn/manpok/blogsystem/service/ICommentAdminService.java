package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface ICommentAdminService {
    ResponseResult getComment(String commentID);
}
