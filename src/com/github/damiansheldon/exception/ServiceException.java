package com.github.damiansheldon.exception;

import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class ServiceException extends NestedRuntimeException {

    private HttpStatus status;

    private HttpHeaders headers;

    private RestErrorInfo errorInfo;

    private ServiceException(Builder builder) {
        super(builder.errorInfo.getMessage(), builder.cause);
        this.errorInfo = builder.errorInfo;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public RestErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public static class Builder {
        private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        private HttpHeaders headers;
        private RestErrorInfo errorInfo;
        private Throwable cause;

        public Builder(RestErrorInfo errorInfo) {
            this.errorInfo = errorInfo;
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public ServiceException build() {
            return new ServiceException(this);
        }

    }

}
