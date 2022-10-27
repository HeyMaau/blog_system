package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogFriendLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IFriendLinkDao extends JpaRepository<BlogFriendLink, String>, JpaSpecificationExecutor<BlogFriendLink> {

    BlogFriendLink findFriendLinkById(String id);

    int deleteFriendLinkById(String id);

    List<BlogFriendLink> findAllFriendLinksByState(String state);
}
