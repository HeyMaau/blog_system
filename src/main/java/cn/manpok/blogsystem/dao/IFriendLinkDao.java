package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogFriendLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFriendLinkDao extends JpaRepository<BlogFriendLink, String> {

    BlogFriendLink findFriendLinkById(String id);

    int deleteFriendLinkById(String id);
}
