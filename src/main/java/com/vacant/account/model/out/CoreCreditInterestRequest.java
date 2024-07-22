package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreCreditInterestRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("CHANNEL.ID:1:1")
    private String channelId;
    @JsonProperty("PARTNER.ID:1:1")
    private String partnerId;
    @JsonProperty("!BRANCH")
    private String branch;
    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;
    @JsonProperty("INTEREST.DAY.BASIS:1:1")
    private String interestDayBasis;
    @JsonProperty("TAX.KEY:1:1")
    private String taxKey;
    @JsonProperty("CR.BALANCE.TYPE:1:1")
    private String crBalanceType;
    @JsonProperty("CR.CALCUL.TYPE:1:1")
    private String crCalculType;
    @JsonProperty("CR.MINIMUM.BAL:1:1")
    private String crMinimumBalance;
    @Nullable
    @JsonProperty("CR.INT.RATE:1:1")
    private String crIntRate;
}
