package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.pojo.BlogComment;
import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 门户评论Api
 */
@Slf4j
@RestController
@RequestMapping("/portal/comment")
public class CommentPortalApi {

    /**
     * 添加评论
     *
     * @param blogComment
     * @return
     */
    @PostMapping
    public ResponseResult addComment(@RequestBody BlogComment blogComment) {
        log.info("添加评论 ----> " + blogComment.toString());
        return null;
    }

    /**
     * 删除评论
     *
     * @param commentID
     * @return
     */
    @DeleteMapping("/{commentID}")
    public ResponseResult deleteComment(@PathVariable("commentID") String commentID) {
        log.info("删除评论 ----> " + commentID);
        return null;
    }

    /**
     * 修改评论
     *
     * @param blogComment
     * @return
     */
    @PutMapping
    public ResponseResult updateComment(@RequestBody BlogComment blogComment) {
        log.info("修改评论 ----> " + blogComment.toString());
        return null;
    }

    /**
     * 门户获取文章下的所有评论
     *
     * @param articleID
     * @return
     */
    @GetMapping("/list/{articleID}")
    public ResponseResult getCommentsByArticle(@PathVariable("articleID") String articleID) {
        log.info("门户获取文章下的所有评论 ----> " + articleID);
        return null;
    }
}
