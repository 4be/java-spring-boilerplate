package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoreActivateDormantRequest {
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
    @JsonProperty("RESET.DATE:1:1")
    private String resetDate;
    @JsonProperty("CHANNEL.REF.NUM")
    private String trxId;
    @JsonProperty("UMG.UNIQUE.ID")
    private String msgId;
    @JsonProperty("UMG.OPERATOR.ID")
    private String createdBy;
    @JsonProperty("UMG.TERM.ID")
    private String terminalId;
    @JsonProperty("UMG.AUTH.ID")
    private String supervisorId;
    private String correlationId;
}
