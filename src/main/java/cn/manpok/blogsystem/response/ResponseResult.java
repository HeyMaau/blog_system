package cn.manpok.blogsystem.response;

public class ResponseResult {

    private boolean success;
    private int code;
    private String message;
    private Object data;

    public ResponseResult(ResponseState responseState) {
        this.code = responseState.getCode();
        this.success = responseState.isSuccess();
        this.message = responseState.getMessage();
    }

    /**
     * 操作成功返回
     *
     * @return
     */
    public static ResponseResult SUCCESS() {
        return new ResponseResult(ResponseState.SUCCESS);
    }

    /**
     * 操作成功返回
     *
     * @return
     */
    public static ResponseResult SUCCESS(ResponseState responseState) {
        return new ResponseResult(responseState);
    }

    /**
     * 操作成功返回
     *
     * @return
     */
    public static ResponseResult SUCCESS(String message) {
        ResponseResult responseResult = new ResponseResult(ResponseState.SUCCESS);
        responseResult.setMessage(message);
        return responseResult;
    }

    public static ResponseResult GET(ResponseState responseState) {
        return new ResponseResult(responseState);
    }

    /**
     * 操作失败返回
     *
     * @return
     */
    public static ResponseResult FAIL() {
        return new ResponseResult(ResponseState.FAIL);
    }

    /**
     * 操作失败返回
     *
     * @return
     */
    public static ResponseResult FAIL(String message) {
        ResponseResult responseResult = new ResponseResult(ResponseState.FAIL);
        responseResult.setMessage(message);
        return responseResult;
    }

    /**
     * 操作失败返回
     *
     * @return
     */
    public static ResponseResult FAIL(ResponseState responseState) {
        return new ResponseResult(responseState);
    }

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

    public Object getData() {
        return data;
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
