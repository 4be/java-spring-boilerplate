package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CoreInquiryLockAmountRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;
    @JsonProperty("ACCOUNT.NUMBER:EQ")
    private String accountNumber;
}
