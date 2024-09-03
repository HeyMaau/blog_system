package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

public interface IShareService {


    ResponseResult getArticleShareLink(String id);
}
