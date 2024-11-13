package cn.manpok.blogsystem.listener;

import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.TextUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

@Slf4j
@Component
public class CustomApplicationListener implements ApplicationListener<ServletRequestHandledEvent> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        Throwable failureCause = event.getFailureCause();
        if (failureCause != null) {
            log.error("api error: " + failureCause.getMessage());
        }
        String ip = TextUtil.isEmpty(request.getHeader(Constants.User.KEY_HEADER_X_REAL_IP)) ? request.getRemoteAddr() : request.getHeader(Constants.User.KEY_HEADER_X_REAL_IP);
        log.info("ip: {}  url: {}  method: {}  time: {}ms", ip, event.getRequestUrl(), event.getMethod(), event.getProcessingTimeMillis());
    }
}
