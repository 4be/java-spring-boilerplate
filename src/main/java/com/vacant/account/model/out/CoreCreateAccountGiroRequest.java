package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoreCreateAccountGiroRequest {
    @JsonProperty("!OPERATION")
    private String operation;
    @JsonProperty("!OPTIONS")
    private String options;
    @JsonProperty("!TRANSACTION_ID")
    private String accountNumber;
    @JsonProperty("!BRANCH")
    private String branchCode;
    @JsonProperty("CUSTOMER:1:1")
    private String cifNo;
    @JsonProperty("CATEGORY:1:1")
    private String productType;
    @JsonProperty("ACCOUNT.TITLE.1:1:")
    private String accountTitle;
    @JsonProperty("CURRENCY:1:1")
    private String currency;
    @JsonProperty("ACCOUNT.OFFICER:1:1")
    private String officerCode;
    @JsonProperty("POSTING.RESTRICT:1:1")
    private String blockedReason;
    @JsonProperty("OPENING.DATE:1:1")
    private String openDate;
    @JsonProperty("CA.PROD.TYPE:1:1")
    private String category;
    @JsonProperty("PROD.TYPE:1:1")
    private String prodType;
    @JsonProperty("INTEREST.TYPE:1:1")
    private String smintType;
    @JsonProperty("AC.MEASURE.CAT:1:1")
    private String acMeasureCat;
    @JsonProperty("SM.MARKETING.ID:1:1")
    private String information4;
    @JsonProperty("SM.REFERRAL.ID:1:1")
    private String information5;
    @JsonProperty("E.MAIL.ADDRESS:2:1")
    private String email2;
    @JsonProperty("E.MAIL.ADDRESS:3:1")
    private String email3;
    @JsonProperty("E.MAIL.ADDRESS:4:1")
    private String email4;
    @JsonProperty("JOINT.HOLDER:1:1")
    private String joinCifNo;
    @JsonProperty("RELATION.CODE:1:1")
    private String joinRelationCode;
    @JsonProperty("JOINT.NOTES:1:1")
    private String joinNotes;
    @JsonProperty("ATM.CARD:1:1")
    private String card;
    @JsonProperty("CARD.NUMBER:1:1")
    private String cardNumber;
    @JsonProperty("CARDHOLDER:1:1")
    private String cardholder;
    @JsonProperty("CARD.STATUS:1:1")
    private String cardStatus;
    @JsonProperty("CARD.DATE:1:1")
    private String lastStatusUpdate;
    @JsonProperty("ATM.MAX.BAL:1:1")
    private String maxCardBalance;
    @JsonProperty("ATM.FLAG:1:1")
    private String flag;
    @JsonProperty("SIGNATORY.CODE:1:1")
    private String signCode;
    @JsonProperty("DATE.OF.SIG:1:1")
    private String signDate;
    @JsonProperty("SIGN.INSTRUC:1:1")
    private String signInstruction;
    @JsonProperty("E.MAIL.FLAG:1:1")
    private String sameCifEmail;
    @JsonProperty("E.MAIL.ADDRESS:1:1")
    private String email;
    @JsonProperty("ADDRESS.FLAG:1:1")
    private String sameCifAddress;
    @JsonProperty("CORESPOND.NAME:1:1")
    private String corespondentName;
    @JsonProperty("PO.BOX.NO:1:1")
    private String streetName1;
    @JsonProperty("PO.SUBURB.TOWN:1:1")
    private String village;
    @JsonProperty("PO.CITY:1:1")
    private String subDistrict;
    @JsonProperty("PRO.PROV.STATE:1:1")
    private String province;
    @JsonProperty("PO.LOCAT.CODE:1:1")
    private String cityTown;
    @JsonProperty("PO.POST.CODE:1:1")
    private String postcode;
    @JsonProperty("UMG.AUTH.ID:1:1")
    private String supervisorId;
    @JsonProperty("UMG.OPERATOR.ID:1:1")
    private String createdBy;
    @JsonProperty("UMG.UNIQUE.ID:1:1")
    private String msgId;
    @JsonProperty("CHANNEL.REF.NUM:1:1")
    private String trxId;
    @JsonProperty("UMG.TERM.ID:1:1")
    private String terminalId;
    @JsonProperty("UMG.AUTH.ID:2:1")
    private String supervisorId2;
    @JsonProperty("ATM.CARD.REQ:1:1")
    private String atmCard;
    @JsonProperty("DIGITAL.SIGN.ID:1:1")
    private String digitalSignId;
    private String correlationId;
}
