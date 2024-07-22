package com.vacant.account.model.out;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreCreateAccountRequest {

    @JsonProperty("!OPERATION")
    private String operation;

    @JsonProperty("!OPTIONS")
    private String options;

    @JsonProperty("VALUE:1:1")
    private String productType;

    @JsonProperty("VALUE:1:2")
    private String cif;

    @JsonProperty("VALUE:1:3")
    private String currencyCode;

    @JsonProperty("VALUE:1:4")
    private String branchCode;

    @JsonProperty("VALUE:2:1")
    private String firstDepositDebitAccount;

    @Builder.Default
    @JsonProperty("VALUE:2:2")
    private String firstDepositAmount = "0";

    @JsonProperty("VALUE:2:3")
    private String firstDepositTreasuryRate;

    @JsonProperty("VALUE:2:4")
    private String firstDepositBranchCode;

    @JsonProperty("CHANNEL.REF.NUM:1:1")
    private String channelReference;

    @JsonProperty("UMG.UNIQUE.ID:1:1")
    private String umgReference;
}