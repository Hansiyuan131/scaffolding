package com.yuanstack.sca.service.system.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @description: 异常处理
 * @author: hansiyuan
 * @date: 2022/6/28 2:45 PM
 */
@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler {

    /**
     * 自定义注解异常拦截
     */
    @ExceptionHandler({BindException.class, ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public Object handleMethodArgumentNotValidException(Exception e, HttpServletRequest request) {

        // 错误信息
        StringBuilder sb = new StringBuilder("参数校验失败：");
        // 错误信息map
        Map<String, String> error = new HashMap<>();

        String msg = "";
        String firstError = null;
        int i = 0;
        if (!(e instanceof BindException) && !(e instanceof MethodArgumentNotValidException)) {
            for (ConstraintViolation cv : ((ConstraintViolationException) e).getConstraintViolations()) {
                msg = cv.getMessage();

                if (i++ == 0) {
                    firstError = msg;
                }

                sb.append(msg).append("；");

                Iterator<Path.Node> it = cv.getPropertyPath().iterator();
                Path.Node last = null;
                while (it.hasNext()) {
                    last = (Path.Node) it.next();
                }
                error.put(last != null ? last.getName() : "", msg);
            }
        } else {
            List<ObjectError> allErrors = null;
            if (e instanceof BindException) {
                allErrors = ((BindException) e).getAllErrors();
            } else {
                allErrors = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
            }

            // 拼接错误信息
            for (ObjectError oe : allErrors) {
                msg = oe.getDefaultMessage();

                if (i++ == 0) {
                    firstError = msg;
                }

                sb.append(msg).append("；");
                if (oe instanceof FieldError) {
                    error.put(((FieldError) oe).getField(), msg);
                } else {
                    error.put(oe.getObjectName(), msg);
                }
            }
        }
        return null;

//        LogUtils.error(log, "请求：" + request.getRequestURI() + "发生异常：", e, sb, error);
//        return new ServiceResult(ErrorEnum.E_SYS_PARAM, firstError);
    }
}
