package com.vacant.account.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacant.account.constants.ApiPath;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.exceptions.BusinessException;
import com.vacant.account.model.in.CheckProductEligibleResponse;
import com.vacant.account.model.in.ProductEligibleResponse;
import com.vacant.account.services.LoggingService;
import com.vacant.account.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoggingService loggingService;

    @MockBean
    private ProductService productService;

    @Test
    void getProductEligible_validInput_then200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PRODUCT_ELIGIBLE)
                        .header("CHANNEL-ID", "20004")
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345"))
                .andExpect(status().isOk());
    }

    @Test
    void getProductEligible_noChannelID_then400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PRODUCT_ELIGIBLE)
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProductEligible_noUIDHeader_then400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PRODUCT_ELIGIBLE)
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CHANNEL-ID", "20004"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProductEligible_validInputReturnProduct_then200() throws Exception {
        var channelId = "20004";
        var ciamID = "12345";
        var clientId = "NANOVEST";

        var product = ProductEligibleResponse.Product.builder()
                .id("SIMASGOLD")
                .type("SAVING")
                .name("Simas Gold")
                .imageUrl("https://image.jpg")
                .build();
        var response = ProductEligibleResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .products(List.of(product))
                .build();

        when(productService.getProductEligible(clientId, ciamID, channelId)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PRODUCT_ELIGIBLE)
                        .header("CHANNEL-ID", "20004")
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.products").isNotEmpty());
    }


    @Test
    void getProductEligible_invalidCustomer_then404() throws Exception {
        var channelId = "20004";
        var ciamID = "12345";
        var clientId = "NANOVEST";

        when(productService.getProductEligible(clientId, ciamID, channelId)).thenThrow(new BusinessException(ResponseCode.INVALID_CUSTOMER));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PRODUCT_ELIGIBLE)
                        .header("CHANNEL-ID", "20004")
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.products").doesNotExist());
    }

    @Test
    void getProductEligible_productNotFound_then404() throws Exception {
        var channelId = "20004";
        var ciamID = "12345";
        var clientId = "NANOVEST";

        when(productService.getProductEligible(clientId, ciamID, channelId)).thenThrow(new BusinessException(ResponseCode.DATA_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.PRODUCT_ELIGIBLE)
                        .header("CHANNEL-ID", "20004")
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.products").doesNotExist());
    }

    @Test
    void checkProductEligible_validInput_then200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.CHECK_PRODUCT_ELIGIBLE)
                        .header("CHANNEL-ID", "20004")
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345")
                        .param("productCode", "SIMASGOLD"))
                .andExpect(status().isOk());
    }

    @Test
    void checkProductEligible_noChannelID_then400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.CHECK_PRODUCT_ELIGIBLE)
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345")
                        .param("productCode", "SIMASGOLD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkProductEligible_noUIDHeader_then400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.CHECK_PRODUCT_ELIGIBLE)
                        .header("CHANNEL-ID", "20004")
                        .header("CLIENT-ID", "NANOVEST")
                        .param("productCode", "SIMASGOLD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkProductEligible_validInputReturnProduct_then200() throws Exception {
        var clientId = "NANOVEST";
        var channelId = "20004";
        var ciamID = "12345";
        var productCode = "SIMASGOLD";

        var response = CheckProductEligibleResponse.builder()
                .responseCode(ResponseCode.SUCCESS.getCode())
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .eligibleStatus(true)
                .build();

        when(productService.checkProductEligible(clientId, ciamID, channelId, productCode)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.CHECK_PRODUCT_ELIGIBLE)
                        .header("CHANNEL-ID", "20004")
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345")
                        .param("productCode", "SIMASGOLD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.eligibleStatus").value(true));
    }


    @Test
    void checkProductEligible_invalidCustomer_then404() throws Exception {
        var clientId = "NANOVEST";
        var channelId = "20004";
        var ciamID = "12345";
        var productCode = "SIMASGOLD";

        when(productService.checkProductEligible(clientId, ciamID, channelId, productCode)).thenThrow(new BusinessException(ResponseCode.INVALID_CUSTOMER));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.CHECK_PRODUCT_ELIGIBLE)
                        .header("CHANNEL-ID", "20004")
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345")
                        .param("productCode", "SIMASGOLD"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.products").doesNotExist());
    }

    @Test
    void checkProductEligible_productNotFound_then404() throws Exception {
        var clientId = "NANOVEST";
        var channelId = "20004";
        var ciamID = "12345";
        var productCode = "SIMASGOLD";

        when(productService.checkProductEligible(clientId, ciamID, channelId, productCode)).thenThrow(new BusinessException(ResponseCode.DATA_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.CHECK_PRODUCT_ELIGIBLE)
                        .header("CHANNEL-ID", "20004")
                        .header("CLIENT-ID", "NANOVEST")
                        .header("CIAM-ID", "12345")
                        .param("productCode", "SIMASGOLD"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.products").doesNotExist());
    }
}