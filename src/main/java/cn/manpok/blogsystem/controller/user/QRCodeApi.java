package cn.manpok.blogsystem.controller.user;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IQRCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /**
     * 获取二维码图片
     *
     * @param code
     */
    @GetMapping("/img/{code}")
    public void getQRCodeImg(@PathVariable("code") String code) {
        qrCodeService.getQRCodeImg(code);
    }

    /**
     * 扫描二维码后，进入到确认状态，需要在手机上确认后才会进入下一阶段
     *
     * @param
     * @return
     */
    @PutMapping("/enquire/{code}")
    public ResponseResult changeQRCodeState2Enquire(@PathVariable("code") String code, @RequestBody Map<String, String> tokenMap) {
        return qrCodeService.changeQRCodeState2Enquire(code, tokenMap);
    }

    @GetMapping("/confirm/{code}")
    public ResponseResult changeQRCodeState2Confirm(@PathVariable("code") String code) {
        return qrCodeService.changeQRCodeState2Confirm(code);
    }

    @GetMapping("/state/{code}")
    public ResponseResult checkQRCodeState(@PathVariable("code") String code) {
        return qrCodeService.checkQRCodeState(code);
    }
}
