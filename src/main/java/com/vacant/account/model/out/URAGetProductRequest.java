package com.vacant.account.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class URAGetProductRequest {

    private String accountCategory;
    private String status;
}
