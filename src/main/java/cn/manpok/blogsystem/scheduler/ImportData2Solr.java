package cn.manpok.blogsystem.scheduler;

import cn.manpok.blogsystem.dao.IArticleAdminDao;
import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.service.ISolrSearchService;
import cn.manpok.blogsystem.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.List;

@Component
public class ImportData2Solr implements ApplicationRunner {

    @Autowired
    private ISolrSearchService solrSearchService;

    @Autowired
    private IArticleAdminDao articleAdminDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //先清空数据库，防止数据错误
        solrSearchService.clearData();
        List<BlogArticle> all = articleAdminDao.findAll((Specification<BlogArticle>) (root, query, criteriaBuilder) -> {
            //条件：筛选文章状态为发布或置顶的
            Predicate statePublishPredicate = criteriaBuilder.equal(root.get("state"), Constants.Article.STATE_PUBLISH);
            Predicate stateTopPredicate = criteriaBuilder.equal(root.get("state"), Constants.Article.STATE_TOP);
            return criteriaBuilder.or(statePublishPredicate, stateTopPredicate);
        });
        for (BlogArticle article : all) {
            solrSearchService.addArticle(article);
        }
    }
}
