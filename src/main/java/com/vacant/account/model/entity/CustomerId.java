package com.vacant.account.model.entity;

import jakarta.persistence.Column;

import java.io.Serializable;

public class CustomerId implements Serializable {
    @Column(name = "ACCOUNT_NO")
    private String accountNo;
    @Column(name = "BALANCE_AMOUNT")
    private String balanceAmount;
}
