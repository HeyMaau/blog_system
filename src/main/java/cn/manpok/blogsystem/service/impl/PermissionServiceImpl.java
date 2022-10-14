package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.service.IPermissionService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 权限管理服务
 */
@Service("permission")
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private IUserService userService;

    /**
     * 校验是否为管理员
     *
     * @return
     */
    @Override
    public boolean isAdmin() {
        //检验token权限
        BlogUser userByToken = userService.checkUserToken();
        //token为空，说明用户未登录
        if (userByToken == null) {
            return false;
        }
        if (userByToken.getRoles().equals(Constants.User.ROLE_ADMIN)) {
            return true;
        }
        return false;
    }
}
