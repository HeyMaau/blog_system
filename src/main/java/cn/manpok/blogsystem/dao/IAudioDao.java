package cn.manpok.blogsystem.dao;

import cn.manpok.blogsystem.pojo.BlogAudio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAudioDao extends JpaRepository<BlogAudio, String> {

    BlogAudio findBlogAudioById(String id);

    int deleteBlogAudioById(String id);
}
