package com.vacant.account.feignclients.decoders;

import com.vacant.account.enums.ResponseCode;
import com.vacant.account.exceptions.BusinessException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoreFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.info("CORE ERROR RESPONSE: {}", response);
        if (response.status() == HttpStatus.REQUEST_TIMEOUT.value() || response.status() == HttpStatus.GATEWAY_TIMEOUT.value()) {
            return new BusinessException(ResponseCode.TIMEOUT);
        } else if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new BusinessException(ResponseCode.DATA_NOT_FOUND);
        } else {
            return new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
