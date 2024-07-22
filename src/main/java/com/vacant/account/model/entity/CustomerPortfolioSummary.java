package com.vacant.account.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DM_ANALYTICS.V_GET_CUST_PORTFOLIO_SUMMARY")
@IdClass(SummaryId.class)
public class CustomerPortfolioSummary {
    @Column(name = "REPORT_DATE")
    private String reportDate;
    @Column(name = "PORTFOLIO_FLAG")
    private String portfolioFlag;
    @Id
    @Column(name = "CIF_NUMBER")
    private String cifNumber;
    @Column(name = "NIK")
    private String nik;
    @Column(name = "ACCOUNT_NO")
    private String accountNo;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_DESC")
    private String productDesc;
    @Column(name = "PRODUCT_GROUP")
    private String productGroup;
    @Column(name = "BALANCE_AMOUNT")
    private String balanceAmount;
    @Column(name = "IDR_BALANCE_AMOUNT")
    private String idrBalanceAmount;
    @Column(name = "OPENING_DATE")
    private String openingDate;
    @Column(name = "CURRENCY")
    private String currency;
    @Column(name = "OPENING_BRANCH_CODE")
    private String openingBranchCode;
    @Column(name = "DEBIT_ACCOUNT")
    private String debitAccount;
    @Column(name = "CREDIT_ACCOUNT")
    private String creditAccount;
    @Column(name = "MATURITY_DATE")
    private String maturityDate;
    @Column(name = "MARKETING_CODE")
    private String marketingCode;
    @Column(name = "REFERRAL_CODE")
    private String referralCode;
}
