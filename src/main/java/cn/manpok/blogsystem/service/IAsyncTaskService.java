package cn.manpok.blogsystem.service;

public interface IAsyncTaskService {

    void sendVerifyCodeEmail(String email, String verifyCode);
}
