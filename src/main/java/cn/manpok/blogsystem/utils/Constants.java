package cn.manpok.blogsystem.utils;

/**
 * 各种常量
 */
public interface Constants {

    String STATE_NORMAL = "1";
    String STATE_FORBIDDEN = "0";

    interface User {
        String DEFAULT_AVATAR = "www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png";
        String DEFAULT_SIGN = "My blog!";
        String ROLE_ADMIN = "role_admin";
        String ROLE_NORMAL = "role_normal";
        String KEY_CAPTCHA_TEXT = "key_captcha_text_";
        String KEY_VERIFY_CODE_TEXT = "key_verify_code_text_";
        String KEY_SEND_EMAIL_REQUEST_IP = "key_send_email_request_ip_";
        String KEY_SEND_EMIAL_ADDR = "key_send_email_addr_";
        String KEY_USER_TOKEN = "key_user_token_";
        String KEY_TOKEN_COOKIE = "manpok_blog_system_token";
        String KEY_FORGET_PASSWORD_TOKEN_COOKIE = "key_forget_password_token_cookie";
    }

    interface Setting {
        String ADMIN_ACCOUNT_INIT_STATE = "admin_account_init_state";
        String WEB_SIZE_INFO_TITLE = "web_size_info_title";
        String WEB_SIZE_INFO_SEO_KEYWORDS = "web_size_info_seo_keywords";
        String WEB_SIZE_INFO_SEO_DESCRIPTION = "web_size_info_seo_description";
        String WEB_SIZE_INFO_VIEW_COUNT = "web_size_info_view_count";
    }

    interface Image {
        String DATE_FORMAT = "yyyy_MM_dd";
        String CONTENT_TYPE_PREFIX = "image/";
        String TYPE_JPG = "jpg";
        String TYPE_GIF = "gif";
        String TYPE_PNG = "png";
        String TYPE_JPEG = "jpeg";
        String TYPE_JPG_WITH_PREFIX = CONTENT_TYPE_PREFIX + TYPE_JPG;
        String TYPE_GIF_WITH_PREFIX = CONTENT_TYPE_PREFIX + TYPE_GIF;
        String TYPE_PNG_WITH_PREFIX = CONTENT_TYPE_PREFIX + TYPE_PNG;
        String TYPE_JPEG_WITH_PREFIX = CONTENT_TYPE_PREFIX + TYPE_JPEG;
    }

    /**
     * 时间常量
     * 基本单位：秒
     */
    interface TimeValue {
        int SECOND = 1;
        int MIN = SECOND * 60;
        int MIN_2 = MIN * 2;
        int MIN_5 = MIN * 5;
        int MIN_10 = MIN * 10;
        int HOUR = MIN * 60;
        int HOUR_2 = HOUR * 2;
        int DAY = HOUR * 24;
        int MONTH = DAY * 30;
        int YEAR = DAY * 365;
    }

    /**
     * 有关分页查询的常量
     */
    interface Page {
        int DEFAULT_PAGE = 1;
        int DEFAULT_SIZE = 5;
        int TOP_ARTICLES_SIZE = 30;
    }

    /**
     * 管理-文章相关常量
     */
    interface Article {
        String STATE_DELETE = "0";
        String STATE_DRAFT = "1";
        String STATE_PUBLISH = "2";
        String STATE_TOP = "3";
        int INITIAL_VIEW_COUNT = 1;
        String TYPE_RICH_TEXT = "0";
        String TYPE_MARKDOWN = "1";
    }

    /**
     * 标签相关常量
     */
    interface Label {
        String LABEL_SEPARATOR = "-";
        int INITIAL_COUNT = 1;
    }

    /**
     * 评论相关常量
     */
    interface Comment {
        String STATE_TOP = "2";
    }

    /**
     * 搜索相关常量
     */
    interface Search {
        int SORT_CREATE_TIME_ASC = 1;
        int SORT_CREATE_TIME_DESC = 2;
        int SORT_VIEW_COUNT_ASC = 3;
        int SORT_VIEW_COUNT_DESC = 4;
        String DEFAULT_FIELD = "search_item";
        String FIELD_VIEW_COUNT = "view_count";
        String FIELD_CREATE_TIME = "create_time";
        String FIELD_CATEGORY_ID = "category_id";
        String FIELD_TITLE = "title";
        String FIELD_CONTENT = "content";
        String FIELD_ID = "id";
        String HIGHLIGHT_PRE = "<font color='red'>";
        String HIGHLIGHT_POST = "</font>";
        int HIGHLIGHT_FRAG_SIZE = 500;
    }
}
