package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoreAccountMaintenanceSavingRequest {
    @JsonProperty("!BRANCH")
    private String auditBranchCode;
    @JsonProperty("UMG.UNIQUE.ID:1:1")
    private String msgId;
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("!TRANSACTION_ID")
    private String accountNumber;
    @JsonProperty("AC.MEASURE.CAT:1:1")
    private String BSL2AcMeasureCat;
    @JsonProperty("ACCOUNT.TITLE.1:1:1")
    private String accountTitle;
    @JsonProperty("ADDRESS.FLAG:1:1")
    private String addrSameCifAddress;
    @JsonProperty("BRANCH.UPDATE:1:1")
    private String branchUpdate;
    @JsonProperty("CHANNEL.REF.NUM:1:1")
    private String trxId;
    @JsonProperty("CHARACTERISTIC:1:1")
    private String BSL2Characteristic;
    @JsonProperty("CORESPOND.NAME:1:1")
    private String addrCorrespondentName;
    @JsonProperty("CUSTOMER:1:1")
    private String cifNo;
    @JsonProperty("E.MAIL.ADDRESS:1:1")
    private String addrEmail;
    @JsonProperty("E.MAIL.FLAG:1:1")
    private String addrSameCifEmail;
    @JsonProperty("INTEREST.TYPE:1:1")
    private String BSL2SmIntType;
    @JsonProperty("MERCHANT.CODE:1:1")
    private String acctInfo1;
    @JsonProperty("PASSBOOK.FLAG:1:1")
    private String BSL2Passbook;
    @JsonProperty("PAYROLL.ACCT:1:1")
    private String flagSalaryAcc;
    @JsonProperty("PO.BOX.NO:1:1")
    private String addrStreetName1;
    @JsonProperty("PO.CITY:1:1")
    private String addrSubDistrict;
    @JsonProperty("PO.LOCAT.CODE:1:1")
    private String addrCityTown;
    @JsonProperty("PO.POST.CODE:1:1")
    private String addrPostcode;
    @JsonProperty("PO.SUBURB.TOWN:1:1")
    private String addrVillage;
    @JsonProperty("POSTING.RESTRICT:1:1")
    private String blockedReason;
    @JsonProperty("PRO.PROV.STATE:1:1")
    private String addrProvince;
    @JsonProperty("SA.PROD.TYPE:1:1")
    private String category;
    @JsonProperty("TO.REF.SINCE:1:1")
    private String BSL2ToRefSince;
    @JsonProperty("TO.REFERENCE:1:1")
    private String BSL2ToReference;
    @JsonProperty("UMG.AUTH.ID:1:1")
    private String auditSupervisorId;
    @JsonProperty("UMG.OPERATOR.ID:1:1")
    private String auditCreatedBy;
    @JsonProperty("UMG.TERM.ID:1:1")
    private String terminalId;
}
