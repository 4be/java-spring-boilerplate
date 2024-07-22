package com.vacant.account.model.out;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreGetAccountStatementRequest {

    @JsonProperty("!OPERATION")
    private String operation;

    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;

    @JsonProperty("ACCOUNT:EQ")
    private String accountNo;

    @JsonProperty("BOOKING.DATE:RG")
    private String bookingDate;

}
