package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户搜索API
 */
@Slf4j
@RestController
@RequestMapping("/portal/search")
public class SearchPortalApi {

    /**
     * 门户搜索
     *
     * @param keyword
     * @param page
     * @return
     */
    @GetMapping
    public ResponseResult doSearch(@RequestParam("keyword") String keyword, @RequestParam("page") int page) {
        log.info("门户搜索 ----> " + "keyword: " + keyword);
        return null;
    }
}
