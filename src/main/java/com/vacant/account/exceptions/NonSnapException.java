package com.vacant.account.exceptions;

import com.vacant.account.enums.NonSnapResponseCode;
import com.vacant.account.enums.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NonSnapException extends RuntimeException {
    private final int httpStatusCode;
    private final String responseCode;
    private final String responseMessage;

    public NonSnapException(String responseCode, String responseMessage) {
        this.httpStatusCode = 500;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public NonSnapException(ResponseCode rc) {
        this.httpStatusCode = rc.getHttpStatus().value();
        this.responseCode = rc.getCode();
        this.responseMessage = rc.getMessage();
    }
    public NonSnapException(NonSnapResponseCode rc) {
        this.httpStatusCode = rc.getHttpStatus().value();
        this.responseCode = rc.getCode();
        this.responseMessage = rc.getMessage();
    }
}
