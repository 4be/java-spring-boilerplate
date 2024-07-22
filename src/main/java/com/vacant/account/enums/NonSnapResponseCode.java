package com.vacant.account.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NonSnapResponseCode {

    SUCCESS("00","Success",HttpStatus.OK),
    DATA_NOT_FOUND("04","Data Not Found",HttpStatus.OK),
    INTERNAL_SERVER_ERROR("01","Internal Server Error",HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVER_ERROR("02","External Server Error",HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("06","Bad Request",HttpStatus.BAD_REQUEST),
    TIMEOUT("68","Timeout",HttpStatus.OK);
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    NonSnapResponseCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
