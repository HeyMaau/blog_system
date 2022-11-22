package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IRefreshTokenDao extends JpaRepository<BlogRefreshToken, String>, JpaSpecificationExecutor<BlogRefreshToken> {

    BlogRefreshToken findByTokenMD5(String tokenMD5);

    int deleteByUserId(String userID);

    int deleteByTokenMD5(String tokenMD5);
}
