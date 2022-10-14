package cn.manpok.blogsystem.response;

public enum ResponseState {

    SUCCESS(true, 20000, "操作成功"),
    REGISTER_SUCCESS(true, 20001, "注册成功"),
    USER_FORBIDDEN(false, 20002, "用户已禁止"),
    FAIL(false, 40000, "操作失败"),
    NOT_LOGIN(false, 40001, "用户未登录"),
    PERMISSION_DENIED(false, 40002, "权限不足"),
    NOT_FOUND(false, 40003, "资源不存在"),
    REQUEST_TIMEOUT(false, 40004, "系统繁忙"),
    SERVER_ERROR(false, 40005, "服务器异常"),
    VERIFY_CODE_ERROR(false, 40006, "邮件验证码错误"),
    EMAIL_NOT_CORRECT(false, 40007, "邮箱不正确");

    ResponseState(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 状态码
     */
    private int code;
    /**
     * 信息
     */
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
