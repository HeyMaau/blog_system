package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IUserService {

    ResponseResult initAdminAccount(BlogUser blogUser, HttpServletRequest request);

    void createCaptcha(String captchaKey, HttpServletResponse response) throws IOException;

    ResponseResult sendVerifyCodeEmail(String email, String type, HttpServletRequest request);

    ResponseResult register(BlogUser blogUser, String captchaKey, String captchaCode, String verifyCode, HttpServletRequest request);

    ResponseResult login(String captchaKey, String captchaCode, BlogUser blogUser, HttpServletRequest request, HttpServletResponse response);
}
