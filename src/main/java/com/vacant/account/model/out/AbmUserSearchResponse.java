package com.vacant.account.model.out;

import com.vacant.account.model.in.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AbmUserSearchResponse extends BaseResponse {

    private String id;
    private String mail;
    private String cif;
    private String userName;
}
