package cn.manpok.blogsystem.service;

import cn.manpok.blogsystem.pojo.BlogFeedback;
import cn.manpok.blogsystem.response.ResponseResult;

public interface IFeedbackService {

    ResponseResult receiveFeedback(BlogFeedback feedback);
}
