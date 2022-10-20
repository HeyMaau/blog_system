package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IImageDao extends JpaRepository<BlogImage, String>, JpaSpecificationExecutor<BlogImage> {

    BlogImage findImageByMD5(String MD5);

    BlogImage findImageById(String id);

    Page<BlogImage> findImagesByUserIdAndState(String userID, String state, Pageable pageable);
}
