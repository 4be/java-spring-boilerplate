package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class CoreWaiveChargeRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String option;
    @JsonProperty("CHANNEL.ID:1:1")
    private String channelId;
    @JsonProperty("PARTNER.ID:1:1")
    private String partnerId;
    @JsonProperty("!BRANCH")
    private String branch;
    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;
    @JsonProperty("SM.WAIVE.CHARGE:1:1")
    private String waiveCharge;
    @JsonProperty("UMG.UNIQUE.ID:1:1")
    private String umgUniqueId;
    @JsonProperty("UMG.AUTH.ID:1:1")
    private String umgAuthId;
    @JsonProperty("UMG.OPERATOR.ID:1:1")
    private String umgOperatorId;
    @JsonProperty("CHANNEL.REF.NUM:1:1")
    private String channelRefNum;
    @JsonProperty("UMG.TERM.ID:1:1")
    private String terminalId;
}
