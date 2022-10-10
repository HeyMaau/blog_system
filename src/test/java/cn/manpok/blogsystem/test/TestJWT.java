package cn.manpok.blogsystem.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

public class TestJWT {

    public static void main(String[] args) {
        String digest = DigestUtils.md5DigestAsHex("manpok_blog_system_+=&".getBytes());
        System.out.println(digest);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches("hh123", "$2a$10$YBTMJyMW/ex1RQHvvojgIeM7pMSa1/4gol1.6iKbEJr/TPXCKk.BC");
        System.out.println(matches);
    }
}
