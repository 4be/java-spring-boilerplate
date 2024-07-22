package com.vacant.account.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCreationSavingRequest {
    private String umgUniqueId;
    private String openingDate;
    private String branch;
    private String accountNumber;
    private String acMeasureCat;
    private String currency;
    private String accountTitle;
    private String accountOfficer;
    private String category;
    private String address;
    private String branchUpdate;
    private String channelRefNum;
    private String characteristic;
    private String corespondName;
    private String customer;
    private String emailAddress;
    private String emailFlag;
    private String interestType;
    private String merchantCode;
    private String passbookFlag;
    private String payrollAcct;
    private String poBoxNo;
    private String poCity;
    private String poLocalCode;
    private String poPostCode;
    private String poSuburbTown;
    private String postingRestrict;
    private String proProvState;
    private String saProdType;
    private String prodType;
    private String toRefSince;
    private String toReference;
    private String umgAuthId;
    private String umgAuthId2;
    private String umgOperatorId;
    private String umgTermId;
}
