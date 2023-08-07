package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogThinking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IThinkingDao extends JpaRepository<BlogThinking, String> {

    BlogThinking findThinkingById(String id);

    int deleteThinkingById(String id);

    Page<BlogThinking> findAllThinkinsByState(String state, Pageable pageable);
}
