package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogFriendLink;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IFriendLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理友情链接API
 */
@Slf4j
@RestController
@RequestMapping("/admin/friend_link")
@PreAuthorize("@permission.isAdmin()")
public class FriendLinkAdminApi {

    @Autowired
    private IFriendLinkService friendLinkService;

    /**
     * 添加友情链接
     *
     * @param blogFriendLink
     * @return
     */
    @PostMapping("/add")
    public ResponseResult addFriendLink(@RequestBody BlogFriendLink blogFriendLink) {
        log.info("管理平台添加友情链接 ----> " + blogFriendLink.toString());
        return friendLinkService.addFriendLink(blogFriendLink);
    }

    /**
     * 删除友情链接
     *
     * @param friendLinkID
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteFriendLink(@PathVariable("id") String friendLinkID) {
        return friendLinkService.deleteFriendLink(friendLinkID);
    }

    /**
     * 修改友情链接
     *
     * @param blogFriendLink
     * @return
     */
    @PutMapping("/update")
    public ResponseResult updateFriendLink(@RequestBody BlogFriendLink blogFriendLink) {
        log.info("管理平台修改友情链接 ----> " + blogFriendLink.toString());
        return friendLinkService.updateFriendLink(blogFriendLink);
    }

}
