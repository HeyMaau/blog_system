package cn.manpok.blogsystem.pojo;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * SOLR搜索封装BEAN类
 */
@SolrDocument(collection = "ik_core")
public class BlogSolrSearch implements Serializable {

    @Id
    @Field("id")
    private String id;
    @Field("view_count")
    private long viewCount;
    @Field("title")
    private String title;
    @Field("content")
    private String content;
    @Field("labels")
    private String labels;
    @Field("url")
    private String url;
    @Field("category_id")
    private String categoryID;
    @Field("create_time")
    private Date createTime;
    @Field("update_time")
    private Date updateTime;
    @Field("search_item")
    private List<String> searchItem;

    @Override
    public String toString() {
        return "BlogSolrSearch{" +
                "id='" + id + '\'' +
                ", viewCount=" + viewCount +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", labels='" + labels + '\'' +
                ", url='" + url + '\'' +
                ", categoryID='" + categoryID + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", searchItem=" + searchItem +
                '}';
    }

    public List<String> getSearchItem() {
        return searchItem;
    }

    public void setSearchItem(List<String> searchItem) {
        this.searchItem = searchItem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
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
}
