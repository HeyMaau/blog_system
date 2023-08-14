package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IThinkingDao;
import cn.manpok.blogsystem.pojo.BlogPaging;
import cn.manpok.blogsystem.pojo.BlogThinking;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IThinkingService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ThinkingServiceImpl implements IThinkingService {

    @Autowired
    private IThinkingDao thinkingDao;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Gson gson;

    @Transactional
    @Override
    public ResponseResult addThinking(BlogThinking thinking) {
        //检查参数是否规范
        if (TextUtil.isEmpty(thinking.getContent())) {
            return ResponseResult.FAIL("想法内容为空");
        }
        //补充参数
        thinking.setId(String.valueOf(snowflake.nextId()));
        thinking.setState(Constants.STATE_NORMAL);
        BlogUser user = userService.checkUserToken();
        thinking.setUserId(user.getId());
        Date date = new Date();
        thinking.setCreateTime(date);
        thinking.setUpdateTime(date);
        thinkingDao.save(thinking);
        //删除redis中的缓存
        redisUtil.del(Constants.Thinking.KEY_THINKINGS_CACHE);
        return ResponseResult.SUCCESS("发布想法成功");
    }

    @Transactional
    @Override
    public ResponseResult updateThinking(BlogThinking thinking) {
        //检查参数是否规范
        if (TextUtil.isEmpty(thinking.getContent())) {
            return ResponseResult.FAIL("想法内容为空");
        }
        //从数据库中查询
        BlogThinking queryThinking = thinkingDao.findThinkingById(thinking.getId());
        if (queryThinking == null) {
            return ResponseResult.FAIL("想法不存在");
        }
        //更新数据
        queryThinking.setTitle(thinking.getTitle());
        queryThinking.setContent(thinking.getContent());
        queryThinking.setImages(thinking.getImages());
        queryThinking.setUpdateTime(new Date());
        //删除redis中的缓存
        redisUtil.del(Constants.Thinking.KEY_THINKINGS_CACHE);
        return ResponseResult.SUCCESS("修改想法成功");
    }

    @Transactional
    @Override
    public ResponseResult deleteThinking(String thinkingID) {
        //从数据库中查询
        BlogThinking queryThinking = thinkingDao.findThinkingById(thinkingID);
        if (queryThinking == null) {
            return ResponseResult.FAIL("想法不存在");
        }
        queryThinking.setState(Constants.STATE_FORBIDDEN);
        //删除redis中的缓存
        redisUtil.del(Constants.Thinking.KEY_THINKINGS_CACHE);
        return ResponseResult.SUCCESS("删除想法成功");
    }

    @Transactional
    @Override
    public ResponseResult deleteThinkingPhysically(String thinkingID) {
        int deleteCount = thinkingDao.deleteThinkingById(thinkingID);
        if (deleteCount <= 0) {
            return ResponseResult.FAIL("彻底删除想法失败");
        }
        //删除redis中的缓存
        redisUtil.del(Constants.Thinking.KEY_THINKINGS_CACHE);
        return ResponseResult.SUCCESS("彻底删除想法成功");
    }

    @Override
    public ResponseResult getNormalThinkings(int page, int size) {
        //检查分页参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        //从redis中取第一页的缓存
        if (pageInfo.page == 1) {
            String thinkingListCacheStr = (String) redisUtil.get(Constants.Thinking.KEY_THINKINGS_CACHE);
            if (!TextUtil.isEmpty(thinkingListCacheStr)) {
                BlogPaging<List<BlogThinking>> thinkingCache = gson.fromJson(thinkingListCacheStr, new TypeToken<BlogPaging<List<BlogThinking>>>() {
                }.getType());
                log.info("从redis中取出第一页想法");
                return ResponseResult.SUCCESS("获取想法列表成功").setData(thinkingCache);
            }
        }
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.DESC, "createTime");
        Page<BlogThinking> pageData = thinkingDao.findAllThinkinsByState(Constants.STATE_NORMAL, pageable);
        BlogPaging<List<BlogThinking>> paging = new BlogPaging<>(pageInfo.page, pageInfo.size, pageData.getTotalElements(), pageData.getContent());
        //缓存第一页想法
        if (pageInfo.page == 1) {
            String thinkingListCacheStr = gson.toJson(paging);
            redisUtil.set(Constants.Thinking.KEY_THINKINGS_CACHE, thinkingListCacheStr, Constants.TimeValue.HOUR_2);
            log.info("已缓存第一页想法到redis");
        }
        return ResponseResult.SUCCESS("获取想法列表成功").setData(paging);
    }

    @Override
    public ResponseResult getAllThinkings(int page, int size, String keyword, String state) {
        //检查分页参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        Specification<BlogThinking> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!TextUtil.isEmpty(keyword)) {
                Predicate titlePre = criteriaBuilder.like(root.get("title"), "%" + keyword + "%");
                Predicate contentPre = criteriaBuilder.like(root.get("content"), "%" + keyword + "%");
                Predicate keywordPre = criteriaBuilder.or(titlePre, contentPre);
                predicateList.add(keywordPre);
            }
            if (!TextUtil.isEmpty(state)) {
                Predicate statePre = criteriaBuilder.equal(root.get("state"), state);
                predicateList.add(statePre);
            }
            Predicate[] predicates = predicateList.toArray(new Predicate[0]);
            return criteriaBuilder.and(predicates);
        };
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.DESC, "createTime");
        Page<BlogThinking> pageData = thinkingDao.findAll(specification, pageable);
        BlogPaging<List<BlogThinking>> paging = new BlogPaging<>(pageInfo.page, pageInfo.size, pageData.getTotalElements(), pageData.getContent());
        return ResponseResult.SUCCESS("获取想法列表成功").setData(paging);
    }
}
