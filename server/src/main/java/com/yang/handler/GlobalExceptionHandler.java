package com.yang.handler;

import com.yang.exception.OrderNumberDuplicateKeyException;
import com.yang.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理数据校验异常（ @NotBlank、@Length 等注解校验失败时抛出的异常）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 从异常中提取校验失败的详细信息
        BindingResult bindingResult = e.getBindingResult();
        // 获取第一个错误信息（也可以收集所有错误信息）
        FieldError fieldError = bindingResult.getFieldError();
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        // 使用项目中定义的 Result 统一返回格式
        return Result.error(errorMsg);
    }

    /**
     * 校验订单号是否重复
     *
     */
    @ExceptionHandler(OrderNumberDuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(OrderNumberDuplicateKeyException e) {
        String message = e.getMessage();
        return Result.error(message);
    }

    /**
     * 处理其他未捕获的异常（兜底处理）
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.info("错误日志:{}",e.getMessage());
        return Result.error(e.getMessage());
    }
}
