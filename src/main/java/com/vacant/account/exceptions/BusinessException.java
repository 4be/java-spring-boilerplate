package com.vacant.account.exceptions;

import com.vacant.account.enums.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BusinessException extends RuntimeException {

    private final ResponseCode responseCode;
}
