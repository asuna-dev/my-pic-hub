package org.zepe.pichub.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zepe.pichub.common.Response;

/**
 * @author zzpus
 * @datetime 2025/4/27 22:04
 * @description
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Response<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return Response.failed(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public Response<?> runtimeExceptionHandler(NotLoginException e) {
        log.error("NotLoginException", e);
        return Response.failed(ErrorCode.NOT_LOGIN_ERROR);
    }

    @ExceptionHandler(NotPermissionException.class)
    public Response<?> runtimeExceptionHandler(NotPermissionException e) {
        log.error("NotPermissionException", e);
        return Response.failed(ErrorCode.NO_AUTH_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public Response<?> runtimeExceptionHandler(Exception e) {
        log.error("Exception", e);
        return Response.failed(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }

}

