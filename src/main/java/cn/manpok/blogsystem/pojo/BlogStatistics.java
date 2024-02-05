package cn.manpok.blogsystem.pojo;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "statistics")
public class BlogStatistics {


    @Id
    private String id;

    @Column(name = "page")
    private String page;

    @Column(name = "component")
    private String component;

    @Column(name = "event")
    private String event;

    @Column(name = "count")
    private long count;

    @Column(name = "record_date")
    private String recordDate;

    @Column(name = "client")
    private String client;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Override
    public String toString() {
        return "BlogStatistics{" +
                "id='" + id + '\'' +
                ", page='" + page + '\'' +
                ", component='" + component + '\'' +
                ", event='" + event + '\'' +
                ", count=" + count +
                ", recordDate='" + recordDate + '\'' +
                ", client='" + client + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
