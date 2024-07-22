package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.beanmapper.annotations.BeanProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoreAccount {

    @JsonProperty("CUST.NO::CifCode")
    @BeanProperty("customerId")
    public String cifCode;

    @JsonProperty("ACC.NO::AccountNumber")
    @BeanProperty("accountNo")
    public String accountNumber;

    @JsonProperty("NAME::Name")
    public String accountName;

    @JsonProperty("ACCOUNT.TYPE::AccountType")
    public String accountType;

    @JsonProperty("PRODUCT.TYPE::ProductType")
    public String productType;

    @JsonProperty("BANK.BRANCH::BankBranch")
    public String branchCode;

    @JsonProperty("CURRENCY::Currency")
    public String currency;

    @JsonProperty("WORKING.BAL::WorkingBalance")
    @BeanProperty("currentBalance")
    public String workingBalance;

    @JsonProperty("CURR.BAL::AvailableBalance")
    public String availableBalance;

}
