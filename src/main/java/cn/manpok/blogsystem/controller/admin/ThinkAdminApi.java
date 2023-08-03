package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.pojo.BlogThinking;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.IThinkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 想法相关API，具备管理功能，添加，删除等
 */
@RestController
@Slf4j
@RequestMapping("/admin/thinking")
@PreAuthorize("@permission.admin")
public class ThinkAdminApi {

    @Autowired
    private IThinkingService thinkingService;

    @PostMapping
    public ResponseResult addThinking(@RequestBody BlogThinking thinking) {
        log.info("添加想法 ----> " + thinking);
        return thinkingService.addThinking(thinking);
    }
}
