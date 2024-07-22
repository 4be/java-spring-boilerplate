package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoreGetDetailAndBalanceMultiAccountRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!BRANCH")
    private String branchCode;
    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;
    @JsonProperty("ACCOUNT:EQ")
    private String accountNumber;
}
