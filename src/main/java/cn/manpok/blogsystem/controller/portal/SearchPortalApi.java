package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.interceptors.CheckRepeatedCommit;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ISolrSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 门户搜索API
 */
@Slf4j
@RestController
@RequestMapping("/portal/search")
public class SearchPortalApi {

    @Autowired
    private ISolrSearchService solrSearchService;

    /**
     * 门户搜索
     *
     * @param keyword
     * @param page
     * @return
     */
    @CheckRepeatedCommit
    @GetMapping("/{keyword}")
    public ResponseResult doSearch(@PathVariable("keyword") String keyword,
                                   @RequestParam(value = "category_id", required = false) String categoryID,
                                   @RequestParam(value = "sort", required = false) Integer sort,
                                   @RequestParam("page") int page,
                                   @RequestParam("size") int size) {
        log.info("门户搜索 ----> " + "keyword: " + keyword);
        return solrSearchService.queryArticle(keyword, categoryID, sort, page, size);
    }
}
