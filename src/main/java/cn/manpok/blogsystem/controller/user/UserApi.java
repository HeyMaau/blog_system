package cn.manpok.blogsystem.controller.user;

import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    public ResponseResult register(@RequestBody BlogUser blogUser) {
        log.info("注册账号 ----> " + blogUser.toString());
        return null;
    }

    /**
     * 用户登录
     *
     * @param captcha
     * @param blogUser
     * @return
     */
    @PostMapping("/{captcha}")
    public ResponseResult login(@PathVariable("captcha") String captcha, @RequestBody BlogUser blogUser) {
        log.info("用户登录 ----> " + blogUser.toString());
        return null;
    }

    /**
     * 获取人类验证码
     *
     * @return
     */
    @GetMapping("/captcha")
    public ResponseResult sendCaptcha() {
        log.info("获取人类验证码 ----> ");
        return null;
    }

    /**
     * 发送激活验证码邮件
     *
     * @param email
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(@RequestParam("email") String email) {
        log.info("发送激活验证码 ----> ");
        return null;
    }

    /**
     * 修改密码
     *
     * @param blogUser
     * @return
     */
    @PutMapping("/password")
    public ResponseResult updatePassword(@RequestBody BlogUser blogUser) {
        log.info("用户修改密码 ----> " + blogUser.toString());
        return null;
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
        return null;
    }

    /**
     * 修改用户信息
     *
     * @param blogUser
     * @return
     */
    @PutMapping
    public ResponseResult updateUserInfo(@RequestBody BlogUser blogUser) {
        log.info("修改用户信息 ----> " + blogUser.toString());
        return null;
    }

    /**
     * 删除用户
     *
     * @param userID
     * @return
     */
    @DeleteMapping("/{userID}")
    public ResponseResult deleteUser(@PathVariable("userID") String userID) {
        return null;
    }

    /**
     * 获取所有用户信息
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getUsers(@RequestParam("page") int page, @RequestParam("size") int size) {
        return null;
    }
}
