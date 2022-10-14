package cn.manpok.blogsystem.controller.user;

import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户管理API
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private IUserService userService;

    /**
     * 初始化管理员账号
     *
     * @param blogUser
     * @return
     */
    @PostMapping("/admin/account")
    public ResponseResult initAdmin(@RequestBody BlogUser blogUser, HttpServletRequest request) {
        log.info("初始化管理员账号 ----> " + blogUser.toString());
        return userService.initAdminAccount(blogUser, request);
    }

    /**
     * 注册账号
     *
     * @param blogUser
     * @return
     */
    @PostMapping
    public ResponseResult register(@RequestBody BlogUser blogUser,
                                   @RequestParam("captcha_key") String captchaKey,
                                   @RequestParam("captcha_code") String captchaCode,
                                   @RequestParam("verify_code") String verifyCode,
                                   HttpServletRequest request) {
        log.info("注册账号 ----> " + blogUser.toString());
        return userService.register(blogUser, captchaKey, captchaCode, verifyCode, request);
    }

    /**
     * 用户登录
     *
     * @param captchaKey  人类验证码key
     * @param captchaCode 人类验证码
     * @param blogUser
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/{captcha_key}/{captcha_code}")
    public ResponseResult login(@PathVariable("captcha_key") String captchaKey,
                                @PathVariable("captcha_code") String captchaCode,
                                @RequestBody BlogUser blogUser,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        log.info("用户登录 ----> " + blogUser.toString());
        return userService.login(captchaKey, captchaCode, blogUser, request, response);
    }

    /**
     * 获取人类验证码
     * 用请求时的时间戳作为验证码存储的key
     *
     * @return
     */
    @GetMapping("/captcha")
    public void getCaptcha(@RequestParam("captcha_key") String captchaKey, HttpServletResponse response) {
        try {
            userService.createCaptcha(captchaKey, response);
        } catch (IOException e) {
            log.error("输出验证码图片异常");
            e.printStackTrace();
        }
        log.info("获取人类验证码 ----> ");
    }

    /**
     * 发送激活验证码邮件
     * 三种情况：
     * 1、注册新用户：邮箱已存在，不发验证码 register
     * 2、找回密码：邮箱不存在，提示邮箱不存在 forget
     * 3、修改邮箱：邮箱已存在，不发验证码 update
     *
     * @param email
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(@RequestParam("email") String email, @RequestParam("type") String type, HttpServletRequest request) {
        log.info("发送激活验证码 ----> ");
        return userService.sendVerifyCodeEmail(email, type, request);
    }

    /**
     * 忘记密码，校验邮件验证码是否正确
     *
     * @param email
     * @param verifyCode
     * @return
     */
    @GetMapping("/forget")
    public ResponseResult forgetPassword(@RequestParam("email") String email, @RequestParam("verify_code") String verifyCode) {
        log.info("用户忘记密码 ----> " + email);
        return userService.forgetPassword(email, verifyCode);
    }

    @PutMapping("/reset")
    public ResponseResult resetPassword(@RequestParam("email") String email, @RequestBody BlogUser blogUser) {
        log.info("用户重设密码 ----> " + email);
        return userService.resetPassword(email, blogUser);
    }

    /**
     * 获取用户信息
     *
     * @param userID
     * @return
     */
    @GetMapping("/{userID}")
    public ResponseResult getUserInfo(@PathVariable("userID") String userID) {
        log.info("获取用户信息 ----> " + userID);
        return userService.getUserInfo(userID);
    }

    /**
     * 修改用户信息
     *
     * @param blogUser
     * @return
     */
    @PutMapping
    public ResponseResult updateUserInfo(HttpServletRequest request, HttpServletResponse response, @RequestBody BlogUser blogUser) {
        log.info("修改用户信息 ----> " + blogUser.toString());
        return userService.updateUserInfo(request, response, blogUser);
    }

    /**
     * 删除用户
     *
     * @param userID
     * @return
     */
    @DeleteMapping("/{userID}")
    @PreAuthorize("@permission.isAdmin()")
    public ResponseResult deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("userID") String userID) {
        log.info("删除用户 ----> " + userID);
        return userService.deleteUser(request, response, userID);
    }

    /**
     * 获取所有用户信息
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("@permission.isAdmin()")
    public ResponseResult getUsers(HttpServletRequest request, HttpServletResponse response, @RequestParam("page") int page, @RequestParam("size") int size) {
        return userService.getUsers(request, response, page, size);
    }

    /**
     * 检查用户名是否已使用
     *
     * @param userName
     * @return
     */
    @GetMapping("/user_name")
    public ResponseResult checkUserNameIsUsed(@RequestParam("user_name") String userName) {
        log.info("检查用户名是否已使用 ----> " + userName);
        return userService.checkUserNameIsUsed(userName);
    }

    /**
     * 检查邮箱是否已使用
     *
     * @param email
     * @return
     */
    @GetMapping("/email")
    public ResponseResult checkEmailIsUsed(@RequestParam("email") String email) {
        log.info("检查邮箱是否已使用 ----> " + email);
        return userService.checkEmailIsUsed(email);
    }
}
