package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogLooper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ILooperDao extends JpaRepository<BlogLooper, String> {

    BlogLooper findLooperById(String id);

    int deleteLooperById(String id);

    List<BlogLooper> findAllNormalLoopersByState(String state);
}
