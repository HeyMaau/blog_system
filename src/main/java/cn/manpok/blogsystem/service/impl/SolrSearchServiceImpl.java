package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.pojo.BlogPaging;
import cn.manpok.blogsystem.pojo.BlogSolrSearch;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ISolrSearchService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.ListUtil;
import cn.manpok.blogsystem.utils.PageUtil;
import cn.manpok.blogsystem.utils.TextUtil;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SolrSearchServiceImpl implements ISolrSearchService {

    @Autowired
    private SolrClient solrClient;

    @Autowired
    private Parser parser;

    @Autowired
    private HtmlRenderer renderer;

    @Override
    @Async("asyncTaskServiceExecutor")
    public void addArticle(BlogArticle blogArticle) {
        SolrInputDocument document = createDocument(blogArticle);
        try {
            solrClient.add(document);
            solrClient.commit();
            log.info("文章保存SOLR成功 ----> " + blogArticle.getId());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("文章保存SOLR失败 ----> " + blogArticle.getId());
        }
    }

    @Override
    public void deleteArticle(String articleID) {
        try {
            solrClient.deleteById(articleID);
            solrClient.commit();
            log.info("solr删除文章成功 ----> " + articleID);
        } catch (Exception e) {
            log.info("solr删除文章失败 ----> " + articleID);
            e.printStackTrace();
        }
    }

    @Override
    public void clearData() {
        try {
            solrClient.deleteByQuery("*:*");
            solrClient.commit();
            log.info("清空solr数据库");
        } catch (Exception e) {
            log.error("清空solr数据库失败");
        }
    }

    @Override
    public void updateArticle(BlogArticle blogArticle) {
        addArticle(blogArticle);
    }

    @Override
    public ResponseResult queryArticle(String keyword, String categoryID, Integer sort, int page, int size) {
        //检查参数
        if (TextUtil.isEmpty(keyword)) {
            return ResponseResult.FAIL("关键词为空");
        }
        SolrQuery solrQuery = new SolrQuery();
        //默认搜索域
        solrQuery.set("df", Constants.Search.DEFAULT_FIELD);
        //搜索关键词
        solrQuery.setQuery(keyword);
        //排序：按发表时间先后，或者按照阅读量高低
        if (sort == null) {
            //默认按照阅读量从高到低的顺序
            sort = Constants.Search.SORT_VIEW_COUNT_DESC;
        }
        switch (sort) {
            case Constants.Search.SORT_CREATE_TIME_ASC ->
                    solrQuery.setSort(Constants.Search.FIELD_CREATE_TIME, SolrQuery.ORDER.asc);
            case Constants.Search.SORT_CREATE_TIME_DESC ->
                    solrQuery.setSort(Constants.Search.FIELD_CREATE_TIME, SolrQuery.ORDER.desc);
            case Constants.Search.SORT_VIEW_COUNT_ASC ->
                    solrQuery.setSort(Constants.Search.FIELD_VIEW_COUNT, SolrQuery.ORDER.asc);
            case Constants.Search.SORT_VIEW_COUNT_DESC ->
                    solrQuery.setSort(Constants.Search.FIELD_VIEW_COUNT, SolrQuery.ORDER.desc);
        }
        //分类关键词
        if (!TextUtil.isEmpty(categoryID)) {
            solrQuery.setFilterQueries(Constants.Search.FIELD_CATEGORY_ID + ":" + categoryID);
        }
        //设置分页
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        solrQuery.setRows(pageInfo.size);
        int start = (pageInfo.page - 1) * pageInfo.size;
        solrQuery.setStart(start);
        //设置高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField(Constants.Search.FIELD_TITLE);
        solrQuery.addHighlightField(Constants.Search.FIELD_CONTENT);
        solrQuery.setHighlightFragsize(Constants.Search.HIGHLIGHT_FRAG_SIZE);
        QueryResponse response = null;
        try {
            response = solrClient.query(solrQuery);
        } catch (Exception e) {
            log.error("solr查询异常");
            e.printStackTrace();
        }
        if (response != null) {
            Map<String, Map<String, List<String>>> highlightings = response.getHighlighting();
            //把查询结果转换为对应的bean类
            List<BlogSolrSearch> searchList = response.getBeans(BlogSolrSearch.class);
            //获取高亮的结果
            for (BlogSolrSearch search : searchList) {
                //获取ID，根据ID获取对应的高亮信息
                Map<String, List<String>> highlighting = highlightings.get(search.getId());
                //目前高亮信息有两个字段：标题、内容
                List<String> titleHighlightList = highlighting.get(Constants.Search.FIELD_TITLE);
                List<String> contentHighlightList = highlighting.get(Constants.Search.FIELD_CONTENT);
                if (!ListUtil.isEmpty(titleHighlightList)) {
                    search.setTitle(titleHighlightList.get(0));
                }
                if (!ListUtil.isEmpty(contentHighlightList)) {
                    search.setContent(contentHighlightList.get(0));
                }
            }
            //获取分页总数
            long numFound = response.getResults().getNumFound();
            //设置返回的分页数据
            BlogPaging<List<BlogSolrSearch>> blogPaging = new BlogPaging<>(pageInfo.size, numFound, pageInfo.page, searchList);
            return ResponseResult.SUCCESS("搜索成功").setData(blogPaging);
        }
        return ResponseResult.FAIL("搜索失败");
    }

    /**
     * 创建添加到solr的文档
     *
     * @param blogArticle
     * @return
     */
    private SolrInputDocument createDocument(BlogArticle blogArticle) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", blogArticle.getId());
        document.addField("view_count", blogArticle.getViewCount());
        document.addField("title", blogArticle.getTitle());
        document.addField("cover", blogArticle.getCover());
        //内容转纯文本存入solr，先判断文章格式类型
        //如果是MD，则先转HTML
        String html;
        if (blogArticle.getType().equals(Constants.Article.TYPE_MARKDOWN)) {
            html = md2Html(blogArticle.getContent());
        } else {
            html = blogArticle.getContent();
        }
        String text = html2Text(html);
        document.addField("content", text);
        document.addField("labels", blogArticle.getLabels());
        document.addField("category_id", blogArticle.getCategoryId());
        document.addField("create_time", blogArticle.getCreateTime());
        document.addField("update_time", blogArticle.getUpdateTime());
        return document;
    }

    /**
     * Markdown转HTML
     *
     * @param md
     * @return
     */
    private String md2Html(String md) {
        Node document = parser.parse(md);
        return renderer.render(document);
    }

    /**
     * HTML转纯文本
     *
     * @param html
     * @return
     */
    private String html2Text(String html) {
        return Jsoup.parse(html).text();
    }
}
