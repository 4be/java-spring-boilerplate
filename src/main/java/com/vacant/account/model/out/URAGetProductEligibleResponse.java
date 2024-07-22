package com.vacant.account.model.out;

import com.vacant.account.model.in.BaseResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class URAGetProductEligibleResponse extends BaseResponse {
    private List<Product> products;

    @Getter
    @Builder
    public static class Product {
        private String id;
        private String type;
        private String name;
        private String imageUrl;
        private Boolean subAccount;
    }
}
