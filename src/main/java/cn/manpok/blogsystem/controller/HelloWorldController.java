package cn.manpok.blogsystem.controller;

import cn.manpok.blogsystem.pojo.User;
import cn.manpok.blogsystem.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/test")
public class HelloWorldController {

    @GetMapping("/hello-world")
    public String helloWorld() {
        System.out.println("Hello World!");
        return "hello world!";
    }

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user) {
        log.info("login success! ");
        return ResponseResult.SUCCESS().setData(user);
    }
}
