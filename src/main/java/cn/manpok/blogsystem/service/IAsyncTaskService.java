package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.response.ResponseResult;

import java.util.concurrent.Future;

public interface IAsyncTaskService {

    /**
     * 异步发送邮件验证码
     *
     * @param email
     * @param verifyCode
     */
    void sendVerifyCodeEmail(String email, String verifyCode);

    /**
     * 异步：长轮询，阻塞，返回二维码状态
     *
     * @param code
     * @return
     */
    Future<ResponseResult> checkQRCodeState(String code);
}
