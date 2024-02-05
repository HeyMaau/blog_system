package cn.manpok.blogsystem.scheduler;

import cn.manpok.blogsystem.dao.IArticleAdminDao;
import cn.manpok.blogsystem.dao.IStatisticsDao;
import cn.manpok.blogsystem.pojo.BlogArticle;
import cn.manpok.blogsystem.pojo.BlogStatistics;
import cn.manpok.blogsystem.service.ISolrSearchService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class AppRunnerImpl implements ApplicationRunner {

    @Autowired
    private ISolrSearchService solrSearchService;

    @Autowired
    private IArticleAdminDao articleAdminDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IStatisticsDao statisticsDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        clearRedisCache();
        importData2Solr();
        migrateStatisticData();
    }

    private void clearRedisCache() {
        Set keys = redisUtil.keys("*");
        log.info(String.format("清除redis缓存时查询出%d条key", keys.size()));
        Long delCount = redisUtil.dels(keys);
        log.info(String.format("清除%s条redis缓存", delCount));
    }

    private void importData2Solr() {
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

    private void migrateStatisticData() {
        //todo:下次发版移除
        List<BlogStatistics> all = statisticsDao.findAll();
        for (BlogStatistics blogStatistics : all) {
            String page = blogStatistics.getPage();
            if (!TextUtil.isEmpty(page) && page.equalsIgnoreCase("AudioPlayer")) {
                blogStatistics.setComponent(blogStatistics.getPage());
                blogStatistics.setPage(null);
                statisticsDao.save(blogStatistics);
            }
        }
    }
}
