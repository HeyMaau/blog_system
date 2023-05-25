package cn.manpok.blogsystem.interceptors;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.utils.Constants;
import cn.manpok.blogsystem.utils.RedisUtil;
import cn.manpok.blogsystem.utils.TextUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
@Slf4j
public class ApiInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Gson gson;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        if (isIPBlocked(ip)) {
            returnJsonResponse(response, "您的IP已被封，请稍后再试");
            return false;
        }
        addIPAccessCount(ip);
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
                returnJsonResponse(response, "频繁提交，请稍后再试");
                return false;
            }
        }
        return true;
    }

    private boolean isIPBlocked(String ip) {
        String blocked = (String) redisUtil.get(Constants.KEY_BLOCK_IP + ip);
        return blocked != null;
    }

    private void addIPAccessCount(String ip) {
        Integer count = (Integer) redisUtil.get(Constants.KEY_IP_ACCESS_COUNT + ip);
        if (count == null) {
            redisUtil.set(Constants.KEY_IP_ACCESS_COUNT + ip, 1, Constants.TimeValue.SECOND);
            return;
        }
        if (count >= Constants.ACCESS_COUNT_LIMIT) {
            redisUtil.set(Constants.KEY_BLOCK_IP + ip, Constants.VALUE_TRUE, Constants.TimeValue.HOUR_2);
            return;
        }
        redisUtil.set(Constants.KEY_IP_ACCESS_COUNT + ip, ++count);
    }

    private void returnJsonResponse(HttpServletResponse response, String msg) {
        ResponseResult result = ResponseResult.FAIL(msg);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(gson.toJson(result));
            writer.flush();
        } catch (Exception e) {
            log.error("response写数据错误");
        }
        log.info(msg);
    }
}
