package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IQRCodeService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.QRCodeUtil;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        String tokenKey = null;
        if (tokenMap != null) {
            tokenKey = tokenMap.get(Constants.User.KEY_TOKEN_KEY);
        }
        BlogUser user = userService.checkUserToken(tokenKey);
        if (user != null) {
            redisUtil.set(Constants.APP.KEY_QR_CODE_STATE + code, Constants.APP.STATE_QR_CODE_ENQUIRE, Constants.TimeValue.MIN);
            return ResponseResult.SUCCESS("验证码扫描成功");
        }
        return ResponseResult.FAIL(ResponseState.NOT_LOGIN);
    }
}
