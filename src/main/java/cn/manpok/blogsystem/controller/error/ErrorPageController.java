package cn.manpok.blogsystem.controller.error;

import cn.manpok.blogsystem.response.ResponseResult;
import cn.manpok.blogsystem.response.ResponseState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorPageController {

    @GetMapping("/403")
    public ResponseResult error403() {
        return ResponseResult.FAIL(ResponseState.PERMISSION_DENIED);
    }

    @GetMapping("/404")
    public ResponseResult error404() {
        return ResponseResult.FAIL(ResponseState.NOT_FOUND);
    }


    @GetMapping("/500")
    public ResponseResult error500() {
        return ResponseResult.FAIL(ResponseState.SERVER_ERROR);
    }

    @GetMapping("/504")
    public ResponseResult error504() {
        return ResponseResult.FAIL(ResponseState.REQUEST_TIMEOUT);
    }
}
