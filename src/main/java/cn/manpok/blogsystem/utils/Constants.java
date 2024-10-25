package cn.manpok.blogsystem.utils;

/**
 * 各种常量
 */
public interface Constants {

    String STATE_NORMAL = "1";
    String STATE_FORBIDDEN = "0";
    String KEY_COMMIT_RECORD = "key_commit_record_";
    String VALUE_TRUE = "true";
    String KEY_BLOCK_IP = "key_block_ip_";
    String KEY_IP_ACCESS_COUNT = "key_ip_access_count_";
    int ACCESS_COUNT_LIMIT = 100;

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
        String KEY_TOKEN_KEY = "token_key";
        String KEY_HEADER_AUTHORIZATION = "authorization";
        String KEY_HEADER_X_REAL_IP = "X-Real-IP";
        String KEY_ADMIN_INFO_CACHE = "key_admin_info_cache";
    }

    interface Setting {
        String ADMIN_ACCOUNT_INIT_STATE = "admin_account_init_state";
        String WEB_SIZE_INFO_TITLE = "web_size_info_title";
        String WEB_SIZE_INFO_SEO_KEYWORDS = "web_size_info_seo_keywords";
        String WEB_SIZE_INFO_SEO_DESCRIPTION = "web_size_info_seo_description";
        String WEB_SIZE_INFO_VIEW_COUNT = "web_size_info_view_count";
    }

    interface Image {
        String TYPE_NORMAL_IMAGE = "0";
        String TYPE_ARTICLE_IMAGE = "1";
        String DATE_FORMAT = "yyyy_MM_dd";
        String CONTENT_TYPE_PREFIX = "image/";
        String TYPE_JPG = "jpg";
        String TYPE_GIF = "gif";
        String TYPE_PNG = "png";
        String TYPE_JPEG = "jpeg";
        String TYPE_WEBP = "webp";
        String TYPE_JPG_WITH_PREFIX = CONTENT_TYPE_PREFIX + TYPE_JPG;
        String TYPE_GIF_WITH_PREFIX = CONTENT_TYPE_PREFIX + TYPE_GIF;
        String TYPE_PNG_WITH_PREFIX = CONTENT_TYPE_PREFIX + TYPE_PNG;
        String TYPE_JPEG_WITH_PREFIX = CONTENT_TYPE_PREFIX + TYPE_JPEG;
        String TYPE_WEBP_WITH_PREFIX = CONTENT_TYPE_PREFIX + TYPE_WEBP;
    }

    /**
     * 时间常量
     * 基本单位：秒
     */
    interface TimeValue {
        int MESC_100 = 100;
        int SECOND = 1;
        int SECOND_5 = SECOND * 5;
        int SECOND_30 = SECOND * 30;
        int MIN = SECOND * 60;
        int MIN_2 = MIN * 2;
        int MIN_5 = MIN * 5;
        int MIN_10 = MIN * 10;
        int HOUR = MIN * 60;
        int HOUR_2 = HOUR * 2;
        int HOUR_4 = HOUR * 4;
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
        String KEY_ARTICLE_CACHE = "key_article_cache_";
        String KEY_VIEW_COUNT_CACHE = "key_view_count_cache_";
        String KEY_ARTICLE_LIST_CACHE = "key_article_list_cache_";
    }

    /**
     * 分类相关常量
     */
    interface Category {
        String KEY_CATEGORY_LIST_CACHE = "key_category_list_cache";
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
        String TYPE_ARTICLE = "0";
        String TYPE_THINKING = "1";
        String STATE_TOP = "2";
        String KEY_ARTICLE_COMMENTS_CACHE = "key_article_comments_cache_";
        String KEY_THINKING_COMMENTS_CACHE = "key_thinking_comments_cache_";
    }

    /**
     * 想法相关常量
     */
    interface Thinking {
        String KEY_THINKINGS_CACHE = "key_thinkings_cache";
        String KEY_THINKING_CACHE = "key_thinking_cache_";
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
        int HIGHLIGHT_FRAG_SIZE = 200;
    }

    /**
     * 埋点相关常量
     */
    interface Statistics {
        String KEY_STATISTICS_CACHE = "key_statistics_page_%s_component_%s_event_%s_date_%s_from_%s";
        String KEY_STATISTICS_CACHE_PREFIX = "key_statistics_page_";
        String PATTERN = "key_statistics_page_(.*)_component_(.*)_event_(.*)_date_(.*)_from_(.*)";
        String KEY_RESPONSE_FIELD_TOTAL_VISIT = "totalVisit";
        String CLIENT_NAME_DESKTOP = "desktop";
        String CLIENT_NAME_MOBILE = "mobile";
    }

    interface Audio {
        String KEY_AUDIO_LIST_CACHE = "key_audio_list_cache";
    }

    interface FriendLink {
        String KEY_FRIEND_LINK_LIST_CACHE = "key_friend_link_list_cache";
    }

    /**
     * 手机客户端相关常量
     */
    interface APP {
        String APP_DOWNLOAD_LINK = "http://manpok.top/app/download/";
        String KEY_QR_CODE_STATE = "key_qr_code_state_";
        String STATE_QR_CODE_TRUE = "true";
        String STATE_QR_CODE_FALSE = "false";
        String STATE_QR_CODE_ENQUIRE = "enquire";
        String KEY_LATEST_APP_INFO = "key_latest_app_info";
    }

    interface Log {
        String BEFORE_LOG = "Request method name: %s, Request IP: %s";
    }
}
