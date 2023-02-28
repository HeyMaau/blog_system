package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFeedbackDao extends JpaRepository<BlogFeedback, String> {
}
