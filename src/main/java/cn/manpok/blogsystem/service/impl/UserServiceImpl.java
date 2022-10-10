package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ISettingDao;
import cn.manpok.blogsystem.dao.IUserDao;
import cn.manpok.blogsystem.pojo.BlogSetting;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.service.IAsyncTaskService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.*;
import com.pig4cloud.captcha.ArithmeticCaptcha;
import com.pig4cloud.captcha.GifCaptcha;
import com.pig4cloud.captcha.SpecCaptcha;
import com.pig4cloud.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IUserDao userDao;

    @Autowired
    private ISettingDao settingDao;

    @Autowired
    private Random random;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IAsyncTaskService asyncTaskService;

    private final int EVERY_SEND_EMAIL_INTERVAL = 60;

    private final int SEND_EMAIL_IP_INTERVAL = 60 * 60;

    private final int VERIFY_CODE_VALID = 60 * 10;

    /**
     * COOKIES失效时间，一年
     */
    private final int COOKIES_EXPIRED_TIME = 60 * 60 * 24 * 365;

    /**
     * token有效时间
     */
    private final int TOKEN_TTL = 60 * 60 * 2;

    /**
     * 初始化邮箱设置
     */
    static {
        String from = "XXX@139.com";
        String pass = "XXX";
        String host = "smtp.139.com";
        MailUtil.setMailConfig(from, pass, host);
    }

    /**
     * 验证码过期时间，5分钟
     */
    private final int CAPTCHA_EXPIRE_TIME = 5 * 60;

    /**
     * 验证码字体
     */
    private int[] captchaFont = {Captcha.FONT_1,
            Captcha.FONT_2,
            Captcha.FONT_3,
            Captcha.FONT_4,
            Captcha.FONT_5,
            Captcha.FONT_6,
            Captcha.FONT_7,
            Captcha.FONT_8,
            Captcha.FONT_9,
            Captcha.FONT_10};

    /**
     * 文本验证码类型
     */
    private int[] captchaCharType = {Captcha.TYPE_DEFAULT,
            Captcha.TYPE_ONLY_NUMBER,
            Captcha.TYPE_ONLY_CHAR,
            Captcha.TYPE_ONLY_UPPER,
            Captcha.TYPE_ONLY_LOWER,
            Captcha.TYPE_NUM_AND_UPPER};

    @Override
    public ResponseResult initAdminAccount(BlogUser blogUser, HttpServletRequest request) {
        BlogSetting setting = settingDao.findByKey(Constants.Setting.ADMIN_ACCOUNT_INIT_STATE);
        if (setting != null) {
            return ResponseResult.FAIL("已有管理员账户！");
        }
        String userName = blogUser.getUserName();
        String password = blogUser.getPassword();
        String email = blogUser.getEmail();
        if (TextUtil.isEmpty(userName)) {
            return ResponseResult.FAIL("用户名为空！");
        }
        if (TextUtil.isEmpty(password)) {
            return ResponseResult.FAIL("密码为空！");
        }
        if (TextUtil.isEmpty(email)) {
            return ResponseResult.FAIL("邮件为空！");
        }
        //补充user信息
        blogUser.setId(String.valueOf(snowflake.nextId()));
        blogUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        blogUser.setRegIP(request.getRemoteAddr());
        blogUser.setLoginIP(request.getRemoteAddr());
        blogUser.setState(Constants.User.DEFAULT_STATE);
        blogUser.setSign(Constants.User.DEFAULT_SIGN);
        blogUser.setRoles(Constants.User.ROLE_ADMIN);
        blogUser.setCreateTime(new Date());
        blogUser.setUpdateTime(new Date());
        //密码加密
        blogUser.setPassword(bCryptPasswordEncoder.encode(blogUser.getPassword()));
        //补充设置信息
        BlogSetting blogSetting = new BlogSetting();
        blogSetting.setId(String.valueOf(snowflake.nextId()));
        blogSetting.setKey(Constants.Setting.ADMIN_ACCOUNT_INIT_STATE);
        blogSetting.setValue("1");
        blogSetting.setCreateTime(new Date());
        blogSetting.setUpdateTime(new Date());
        //保存数据库
        settingDao.save(blogSetting);
        userDao.save(blogUser);
        return ResponseResult.SUCCESS("初始化管理员账户成功！");
    }

    @Override
    public void createCaptcha(String captchaKey, HttpServletResponse response) throws IOException {
        //验证码KEY为空或者长度不符合，直接返回
        if (TextUtil.isEmpty(captchaKey) || captchaKey.length() < 13) {
            log.info("验证码为空或长度不符");
            return;
        }
        //把key转换成Long类型，做校验
        long key = 0L;
        try {
            key = Long.parseLong(captchaKey);
        } catch (NumberFormatException e) {
            log.info("验证码非时间戳");
            e.printStackTrace();
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //随机生成验证码类型（文本、gif、算术）
        Captcha captcha = null;
        switch (random.nextInt(3)) {
            case 0:
                // 三个参数分别为宽、高、位数+
                captcha = new SpecCaptcha(200, 60, 5);
                break;
            case 1:
                // gif类型
                captcha = new GifCaptcha(200, 60);
                break;
            default:
                // 算术类型
                //简单算术类型 SimpleArithmeticCaptcha,用法同ArithmeticCaptcha,只支持加减，计算结果为正整数
                captcha = new ArithmeticCaptcha(200, 60);
                break;
        }
        // 设置字体
        captcha.setFont(new Font("Verdana", captchaFont[random.nextInt(10)], 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        captcha.setCharType(captchaCharType[random.nextInt(6)]);
        // 验证码存入redis
        redisUtil.set(Constants.User.KEY_CAPTCHA_TEXT + key, captcha.text(), CAPTCHA_EXPIRE_TIME);
        // 输出图片流
        captcha.out(response.getOutputStream());
    }

    @Override
    public ResponseResult sendVerifyCodeEmail(String email, String type, HttpServletRequest request) {
        //根据类型做不同的处理：注册、找回密码、修改邮箱
        BlogUser queryUser = userDao.findByEmail(email);
        if ("register".equals(type) || "update".equals(type)) {
            if (queryUser != null) {
                return ResponseResult.FAIL("该邮箱已注册");
            }
        }
        if ("forget".equals(type)) {
            if (queryUser == null) {
                return ResponseResult.FAIL("该邮箱未注册");
            }
        }
        //获取IP地址
        String ip = request.getRemoteAddr().replaceAll(":", "-");
        //如果IP地址一个小时内访问10次，则拒绝发送
        Integer requestCount = (Integer) redisUtil.get(Constants.User.KEY_SEND_EMAIL_REQUEST_IP + ip);
        if (requestCount != null && requestCount > 10) {
            return ResponseResult.FAIL("发送验证码过于频繁！");
        }
        //如果上一次EMAIL访问时间少于60秒，则拒绝发送
        String isSent = (String) redisUtil.get(Constants.User.KEY_SEND_EMIAL_ADDR + email);
        if (isSent != null) {
            return ResponseResult.FAIL("发送验证码过于频繁！");
        }
        //检查邮箱地址是否正确
        if (!TextUtil.checkEmailAddr(email)) {
            return ResponseResult.FAIL("邮箱地址不正确！");
        }
        //产生随机验证码，六位数
        int verifyCode = random.nextInt(1000000);
        if (verifyCode < 100000) {
            verifyCode += 100000;
        }
        //发送邮件
        asyncTaskService.sendVerifyCodeEmail(email, String.valueOf(verifyCode));
        //redis存储
        redisUtil.set(Constants.User.KEY_SEND_EMIAL_ADDR + email, "true", EVERY_SEND_EMAIL_INTERVAL);
        redisUtil.set(Constants.User.KEY_VERIFY_CODE_TEXT + email, String.valueOf(verifyCode), VERIFY_CODE_VALID);
        if (requestCount == null) {
            requestCount = 0;
        }
        requestCount++;
        redisUtil.set(Constants.User.KEY_SEND_EMAIL_REQUEST_IP + ip, requestCount, SEND_EMAIL_IP_INTERVAL);
        return ResponseResult.SUCCESS("发送验证码成功！");
    }

    @Override
    public ResponseResult register(BlogUser blogUser, String captchaKey, String captchaCode, String verifyCode, HttpServletRequest request) {
        //1、校验用户名是否为空或已注册
        String userName = blogUser.getUserName();
        if (TextUtil.isEmpty(userName)) {
            return ResponseResult.FAIL("用户名为空");
        }
        BlogUser queryUser = userDao.findByUserName(userName);
        if (queryUser != null) {
            return ResponseResult.FAIL("用户名已注册");
        }
        //2、校验邮箱是否为空或已注册
        String email = blogUser.getEmail();
        if (TextUtil.isEmpty(email)) {
            return ResponseResult.FAIL("邮箱地址为空");
        }
        if (!TextUtil.checkEmailAddr(email)) {
            return ResponseResult.FAIL("邮箱地址不正确");
        }
        queryUser = userDao.findByEmail(email);
        if (queryUser != null) {
            return ResponseResult.FAIL("该邮箱已被注册");
        }
        //3、校验密码
        String password = blogUser.getPassword();
        if (TextUtil.isEmpty(password)) {
            return ResponseResult.FAIL("密码为空");
        }
        //3、校验人类验证码
        String captchaText = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_TEXT + captchaKey);
        if (TextUtil.isEmpty(captchaText)) {
            return ResponseResult.FAIL("人类验证码无效");
        }
        if (!captchaCode.equalsIgnoreCase(captchaText)) {
            return ResponseResult.FAIL("人类验证码错误");
        } else {
            redisUtil.del(Constants.User.KEY_CAPTCHA_TEXT + captchaKey);
        }
        //4、校验邮件验证码
        String verifyCodeText = (String) redisUtil.get(Constants.User.KEY_VERIFY_CODE_TEXT + email);
        if (TextUtil.isEmpty(verifyCodeText)) {
            return ResponseResult.FAIL("邮箱验证码无效");
        }
        if (!verifyCode.equals(verifyCodeText)) {
            return ResponseResult.FAIL("邮件验证码错误");
        } else {
            redisUtil.del(Constants.User.KEY_VERIFY_CODE_TEXT + email);
        }
        //5、对密码进行加密
        String encodePassword = bCryptPasswordEncoder.encode(password);
        blogUser.setPassword(encodePassword);
        //5、补充数据
        blogUser.setRoles(Constants.User.ROLE_NORMAL);
        blogUser.setId(String.valueOf(snowflake.nextId()));
        blogUser.setSign(Constants.User.DEFAULT_SIGN);
        blogUser.setState(Constants.User.DEFAULT_STATE);
        blogUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        blogUser.setRegIP(request.getRemoteAddr());
        blogUser.setLoginIP(request.getRemoteAddr());
        blogUser.setCreateTime(new Date());
        blogUser.setUpdateTime(new Date());
        userDao.save(blogUser);
        return ResponseResult.GET(ResponseState.REGISTER_SUCCESS);
    }

    @Override
    public ResponseResult login(String captchaKey, String captchaCode, BlogUser blogUser, HttpServletRequest request, HttpServletResponse response) {
        //1、校验人类验证码是否正确
        String captchaText = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_TEXT + captchaKey);
        if (!captchaText.equalsIgnoreCase(captchaCode)) {
            return ResponseResult.FAIL("验证码错误");
        } else {
            redisUtil.del(Constants.User.KEY_CAPTCHA_TEXT + captchaKey);
        }
        //2、校验用户名或邮箱是否存在
        String userName = blogUser.getUserName();
        String email = blogUser.getEmail();
        if (TextUtil.isEmpty(userName) && TextUtil.isEmpty(email)) {
            return ResponseResult.FAIL("用户名或邮箱不能为空");
        }
        BlogUser queryUser = userDao.findByUserName(userName);
        if (queryUser == null) {
            queryUser = userDao.findByEmail(email);
            if (queryUser == null) {
                return ResponseResult.FAIL("用户名或密码错误");
            }
        }
        //3、校验用户状态是否异常
        String state = queryUser.getState();
        if (!state.equals(Constants.User.DEFAULT_STATE)) {
            return ResponseResult.GET(ResponseState.USER_FORBIDDEN);
        }
        //4、校验密码是否正确
        if (!bCryptPasswordEncoder.matches(blogUser.getPassword(), queryUser.getPassword())) {
            return ResponseResult.FAIL("用户名或密码不正确");
        }
        //5、生成token
        Map<String, String> payload = new HashMap<>();
        payload.put("id", queryUser.getId());
        payload.put("user_name", queryUser.getUserName());
        payload.put("roles", queryUser.getRoles());
        payload.put("avatar", queryUser.getAvatar());
        payload.put("email", queryUser.getEmail());
        payload.put("sign", queryUser.getSign());
        String token = JWTUtil.generateToken(payload);
        log.info("user token ----> " + token);
        //6、生成token的MD5返回给客户端
        String tokenMD5 = DigestUtils.md5DigestAsHex(token.getBytes());
        redisUtil.set(Constants.User.KEY_USER_TOKEN + tokenMD5, token, TOKEN_TTL);
        Cookie cookie = new Cookie(Constants.User.KEY_TOKEN_COOKIE, tokenMD5);
        cookie.setDomain("localhost");
        cookie.setMaxAge(COOKIES_EXPIRED_TIME);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseResult.SUCCESS("登录成功");
    }
}
