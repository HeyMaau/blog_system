package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ISettingDao;
import cn.manpok.blogsystem.dao.IUserDao;
import cn.manpok.blogsystem.pojo.BlogSetting;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import com.pig4cloud.captcha.*;
import com.pig4cloud.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
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

    /**
     * 验证码过期时间，5分钟
     */
    private final int CAPTCHA_EXPIRE_TIME = 5;

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
        long key = 0l;
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
}
