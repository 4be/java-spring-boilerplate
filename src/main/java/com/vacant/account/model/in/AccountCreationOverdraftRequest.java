package com.vacant.account.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCreationOverdraftRequest {
    private String umgUniqueId;
    private String branch;
    private String userInformation;
    private String responseHeader;
    private String customer;
    private String category;
    private String accountTitle;
    private String shortTitle;
    private String positionType;
    private String currency;
    private String currencyMarket;
    private String accountOfficer;
    private String smGender;
    private String employeeNo;
    private String emplyrsCode;
    private String caProdType;
    private String payrollAcct;
    private String unusedLoan;
    private String emailFlag;
    private String addressFlag;
    private String prodType;
    private String startLDate;
    private String channelRefNum;
    private String umgTermId;
    private String umgAuthId;
    private String ugmAuthId2;
    private String umgOperatorId;
    private String conditionGroup;
    private String passbook;
    private String openingDate;
    private String openCategory;
    private String chargeCcy;
    private String chargeMkt;
    private String interestCcy;
    private String interestMkt;
    private String altAcctType;
    private String allowNetting;
    private String liquidationMode;
    private String singleLimit;
    private String currNo;
    private String inputter;
    private String dateTime;
    private String authoriser;
    private String coCode;
    private String deptCode;
    private String corespondName;
    private String emailAddress;
    private String poBoxNo;
    private String poCity;
    private String poLocatCode;
    private String poPostCode;
    private String poSuburbTown;
    private String proProvState;
    private String umgAuthId2;
    private String responseCode;
}
