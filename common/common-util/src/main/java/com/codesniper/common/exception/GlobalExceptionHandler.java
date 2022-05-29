package com.codesniper.common.exception;


import com.codesniper.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 *
 * @author CodeSniper
 * @since 2022-05-30
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Boolean> error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }
}
