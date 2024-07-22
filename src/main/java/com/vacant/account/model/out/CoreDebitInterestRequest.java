package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoreDebitInterestRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("CHANNEL.ID:1:1")
    private String channelId;
    @JsonProperty("PARTNER.ID:1:1")
    private String partnerId;
    @JsonProperty("!BRANCH")
    private String branchCode;
    @JsonProperty("!TRANSACTION_ID")
    private String accountNumber;
    @JsonProperty("CHARGE.KEY:1:1")
    private String chargesKey;
    @JsonProperty("INTEREST.DAY.BASIS:1:1")
    private String interestDayBasis;
    @JsonProperty("TAX.KEY:1:1")
    private String taxKey;
    @JsonProperty("DR.BALANCE.TYPE:1:1")
    private String drBalanceType;
    @JsonProperty("DR.CALCUL.TYPE:1:1")
    private String drCalculType;
    @JsonProperty("DR.INT.RATE:1:1")
    private String drIntRate1;
    @JsonProperty("DR.INT.RATE:2:1")
    private String drIntRate2;
    @JsonProperty("DR.INT.RATE:3:1")
    private String drIntRate3;
    @JsonProperty("DR.INT.RATE:4:1")
    private String drIntRate4;
    @JsonProperty("DR.INT.RATE:5:1")
    private String drIntRate5;
    @JsonProperty("DR.INT.RATE:6:1")
    private String drIntRate6;
    @JsonProperty("DR.LIMIT.AMT:1:1")
    private String drLimitAmount1;
    @JsonProperty("DR.LIMIT.AMT:2:1")
    private String drLimitAmount2;
    @JsonProperty("DR.LIMIT.AMT:3:1")
    private String drLimitAmount3;
    @JsonProperty("DR.LIMIT.AMT:4:1")
    private String drLimitAmount4;
    @JsonProperty("DR.LIMIT.AMT:5:1")
    private String drLimitAmount5;
    @JsonProperty("DR.LIMIT.AMT:6:1")
    private String drLimitAmount6;
}
