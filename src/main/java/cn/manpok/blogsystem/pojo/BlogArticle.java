package cn.manpok.blogsystem.pojo;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "articles")
public class BlogArticle {

    @Id
    private String id;
    @Column(name = "title")
    private String title;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "category_id")
    private String categoryId;
    @Column(name = "content")
    private String content;
    @Column(name = "type")
    private String type;
    @Column(name = "state")
    private String state;
    @Column(name = "summary")
    private String summary;
    @Column(name = "labels")
    private String labels;
    @Column(name = "view_count")
    private long viewCount;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private BlogUserSimple user;
    @Column(name = "cover")
    private String cover;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }


    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
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

    public BlogUserSimple getUser() {
        return user;
    }

    public void setUser(BlogUserSimple user) {
        this.user = user;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "BlogArticle{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", userId='" + userId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                ", summary='" + summary + '\'' +
                ", labels='" + labels + '\'' +
                ", viewCount=" + viewCount +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", user=" + user +
                ", cover='" + cover + '\'' +
                '}';
    }
}
