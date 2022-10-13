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

    ResponseResult getUserInfo(String userID);

    ResponseResult checkUserNameIsUsed(String userName);

    ResponseResult checkEmailIsUsed(String email);

    ResponseResult updateUserInfo(HttpServletRequest request, HttpServletResponse response, BlogUser blogUser);

    ResponseResult deleteUser(HttpServletRequest request, HttpServletResponse response, String userID);

    ResponseResult getUsers(HttpServletRequest request, HttpServletResponse response, int page, int size);

    BlogUser checkUserToken(HttpServletRequest request, HttpServletResponse response);
}
