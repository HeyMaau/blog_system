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
    }

    interface Setting {
        String ADMIN_ACCOUNT_INIT_STATE = "admin_account_init_state";
    }
}
