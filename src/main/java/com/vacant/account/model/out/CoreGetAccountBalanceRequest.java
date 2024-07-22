package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreGetAccountBalanceRequest {

    @JsonProperty("!OPERATION")
    public String operation;

    @JsonProperty("!TRANSACTION_ID")
    public String transactionId;

    @JsonProperty("ACCOUNT.NO:EQ")
    public String accountNo;
}
