package com.vacant.account.model.out;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CentralValidateB2BAccountRequest {

    private String appName;
    private String accountNumber;
}
