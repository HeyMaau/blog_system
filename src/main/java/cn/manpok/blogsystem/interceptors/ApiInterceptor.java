package cn.manpok.blogsystem.interceptors;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.TextUtil;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Component
@Slf4j
public class ApiInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Gson gson;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            //检查方法上是否有防止重复提交的注解
            CheckRepeatedCommit checkRepeatedCommit = handlerMethod.getMethodAnnotation(CheckRepeatedCommit.class);
            if (checkRepeatedCommit != null) {
                //获取用户的token key
                String tokenKey = request.getHeader(Constants.User.KEY_HEADER_AUTHORIZATION);
                //拼接redis的key
                String methodName = handlerMethod.getMethod().getName();
                StringBuilder redisKey = new StringBuilder();
                redisKey.append(Constants.KEY_COMMIT_RECORD);
                redisKey.append(tokenKey);
                redisKey.append("_");
                redisKey.append(methodName);
                //从redis中获取已提交的记录
                String commitRecord = (String) redisUtil.get(redisKey.toString());
                if (TextUtil.isEmpty(commitRecord)) {
                    //如果没有记录，放行，并记录
                    redisUtil.set(redisKey.toString(), Constants.VALUE_TRUE, Constants.TimeValue.SECOND_5);
                    return true;
                }
                returnJsonResponse(response, ResponseResult.FAIL(ResponseState.REPEATED_COMMIT));
                return false;
            }
        }
        return true;
    }

    private void returnJsonResponse(HttpServletResponse response, ResponseResult result) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(gson.toJson(result));
            writer.flush();
        } catch (Exception e) {
            log.error("response写数据错误");
        }
        log.info(result.toString());
    }
}
