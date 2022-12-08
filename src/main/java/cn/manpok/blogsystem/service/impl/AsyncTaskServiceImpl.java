package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IAsyncTaskService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.MailUtil;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AsyncTaskServiceImpl implements IAsyncTaskService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 二维码未确认的错误码
     */
    private final int CODE_QR_CODE_STATE_NOT_CONFIRM = 40013;

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

    @Override
    @Async("asyncTaskServiceExecutor")
    public Future<ResponseResult> checkQRCodeState(String code) {
        while (true) {
            ResponseResult state = checkQRCodeStateFromRedis(code);
            int stateCode = state.getCode();
            if (stateCode != CODE_QR_CODE_STATE_NOT_CONFIRM) {
                return new AsyncResult<>(state);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(Constants.TimeValue.MESC_100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查REDIS中的二维码状态
     *
     * @param code
     * @return
     */
    private ResponseResult checkQRCodeStateFromRedis(String code) {
        //检查二维码状态
        String state = (String) redisUtil.get(Constants.APP.KEY_QR_CODE_STATE + code);
        if (TextUtil.isEmpty(state)) {
            //如果redis中没有，说明二维码过期了
            return ResponseResult.FAIL(ResponseState.QR_CODE_STATE_EXPIRED);
        }
        if (state.equals(Constants.APP.STATE_QR_CODE_TRUE)) {
            return ResponseResult.SUCCESS("扫码登录成功");
        }
        return ResponseResult.FAIL(ResponseState.QR_CODE_STATE_NOT_CONFIRM);
    }
}
