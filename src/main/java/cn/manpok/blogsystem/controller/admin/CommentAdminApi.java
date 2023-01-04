package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.service.ICommentAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理评论Api
 */
@Slf4j
@RestController
@RequestMapping("/admin/comment")
@PreAuthorize("@permission.admin")
public class CommentAdminApi {

    @Autowired
    private ICommentAdminService commentAdminService;

    /**
     * 通过改变状态删除评论
     *
     * @param commentID
     * @return
     */
    @DeleteMapping("/state/{commentID}")
    public ResponseResult deleteCommentByState(@PathVariable("commentID") String commentID) {
        log.info("通过改变状态删除评论 ----> " + commentID);
        return commentAdminService.deleteCommentByState(commentID);
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
        return commentAdminService.deleteComment(commentID);
    }

    /**
     * 获取评论
     *
     * @param commentID
     * @return
     */
    @GetMapping("/{commentID}")
    public ResponseResult getComment(@PathVariable("commentID") String commentID) {
        log.info("获取评论 ----> " + commentID);
        return commentAdminService.getComment(commentID);
    }

    /**
     * 获取评论列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getComments(@RequestParam("page") int page, @RequestParam("size") int size,
                                      @RequestParam(value = "state", required = false) String state) {
        log.info("获取评论列表 ----> ");
        return commentAdminService.getComments(page, size, state);
    }

    /**
     * 置顶评论
     *
     * @param commentID
     * @return
     */
    @PutMapping("/top/{commentID}")
    public ResponseResult topComment(@PathVariable("commentID") String commentID) {
        log.info("置顶评论 ----> " + commentID);
        return commentAdminService.topComment(commentID);
    }

    /**
     * 把评论从删除状态恢复到正常状态
     *
     * @param commentID
     * @return
     */
    @PutMapping("/recover/{commentID}")
    public ResponseResult recoverComment(@PathVariable("commentID") String commentID) {
        log.info("恢复评论 ----> " + commentID);
        return commentAdminService.recoverComment(commentID);
    }
}
