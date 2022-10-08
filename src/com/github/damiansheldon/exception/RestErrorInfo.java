package com.github.damiansheldon.exception;

public class RestErrorInfo {
    private Integer code;

    private String message;

    private String info;

    public RestErrorInfo(String message) {
        this(0, message, null);
    }

    public RestErrorInfo(Integer code, String message) {
        this(code, message, null);
    }

    public RestErrorInfo(Integer code, String message, String info) {
        this.code = code;
        this.message = message;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
