package com.vacant.account.model.entity;

import jakarta.persistence.Column;

import java.io.Serializable;

public class SummaryId implements Serializable {
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "CIF_NUMBER")
    private String cifNumber;
    @Column(name = "NIK")
    private String nik;
    @Column(name = "BALANCE_AMOUNT")
    private String balanceAmount;
    @Column(name = "ACCOUNT_NO")
    private String accountNo;
}
