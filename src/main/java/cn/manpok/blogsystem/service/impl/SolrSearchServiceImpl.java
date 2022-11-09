package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.service.ISolrSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SolrSearchServiceImpl implements ISolrSearchService {

    @Autowired
    private SolrClient solrClient;

    @Override
    public void addArticle(BlogArticle blogArticle) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", blogArticle.getId());
        document.addField("view_count", blogArticle.getViewCount());
        document.addField("title", blogArticle.getTitle());
        document.addField("content", blogArticle.getContent());
        document.addField("labels", blogArticle.getLabels());
        document.addField("category_id", blogArticle.getCategoryId());
        document.addField("create_time", blogArticle.getCreateTime());
        document.addField("update_time", blogArticle.getUpdateTime());
        try {
            solrClient.add(document);
            solrClient.commit();
            log.info("文章保存SOLR成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("文章保存SOLR异常");
        }
    }
}
