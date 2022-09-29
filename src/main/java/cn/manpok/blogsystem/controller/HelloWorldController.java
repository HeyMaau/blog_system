package cn.manpok.blogsystem.controller;

import cn.manpok.blogsystem.pojo.User;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.RedisUtil;
import com.pig4cloud.captcha.SpecCaptcha;
import com.pig4cloud.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;

@Slf4j
@RestController
@RequestMapping("/test")
public class HelloWorldController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/hello-world")
    public String helloWorld() {
        System.out.println("Hello World!");
        String captcha = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_TEXT + "123456");
        log.info("captcha ---> " + captcha);
        return "hello world!";
    }

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user) {
        log.info("login success! ");
        return ResponseResult.SUCCESS().setData(user);
    }

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        // 验证码存入session
        request.getSession().setAttribute("captcha", specCaptcha.text().toLowerCase());
        redisUtil.set(Constants.User.KEY_CAPTCHA_TEXT + "123456", specCaptcha.text().toLowerCase(), 5);
        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }
}
