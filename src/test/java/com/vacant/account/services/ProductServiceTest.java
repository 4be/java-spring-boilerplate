package com.vacant.account.services;

import com.vacant.account.constants.ProductConstant;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.exceptions.BusinessException;
import com.vacant.account.feignclients.ApiBankingManagerClient;
import com.vacant.account.model.UserAccountInfoRedis;
import com.simas.account.model.out.*;
import com.vacant.account.model.out.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private com.vacant.account.feignclients.URAClient URAClient;

    @Mock
    private AccountService accountService;

    @Mock
    private ApiBankingManagerClient apiBankingManagerClient;
    @Mock
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(
                URAClient,
                accountService,
                apiBankingManagerClient,
                cacheService
        );
    }

    @Test
    void getProducts_Success() {
        var channelCode = "1234";

        var product = URAProduct.builder()
                .id("SIMAS")
                .coreProductCode("6001")
                .name("Tabungan Simas")
                .status("active")
                .build();

        var getProductRequest = URAGetProductRequest.builder()
                .accountCategory(ProductConstant.ACCOUNT_CATEGORY_ALL)
                .status(ProductConstant.STATUS_ALL)
                .build();

        var getProductResponse = URAGetProductResponse.builder()
                .responseCode("2000101")
                .products(List.of(product))
                .build();

        when(URAClient.getProducts(channelCode, getProductRequest)).thenReturn(getProductResponse);

        var result = productService.getProducts(channelCode);

        assertFalse(result.isEmpty());
        Assertions.assertEquals("6001", result.get(0).getCoreProductCode());

        verify(URAClient).getProducts(channelCode, getProductRequest);
    }

    @Test
    void getProducts_Success_emptyData() {
        var channelCode = "1234";


        var getProductRequest = URAGetProductRequest.builder()
                .accountCategory(ProductConstant.ACCOUNT_CATEGORY_ALL)
                .status(ProductConstant.STATUS_ALL)
                .build();

        var getProductResponse = URAGetProductResponse.builder()
                .responseCode("2000101")
                .products(List.of())
                .build();

        when(URAClient.getProducts(channelCode, getProductRequest)).thenReturn(getProductResponse);

        var result = productService.getProducts(channelCode);

        assertTrue(result.isEmpty());

        verify(URAClient).getProducts(channelCode, getProductRequest);
    }

    @Test
    void getProducts_ErrorFromUR() {
        var channelCode = "1234";

        var getProductRequest = URAGetProductRequest.builder()
                .accountCategory(ProductConstant.ACCOUNT_CATEGORY_ALL)
                .status(ProductConstant.STATUS_ALL)
                .build();

        when(URAClient.getProducts(channelCode, getProductRequest)).thenThrow(new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR));

        assertThrows(BusinessException.class, () -> productService.getProducts(channelCode));

        verify(URAClient).getProducts(channelCode, getProductRequest);
        verify(URAClient).getProducts(channelCode, getProductRequest);
    }

    @Test
    void getProductEligible_Success() {
        var channelCode = "1234";
        var ciamID = "abc-123";

        var accountProduct = UserAccountInfoRedis.AccountProduct.builder()
                .accountNo("1234")
                .coreProductCode("60002")
                .build();
        UserAccountInfoRedis userAccountInfoRedis = UserAccountInfoRedis.builder()
                .customerId("123")
                .accounts(List.of(accountProduct))
                .build();

        when(cacheService.get(any(), any())).thenReturn(userAccountInfoRedis);

        var productList = List.of(URAGetProductEligibleResponse.Product.builder()
                .id("SIMASGOLD")
                .build());
        var uraGetProductEligibleResponse = URAGetProductEligibleResponse
                .builder()
                .responseMessage("success")
                .responseCode("2000100")
                .products(productList)
                .build();
        when(URAClient.getProductEligible(any(), any())).thenReturn(uraGetProductEligibleResponse);

        var result = productService.getProductEligible(channelCode, ciamID, "");

        Assertions.assertFalse(result.getProducts().isEmpty());
        Assertions.assertEquals("SIMASGOLD", result.getProducts().get(0).getId());

        verify(cacheService).get(any(), any());
        verify(URAClient).getProductEligible(any(), any());

    }

    @Test
    void getProductEligible_Success_empty_account_redis() {
        var channelCode = "1234";
        var ciamID = "abc-123";

        UserAccountInfoRedis userAccountInfoRedis = UserAccountInfoRedis.builder()
                .customerId("123")
                .accounts(List.of())
                .build();

        when(cacheService.get(any(), any())).thenReturn(userAccountInfoRedis);

        var productList = List.of(URAGetProductEligibleResponse.Product.builder()
                .id("SIMASGOLD")
                .build());
        var uraGetProductEligibleResponse = URAGetProductEligibleResponse
                .builder()
                .responseMessage("success")
                .responseCode("2000100")
                .products(productList)
                .build();
        when(URAClient.getProductEligible(any(), any())).thenReturn(uraGetProductEligibleResponse);

        var result = productService.getProductEligible(channelCode, ciamID, "");

        Assertions.assertFalse(result.getProducts().isEmpty());
        Assertions.assertEquals("SIMASGOLD", result.getProducts().get(0).getId());

        verify(cacheService).get(any(), any());
        verify(URAClient).getProductEligible(any(), any());

    }

    @Test
    void getProductEligible_Success_redis_expire() {
        var channelCode = "1234";
        var ciamID = "abc-123";


        var userSearchResponse = AbmUserSearchResponse.builder()
                .cif("4981674")
                .build();

        var coreAccounts = List.of(CoreAccount.builder()
                .accountNumber("1234566")
                .accountType("6002")
                .build());
        when(cacheService.get(any(), any())).thenReturn(null);
        when(apiBankingManagerClient.searchUser(ciamID)).thenReturn(userSearchResponse);
        when(accountService.getSavingAccounts(userSearchResponse.getCif())).thenReturn(coreAccounts);

        var productList = List.of(URAGetProductEligibleResponse.Product.builder()
                .id("SIMASGOLD")
                .build());
        var uraGetProductEligibleResponse = URAGetProductEligibleResponse
                .builder()
                .responseMessage("success")
                .responseCode("2000100")
                .products(productList)
                .build();
        when(URAClient.getProductEligible(any(), any())).thenReturn(uraGetProductEligibleResponse);

        var result = productService.getProductEligible(channelCode, ciamID, "");

        Assertions.assertFalse(result.getProducts().isEmpty());
        Assertions.assertEquals("SIMASGOLD", result.getProducts().get(0).getId());

        verify(cacheService).get(any(), any());
        verify(apiBankingManagerClient).searchUser(ciamID);
        verify(accountService).getSavingAccounts(userSearchResponse.getCif());
        verify(URAClient).getProductEligible(any(), any());

    }

    @Test
    void getProductEligible_Success_redis_expire_user_cif_null() {
        var channelCode = "1234";
        var ciamID = "abc-123";


        var userSearchResponse = AbmUserSearchResponse.builder()
                .cif(null)
                .build();
        when(cacheService.get(any(), any())).thenReturn(null);
        when(apiBankingManagerClient.searchUser(ciamID)).thenReturn(userSearchResponse);

        var productList = List.of(URAGetProductEligibleResponse.Product.builder()
                .id("SIMASGOLD")
                .build());
        var uraGetProductEligibleResponse = URAGetProductEligibleResponse
                .builder()
                .responseMessage("success")
                .responseCode("2000100")
                .products(productList)
                .build();
        when(URAClient.getProductEligible(any(), any())).thenReturn(uraGetProductEligibleResponse);

        var result = productService.getProductEligible(channelCode, ciamID, "");

        Assertions.assertFalse(result.getProducts().isEmpty());
        Assertions.assertEquals("SIMASGOLD", result.getProducts().get(0).getId());

        verify(cacheService).get(any(), any());
        verify(apiBankingManagerClient).searchUser(ciamID);
        verify(URAClient).getProductEligible(any(), any());

    }

    @Test
    void getProductEligible_error_redisError() {
        var channelCode = "1234";
        var ciamID = "abc-123";

        when(cacheService.get(any(), any())).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class, () -> productService.getProductEligible(channelCode, ciamID, ""));

        verify(cacheService).get(any(), any());
    }

    @Test
    void getProductEligible_error_ciamUserNotFound() {
        var channelCode = "1234";
        var ciamID = "abc-123";

        when(cacheService.get(any(), any())).thenReturn(null);
        when(apiBankingManagerClient.searchUser(ciamID)).thenThrow(new BusinessException(ResponseCode.DATA_NOT_FOUND));

        assertThrows(BusinessException.class, () -> productService.getProductEligible(channelCode, ciamID, ""));

        verify(cacheService).get(any(), any());
        verify(apiBankingManagerClient).searchUser(ciamID);
    }

    @Test
    void getProductEligible_error_ciamUserEmpty() {
        var channelCode = "1234";
        var ciamID = "abc-123";

        when(cacheService.get(any(), any())).thenReturn(null);
        when(apiBankingManagerClient.searchUser(ciamID)).thenReturn(null);

        assertThrows(BusinessException.class, () -> productService.getProductEligible(channelCode, ciamID, ""));

        verify(cacheService).get(any(), any());
        verify(apiBankingManagerClient).searchUser(ciamID);
    }

    @Test
    void getProductEligible_Success_coreEmpty() {
        var channelCode = "1234";
        var ciamID = "abc-123";


        var userSearchResponse = AbmUserSearchResponse.builder()
                .cif("4981674")
                .build();

        when(cacheService.get(any(), any())).thenReturn(null);
        when(apiBankingManagerClient.searchUser(ciamID)).thenReturn(userSearchResponse);
        when(accountService.getSavingAccounts(userSearchResponse.getCif())).thenReturn(List.of());

        var productList = List.of(URAGetProductEligibleResponse.Product.builder()
                .id("SIMASGOLD")
                .build());
        var uraGetProductEligibleResponse = URAGetProductEligibleResponse
                .builder()
                .responseMessage("success")
                .responseCode("2000100")
                .products(productList)
                .build();
        when(URAClient.getProductEligible(any(), any())).thenReturn(uraGetProductEligibleResponse);

        var result = productService.getProductEligible(channelCode, ciamID, "");

        Assertions.assertFalse(result.getProducts().isEmpty());
        Assertions.assertEquals("SIMASGOLD", result.getProducts().get(0).getId());

        verify(cacheService).get(any(), any());
        verify(apiBankingManagerClient).searchUser(ciamID);
        verify(accountService).getSavingAccounts(userSearchResponse.getCif());
        verify(URAClient).getProductEligible(any(), any());

    }

    @Test
    void checkProductEligible_Success() {
        var channelCode = "1234";
        var ciamID = "abc-123";
        var productCode = "SIMASGOLD";

        var accountProduct = UserAccountInfoRedis.AccountProduct.builder()
                .accountNo("1234")
                .coreProductCode("60002")
                .build();
        UserAccountInfoRedis userAccountInfoRedis = UserAccountInfoRedis.builder()
                .customerId("123")
                .accounts(List.of(accountProduct))
                .build();

        when(cacheService.get(any(), any())).thenReturn(userAccountInfoRedis);
        var uraCheckProductEligibleResponse = URACheckProductEligibleResponse
                .builder()
                .responseMessage("success")
                .responseCode("2000100")
                .eligibleStatus(true)
                .build();
        when(URAClient.checkProductEligible(any(), any())).thenReturn(uraCheckProductEligibleResponse);

        var result = productService.checkProductEligible(channelCode, ciamID, "", productCode);

        Assertions.assertTrue(result.isEligibleStatus());

        verify(cacheService).get(any(), any());
        verify(URAClient).checkProductEligible(any(), any());

    }

    @Test
    void checkProductEligible_Success_empty_account_redis() {
        var channelCode = "1234";
        var ciamID = "abc-123";
        var productCode = "SIMASGOLD";

        UserAccountInfoRedis userAccountInfoRedis = UserAccountInfoRedis.builder()
                .customerId("123")
                .accounts(List.of())
                .build();

        when(cacheService.get(any(), any())).thenReturn(userAccountInfoRedis);
        var uraCheckProductEligibleResponse = URACheckProductEligibleResponse
                .builder()
                .responseMessage("success")
                .responseCode("2000100")
                .eligibleStatus(true)
                .build();
        when(URAClient.checkProductEligible(any(), any())).thenReturn(uraCheckProductEligibleResponse);

        var result = productService.checkProductEligible(channelCode, ciamID, "", productCode);

        Assertions.assertTrue(result.isEligibleStatus());

        verify(cacheService).get(any(), any());
        verify(URAClient).checkProductEligible(any(), any());

    }

    @Test
    void checkProductEligible_Success_expire_account_redis() {
        var channelCode = "1234";
        var ciamID = "abc-123";
        var productCode = "SIMASGOLD";


        var userSearchResponse = AbmUserSearchResponse.builder()
                .cif("4981674")
                .build();

        var coreAccounts = List.of(CoreAccount.builder()
                .accountNumber("1234566")
                .accountType("6002")
                .build());
        when(cacheService.get(any(), any())).thenReturn(null);
        when(apiBankingManagerClient.searchUser(ciamID)).thenReturn(userSearchResponse);
        when(accountService.getSavingAccounts(userSearchResponse.getCif())).thenReturn(coreAccounts);

        var uraCheckProductEligibleResponse = URACheckProductEligibleResponse
                .builder()
                .responseMessage("success")
                .responseCode("2000100")
                .eligibleStatus(true)
                .build();
        when(URAClient.checkProductEligible(any(), any())).thenReturn(uraCheckProductEligibleResponse);

        var result = productService.checkProductEligible(channelCode, ciamID, "", productCode);

        Assertions.assertTrue(result.isEligibleStatus());

        verify(cacheService).get(any(), any());
        verify(apiBankingManagerClient).searchUser(ciamID);
        verify(accountService).getSavingAccounts(userSearchResponse.getCif());
        verify(URAClient).checkProductEligible(any(), any());

    }

    @Test
    void checkProductEligible_error_ciamUserNotFound() {
        var channelCode = "1234";
        var ciamID = "abc-123";
        var productCode = "SIMASGOLD";
        when(cacheService.get(any(), any())).thenReturn(null);
        when(apiBankingManagerClient.searchUser(ciamID)).thenThrow(new BusinessException(ResponseCode.DATA_NOT_FOUND));

        assertThrows(BusinessException.class, () -> productService.checkProductEligible(channelCode, ciamID, "", productCode));

        verify(cacheService).get(any(), any());
        verify(apiBankingManagerClient).searchUser(ciamID);
    }

    @Test
    void checkProductEligible_error_ciamUserEmpty() {
        var channelCode = "1234";
        var ciamID = "abc-123";
        var productCode = "SIMASGOLD";

        when(cacheService.get(any(), any())).thenReturn(null);
        when(apiBankingManagerClient.searchUser(ciamID)).thenReturn(null);

        assertThrows(BusinessException.class, () -> productService.checkProductEligible(channelCode, ciamID, "", productCode));

        verify(cacheService).get(any(), any());
        verify(apiBankingManagerClient).searchUser(ciamID);
    }

    @Test
    void checkProductEligible_Success_coreEmpty() {
        var channelCode = "1234";
        var ciamID = "abc-123";
        var productCode = "SIMASGOLD";


        var userSearchResponse = AbmUserSearchResponse.builder()
                .cif("4981674")
                .build();

        when(cacheService.get(any(), any())).thenReturn(null);
        when(apiBankingManagerClient.searchUser(ciamID)).thenReturn(userSearchResponse);
        when(accountService.getSavingAccounts(userSearchResponse.getCif())).thenReturn(List.of());


        var uraCheckProductEligibleResponse = URACheckProductEligibleResponse
                .builder()
                .responseMessage("success")
                .responseCode("2000100")
                .eligibleStatus(true)
                .build();
        when(URAClient.checkProductEligible(any(), any())).thenReturn(uraCheckProductEligibleResponse);

        var result = productService.checkProductEligible(channelCode, ciamID, "", productCode);

        Assertions.assertTrue(result.isEligibleStatus());

        verify(cacheService).get(any(), any());
        verify(apiBankingManagerClient).searchUser(ciamID);
        verify(accountService).getSavingAccounts(userSearchResponse.getCif());
        verify(URAClient).checkProductEligible(any(), any());

    }

}
