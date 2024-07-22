package com.vacant.account.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DM_ANALYTICS.V_GET_CUST_PORTFOLIO")
@IdClass(CustomerId.class)
public class EDWCustomerPortfolio {
    @Column(name = "CIF_NUMBER")
    private String cifNumber;
    @Column(name = "REPORT_DATE")
    private String reportDate;
    @Column(name = "PORTFOLIO_FLAG")
    private String portfolioFlag;
    @Column(name = "NIK")
    private String nik;
    @Id
    @Column(name = "ACCOUNT_NO")
    private String accountNo;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_DESC")
    private String productDesc;
    @Column(name = "PRODUCT_GROUP")
    private String productGroup;
    @Id
    @Column(name = "BALANCE_AMOUNT")
    private String balanceAmount;
    @Column(name = "IDR_BALANCE_AMOUNT")
    private String idrBalanceAmount;
    @Column(name = "T24_SAVING")
    private String t24Saving;
    @Column(name = "SSL_SAVING")
    private String sslSaving;
    @Column(name = "CUSTOMER_NAME")
    private String customerName;
    @Column(name = "MOTHER_MAIDEN_NAME")
    private String motherMaidenName;
    @Column(name = "MOBILE_PHONE")
    private String mobilePhone;
    @Column(name = "OPENING_DATE")
    private String openingDate;
    @Column(name = "CURRENCY")
    private String currency;
    @Column(name = "DEBIT_ACCOUNT")
    private String debitAccount;
    @Column(name = "CREDIT_ACCOUNT")
    private String creditAccount;
    @Column(name = "MATURITY_DATE")
    private String maturityDate;
    @Column(name = "DORMANT_FLAG")
    private String dormantFlag;
    @Column(name = "DORMANT_FLAG_12M")
    private String dormantFlag12M;
    @Column(name = "TERM")
    private String term;
    @Column(name = "ROLLOVER_TYPE")
    private String rolloverType;
    @Column(name = "CREDIT_LIMIT")
    private String creditLimit;
    @Column(name = "COLLECTIBILITY")
    private String collectibility;
    @Column(name = "ATM_CARD_FLAG")
    private String atmCardFlag;
    @Column(name = "LOCKED_AMOUNT_FLAG")
    private String lockedAmountFlag;
    @Column(name = "ADDITIONAL_DATA")
    private String additionalData;
}
