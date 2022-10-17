package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogFriendLink;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IFriendLinkService {
    ResponseResult addFriendLink(BlogFriendLink blogFriendLink);

    ResponseResult deleteFriendLink(String friendLinkID);

    ResponseResult updateFriendLink(BlogFriendLink blogFriendLink);

    ResponseResult getFriendLink(String friendLinkID);

    ResponseResult getFriendLinks(int page, int size);
}
