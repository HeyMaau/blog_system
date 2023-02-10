package cn.manpok.blogsystem.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "users")
public class BlogUserSimple {

    @Id
    private String id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "roles")
    private String roles;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "email")
    private String email;

    @Column(name = "sign")
    private String sign;

    @Column(name = "state")
    private String state;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "major")
    private String major;

    @Column(name = "hub_site")
    private String hubSite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getHubSite() {
        return hubSite;
    }

    public void setHubSite(String hubSite) {
        this.hubSite = hubSite;
    }

    @Override
    public String toString() {
        return "BlogUserSimple{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", roles='" + roles + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", sign='" + sign + '\'' +
                ", state='" + state + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", major='" + major + '\'' +
                ", hubSite='" + hubSite + '\'' +
                '}';
    }
}
