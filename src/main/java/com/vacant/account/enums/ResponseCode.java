package com.vacant.account.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    SUCCESS("00", "Successful", HttpStatus.OK),
    BAD_REQUEST("00", "Bad Request", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_TYPE("01", "Invalid Product", HttpStatus.BAD_REQUEST),
    DATA_NOT_FOUND("00", "Data not found", HttpStatus.NOT_FOUND),
    TRANSACTION_NOT_FOUND("01", "Transaction not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("02", "Product not found", HttpStatus.NOT_FOUND),
    INVALID_ACCOUNT("11", "Invalid Account", HttpStatus.NOT_FOUND),
    INVALID_CUSTOMER("11", "Invalid Customer", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("01", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVER_ERROR("02", "External Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    PARTNER_PRODUCT_NOT_EXISTS("03", "Invalid Product", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_PARTNER_REFERENCE_NO("01", "Duplicate partnerReferenceNo", HttpStatus.CONFLICT),
    TIMEOUT("00", "Timeout", HttpStatus.GATEWAY_TIMEOUT),
    TRANSACTION_DUPLICATE("01","Transaction duplicate", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ResponseCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
