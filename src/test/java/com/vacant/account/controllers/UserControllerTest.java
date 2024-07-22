package com.vacant.account.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacant.account.constants.ApiPath;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.model.UserAccountProduct;
import com.vacant.account.model.in.UserStatusCheckResponse;
import com.vacant.account.services.LoggingService;
import com.vacant.account.services.UserService;
import com.vacant.account.utils.ResponseConstructor;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoggingService loggingService;

    @MockBean
    private UserService userService;

    @Test
    void checkUserStatus_validInput_then200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.USER_STATUS_CHECK)
                        .header("CLIENT-ID", "12345")
                        .header("CHANNEL-ID", "12345")
                        .header("USER-REFERENCE", "12345"))
                .andExpect(status().isOk());
    }

    @Test
    void checkUserStatus_noClientIdHeader_then400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.USER_STATUS_CHECK)
                        .header("CHANNEL-ID", "12345")
                        .header("USER-REFERENCE", "12345"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkUserStatus_noCiamIdHeader_then400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.USER_STATUS_CHECK)
                        .header("CLIENT-ID", "12345")
                        .header("CHANNEL-ID", "12345"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkUserStatus_validInputAndOutputKYC_then200() throws Exception {
        var clientId = "12345";
        var userReference = "12345";
        var channelId = "12345";
        String cif = null;

        var response = UserStatusCheckResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .customerLinked(false)
                .build();

        when(userService.checkUserStatus(clientId, userReference, channelId, cif)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.USER_STATUS_CHECK)
                        .header("CLIENT-ID", "12345")
                        .header("CHANNEL-ID", "12345")
                        .header("USER-REFERENCE", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.customerLinked").value("false"));
    }

    @Test
    void checkUserStatus_validInputAndOutputAccountCreation_then200() throws Exception {
        var clientId = "12345";
        var userReference = "12345";
        var channelId = "12345";
        String cif = "12345";

        var response = UserStatusCheckResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accounts(null)
                .customerLinked(true)
                .build();

        when(userService.checkUserStatus(clientId, userReference, channelId, cif)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.USER_STATUS_CHECK)
                        .header("CLIENT-ID", "12345")
                        .header("CHANNEL-ID", "12345")
                        .header("USER-REFERENCE", "12345")
                        .param("cif", cif))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.customerLinked").value("true"))
                .andExpect(jsonPath("$.accounts").doesNotExist());
    }

    @Test
    void checkUserStatus_validInputAndOutputAccountBinding_then200() throws Exception {
        var clientId = "12345";
        var userReference = "12345";
        var channelId = "12345";
        String cif = "12345";

        var response = UserStatusCheckResponse.builder()
                .responseCode(ResponseConstructor.constructResponseCode(ResponseCode.SUCCESS))
                .responseMessage(ResponseCode.SUCCESS.getMessage())
                .accounts(List.of(UserAccountProduct.builder().accountNo("11111").build()))
                .customerLinked(true)
                .build();

        when(userService.checkUserStatus(clientId, userReference, channelId, cif)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0" + ApiPath.USER_STATUS_CHECK)
                        .header("CLIENT-ID", clientId)
                        .header("CHANNEL-ID", channelId)
                        .header("USER-REFERENCE", userReference)
                        .param("cif", cif))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(response.getResponseCode()))
                .andExpect(jsonPath("$.responseMessage").value(response.getResponseMessage()))
                .andExpect(jsonPath("$.customerLinked").value("true"))
                .andExpect(jsonPath("$.accounts[0].accountNo").value("11111"));
    }

}
