package cn.manpok.blogsystem.test;

import cn.manpok.blogsystem.utils.JWTUtil;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TestJWT {

    public static void main(String[] args) {
        /*String digest = DigestUtils.md5DigestAsHex("manpok_blog_system_+=&".getBytes());
        System.out.println(digest);

        DecodedJWT decodedJWT = JWTUtil.decodeToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX25hbWUiOiJ0ZXh0X2FjY291bnQiLCJyb2xlcyI6InJvbGVfbm9ybWFsIiwic2lnbiI6Ik15IGJsb2chIiwiaWQiOiIzMzMzODA3MzgxMzA5MDMwNDAiLCJhdmF0YXIiOiJ3d3cuYmFpZHUuY29tL2ltZy9QQ3RtX2Q5Yzg3NTBiZWQwYjNjN2QwODlmYTdkNTU3MjBkNmNmLnBuZyIsImV4cCI6MTY2NTM5NDkwNiwiZW1haWwiOiI4NDEyOTQxODBAcXEuY29tIn0.kJoz90TOJsqayXFY-0zwQQr4p9u5-uogUoGkXk_0DIs");
        Map<String, Claim> claims = decodedJWT.getClaims();
        Claim userName = claims.get("user_name");
        String s = userName.asString();
        System.out.println(s);*/

        /*Map<String, String> payload = new HashMap<>();
        payload.put("id", "1215sadg0");
        String token = JWTUtil.generateToken(payload);
        System.out.println(token);*/

        DecodedJWT decodedJWT = JWTUtil.decodeToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEyMTVzYWRnMCIsImV4cCI6MTY2NTQ1Njk3M30.-HLF6yqr-KRhpepzwrmqOl61bczxppN6hCPscD6Z9uI");
        String id = decodedJWT.getClaim("id").asString();
        System.out.println("id");
    }
}
