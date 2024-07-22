package com.vacant.account.model.out;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class URACheckProductEligibleRequest {
    private String productCode;
    private List<Account> accounts;
    @Data
    @Builder
    public static class Account {
        private String accountNo;
        private String coreProductCode;
    }
}