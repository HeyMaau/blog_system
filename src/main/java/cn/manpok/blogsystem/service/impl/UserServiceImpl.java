package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.ISettingDao;
import cn.manpok.blogsystem.dao.IUserDao;
import cn.manpok.blogsystem.pojo.BlogSetting;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;

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
}
