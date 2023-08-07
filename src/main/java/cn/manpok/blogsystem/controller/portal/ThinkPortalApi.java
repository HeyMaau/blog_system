package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IThinkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 想法相关API，供门户使用
 */
@RestController
@Slf4j
@RequestMapping("/portal/thinking")
public class ThinkPortalApi {

    @Autowired
    private IThinkingService thinkingService;

    @GetMapping
    public ResponseResult getNormalThinkings(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("门户获取想法列表");
        return thinkingService.getNormalThinkings(page, size);
    }
}
