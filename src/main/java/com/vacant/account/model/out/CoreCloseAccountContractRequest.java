package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoreCloseAccountContractRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("VALUE:2:5")
    private String closingFee;
    @JsonProperty("VALUE:2:6")
    private String currency;
    @JsonProperty("VALUE:3:1")
    private String accNo;
    @JsonProperty("VALUE:2:3")
    private String accSettlement;
    @JsonProperty("CHANNEL.REF.NUM:1:1")
    private String channelRefNum;
    @JsonProperty("UMG.AUTH.ID:1:1")
    private String umgAuthId;
    @JsonProperty("UMG.TERM.ID:1:1")
    private String umgTermId;
    @JsonProperty("UMG.OPERATOR.ID:1:1")
    private String umgOperatorId;
    @JsonProperty("UMG.UNIQUE.ID:1:1")
    private String umgUniqueId;
}
