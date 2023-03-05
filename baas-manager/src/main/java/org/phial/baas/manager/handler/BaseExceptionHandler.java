package org.phial.baas.manager.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {
    /**
     * Request attribute key to access HandlerMethod object
     */
    public static final String SERVLET_APPLICATION_CONTEXT_NAME = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher";

    /**
     * Assert异常
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public Object illegalStateException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("illegalStateException error! method:{} uri:{}", request.getMethod(), request.getRequestURI(), e);
        return ResponseEntity.ok(e);
    }

    /**
     * Controller上一层相关异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            // BindException.class,
            // MethodArgumentNotValidException.class
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    public Object handleServletException(Exception e) {
        log.error("handleServletException error:{} ", e.getMessage(), e);
        return ResponseEntity.ok(e);
    }


    /**
     * 参数绑定异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BindException.class)
    public Object handleBindException(BindException e) {
        log.error("参数绑定校验异常", e);
        return ResponseEntity.ok(e);
    }

    /**
     * 参数校验异常，将校验失败的所有异常组合成一条错误信息
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Object handleValidException(MethodArgumentNotValidException e) {
        log.error("参数绑定校验异常", e);
        return ResponseEntity.ok(e);
    }

    /**
     * 服务运行中出现异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = RuntimeException.class)
    public Object handleRuntimeException(RuntimeException e) {
        log.error("服务运行异常", e);
        return ResponseEntity.ok(e);
    }


    @ExceptionHandler(Exception.class)
    public Object allExceptionHandler(Exception e) {
        log.error("服务运行异常", e);
        return ResponseEntity.ok(e);
    }


    /**
     * 抓取所有的错误,未定义异常
     */
    @ExceptionHandler(Throwable.class)
    private Object handleThrowableException(Throwable t, HttpServletRequest request) {
        /**
         * If this.class is annotated by @RestController
         */
        return ApplicationExceptionHandler.handleAllException(t);
    }

    private boolean isRestController() {
        Class<?> c = this.getClass();
        while (c != Object.class) {
            if (c.isAnnotationPresent(RestController.class)) return true;
            c = c.getSuperclass();
        }
        return false;
    }

    /**
     * 获取代码报错详细位置信息
     */
    public String getExceptionDetail(Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(e.getClass()).append(System.getProperty("line.separator"));
        stringBuilder.append(e.getLocalizedMessage()).append(System.getProperty("line.separator"));
        StackTraceElement[] arr = e.getStackTrace();
        for (StackTraceElement stackTraceElement : arr) {
            stringBuilder.append(stackTraceElement.toString()).append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }
}
