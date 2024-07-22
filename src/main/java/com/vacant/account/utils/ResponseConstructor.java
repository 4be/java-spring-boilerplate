package com.vacant.account.utils;

import com.vacant.account.constants.CoreConstant;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.exceptions.BusinessException;
import com.vacant.account.model.out.CoreGenericResponse;
import com.vacant.account.model.out.CoreResponse;
import com.vacant.account.model.out.CoreWriteOperationResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class ResponseConstructor {

    private static final String SERVICE_CODE = "21";

    public String constructResponseCode(ResponseCode responseCode) {
        return String.format("%d%s%s", responseCode.getHttpStatus().value(), SERVICE_CODE, responseCode.getCode());
    }

    public <T> CoreGenericResponse<T> validateAndMapCoreResponse(CoreResponse response, Class<T> type) {
        log.info("[ms-core] response : {}", JsonMapper.toString(response));

        if (ObjectUtils.isEmpty(response)) {
            log.error("[ms-core] response : empty body");
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (!response.getResponseMessage().equalsIgnoreCase(CoreConstant.CORE_SUCCESS_RESPONSE)) {
            if (response.getResponseMessage().equalsIgnoreCase(CoreConstant.CORE_TIMEOUT_RESPONSE)) {
                log.error("[ms-core] response : timeout");
                throw new BusinessException(ResponseCode.TIMEOUT);
            }

            if (!ObjectUtils.isEmpty(response.getError())) {
                var noRecordFound = response.getError().values().stream().anyMatch(error -> error.equalsIgnoreCase(CoreConstant.NO_RECORD_FOUND_ERROR));

                if (noRecordFound) {
                    return (CoreGenericResponse<T>) CoreGenericResponse.builder()
                            .responseMessage(CoreConstant.CORE_SUCCESS_RESPONSE)
                            .data(null)
                            .build();
                }
            }

            log.error("[ms-core] responseMessage : {}", response.getResponseMessage());
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (ObjectUtils.isEmpty(response.getData())) {
            log.error("[ms-core] response : data is null");
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        return (CoreGenericResponse<T>) CoreGenericResponse.builder()
                .responseMessage(response.getResponseMessage())
                .data(response.getData().values().stream().map(x -> JsonMapper.objectToOther(x, type)).collect(Collectors.toList()))
                .correlationId(response.getCorrelationId())
                .build();
    }

    public void validateCoreWriteResponse(CoreWriteOperationResponse response) {
        log.info("[ms-core] response : {}", JsonMapper.toString(response));

        if (ObjectUtils.isEmpty(response)) {
            log.error("[ms-core] response : empty body");
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (!response.getResponseCode().equalsIgnoreCase(CoreConstant.CORE_SUCCESS_RESPONSE_CODE)) {
            if (response.getResponseCode().equalsIgnoreCase(CoreConstant.CORE_TIMEOUT_RESPONSE_CODE)) {
                log.error("[ms-core] response : timeout");
                throw new BusinessException(ResponseCode.TIMEOUT);
            }

            if (response.getResponseCode().equalsIgnoreCase(CoreConstant.CORE_DUPLICATE_REFERENCE_NO_RESPONSE_CODE)) {
                log.error("[ms-core] response : Duplicate partnerReferenceNo\t");
                throw new BusinessException(ResponseCode.DUPLICATE_PARTNER_REFERENCE_NO);
            }

            if (response.getResponseCode().equalsIgnoreCase(CoreConstant.CORE_TRANSACTION_DUPLICATE_RESPONSE_CODE)) {
                log.error("[ms-core] response : Transaction duplicate\t");
                throw new BusinessException(ResponseCode.TRANSACTION_DUPLICATE);
            }

            log.error("[ms-core] responseCode : {}", response.getResponseCode());
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (ObjectUtils.isEmpty(response.getData())) {
            log.error("[ms-core] response : data is null");
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    public <T> ResponseEntity<T> constructSnapApiResponse(T body) {
        var responseHeaders = new HttpHeaders();
        responseHeaders.set("X-TIMESTAMP", LocalDateTime.now().atOffset(DateUtil.ZONE_OFFSET_WIB).format(
                DateTimeFormatter.ofPattern(DateUtil.ISO_DATE_TIME_WITHOUT_NANO_SECOND)));

        return ResponseEntity.ok().headers(responseHeaders).body(body);
    }

    public Boolean isSuccessful(String responseCode) {
        return responseCode.startsWith(String.valueOf(HttpStatus.OK.value()));
    }
}
