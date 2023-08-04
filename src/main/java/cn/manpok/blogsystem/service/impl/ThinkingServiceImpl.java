package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IThinkingDao;
import cn.manpok.blogsystem.pojo.BlogThinking;
import cn.manpok.blogsystem.pojo.BlogUser;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IThinkingService;
import cn.manpok.blogsystem.service.IUserService;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ThinkingServiceImpl implements IThinkingService {

    @Autowired
    private IThinkingDao thinkingDao;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private IUserService userService;

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
        return ResponseResult.SUCCESS("删除想法成功");
    }
}
