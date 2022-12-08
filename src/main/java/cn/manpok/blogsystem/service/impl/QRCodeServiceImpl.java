package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IAsyncTaskService;
import cn.manpok.blogsystem.service.IQRCodeService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class QRCodeServiceImpl implements IQRCodeService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IUserService userService;

    @Autowired
    private IAsyncTaskService asyncTaskService;

    @Override
    public ResponseResult getQRCodeInfo() {
        //产生一个随机ID，返回给前端，用于下次请求二维码图片
        long randomID = snowflake.nextId();
        Map<String, String> result = new HashMap<>(1);
        result.put("code", String.valueOf(randomID));
        return ResponseResult.SUCCESS("获取二维码ID成功").setData(result);
    }

    @Override
    public void getQRCodeImg(String code) {
        log.info(code + " ----> 请求二维码");
        redisUtil.set(Constants.APP.KEY_QR_CODE_STATE + code, Constants.APP.STATE_QR_CODE_FALSE, Constants.TimeValue.MIN_5);
        String content = Constants.APP.APP_DOWNLOAD_LINK + code;
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            QRCodeUtil.createCodeToOutputStream(content, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ResponseResult changeQRCodeState2Enquire(String code, Map<String, String> tokenMap) {
        //检查二维码状态，只有是未扫描状态才允许走该流程
        String state = (String) redisUtil.get(Constants.APP.KEY_QR_CODE_STATE + code);
        if (TextUtil.isEmpty(state) || !state.equals(Constants.APP.STATE_QR_CODE_FALSE)) {
            return ResponseResult.FAIL(ResponseState.QR_CODE_STATE_ILLEGAL);
        }
        String tokenKey = null;
        if (tokenMap != null) {
            tokenKey = tokenMap.get(Constants.User.KEY_TOKEN_KEY);
        }
        BlogUser user = userService.checkUserToken(tokenKey);
        if (user != null) {
            redisUtil.set(Constants.APP.KEY_QR_CODE_STATE + code, Constants.APP.STATE_QR_CODE_ENQUIRE, Constants.TimeValue.MIN);
            log.info("验证码扫描成功 ----> " + code);
            return ResponseResult.SUCCESS("验证码扫描成功").setData(user);
        }
        return ResponseResult.FAIL(ResponseState.NOT_LOGIN);
    }

    @Override
    public ResponseResult changeQRCodeState2Confirm(String code) {
        //检查二维码状态，只有是待确认状态才允许走该流程
        String state = (String) redisUtil.get(Constants.APP.KEY_QR_CODE_STATE + code);
        if (TextUtil.isEmpty(state) || !state.equals(Constants.APP.STATE_QR_CODE_ENQUIRE)) {
            return ResponseResult.FAIL(ResponseState.QR_CODE_STATE_ILLEGAL);
        }
        redisUtil.set(Constants.APP.KEY_QR_CODE_STATE + code, Constants.APP.STATE_QR_CODE_TRUE, Constants.TimeValue.MIN);
        log.info("二维码确认成功 ----> " + code);
        return ResponseResult.SUCCESS("二维码确认成功");
    }

    @Override
    public ResponseResult checkQRCodeState(String code) {
        Future<ResponseResult> future = asyncTaskService.checkQRCodeState(code);
        try {
            future.get(Constants.TimeValue.SECOND_30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            return ResponseResult.FAIL(ResponseState.LONG_POLL_TIME_OUT);
        }
        return ResponseResult.SUCCESS("扫码登录成功");
    }
}
