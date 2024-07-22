package com.vacant.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    public String customerId;
    public String accountNo;
    public String accountName;
    public String accountType;
    public String productType;
    public String branchCode;
    public String currency;
    public String currentBalance;
    public String availableBalance;

}
