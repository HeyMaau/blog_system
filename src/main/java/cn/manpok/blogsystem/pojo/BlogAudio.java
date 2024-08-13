package cn.manpok.blogsystem.pojo;


import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "audio")
public class BlogAudio {

    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "artist")
    private String artist;

    @Column(name = "album")
    private String album;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "audio_order")
    private int audioOrder;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Override
    public String toString() {
        return "BlogAudio{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", audioOrder=" + audioOrder +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
