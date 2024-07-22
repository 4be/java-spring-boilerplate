package com.vacant.account.model.in;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class AccountCreationSavingResponse {
    @JsonAlias("responseHeader")
    private String responseHeader;
    @JsonAlias("CUSTOMER:1:1")
    private String customer;
    @JsonAlias("CATEGORY:1:1")
    private String category;
    @JsonAlias("ACCOUNT.TITLE.1:1:1")
    private String accountTitle;
    @JsonAlias("SHORT.TITLE:1:1")
    private String shortTitle;
    @JsonAlias("POSITION.TYPE:1:1")
    private String positionType;
    @JsonAlias("CURRENCY:1:1")
    private String currency;
    @JsonAlias("CURRENCY.MARKET:1:1")
    private String currencyMarket;
    @JsonAlias("ACCOUNT.OFFICER:1:1")
    private String accountOfficer;
    @JsonAlias("POSTING.RESTRICT:1:1")
    private String postingRestrict;
    @JsonAlias("CLASSIFICATION:1:1")
    private String classification;
    @JsonAlias("SM.GENDER:1:1")
    private String smGender;
    @JsonAlias("EMPLYRS.CODE:1:1")
    private String EmplyrsCode;
    @JsonAlias("INS.Y.N:1:1")
    private String insYN;
    @JsonAlias("SA.PROD.TYPE:1:1")
    private String saProdType;
    @JsonAlias("PAYROLL.ACCT:1:1")
    private String payrollAcct;
    @JsonAlias("E.MAIL.FLAG:1:1")
    private String emailFlag;
    @JsonAlias("ADDRESS.FLAG:1:1")
    private String addressFlag;
    @JsonAlias("PROD.TYPE:1:1")
    private String prodType;
    @JsonAlias("CHARACTERISTIC:1:1")
    private String characteristic;
    @JsonAlias("INTEREST.TYPE:1:1")
    private String interestType;
    @JsonAlias("CHANNEL.REF.NUM:1:1")
    private String channelRefNum;
    @JsonAlias("UMG.TERM.ID:1:1")
    private String umgTermId;
    @JsonAlias("UMG.AUTH.ID:1:1")
    private String umgAuthId;
    @JsonAlias("UMG.AUTH.ID:2:1")
    private String umgAuthId2;
    @JsonAlias("UMG.OPERATOR.ID:1:1")
    private String umgOperatorId;
    @JsonAlias("UMG.UNIQUE.ID:1:1")
    private String umgUniqueId;
    @JsonAlias("AC.MEASURE.CAT:1:1")
    private String acMeasureCat;
    @JsonAlias("PASSBOOK.FLAG:1:1")
    private String passbookFlag;
    @JsonAlias("BRANCH.UPDATE:1:1")
    private String branchUpdate;
    @JsonAlias("CONDITION.GROUP:1:1")
    private String contitionGroup;
    @JsonAlias("PASSBOOK:1:1")
    private String passbook;
    @JsonAlias("OPENING.DATE:1:1")
    private String openingDate;
    @JsonAlias("OPEN.CATEGORY:1:1")
    private String openCategory;
    @JsonAlias("CHARGE.CCY:1:1")
    private String chargeCcy;
    @JsonAlias("CHARGE.MKT:1:1")
    private String chargeMkt;
    @JsonAlias("INTEREST.CCY:1:1")
    private String interestCcy;
    @JsonAlias("INTEREST.MKT:1:1")
    private String interestMkt;
    @JsonAlias("ALT.ACCT.TYPE:1:1")
    private String altAcctType;
    @JsonAlias("ALLOW.NETTING:1:1")
    private String allowNetting;
    @JsonAlias("CURR.NO:1:1")
    private String currNo;
    @JsonAlias("INPUTTER:1:1")
    private String inputter;
    @JsonAlias("DATE.TIME:1:1")
    private String dateTime;
    @JsonAlias("AUTHORISER:1:1")
    private String authoriser;
    @JsonAlias("CO.CODE:1:1")
    private String coCode;
    @JsonAlias("DEPT.CODE:1:1")
    private String deptCode;
    @JsonAlias("Y.ISO.RESPONSE:1:1")
    private String responseCode;
}
