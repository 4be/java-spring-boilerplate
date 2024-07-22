package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreGetSavingAccountsRequest {

    @JsonProperty("!OPERATION")
    private String operation;

    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;

    @JsonProperty("CUSTOMER:EQ")
    private String customerEq;

}
