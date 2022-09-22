package cn.manpok.blogsystem.controller.admin;

import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理评论Api
 */
@Slf4j
@RestController
@RequestMapping("/admin/comment")
public class CommentApi {

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
     * 获取评论
     *
     * @param commentID
     * @return
     */
    @GetMapping("/{commentID}")
    public ResponseResult getComment(@PathVariable("commentID") String commentID) {
        log.info("获取评论 ----> " + commentID);
        return null;
    }

    /**
     * 获取评论列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getComments(@RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("获取评论列表 ----> ");
        return null;
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
        return null;
    }
}
