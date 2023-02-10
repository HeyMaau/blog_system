package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/portal/user")
public class UserPortalApi {

    @Autowired
    private IUserService userService;

    /**
     * 获取管理员信息
     *
     * @return
     */
    @GetMapping("/admin")
    public ResponseResult getAdminInfo() {
        log.info("门户获取管理员信息");
        return userService.getAdminInfo();
    }
}
