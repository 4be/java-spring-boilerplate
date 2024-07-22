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
public class CentralValidateB2BAccountResponse {

    private String error;
    private Long id;
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("mitra_name")
    private String mitraName;
    private String status;
}
