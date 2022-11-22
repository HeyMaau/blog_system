package cn.manpok.blogsystem.controller.user;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IQRCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 二维码相关API
 */
@RestController
@RequestMapping("/qr_code")
@Slf4j
public class QRCodeApi {

    @Autowired
    private IQRCodeService qrCodeService;

    /**
     * 获取二维码的信息
     *
     * @return
     */
    @GetMapping("/info")
    public ResponseResult getQRCodeInfo() {
        return qrCodeService.getQRCodeInfo();
    }

    @GetMapping("/img/{code}")
    public void getQRCodeImg(@PathVariable("code") String code) {
        qrCodeService.getQRCodeImg(code);
    }
}
