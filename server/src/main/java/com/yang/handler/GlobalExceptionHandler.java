package com.yang.handler;

import com.yang.result.Result;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理数据校验异常（例如 @NotBlank、@Length 等注解校验失败时抛出的异常）
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
     * 处理其他未捕获的异常（兜底处理）
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 实际项目中可记录日志（e.printStackTrace() 仅为示例）
        e.printStackTrace();
        return Result.error("系统异常，请联系管理员");
    }
}
