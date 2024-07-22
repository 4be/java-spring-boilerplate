package com.vacant.account.exceptions;

import com.vacant.account.enums.ResponseCode;
import com.vacant.account.model.in.BaseResponse;
import com.vacant.account.model.in.GenericResponse;
import com.vacant.account.utils.ResponseConstructor;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<?> businessException(BusinessException exception) {
        log.error("businessException : {}", exception.getResponseCode().getMessage());

        var responseCode = exception.getResponseCode();

        return ResponseEntity.status(responseCode.getHttpStatus().value())
                .body(BaseResponse.builder()
                        .responseCode(ResponseConstructor.constructResponseCode(responseCode))
                        .responseMessage(responseCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = NonSnapException.class)
    public ResponseEntity<?> nonSnapException(NonSnapException exception) {
        log.error("nonSnapException : {}", exception.getResponseMessage());

        return ResponseEntity.status(exception.getHttpStatusCode())
                .body(GenericResponse.builder()
                        .responseCode(exception.getResponseCode())
                        .responseMessage(exception.getResponseMessage())
                        .partnerReferenceNo(MDC.get("partnerReferenceNo"))
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(String.format("error field %s : %s", fieldError.getField(), fieldError.getDefaultMessage()));
        }

        log.error("parameter errors : {}", errors);

        var responseCode = ResponseCode.BAD_REQUEST;
        return ResponseEntity.status(responseCode.getHttpStatus().value())
                .body(BaseResponse.builder()
                        .responseCode(ResponseConstructor.constructResponseCode(responseCode))
                        .responseMessage(responseCode.getMessage())
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("missing request parameter for {}", e.getParameterName());

        var responseCode = ResponseCode.BAD_REQUEST;
        return ResponseEntity.status(responseCode.getHttpStatus().value())
                .body(BaseResponse.builder()
                        .responseCode(ResponseConstructor.constructResponseCode(responseCode))
                        .responseMessage(responseCode.getMessage())
                        .build());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> missingRequestHeaderException(MissingRequestHeaderException e) {
        log.error("missing request header for {}", e.getHeaderName());

        var responseCode = ResponseCode.BAD_REQUEST;
        return ResponseEntity.status(responseCode.getHttpStatus().value())
                .body(BaseResponse.builder()
                        .responseCode(ResponseConstructor.constructResponseCode(responseCode))
                        .responseMessage(responseCode.getMessage())
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException e) {
        log.error("constraintViolationException : {}", e.getMessage());

        var responseCode = ResponseCode.BAD_REQUEST;
        return ResponseEntity.status(responseCode.getHttpStatus().value())
                .body(BaseResponse.builder()
                        .responseCode(ResponseConstructor.constructResponseCode(responseCode))
                        .responseMessage(responseCode.getMessage())
                        .build());
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<?> socketTimeoutException(SocketTimeoutException e) {
        log.error("socketTimeoutException : {}", e.getMessage());

        var responseCode = ResponseCode.TIMEOUT;
        return ResponseEntity.status(responseCode.getHttpStatus().value())
                .body(BaseResponse.builder()
                        .responseCode(ResponseConstructor.constructResponseCode(responseCode))
                        .responseMessage(responseCode.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e) {
        log.error("exception : {}", e.getMessage());
        e.printStackTrace();
        var responseCode = ResponseCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(responseCode.getHttpStatus().value())
                .body(BaseResponse.builder()
                        .responseCode(ResponseConstructor.constructResponseCode(responseCode))
                        .responseMessage(responseCode.getMessage())
                        .build());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> feignException(FeignException e) {
        log.error("feignException : {}", e.getMessage());

        var responseCode = ResponseCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(responseCode.getHttpStatus().value())
                .body(BaseResponse.builder()
                        .responseCode(ResponseConstructor.constructResponseCode(responseCode))
                        .responseMessage(responseCode.getMessage())
                        .build());
    }
}
