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
public class CoreAccountStatement {

    @JsonProperty("Datetime::Datetime")
    private String dateTime;

    @JsonProperty("Description::Description")
    private String description;

    @JsonProperty("RefNo::RefNo")
    private String referenceNo;

    @JsonProperty("Currency::Currency")
    private String currency;

    @JsonProperty("Amount::Amount")
    private String amount;

    @JsonProperty("Balance::Balance")
    private String balance;

    @JsonProperty("Narrative::Narrative")
    private String narrative;

    @JsonProperty("STMT.ID::STMT.ID")
    private String statementId;

    @JsonProperty("OriCurr::OriCurr")
    private String oriCurrency;

    @JsonProperty("OriAmt::OriAmt")
    private String oriAmount;

}
