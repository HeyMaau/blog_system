package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface ICommentAdminService {
    ResponseResult getComment(String commentID);

    ResponseResult topComment(String commentID);

    ResponseResult deleteCommentByState(String commentID);

    ResponseResult deleteComment(String commentID);

    ResponseResult getComments(int page, int size, String state);

    ResponseResult recoverComment(String commentID);
}
