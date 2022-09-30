package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.service.IAsyncTaskService;
import cn.manpok.blogsystem.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AsyncTaskServiceImpl implements IAsyncTaskService {

    @Override
    @Async("asyncTaskServiceExecutor")
    public void sendVerifyCodeEmail(String email, String verifyCode) {
        String subject = "某博博客系统验证码";
        String msg = "您的验证码是：" + verifyCode + "，验证码十分钟内有效，感谢注册！";
        boolean success = MailUtil.sendForeach(email, subject, msg);
        if (success) {
            log.info("后台：发送邮件成功");
            return;
        }
        log.error("后台：发送邮件失败");
    }
}
