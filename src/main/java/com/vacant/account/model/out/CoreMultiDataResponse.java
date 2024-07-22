package com.vacant.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreMultiDataResponse {
    private String count;
    private String responseCode;
    private String rawResponse;
    private List<Map<String, String>> data;
    private String correlationId;
}
