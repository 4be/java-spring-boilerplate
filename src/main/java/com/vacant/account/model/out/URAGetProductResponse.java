package com.vacant.account.model.out;

import com.vacant.account.model.in.BaseResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class URAGetProductResponse extends BaseResponse {

    private List<URAProduct> products;
}
