package cn.manpok.blogsystem.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "app")
@Getter
@Setter
public class BlogApp {

    @Id
    private String id;
    @Column(name = "version_name")
    private String versionName;
    @Column(name = "version_code")
    private int versionCode;
    @Column(name = "app_name")
    private String appName;
    @Column(name = "download_url")
    private String downloadUrl;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "change_log")
    private String changeLog;
}
