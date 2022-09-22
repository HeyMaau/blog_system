package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogFriendLink;
import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理友情链接API
 */
@Slf4j
@RestController
@RequestMapping("/admin/friend_link")
public class FriendLinkApi {

    /**
     * 添加友情链接
     *
     * @param blogFriendLink
     * @return
     */
    @PostMapping
    public ResponseResult addFriendLink(@RequestBody BlogFriendLink blogFriendLink) {
        log.info("添加友情链接 ----> " + blogFriendLink.toString());
        return null;
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
        return null;
    }

    /**
     * 修改友情链接
     *
     * @param BlogFriendLink
     * @return
     */
    @PutMapping
    public ResponseResult updateFriendLink(@RequestBody BlogFriendLink BlogFriendLink) {
        log.info("修改友情链接 ----> " + BlogFriendLink.toString());
        return null;
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
        return null;
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
        return null;
    }
}
