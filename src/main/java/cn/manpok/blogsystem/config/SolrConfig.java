package cn.manpok.blogsystem.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfig {

    @Value("${blog.system.solr.path}")
    private String baseUrl;

    @Bean
    public SolrClient createSolrClient() {
        return new HttpSolrClient.Builder(baseUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();
    }
}
