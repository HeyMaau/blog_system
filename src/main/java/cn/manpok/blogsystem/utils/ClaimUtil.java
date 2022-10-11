package cn.manpok.blogsystem.utils;

import cn.manpok.blogsystem.pojo.BlogUser;
import com.auth0.jwt.interfaces.Claim;

import java.util.HashMap;
import java.util.Map;

/**
 * token的claim与bean互转工具类
 */
public class ClaimUtil {

    public static Map<String, String> userBean2Claims(BlogUser blogUser) {
        Map<String, String> payload = new HashMap<>();
        payload.put("id", blogUser.getId());
        payload.put("user_name", blogUser.getUserName());
        payload.put("roles", blogUser.getRoles());
        payload.put("avatar", blogUser.getAvatar());
        payload.put("email", blogUser.getEmail());
        payload.put("sign", blogUser.getSign());
        return payload;
    }

    public static BlogUser Claims2UserBean(Map<String, Claim> claims) {
        String id = claims.get("id").asString();
        String userName = claims.get("user_name").asString();
        String roles = claims.get("roles").asString();
        String avatar = claims.get("avatar").asString();
        String email = claims.get("email").asString();
        String sign = claims.get("sign").asString();
        BlogUser blogUser = new BlogUser();
        blogUser.setId(id);
        blogUser.setUserName(userName);
        blogUser.setRoles(roles);
        blogUser.setAvatar(avatar);
        blogUser.setEmail(email);
        blogUser.setSign(sign);
        return blogUser;
    }
}
