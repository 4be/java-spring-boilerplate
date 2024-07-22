package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreAccountMaintenanceOverdraftRequest {
    @JsonProperty("UMG.UNIQUE.ID:1:1")
    private String umgUniqueId;
    @JsonProperty("!BRANCH")
    private String branch;
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("!USER_INFORMATION")
    private String userInformation;
    @JsonProperty("!TRANSACTION_ID")
    private String transactionId;
    @JsonProperty("ACCOUNT.OFFICER:1:1")
    private String accountOfficer;
    @JsonProperty("ACCOUNT.TITLE.1:1:1")
    private String accountTitle;
    @JsonProperty("ADDRESS.FLAG:1:1")
    private String addressFlag;
    @JsonProperty("PROD.TYPE:1:1")
    private String prodType;
    @JsonProperty("CATEGORY:1:1")
    private String category;
    @JsonProperty("CHANNEL.REF.NUM:1:1")
    private String channelRefNum;
    @JsonProperty("CORESPOND.NAME:1:1")
    private String corespondName;
    @JsonProperty("CURRENCY:1:1")
    private String currency;
    @JsonProperty("CUSTOMER:1:1")
    private String customer;
    @JsonProperty("E.MAIL.ADDRESS:1:1")
    private String emailAddress;
    @JsonProperty("E.MAIL.FLAG:1:1")
    private String emailFlag;
    @JsonProperty("OPENING.DATE:1:1")
    private String openingDate;
    @JsonProperty("PO.BOX.NO:1:1")
    private String poBoxNo;
    @JsonProperty("PO.CITY:1:1")
    private String poCity;
    @JsonProperty("PO.LOCAT.CODE:1:1")
    private String poLocatCode;
    @JsonProperty("PO.POST.CODE:1:1")
    private String poPostCode;
    @JsonProperty("PO.SUBURB.TOWN:1:1")
    private String poSuburbTown;
    @JsonProperty("PRO.PROV.STATE:1:1")
    private String proProvState;
    @JsonProperty("START.L.DATE:1:1")
    private String startLDate;
    @JsonProperty("UMG.AUTH.ID:1:1")
    private String umgAuthId;
    @JsonProperty("UMG.AUTH.ID:2:1")
    private String umgAuthId2;
    @JsonProperty("UMG.OPERATOR.ID:1:1")
    private String umgOperatorId;
    @JsonProperty("UMG.TERM.ID:1:1")
    private String umgTermId;
}
