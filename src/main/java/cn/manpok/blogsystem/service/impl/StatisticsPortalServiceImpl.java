package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IStatisticsDao;
import cn.manpok.blogsystem.pojo.BlogStatistics;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IStatisticsPortalService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class StatisticsPortalServiceImpl implements IStatisticsPortalService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IStatisticsDao statisticsDao;

    @Autowired
    private Snowflake snowflake;

    private final SimpleDateFormat simpleDateFormat;

    public StatisticsPortalServiceImpl() {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
    }

    @Override
    public synchronized ResponseResult commitRecord(BlogStatistics blogStatistics) {
        Date date = new Date();
        String recordDate = simpleDateFormat.format(date);
        String key = String.format(Constants.Statistics.KEY_STATISTICS_CACHE, blogStatistics.getPage(), blogStatistics.getComponent(), blogStatistics.getEvent(), recordDate, blogStatistics.getClient());
        if (redisUtil.hasKey(key)) {
            String countStr = (String) redisUtil.get(key);
            long count = Long.parseLong(countStr);
            redisUtil.set(key, String.valueOf(++count), Constants.TimeValue.DAY);
        } else {
            //先从数据库查询
            BlogStatistics queryBlogStatistics;
            if (TextUtil.isEmpty(blogStatistics.getPage())) {
                queryBlogStatistics = statisticsDao.findByComponentAndEventAndRecordDateAndClient(blogStatistics.getComponent(), blogStatistics.getEvent(), recordDate, blogStatistics.getClient());
            } else {
                queryBlogStatistics = statisticsDao.findByPageAndEventAndRecordDateAndClient(blogStatistics.getPage(), blogStatistics.getEvent(), recordDate, blogStatistics.getClient());
            }
            if (queryBlogStatistics != null) {
                long count = queryBlogStatistics.getCount();
                redisUtil.set(key, String.valueOf(++count), Constants.TimeValue.DAY);
            } else {
                queryBlogStatistics = new BlogStatistics();
                queryBlogStatistics.setId(String.valueOf(snowflake.nextId()));
                queryBlogStatistics.setPage(blogStatistics.getPage());
                queryBlogStatistics.setComponent(blogStatistics.getComponent());
                queryBlogStatistics.setEvent(blogStatistics.getEvent());
                queryBlogStatistics.setCount(1);
                queryBlogStatistics.setRecordDate(recordDate);
                queryBlogStatistics.setClient(blogStatistics.getClient());
                queryBlogStatistics.setCreateTime(date);
                queryBlogStatistics.setUpdateTime(date);
                statisticsDao.save(queryBlogStatistics);
                redisUtil.set(key, "1", Constants.TimeValue.DAY);

            }
        }
        return ResponseResult.SUCCESS("埋点上报成功");
    }

    @Override
    @PreDestroy
    @Async("asyncTaskServiceExecutor")
    @Scheduled(cron = "0 0/30 * * * ?")
    public void saveRecord2DB() {
        Pattern pattern = Pattern.compile(Constants.Statistics.PATTERN);
        Set keys = redisUtil.keys(Constants.Statistics.KEY_STATISTICS_CACHE_PREFIX + "*");
        for (Object key : keys) {
            String keyStr = (String) key;
            Matcher matcher = pattern.matcher(keyStr);
            if (matcher.matches()) {
                String page = matcher.group(1);
                String component = matcher.group(2);
                String event = matcher.group(3);
                String recordDate = matcher.group(4);
                String client = matcher.group(5);
                BlogStatistics queryBlogStatistics;
                if (!TextUtil.isEmpty(page) && page.equalsIgnoreCase("null")) {
                    queryBlogStatistics = statisticsDao.findByComponentAndEventAndRecordDateAndClient(component, event, recordDate, client);
                } else {
                    queryBlogStatistics = statisticsDao.findByPageAndEventAndRecordDateAndClient(page, event, recordDate, client);
                }
                if (queryBlogStatistics != null) {
                    String countStr = (String) redisUtil.get(keyStr);
                    queryBlogStatistics.setCount(Long.parseLong(countStr));
                    queryBlogStatistics.setUpdateTime(new Date());
                    statisticsDao.save(queryBlogStatistics);
                }
            }
        }
        if (!keys.isEmpty()) {
            log.info("埋点数据保存到数据库");
        }
    }
}
