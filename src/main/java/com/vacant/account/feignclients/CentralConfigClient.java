package com.vacant.account.feignclients;

import com.vacant.account.model.out.CentralValidateB2BAccountRequest;
import com.vacant.account.model.out.CentralValidateB2BAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "central-config", url = "${central-config.url}")
public interface CentralConfigClient {

    @PostMapping(value = "/accountmanagement/128")
    CentralValidateB2BAccountResponse getB2BAccount(CentralValidateB2BAccountRequest request);
}
