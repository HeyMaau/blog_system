package cn.manpok.blogsystem.response;

public enum ResponseState {

    SUCCESS(true, 20000, "操作成功"),
    FAIL(false, 40000, "操作失败"),
    REGISTER_SUCCESS(true, 20001, "注册成功");

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
