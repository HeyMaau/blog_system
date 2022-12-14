package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IFriendLinkDao;
import cn.manpok.blogsystem.pojo.BlogFriendLink;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IFriendLinkService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.PageUtil;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FriendLinkServiceImpl implements IFriendLinkService {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IFriendLinkDao friendLinkDao;

    @Override
    public ResponseResult addFriendLink(BlogFriendLink blogFriendLink) {
        //检查参数
        if (TextUtil.isEmpty(blogFriendLink.getName())) {
            return ResponseResult.FAIL("友情链接名称为空");
        }
        if (TextUtil.isEmpty(blogFriendLink.getLogo())) {
            return ResponseResult.FAIL("友情链接LOGO为空");
        }
        if (TextUtil.isEmpty(blogFriendLink.getUrl())) {
            return ResponseResult.FAIL("友情链接URL为空");
        }
        //补充数据
        blogFriendLink.setId(String.valueOf(snowflake.nextId()));
        blogFriendLink.setState(Constants.STATE_NORMAL);
        blogFriendLink.setCreateTime(new Date());
        blogFriendLink.setUpdateTime(new Date());
        friendLinkDao.save(blogFriendLink);
        return ResponseResult.SUCCESS("添加友情链接成功");
    }

    @Override
    public ResponseResult deleteFriendLink(String friendLinkID) {
        int deleteCount = friendLinkDao.deleteFriendLinkById(friendLinkID);
        if (deleteCount < 1) {
            return ResponseResult.FAIL("友情链接不存在");
        }
        return ResponseResult.SUCCESS("删除友情链接成功");
    }

    @Override
    public ResponseResult updateFriendLink(BlogFriendLink blogFriendLink) {
        //检查参数，有些不能为空
        if (TextUtil.isEmpty(blogFriendLink.getName())) {
            return ResponseResult.FAIL("友情链接名称为空");
        }
        if (TextUtil.isEmpty(blogFriendLink.getLogo())) {
            return ResponseResult.FAIL("友情链接LOGO为空");
        }
        if (TextUtil.isEmpty(blogFriendLink.getUrl())) {
            return ResponseResult.FAIL("友情链接URL为空");
        }
        BlogFriendLink queryFriendLink = friendLinkDao.findFriendLinkById(blogFriendLink.getId());
        if (queryFriendLink == null) {
            return ResponseResult.FAIL("友情链接不存在");
        }
        queryFriendLink.setName(blogFriendLink.getName());
        queryFriendLink.setLogo(blogFriendLink.getLogo());
        queryFriendLink.setUrl(blogFriendLink.getUrl());
        queryFriendLink.setOrder(blogFriendLink.getOrder());
        queryFriendLink.setUpdateTime(new Date());
        return ResponseResult.SUCCESS("修改友情链接成功");
    }

    @Override
    public ResponseResult getFriendLink(String friendLinkID) {
        BlogFriendLink queryFriendLink = friendLinkDao.findFriendLinkById(friendLinkID);
        if (queryFriendLink == null) {
            return ResponseResult.FAIL("友情链接不存在");
        }
        return ResponseResult.SUCCESS("获取友情链接成功").setData(queryFriendLink);
    }

    @Override
    public ResponseResult getFriendLinks(int page, int size) {
        //检查分页参数
        PageUtil.PageInfo pageInfo = PageUtil.checkPageParam(page, size);
        Pageable pageable = PageRequest.of(pageInfo.page - 1, pageInfo.size, Sort.Direction.ASC, "createTime");
        Page<BlogFriendLink> queryFriendLinks = friendLinkDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取所有友情链接成功").setData(queryFriendLinks);
    }

    @Override
    public ResponseResult getNormalFriendLinks() {
        List<BlogFriendLink> all = friendLinkDao.findAllFriendLinksByState(Constants.STATE_NORMAL);
        return ResponseResult.SUCCESS("获取所有友情链接成功").setData(all);
    }
}
