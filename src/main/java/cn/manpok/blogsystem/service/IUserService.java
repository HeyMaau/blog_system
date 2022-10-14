package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;

import java.io.IOException;

public interface IUserService {

    ResponseResult initAdminAccount(BlogUser blogUser);

    void createCaptcha(String captchaKey) throws IOException;

    ResponseResult sendVerifyCodeEmail(String email, String type);

    ResponseResult register(BlogUser blogUser, String captchaKey, String captchaCode, String verifyCode);

    ResponseResult login(String captchaKey, String captchaCode, BlogUser blogUser);

    ResponseResult getUserInfo(String userID);

    ResponseResult checkUserNameIsUsed(String userName);

    ResponseResult checkEmailIsUsed(String email);

    ResponseResult updateUserInfo(BlogUser blogUser);

    ResponseResult deleteUser(String userID);

    ResponseResult getUsers(int page, int size);

    BlogUser checkUserToken();

    ResponseResult forgetPassword(String email, String verifyCode);

    ResponseResult resetPassword(String email, BlogUser blogUser);

    ResponseResult updateEmail(String email, String verifyCode);
}
