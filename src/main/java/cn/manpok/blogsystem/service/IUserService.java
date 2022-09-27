package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;

public interface IUserService {

    ResponseResult initAdminAccount(BlogUser blogUser, HttpServletRequest request);
}
