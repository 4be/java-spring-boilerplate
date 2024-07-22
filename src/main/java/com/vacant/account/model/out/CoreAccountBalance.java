package com.vacant.account.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoreAccountBalance {

    @JsonProperty("ACCOUNT.NO::ACCOUNT.NO")
    public String accountNo;

    @JsonProperty("NAME::NAME")
    public String accountName;

    @JsonProperty("ACCOUNT.TYPE::ACCOUNT.TYPE")
    public String accountType;

    @JsonProperty("CURRENCY::CURRENCY")
    public String currency;

    @JsonProperty("AMOUNT::AMOUNT")
    public String amount;

    @JsonProperty("HOLD.AMOUNT::HOLD.AMOUNT")
    public String holdAmount;

    @JsonProperty("AVAILABLE.BAL::AVAILABLE.BAL")
    public String availableBalance;

    @JsonProperty("OPEN.BAL::OPEN.BAL")
    public String openBalance;

    @JsonProperty("LIMIT.AMT::LIMIT.AMT")
    public String limitAmount;

    @JsonProperty("STATUS::STATUS")
    public String status;

}
