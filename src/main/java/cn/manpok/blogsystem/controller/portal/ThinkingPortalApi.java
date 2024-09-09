package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IThinkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 想法相关API，供门户使用
 */
@RestController
@Slf4j
@RequestMapping("/portal/thinking")
public class ThinkingPortalApi {

    private final IThinkingService thinkingService;

    public ThinkingPortalApi(IThinkingService thinkingService) {
        this.thinkingService = thinkingService;
    }

    @GetMapping("/list")
    public ResponseResult getNormalThinkings(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("门户获取想法列表");
        return thinkingService.getNormalThinkings(page, size);
    }

    @GetMapping("/{id}")
    public ResponseResult getThinking(@PathVariable String id) {
        log.info("门户获取想法：" + id);
        return thinkingService.getThinking(id);
    }
}
