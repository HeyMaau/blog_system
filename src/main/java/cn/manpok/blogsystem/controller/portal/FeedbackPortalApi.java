package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.pojo.BlogFeedback;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/feedback")
public class FeedbackPortalApi {

    @Autowired
    private IFeedbackService feedbackService;

    @PostMapping()
    public ResponseResult receiveFeedback(@RequestBody BlogFeedback feedback) {
        return feedbackService.receiveFeedback(feedback);
    }
}
