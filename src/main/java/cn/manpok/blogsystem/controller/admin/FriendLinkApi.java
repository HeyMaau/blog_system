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
public class FriendLinkApi {

    @Autowired
    private IFriendLinkService friendLinkService;

    /**
     * 添加友情链接
     *
     * @param blogFriendLink
     * @return
     */
    @PostMapping
    public ResponseResult addFriendLink(@RequestBody BlogFriendLink blogFriendLink) {
        log.info("添加友情链接 ----> " + blogFriendLink.toString());
        return friendLinkService.addFriendLink(blogFriendLink);
    }

    /**
     * 删除友情链接
     *
     * @param friendLinkID
     * @return
     */
    @DeleteMapping("/{friendLinkID}")
    public ResponseResult deleteFriendLink(@PathVariable("friendLinkID") String friendLinkID) {
        log.info("删除友情链接 ----> " + friendLinkID);
        return friendLinkService.deleteFriendLink(friendLinkID);
    }

    /**
     * 修改友情链接
     *
     * @param blogFriendLink
     * @return
     */
    @PutMapping
    public ResponseResult updateFriendLink(@RequestBody BlogFriendLink blogFriendLink) {
        log.info("修改友情链接 ----> " + blogFriendLink.toString());
        return friendLinkService.updateFriendLink(blogFriendLink);
    }

    /**
     * 获取友情链接
     *
     * @param friendLinkID
     * @return
     */
    @GetMapping("/{friendLinkID}")
    public ResponseResult getFriendLink(@PathVariable("friendLinkID") String friendLinkID) {
        log.info("获取友情链接 ----> " + friendLinkID);
        return friendLinkService.getFriendLink(friendLinkID);
    }

    /**
     * 获取所有友情链接
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getFriendLinks(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("获取所有友情链接 ----> ");
        return friendLinkService.getFriendLinks(page, size);
    }
}
