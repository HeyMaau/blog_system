package cn.manpok.blogsystem.service.impl;

import cn.manpok.blogsystem.dao.IFeedbackDao;
import cn.manpok.blogsystem.pojo.BlogFeedback;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IFeedbackService;
import cn.manpok.blogsystem.utils.Snowflake;
import cn.manpok.blogsystem.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FeedbackServiceImpl implements IFeedbackService {

    @Autowired
    private IFeedbackDao feedbackDao;

    @Autowired
    private Snowflake snowflake;

    @Override
    public ResponseResult receiveFeedback(BlogFeedback feedback) {
        if (TextUtil.isEmpty(feedback.getContent())) {
            return ResponseResult.FAIL("反馈内容为空");
        }
        feedback.setId(String.valueOf(snowflake.nextId()));
        feedback.setCreateTime(new Date());
        feedbackDao.save(feedback);
        return ResponseResult.SUCCESS("反馈成功");
    }
}
