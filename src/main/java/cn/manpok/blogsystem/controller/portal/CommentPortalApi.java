package cn.manpok.blogsystem.controller.portal;

import cn.manpok.blogsystem.interceptors.CheckRepeatedCommit;
import cn.manpok.blogsystem.pojo.BlogComment;
import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ICommentPortalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 门户评论Api
 */
@Slf4j
@RestController
@RequestMapping("/portal/comment")
public class CommentPortalApi {

    @Autowired
    private ICommentPortalService commentPortalService;

    /**
     * 添加评论
     *
     * @param blogComment
     * @return
     */
    @CheckRepeatedCommit
    @PostMapping
    public ResponseResult addComment(@RequestBody BlogComment blogComment) {
        log.info("添加评论 ----> " + blogComment.toString());
        return commentPortalService.addComment(blogComment);
    }

    /**
     * 删除评论
     *
     * @param commentID
     * @return
     */
    /*@DeleteMapping("/{commentID}")
    public ResponseResult deleteComment(@PathVariable("commentID") String commentID) {
        log.info("删除评论 ----> " + commentID);
        return commentPortalService.deleteCommend(commentID);
    }*/

    /**
     * 修改评论
     *
     * @param blogComment
     * @return
     */
    /*@PutMapping
    public ResponseResult updateComment(@RequestBody BlogComment blogComment) {
        log.info("修改评论 ----> " + blogComment.toString());
        return commentPortalService.updateComment(blogComment);
    }*/

    /**
     * 门户获取文章或想法下的所有评论
     *
     * @param articleID
     * @return
     */
    @GetMapping("/list/{type}/{articleID}")
    public ResponseResult getCommentsByArticle(@PathVariable("articleID") String articleID,
                                               @PathVariable("type") String type,
                                               @RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("门户获取文章或想法的所有评论 ----> " + articleID);
        return commentPortalService.getComments(articleID, type, page, size);
    }
}
