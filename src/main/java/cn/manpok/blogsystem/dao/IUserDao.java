package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserDao extends JpaRepository<BlogUser, String>, JpaSpecificationExecutor<BlogUser> {

    BlogUser findByUserName(String userName);

    BlogUser findByEmail(String email);

    BlogUser findUserById(String id);

    @Query(value = "select new BlogUser (u.id, u.userName, u.roles, u.avatar, u.email, u.sign, u.state, u.regIP, u.loginIP, u.createTime, u.updateTime) from BlogUser u")
    Page<BlogUser> findAllUsers(Pageable pageable);
}
