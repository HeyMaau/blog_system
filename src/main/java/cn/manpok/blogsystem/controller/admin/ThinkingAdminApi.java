package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogThinking;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IThinkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 想法相关API，具备管理功能，添加，删除等
 */
@RestController
@Slf4j
@RequestMapping("/admin/thinking")
@PreAuthorize("@permission.admin")
public class ThinkingAdminApi {

    @Autowired
    private IThinkingService thinkingService;

    @PostMapping
    public ResponseResult addThinking(@RequestBody BlogThinking thinking) {
        log.info("添加想法 ----> " + thinking);
        return thinkingService.addThinking(thinking);
    }

    @PutMapping
    public ResponseResult updateThinking(@RequestBody BlogThinking thinking) {
        log.info("修改想法 ----> " + thinking);
        return thinkingService.updateThinking(thinking);
    }

    @DeleteMapping("/{thinkingID}")
    public ResponseResult deleteThinking(@PathVariable("thinkingID") String thinkingID) {
        log.info("删除想法 ----> " + thinkingID);
        return thinkingService.deleteThinking(thinkingID);
    }

    @DeleteMapping("/delete/{thinkingID}")
    public ResponseResult deleteThinkingPhysically(@PathVariable("thinkingID") String thinkingID) {
        log.info("彻底删除想法 ----> " + thinkingID);
        return thinkingService.deleteThinkingPhysically(thinkingID);
    }

    @GetMapping("/list")
    public ResponseResult getAllThinkings(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("管理平台获取想法列表");
        return thinkingService.getAllThinkings(page, size);
    }
}
