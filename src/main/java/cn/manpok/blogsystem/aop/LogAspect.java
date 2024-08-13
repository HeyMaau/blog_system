package cn.manpok.blogsystem.aop;

import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.TextUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Aspect
@Slf4j
@Component
public class LogAspect {

    @Autowired
    private HttpServletRequest request;

    @Pointcut("execution(* cn.manpok.blogsystem.controller.portal.*.*(..))")
    public void log() {
    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        String ip = TextUtil.isEmpty(request.getHeader(Constants.User.KEY_HEADER_X_REAL_IP)) ? request.getRemoteAddr() : request.getHeader(Constants.User.KEY_HEADER_X_REAL_IP);
        log.info(String.format(Constants.Log.BEFORE_LOG, joinPoint.getSignature().getName(), ip));
    }
}
