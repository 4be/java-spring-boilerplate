package com.vacant.account.model.out;

import com.vacant.account.model.in.BaseResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class URACheckProductEligibleResponse extends BaseResponse {
    private Boolean eligibleStatus;

}
