package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IFriendLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 友情链接门户API
 */
@Slf4j
@RestController
@RequestMapping("/portal/friend_link")
public class FriendLinkPortalApi {

    @Autowired
    private IFriendLinkService friendLinkService;

    /**
     * 获取所有友情链接
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getFriendLinks(@RequestParam("page") int page, @RequestParam("size") int size) {
        return friendLinkService.getFriendLinks(page, size);
    }
}
