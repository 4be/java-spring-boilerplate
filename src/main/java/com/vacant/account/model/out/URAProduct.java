package com.vacant.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class URAProduct {

    private String id;
    private String name;
    private String type;
    private Boolean subAccount;
    private Integer maxAccount;
    private String coreProductCode;
    private Integer sortOrder;
    private String status;
}
