package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ISolrSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ISolrSearchService solrSearchService;

    /**
     * 门户搜索
     *
     * @param keyword
     * @param page
     * @return
     */
    @GetMapping()
    public ResponseResult doSearch(@RequestParam("keyword") String keyword,
                                   @RequestParam(value = "category_id", required = false) String categoryID,
                                   @RequestParam(value = "sort", required = false) Integer sort,
                                   @RequestParam("page") int page,
                                   @RequestParam("size") int size) {
        log.info("门户搜索 ----> " + "keyword: " + keyword);
        return solrSearchService.queryArticle(keyword, categoryID, sort, page, size);
    }
}
