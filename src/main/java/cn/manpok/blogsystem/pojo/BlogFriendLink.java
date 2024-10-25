package cn.manpok.blogsystem.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "friend_link")
public class BlogFriendLink {

    @Id
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "logo")
    private String logo;
    @Column(name = "url")
    private String url;
    @Column(name = "link_order")
    private long linkOrder;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLinkOrder() {
        return linkOrder;
    }

    public void setLinkOrder(long linkOrder) {
        this.linkOrder = linkOrder;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BlogFriendLink{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", url='" + url + '\'' +
                ", linkOrder=" + linkOrder +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
