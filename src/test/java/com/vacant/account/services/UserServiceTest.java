package com.vacant.account.services;

import com.vacant.account.constants.ProductConstant;
import com.vacant.account.enums.ProductPage;
import com.vacant.account.enums.ResponseCode;
import com.vacant.account.exceptions.BusinessException;
import com.vacant.account.model.UserAccountInfoRedis;
import com.vacant.account.model.out.CoreAccount;
import com.vacant.account.model.out.URAProduct;
import com.vacant.account.utils.RedisUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {


    @InjectMocks
    private UserService userService;

    @Mock
    private AccountService accountService;

    @Mock
    private ProductService productService;

    @Mock
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(
                accountService,
                productService,
                cacheService
        );
    }

    @Test
    void checkUserStatus_Success_KYC_WithProductDetail() {
        var userReference = "123";
        var channelId = "123";
        var clientId = "123";
        String cif = null;

        var urProducts = List.of(URAProduct.builder().coreProductCode("6003").subAccount(false).status(ProductConstant.STATUS_ACTIVE).build());

        var redisKey = RedisUtil.constructApiBankingRedisKey(RedisUtil.ACCOUNT_INFO_TYPE, clientId, userReference);
        var redisValue = UserAccountInfoRedis.builder()
                .accounts(List.of())
                .build();
        var redisDuration = Duration.ofMinutes(30);

        when(productService.getProducts(channelId)).thenReturn(urProducts);
        doNothing().when(cacheService).save(redisKey, redisValue, redisDuration);

        var result = userService.checkUserStatus(clientId, userReference, channelId, cif);

        Assertions.assertFalse(result.isCustomerLinked());
        assertNull(result.getAccounts());
        Assertions.assertEquals(ProductPage.DETAIL, result.getProductPage());

        verify(productService).getProducts(channelId);
        verify(cacheService).save(redisKey, redisValue, redisDuration);
    }

    @Test
    void checkUserStatus_Success_KYC_WithProductList() {
        var userReference = "123";
        var channelId = "123";
        var clientId = "123";
        String cif = null;

        var urProducts = List.of(URAProduct.builder().coreProductCode("6003").subAccount(false).status(ProductConstant.STATUS_ACTIVE).build(),
                URAProduct.builder().coreProductCode("6004").subAccount(false).status(ProductConstant.STATUS_ACTIVE).build());

        var redisKey = RedisUtil.constructApiBankingRedisKey(RedisUtil.ACCOUNT_INFO_TYPE, clientId, userReference);
        var redisValue = UserAccountInfoRedis.builder()
                .accounts(List.of())
                .build();
        var redisDuration = Duration.ofMinutes(30);

        when(productService.getProducts(channelId)).thenReturn(urProducts);
        doNothing().when(cacheService).save(redisKey, redisValue, redisDuration);

        var result = userService.checkUserStatus(clientId, userReference, channelId, cif);

        Assertions.assertFalse(result.isCustomerLinked());
        assertNull(result.getAccounts());
        Assertions.assertEquals(ProductPage.LIST, result.getProductPage());

        verify(productService).getProducts(channelId);
        verify(cacheService).save(redisKey, redisValue, redisDuration);
    }

    @Test
    void checkUserStatus_Success_AccountBinding() {
        var userReference = "123";
        var channelId = "123";
        var clientId = "123";
        var cif = "4981674";

        var coreAccounts = List.of(CoreAccount.builder().accountType("6003").build());
        var urProducts = List.of(URAProduct.builder().coreProductCode("6003").subAccount(false).status(ProductConstant.STATUS_ACTIVE).build());

        when(accountService.getSavingAccounts(cif)).thenReturn(coreAccounts);
        when(productService.getProducts(channelId)).thenReturn(urProducts);

        var result = userService.checkUserStatus(clientId, userReference, channelId, cif);

        Assertions.assertTrue(result.isCustomerLinked());
        Assertions.assertFalse(result.getAccounts().isEmpty());

        verify(accountService).getSavingAccounts(cif);
        verify(productService).getProducts(channelId);
    }

    @Test
    void checkUserStatus_Success_AccountCreation_WithProductDetail() {
        var userReference = "123";
        var channelId = "123";
        var clientId = "123";
        String cif = "4981674";

        var coreAccounts = List.of(CoreAccount.builder().accountType("6002").build());
        var urProducts = List.of(URAProduct.builder().coreProductCode("6003").subAccount(false).status(ProductConstant.STATUS_ACTIVE).build());
        var redisKey = RedisUtil.constructApiBankingRedisKey(RedisUtil.ACCOUNT_INFO_TYPE, clientId, userReference);
        var redisValue = UserAccountInfoRedis.builder()
                .customerId("4981674")
                .accounts(List.of())
                .build();
        var redisDuration = Duration.ofMinutes(30);

        when(accountService.getSavingAccounts(cif)).thenReturn(coreAccounts);
        when(productService.getProducts(channelId)).thenReturn(urProducts);
        doNothing().when(cacheService).save(redisKey, redisValue, redisDuration);

        var result = userService.checkUserStatus(clientId, userReference, channelId, cif);

        Assertions.assertTrue(result.isCustomerLinked());
        assertNull(result.getAccounts());
        Assertions.assertEquals(ProductPage.DETAIL, result.getProductPage());

        verify(accountService).getSavingAccounts(cif);
        verify(productService).getProducts(channelId);
        verify(cacheService).save(redisKey, redisValue, redisDuration);
    }

    @Test
    void checkUserStatus_Success_AccountCreation_WithProductList() {
        var userReference = "123";
        var channelId = "123";
        var clientId = "123";
        String cif = "4981674";

        var coreAccounts = List.of(CoreAccount.builder().accountType("6002").build());
        var urProducts = List.of(URAProduct.builder().coreProductCode("6003").subAccount(false).status(ProductConstant.STATUS_ACTIVE).build(),
                URAProduct.builder().coreProductCode("6004").subAccount(false).status(ProductConstant.STATUS_ACTIVE).build());

        var redisKey = RedisUtil.constructApiBankingRedisKey(RedisUtil.ACCOUNT_INFO_TYPE, clientId, userReference);
        var redisValue = UserAccountInfoRedis.builder()
                .customerId("4981674")
                .accounts(List.of())
                .build();
        var redisDuration = Duration.ofMinutes(30);

        when(accountService.getSavingAccounts(cif)).thenReturn(coreAccounts);
        when(productService.getProducts(channelId)).thenReturn(urProducts);
        doNothing().when(cacheService).save(redisKey, redisValue, redisDuration);

        var result = userService.checkUserStatus(clientId, userReference, channelId, cif);

        Assertions.assertTrue(result.isCustomerLinked());
        assertNull(result.getAccounts());
        Assertions.assertEquals(ProductPage.LIST, result.getProductPage());

        verify(accountService).getSavingAccounts(cif);
        verify(productService).getProducts(channelId);
        verify(cacheService).save(redisKey, redisValue, redisDuration);
    }

    @Test
    void checkUserStatus_Error_GetProducts() {
        var userReference = "123";
        var channelId = "123";
        var clientId = "123";
        String cif = "4981674";

        when(productService.getProducts(channelId)).thenThrow(new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR));

        try {
            userService.checkUserStatus(clientId, userReference, channelId, cif);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), be.getResponseCode().getCode());
        }

        verify(productService).getProducts(channelId);
    }

    @Test
    void checkUserStatus_Error_GetAccounts() {
        var userReference = "123";
        var channelId = "123";
        var clientId = "123";
        String cif = "4981674";

        var urProducts = List.of(URAProduct.builder().coreProductCode("6003").subAccount(false).status(ProductConstant.STATUS_ACTIVE).build());

        when(accountService.getSavingAccounts(cif)).thenThrow(new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR));
        when(productService.getProducts(channelId)).thenReturn(urProducts);

        try {
            userService.checkUserStatus(clientId, userReference, channelId, cif);
        } catch (BusinessException be) {
            Assertions.assertEquals(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), be.getResponseCode().getCode());
        }

        verify(accountService).getSavingAccounts(cif);
        verify(productService).getProducts(channelId);
    }
}
