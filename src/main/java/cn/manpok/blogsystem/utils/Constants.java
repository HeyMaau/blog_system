package cn.manpok.blogsystem.utils;

/**
 * 各种常量
 */
public interface Constants {

    interface User {
        String DEFAULT_AVATAR = "www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png";
        String DEFAULT_STATE = "1";
        String FORBIDDEN_STATE = "0";
        String DEFAULT_SIGN = "My blog!";
        String ROLE_ADMIN = "role_admin";
        String ROLE_NORMAL = "role_normal";
        String KEY_CAPTCHA_TEXT = "key_captcha_text_";
        String KEY_VERIFY_CODE_TEXT = "key_verify_code_text_";
        String KEY_SEND_EMAIL_REQUEST_IP = "key_send_email_request_ip_";
        String KEY_SEND_EMIAL_ADDR = "key_send_emial_addr_";
        String KEY_USER_TOKEN = "key_user_token_";
        String KEY_TOKEN_COOKIE = "manpok_blog_system_token";
    }

    interface Setting {
        String ADMIN_ACCOUNT_INIT_STATE = "admin_account_init_state";
    }

    /**
     * 时间常量
     * 基本单位：秒
     */
    interface TimeValue {
        int SECOND = 1;
        int MIN = SECOND * 60;
        int MIN_10 = MIN * 10;
        int HOUR = MIN * 60;
        int HOUR_2 = HOUR * 2;
        int DAY = HOUR * 24;
        int MONTH = DAY * 30;
        int YEAR = DAY * 365;
    }
}
