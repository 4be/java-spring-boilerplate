package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreLockAmountResponse {
    private String responseHeader;
    @JsonAlias("Y.ISO.RESPONSE:1:1")
    private String responseCode;
    @JsonAlias("!OPERATION")
    private String operation;
    @JsonAlias("!OPTIONS")
    private String options;
    @JsonAlias("!BRANCH")
    private String branchCode;
    @JsonAlias("ACCOUNT.NUMBER:1:1")
    private String accountNumber;
    @JsonAlias("DESCRIPTION:1:1")
    private String description;
    @JsonAlias("FROM.DATE:1:1")
    private String fromDate;
    @JsonAlias("TO.DATE:1:1")
    private String toDate;
    @JsonAlias("LOCKED.AMOUNT:1:1")
    private String lockedAmount;
    @JsonAlias("UMG.AUTH.ID:1:1")
    private String supervisorId;
    @JsonAlias("UMG.OPERATOR.ID:1:1")
    private String createdBy;
    @JsonAlias("UMG.UNIQUE.ID:1:1")
    private String msgId;
    @JsonAlias("CHANNEL.REF.NUM:1:1")
    private String trxId;
    @JsonAlias("UMG.TERM.ID:1:1")
    private String terminalId;
}











