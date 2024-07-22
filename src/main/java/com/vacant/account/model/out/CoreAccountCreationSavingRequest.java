package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreAccountCreationSavingRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("UMG.UNIQUE.ID:1:1")
    private String umgUniqueId;
    @JsonProperty("OPENING.DATE:1:1")
    private String openingDate;
    @JsonProperty("!BRANCH")
    private String branch;
    @JsonProperty("AC.MEASURE.CAT:1:1")
    private String acMeasureCat;
    @JsonProperty("CURRENCY:1:1")
    private String currency;
    @JsonProperty("ACCOUNT.TITLE.1:1:1")
    private String accountTitle;
    @JsonProperty("ACCOUNT.OFFICER:1:1")
    private String accountOfficer;
    @JsonProperty("CATEGORY:1:1")
    private String category;
    @JsonProperty("ADDRESS.FLAG:1:1")
    private String address;
    @JsonProperty("BRANCH.UPDATE:1:1")
    private String branchUpdate;
    @JsonProperty("CHANNEL.REF.NUM:1:1")
    private String channelRefNum;
    @JsonProperty("CHARACTERISTIC:1:1")
    private String characteristic;
    @JsonProperty("CORESPOND.NAME:1:1")
    private String corespondName;
    @JsonProperty("CUSTOMER:1:1")
    private String customer;
    @JsonProperty("E.MAIL.ADDRESS:1:1")
    private String emailAddress;
    @JsonProperty("E.MAIL.FLAG:1:1")
    private String emailFlag;
    @JsonProperty("INTEREST.TYPE:1:1")
    private String interestType;
    @JsonProperty("MERCHANT.CODE:1:1")
    private String merchantCode;
    @JsonProperty("PASSBOOK.FLAG:1:1")
    private String passbookFlag;
    @JsonProperty("PAYROLL.ACCT:1:1")
    private String payrollAcct;
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
    @JsonProperty("POSTING.RESTRICT:1:1")
    private String postingRestrict;
    @JsonProperty("PRO.PROV.STATE:1:1")
    private String proProvState;
    @JsonProperty("SA.PROD.TYPE:1:1")
    private String saProdType;
    @JsonProperty("PROD.TYPE:1:1")
    private String prodType;
    @JsonProperty("TO.REF.SINCE:1:1")
    private String toRefSince;
    @JsonProperty("TO.REFERENCE:1:1")
    private String toReference;
    @JsonProperty("UMG.AUTH.ID:1:1")
    private String umgAuthId;
    @JsonProperty("UMG.AUTH.ID:2:1")
    private String umgAuthId2;
    @JsonProperty("UMG.OPERATOR.ID:1:1")
    private String umgOperatorId;
    @JsonProperty("UMG.TERM.ID:1:1")
    private String umgTermId;
}
