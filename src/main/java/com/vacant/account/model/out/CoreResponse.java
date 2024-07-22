package com.vacant.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreResponse {
    private String size;
    private String responseMessage;
    private String responseCode;
    private String rawResponse;
    private Map<String, Object> data;
    private Map<String, String> error;
    private String correlationId;
}
