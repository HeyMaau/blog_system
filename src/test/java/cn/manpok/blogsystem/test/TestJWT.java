package cn.manpok.blogsystem.test;

import org.springframework.util.DigestUtils;

public class TestJWT {

    public static void main(String[] args) {
        String digest = DigestUtils.md5DigestAsHex("manpok_blog_system_+=&".getBytes());
        System.out.println(digest);
    }
}
