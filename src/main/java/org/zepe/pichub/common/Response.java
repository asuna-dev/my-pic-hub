package org.zepe.pichub.common;

import lombok.Data;
import org.zepe.pichub.exception.ErrorCode;

import java.io.Serializable;

/**
 * @author zzpus
 * @datetime 2025/4/27 21:52
 * @description
 */

@Data
public class Response<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private Response(T data, int code, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    static public <T> Response<T> success(T data) {
        return new Response<>(data, ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage());
    }

    static public <T> Response<T> success(T data, String message) {
        return new Response<>(data, ErrorCode.SUCCESS.getCode(), message);
    }

    static public <T> Response<T> failed(ErrorCode errorCode) {
        return new Response<>(null, errorCode.getCode(), errorCode.getMessage());
    }

    static public <T> Response<T> failed(ErrorCode errorCode, String message) {
        return new Response<>(null, errorCode.getCode(), errorCode.getMessage());
    }

    static public <T> Response<T> failed(int code, String message) {
        return new Response<>(null, code, message);
    }

}

