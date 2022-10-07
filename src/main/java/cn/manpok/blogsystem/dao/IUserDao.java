package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserDao extends JpaRepository<BlogUser, String>, JpaSpecificationExecutor<BlogUser> {

    BlogUser findByUserName(String userName);

    BlogUser findByEmail(String email);
}
