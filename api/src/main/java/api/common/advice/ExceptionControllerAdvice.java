package api.common.advice;

import api.response.ApiErrorResponse;
import exception.CommonException;
import exception.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

import static exception.Status.*;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(CommonException.class)
    ResponseEntity<ApiErrorResponse> handleCommonException(CommonException exception){
        return new ResponseEntity<>(
                ApiErrorResponse.fromCommonException(exception)
                , exception.getHttpStatus());
    }

    @ExceptionHandler(IOException.class)
    ResponseEntity<ApiErrorResponse> handleIOException(){
        return new ResponseEntity<>(
                ApiErrorResponse.fromIOException()
                , UPLOAD_FAIL.getHttpStatus());
    }
}
