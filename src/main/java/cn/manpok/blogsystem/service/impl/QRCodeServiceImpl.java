package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IQRCodeService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.QRCodeUtil;
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
        String content = Constants.APP.APP_DOWNLOAD_LINK + code;
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            QRCodeUtil.createCodeToOutputStream(content, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
