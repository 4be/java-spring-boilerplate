package com.vacant.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreGenericResponse<T> {

    private String responseMessage;
    private List<T> data;
    private String correlationId;

}
