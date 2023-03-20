package org.phial.baas.manager.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;


@Slf4j
public class ApplicationExceptionHandler {

    private static volatile UnknownExceptionHandler UNKNOWN_EXCEPTION_HANDLER;

    public static Object handleAllException(Throwable t) {
        ResponseEntity resp = null;
        if (t instanceof MethodArgumentTypeMismatchException) {
            String name = ((MethodArgumentTypeMismatchException) t).getName();
            resp = ResponseEntity.of(Optional.of("参数错误:" + name));
        } else if (t instanceof BindException) {
            FieldError error = ((BindException) t).getFieldError();
            String fieldName = error.getField();
            resp = ResponseEntity.of(Optional.of("参数错误:" + fieldName));
        } else if (t instanceof MissingServletRequestParameterException) {
            String paramName = ((MissingServletRequestParameterException) t).getParameterName();
            resp = ResponseEntity.of(Optional.of("缺少参数" + paramName));
        } else if (t instanceof RuntimeException) {
            resp = handleSCFServiceException(t);
        } else {
            resp = handleUnknownException(t);
        }
        if (resp == null) return ResponseEntity.ok(t);
        return resp;
    }

    private static ResponseEntity handleSCFServiceException(Throwable t) {
        String message = t.getMessage();
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(message)) {
            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);
                if (c == '\r' || c == '\n') break;
                sb.append(c);
            }
            String head = sb.toString();
            if (!StringUtils.isBlank(head)) {
                String msg = head.substring(head.indexOf(":") + 1);
                if (StringUtils.isNoneBlank(msg)) msg = msg.trim();
                else msg = "unknown exception";
                return handleInternalServiceException(new RuntimeException(msg));
            }
        }
        return handleUnknownException(t);
    }

    private static ResponseEntity handleInternalServiceException(Throwable t) {
        return ResponseEntity.ok(t);
    }

    private static ResponseEntity handleUnknownException(Throwable t) {
        log.error("Error Detected: {} -> {}", t.getCause().getClass().getName(), t.getCause().getMessage(), t);
        try {
            if (UNKNOWN_EXCEPTION_HANDLER != null) return UNKNOWN_EXCEPTION_HANDLER.handleException(t);
        } catch (Throwable e) {
            return ResponseEntity.ok(e);
        }
        return ResponseEntity.ok(t);
    }

    /**
     * 未知异常处理器
     *
     * @param handler
     */
    public static void installUnknownExceptionHandler(UnknownExceptionHandler handler) {
        UNKNOWN_EXCEPTION_HANDLER = handler;
    }

    /**
     * 自定义的未知异常处理器
     */
    public interface UnknownExceptionHandler {
        ResponseEntity handleException(Throwable t);
    }
}
