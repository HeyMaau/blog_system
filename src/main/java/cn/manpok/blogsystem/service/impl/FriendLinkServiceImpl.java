package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IFriendLinkDao;
import cn.manpok.blogsystem.pojo.BlogFriendLink;
import cn.manpok.blogsystem.pojo.BlogPaging;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IFriendLinkService;
import cn.manpok.blogsystem.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FriendLinkServiceImpl implements IFriendLinkService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IFriendLinkDao friendLinkDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Gson gson;

    @Override
    public ResponseResult addFriendLink(BlogFriendLink blogFriendLink) {
        //检查参数
        if (checkFriendLinkParams(blogFriendLink)) {
            return ResponseResult.FAIL("友情链接参数有误");
        }
        //补充数据
        blogFriendLink.setId(String.valueOf(snowflake.nextId()));
        Date date = new Date();
        blogFriendLink.setCreateTime(date);
        blogFriendLink.setUpdateTime(date);
        friendLinkDao.save(blogFriendLink);
        redisUtil.del(Constants.FriendLink.KEY_FRIEND_LINK_LIST_CACHE);
        return ResponseResult.SUCCESS("添加友情链接成功");
    }

    @Override
    @Transactional
    public ResponseResult deleteFriendLink(String friendLinkID) {
        int deleteCount = friendLinkDao.deleteFriendLinkById(friendLinkID);
        if (deleteCount < 1) {
            log.info("管理平台删除友情链接失败 ----> " + friendLinkID);
            return ResponseResult.FAIL("删除友情链接失败");
        }
        log.info("管理平台删除友情链接成功 ----> " + friendLinkID);
        redisUtil.del(Constants.FriendLink.KEY_FRIEND_LINK_LIST_CACHE);
        return ResponseResult.SUCCESS("删除友情链接成功");
    }

    @Override
    @Transactional
    public ResponseResult updateFriendLink(BlogFriendLink blogFriendLink) {
        //检查参数，有些不能为空
        if (checkFriendLinkParams(blogFriendLink)) {
            return ResponseResult.FAIL("友情链接参数有误");
        }
        BlogFriendLink queryFriendLink = friendLinkDao.findFriendLinkById(blogFriendLink.getId());
        if (queryFriendLink == null) {
            return ResponseResult.FAIL("友情链接不存在");
        }
        queryFriendLink.setName(blogFriendLink.getName());
        queryFriendLink.setLogo(blogFriendLink.getLogo());
        queryFriendLink.setUrl(blogFriendLink.getUrl());
        queryFriendLink.setLinkOrder(blogFriendLink.getLinkOrder());
        queryFriendLink.setUpdateTime(new Date());
        redisUtil.del(Constants.FriendLink.KEY_FRIEND_LINK_LIST_CACHE);
        return ResponseResult.SUCCESS("修改友情链接成功");
    }

    @Override
    public ResponseResult getFriendLinks(int page, int size) {
        String friendLinkCacheStr = (String) redisUtil.get(Constants.FriendLink.KEY_FRIEND_LINK_LIST_CACHE);
        if (!TextUtil.isEmpty(friendLinkCacheStr)) {
            BlogPaging<List<BlogFriendLink>> friendLinkCache = gson.fromJson(friendLinkCacheStr, new TypeToken<BlogPaging<List<BlogFriendLink>>>() {
            }.getType());
            if (friendLinkCache != null) {
                log.info("前端从redis获取友情链接");
                return ResponseResult.SUCCESS("获取友情链接列表成功").setData(friendLinkCache);
            }
        }
        //检查分页参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.ASC, "linkOrder");
        Page<BlogFriendLink> queryFriendLinks = friendLinkDao.findAll(pageable);
        BlogPaging<List<BlogFriendLink>> paging = new BlogPaging<>(pageInfo.page, pageInfo.size, queryFriendLinks.getTotalElements(), queryFriendLinks.getContent());
        redisUtil.set(Constants.FriendLink.KEY_FRIEND_LINK_LIST_CACHE, gson.toJson(paging), Constants.TimeValue.HOUR);
        log.info("友情链接列表已缓存到redis");
        return ResponseResult.SUCCESS("获取友情链接列表成功").setData(paging);
    }

    private boolean checkFriendLinkParams(BlogFriendLink blogFriendLink) {
        return TextUtil.isEmpty(blogFriendLink.getName()) || TextUtil.isEmpty(blogFriendLink.getUrl());
    }
}
